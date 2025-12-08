// src/pages/Cart/Cart.tsx

import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { AppDispatch, RootState } from '@/redux/store';
import {
    fetchCart,
    increaseQuantity,
    decreaseQuantity,
    removeFromCart,
    clearCart
} from '@/redux/thunks/cart';
import { toggleCartItem } from '@/redux/slices/cart';
import CartItem from './components/CartItem';
import CartSummary from './components/CartSummary';
import EmptyCart from './components/EmptyCart';
import { Trash2 } from 'lucide-react';
import { useToast } from '@/hooks/use-toast';
import { get } from '@/services/api.service';

export default function Cart() {
    const dispatch = useDispatch<AppDispatch>();
    const navigate = useNavigate();
    const { toast } = useToast();

    const { cartItems, totalQuantity, totalPrice, loading, error } = useSelector((state: RootState) => state.cart);
    const { user } = useSelector((state: RootState) => state.auth);
    // resolvedCustomerId will hold the actual customer id (Mongo ObjectId) to use for API calls
    const [resolvedCustomerId, setResolvedCustomerId] = useState<string>('');

    // helper: decode JWT payload (base64url) and return parsed payload or null
    const decodeJwt = (token?: string) => {
        if (!token) return null;
        try {
            const parts = token.split('.');
            if (parts.length < 2) return null;
            const payload = parts[1];
            let base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
            while (base64.length % 4) base64 += '=';
            const decoded = atob(base64);
            const json = decodeURIComponent(decoded.split('').map(c => {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));
            return JSON.parse(json);
        } catch (e) {
            console.error('decodeJwt error', e);
            return null;
        }
    };

    const isValidObjectId = (id?: string) => /^[a-fA-F0-9]{24}$/.test(String(id ?? ''));

    // Resolve customerId from Redux user, localStorage, token payload, or backend lookup by username
    useEffect(() => {
        let cancelled = false;
        const resolve = async () => {
            // 1) prefer Redux user id
            if (user?.id) {
                setResolvedCustomerId(user.id);
                return;
            }

            // 2) check common localStorage keys
            const possible = localStorage.getItem('userId') || localStorage.getItem('user') || localStorage.getItem('authUser') || localStorage.getItem('userInfo');
            if (possible) {
                try {
                    const parsed = JSON.parse(possible);
                    const id = parsed?.id || parsed?.userId || parsed?.customerId || parsed?._id;
                    if (id && isValidObjectId(id)) {
                        setResolvedCustomerId(id);
                        return;
                    }
                } catch {
                    if (isValidObjectId(possible)) {
                        setResolvedCustomerId(possible);
                        return;
                    }
                }
            }

            // 3) decode token, prefer payload.customerId
            const token = localStorage.getItem('token');
            if (token) {
                const payload = decodeJwt(token);
                if (payload?.customerId && isValidObjectId(payload.customerId)) {
                    setResolvedCustomerId(payload.customerId);
                    return;
                }
                // 4) fallback: if payload.sub (username) exists, try backend lookup
                if (payload?.sub) {
                    try {
                        const resp = await get<{ result: any }>(`/api/customers/username/${encodeURIComponent(payload.sub)}`);
                        const userFromApi = resp.data?.result;
                        const id = userFromApi?.id || userFromApi?._id;
                        if (id && isValidObjectId(id) && !cancelled) {
                            setResolvedCustomerId(id);
                            try { localStorage.setItem('userId', id); } catch { }
                            return;
                        }
                    } catch (e) {
                        console.warn('Lookup by username failed', e);
                    }
                }
            }
        };
        resolve();
        return () => { cancelled = true; };
    }, [user, dispatch]);

    useEffect(() => {
        if (resolvedCustomerId) {
            dispatch(fetchCart(resolvedCustomerId));
        }
    }, [resolvedCustomerId, dispatch]);

    // Items are automatically checked when fetched via Redux

    // selected items state
    const toggleSelect = (productId: string) => {
        dispatch(toggleCartItem(productId));
    };

    // compute totals for selected items
    const selectedItems = cartItems.filter(it => it.checked);
    const subtotal = selectedItems.reduce((s, it) => s + (it.product?.priceAfterDiscount ?? 0) * it.quantity, 0);
    const originalTotal = selectedItems.reduce((s, it) => s + (it.product?.originalPrice ?? it.product?.priceAfterDiscount ?? 0) * it.quantity, 0);
    const savings = Math.max(0, originalTotal - subtotal);
    const shipping = subtotal >= 500000 ? 0 : 0; // business rule: free over threshold; adapt if needed
    const grandTotal = subtotal + shipping;
    const selectedCount = selectedItems.reduce((c, it) => c + it.quantity, 0);

    const handleIncrease = async (productId: string) => {
        const id = resolvedCustomerId;
        if (!id) {
            toast({ variant: 'destructive', title: 'Chưa có customerId', description: 'Vui lòng đăng nhập' });
            return;
        }
        try {
            await dispatch(increaseQuantity({ customerId: id, productId })).unwrap();
            await dispatch(fetchCart(id));
            toast({ title: 'Đã tăng số lượng' });
        } catch (error: any) {
            toast({ variant: 'destructive', title: 'Lỗi', description: error || 'Không thể tăng số lượng' });
        }
    };

    const handleDecrease = async (productId: string) => {
        const id = resolvedCustomerId;
        if (!id) {
            toast({ variant: 'destructive', title: 'Chưa có customerId', description: 'Vui lòng đăng nhập' });
            return;
        }
        try {
            await dispatch(decreaseQuantity({ customerId: id, productId })).unwrap();
            await dispatch(fetchCart(id));
            toast({ title: 'Đã giảm số lượng' });
        } catch (error: any) {
            toast({ variant: 'destructive', title: 'Lỗi', description: error || 'Không thể giảm số lượng' });
        }
    };

    const handleRemove = async (productId: string) => {
        if (!confirm('Bạn có chắc muốn xóa sản phẩm này?')) return;
        const id = resolvedCustomerId;
        if (!id) {
            toast({ variant: 'destructive', title: 'Chưa có customerId', description: 'Vui lòng đăng nhập' });
            return;
        }
        try {
            await dispatch(removeFromCart({ customerId: id, productId })).unwrap();
            await dispatch(fetchCart(id));
            toast({ title: 'Đã xóa sản phẩm' });
        } catch (error: any) {
            toast({ variant: 'destructive', title: 'Lỗi', description: error || 'Không thể xóa sản phẩm' });
        }
    };

    const handleClearCart = async () => {
        if (!confirm('Bạn có chắc muốn xóa toàn bộ giỏ hàng?')) return;
        const id = resolvedCustomerId;
        if (!id) {
            toast({ variant: 'destructive', title: 'Chưa có customerId', description: 'Vui lòng đăng nhập' });
            return;
        }
        try {
            await dispatch(clearCart(id)).unwrap();
            await dispatch(fetchCart(id));
            toast({ title: 'Đã xóa toàn bộ giỏ hàng' });
        } catch (error: any) {
            toast({ variant: 'destructive', title: 'Lỗi', description: error || 'Không thể xóa giỏ hàng' });
        }
    };

    const handleCheckout = () => {
        if (selectedItems.length === 0) {
            toast({
                variant: 'destructive',
                title: 'Chưa chọn sản phẩm',
                description: 'Vui lòng chọn ít nhất một sản phẩm để thanh toán'
            });
            return;
        }
        navigate('/check-order');
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="text-center">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
                    <p className="text-gray-600">Đang tải giỏ hàng...</p>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="text-center">
                    <p className="text-red-500 mb-4">{error}</p>
                    <button
                        onClick={() => resolvedCustomerId && dispatch(fetchCart(resolvedCustomerId))}
                        className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                    >
                        Thử lại
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="container mx-auto px-4 py-8">
                <div className="mb-8">
                    <h1 className="text-3xl font-bold text-gray-800 mb-2">
                        Giỏ hàng của bạn
                    </h1>
                    <p className="text-gray-600">{totalQuantity} sản phẩm</p>
                </div>

                {cartItems.length === 0 ? (
                    <EmptyCart />
                ) : (
                    <div className="flex gap-8">
                        <div className="flex-1">
                            <div className="bg-white rounded-xl p-6 mb-4 border border-gray-200">
                                <div className="flex items-center justify-between mb-4">
                                    <h2 className="font-semibold text-gray-800">
                                        Sản phẩm ({cartItems.length})
                                    </h2>
                                    <button
                                        onClick={handleClearCart}
                                        className="flex items-center gap-2 px-4 py-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                                    >
                                        <Trash2 className="w-4 h-4" />
                                        Xóa tất cả
                                    </button>
                                </div>

                                <div className="space-y-4">
                                    {cartItems.map((item) => (
                                        <CartItem
                                            key={item.productId}
                                            item={item}
                                            selected={!!item.checked}
                                            onToggle={() => toggleSelect(item.productId)}
                                            onIncrease={() => handleIncrease(item.productId)}
                                            onDecrease={() => handleDecrease(item.productId)}
                                            onRemove={() => handleRemove(item.productId)}
                                        />
                                    ))}
                                </div>
                            </div>
                        </div>

                        <div className="w-96 flex-shrink-0">
                            <CartSummary
                                totalItems={selectedCount}
                                subtotal={subtotal}
                                savings={savings}
                                shipping={shipping}
                                grandTotal={grandTotal}
                                onCheckout={handleCheckout}
                            />
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}