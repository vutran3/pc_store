// src/pages/Product/ProductDetail.tsx

import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { ShoppingCart, Heart, Star, Truck, Shield, Plus, Minus } from "lucide-react";
import { fetchProductDetail } from "@/redux/thunks/product";
import { clearCurrentProduct } from "@/redux/slices/product";
import { addToCart, fetchCart } from "@/redux/thunks/cart";
import { AppDispatch, RootState } from "@/redux/store";
import ProductDetailSkeleton from "./components/ProductDetailSkeleton";
import ImageModal from "@/components/ImageModal";
import { useToast } from "@/hooks/use-toast";

export default function ProductDetail() {
    const { id } = useParams();
    const dispatch = useDispatch<AppDispatch>();
    const navigate = useNavigate();
    const { toast } = useToast();

    const {
        currentProduct: product,
        productDetailLoading: loading,
        productDetailError: error
    } = useSelector((state: RootState) => state.product);
    const { user } = useSelector((state: RootState) => state.auth);
    const customerId = user?.id || "";

    const [quantity, setQuantity] = useState(1);
    const [isImageModalOpen, setIsImageModalOpen] = useState(false);
    const [isAddingToCart, setIsAddingToCart] = useState(false);

    useEffect(() => {
        if (id) {
            dispatch(fetchProductDetail(id));
        }

        return () => {
            dispatch(clearCurrentProduct());
        };
    }, [id, dispatch]);

    // helper: decode JWT payload (base64url) and return parsed payload or null
    const decodeJwt = (token?: string) => {
        if (!token) return null;
        try {
            const parts = token.split(".");
            if (parts.length < 2) return null;
            const payload = parts[1];
            // base64url -> base64
            let base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
            // pad with '='
            while (base64.length % 4) base64 += "=";
            const decoded = atob(base64);
            // decode percent-encoded utf8
            const json = decodeURIComponent(
                decoded
                    .split("")
                    .map((c) => {
                        return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
                    })
                    .join("")
            );
            return JSON.parse(json);
        } catch (e) {
            console.error("decodeJwt error", e);
            return null;
        }
    };

    // check ObjectId-like hex string (24 hex chars)
    const isValidObjectId = (id?: string) => /^[a-fA-F0-9]{24}$/.test(String(id ?? ""));

    const handleAddToCart = async () => {
        // resolve customerId: prefer Redux user, else try decoding JWT from localStorage
        let effectiveCustomerId = customerId || "";
        if (!effectiveCustomerId) {
            const token = localStorage.getItem("token");
            if (token) {
                const payload = decodeJwt(token);
                // backend set claim "customerId" when generating token
                effectiveCustomerId = payload?.customerId ?? payload?.sub ?? "";
                // store for future quick access
                if (effectiveCustomerId) {
                    try {
                        localStorage.setItem("userId", effectiveCustomerId);
                    } catch {}
                }
            }
        }

        // fallback: try some common localStorage keys (if login saved user object)
        if (!effectiveCustomerId) {
            const possible =
                localStorage.getItem("user") || localStorage.getItem("authUser") || localStorage.getItem("userInfo");
            if (possible) {
                try {
                    const parsed = JSON.parse(possible);
                    effectiveCustomerId = parsed?.id || parsed?.userId || parsed?.customerId || "";
                } catch {
                    effectiveCustomerId = possible;
                }
            }
        }

        // validate format: must be 24-char hex (Mongo ObjectId). Nếu không hợp lệ -> show message & debug
        if (!effectiveCustomerId || !isValidObjectId(effectiveCustomerId)) {
            const token = localStorage.getItem("token");
            const payload = token ? decodeJwt(token) : null;
            console.warn("Invalid customerId for addToCart", { effectiveCustomerId, payload });
            toast({
                variant: "destructive",
                title: "Không thể thêm vào giỏ",
                description: "Customer ID không hợp lệ. Vui lòng đăng nhập lại để lấy thông tin người dùng hợp lệ."
            });
            // Nếu muốn thăm dò thêm: bạn có thể gọi endpoint /api/auth/me (nếu BE có) để lấy customerId từ token.
            navigate("/login");
            return;
        }

        if (!product?.id) return;

        try {
            setIsAddingToCart(true);
            console.log("Adding to cart with", { customerId: effectiveCustomerId, productId: product.id, quantity });
            await dispatch(
                addToCart({
                    customerId: effectiveCustomerId,
                    productId: product.id,
                    quantity
                })
            ).unwrap();

            // best-effort: refresh cart state after add
            try {
                await dispatch(fetchCart(effectiveCustomerId));
            } catch (e) {
                /* ignore */
            }

            toast({
                title: "Thành công!",
                description: `Đã thêm ${quantity} sản phẩm vào giỏ hàng`
            });

            setQuantity(1);
        } catch (error: any) {
            toast({
                variant: "destructive",
                title: "Lỗi",
                description: error?.message ?? "Không thể thêm sản phẩm vào giỏ hàng"
            });
        } finally {
            setIsAddingToCart(false);
        }
    };

    if (loading) return <ProductDetailSkeleton />;

    if (error) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="text-center">
                    <p className="text-red-500 mb-4">{error}</p>
                    <button
                        onClick={() => id && dispatch(fetchProductDetail(id))}
                        className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                    >
                        Thử lại
                    </button>
                </div>
            </div>
        );
    }

    if (!product) return <div>Không tìm thấy sản phẩm</div>;

    return (
        <div className="min-h-screen bg-gray-50">
            <ImageModal
                isOpen={isImageModalOpen}
                onClose={() => setIsImageModalOpen(false)}
                imageUrl={product.img}
                alt={product.name}
            />
            <div className="container mx-auto px-4 py-8">
                <div className="bg-white rounded-xl p-6 md:p-8">
                    <div className="flex gap-8">
                        <div className="w-[35%]">
                            <div
                                className="aspect-square rounded-xl overflow-hidden bg-gray-100 cursor-zoom-in"
                                onClick={() => setIsImageModalOpen(true)}
                            >
                                <img src={product.img} alt={product.name} className="w-full h-full object-cover" />
                            </div>
                        </div>

                        <div className="flex-1 space-y-6">
                            <div>
                                <h1 className="text-2xl font-bold text-gray-800 mb-2">{product.name}</h1>
                                <div className="flex items-center gap-4">
                                    <div className="flex items-center gap-1">
                                        <Star className="w-5 h-5 fill-yellow-400 text-yellow-400" />
                                        <span className="text-sm text-gray-600">4.8</span>
                                    </div>
                                    <span className="text-sm text-blue-600">{product.supplier?.name}</span>
                                </div>
                            </div>

                            <div className="border-t border-b border-gray-200 py-6">
                                <div className="flex items-baseline gap-3 mb-2">
                                    <span className="text-3xl font-bold text-blue-600">
                                        {new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
                                            product.priceAfterDiscount
                                        )}
                                    </span>
                                    {product.originalPrice && (
                                        <span className="text-lg text-gray-400 line-through">
                                            {new Intl.NumberFormat("vi-VN", {
                                                style: "currency",
                                                currency: "VND"
                                            }).format(product.originalPrice)}
                                        </span>
                                    )}
                                </div>
                                {product.discountPercent > 0 && (
                                    <span className="inline-block bg-red-100 text-red-600 text-sm font-medium px-2.5 py-1 rounded">
                                        Giảm {product.discountPercent}%
                                    </span>
                                )}
                            </div>

                            <div className="space-y-4">
                                <div className="flex items-center gap-4">
                                    <span className="text-gray-600">Số lượng:</span>
                                    <div className="flex items-center border border-gray-300 rounded-lg">
                                        <button
                                            onClick={() => setQuantity(Math.max(1, quantity - 1))}
                                            className="p-2 hover:bg-gray-100"
                                        >
                                            <Minus className="w-4 h-4" />
                                        </button>
                                        <span className="w-12 text-center">{quantity}</span>
                                        <button
                                            onClick={() => setQuantity(quantity + 1)}
                                            className="p-2 hover:bg-gray-100"
                                        >
                                            <Plus className="w-4 h-4" />
                                        </button>
                                    </div>
                                </div>

                                <div className="flex gap-4">
                                    <button
                                        onClick={handleAddToCart}
                                        disabled={isAddingToCart}
                                        className="flex-1 bg-blue-600 hover:bg-blue-700 text-white py-3 px-6 rounded-lg font-medium transition-colors flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
                                    >
                                        <ShoppingCart className="w-5 h-5" />
                                        {isAddingToCart ? "Đang thêm..." : "Thêm vào giỏ hàng"}
                                    </button>
                                    <button className="p-3 border border-gray-300 rounded-lg hover:bg-gray-100">
                                        <Heart className="w-5 h-5" />
                                    </button>
                                </div>
                            </div>

                            <div className="space-y-4 pt-6">
                                <div className="flex items-center gap-3 text-gray-600">
                                    <Truck className="w-5 h-5" />
                                    <span>Giao hàng miễn phí toàn quốc</span>
                                </div>
                                <div className="flex items-center gap-3 text-gray-600">
                                    <Shield className="w-5 h-5" />
                                    <span>Bảo hành 24 tháng chính hãng</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="mt-12">
                        <h2 className="text-xl font-bold text-gray-800 mb-4">Mô tả sản phẩm</h2>
                        <div className="prose max-w-none">
                            <p>Đây là phần mô tả chi tiết về sản phẩm...</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
