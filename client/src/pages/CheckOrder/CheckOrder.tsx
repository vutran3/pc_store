import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAppSelector } from '@/hooks'
import { RootState } from '@/redux/store'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useToast } from '@/hooks/use-toast'
import { get, post } from '@/services/api.service'
import { orderApi } from '@/services/api/orderApi'
import OrderItems from './components/OrderItems'
import PaymentMethod from './components/PaymentMethod'
import OrderTotal from './components/OrderTotal'
import { Address, CustomerInfo } from '@/types/Auth'

interface ShippingInfo {
    name: string
    phone: string
    address: string
    note: string
}

const CheckOrder = () => {
    const navigate = useNavigate()
    const { toast } = useToast()
    const [paymentMethod, setPaymentMethod] = useState('cod')
    const [isLoading, setIsLoading] = useState(false)
    const [resolvedCustomerId, setResolvedCustomerId] = useState<string>('')
    const [showSuccessModal, setShowSuccessModal] = useState(false)
    const [countdown, setCountdown] = useState(5)
    const [shippingInfo, setShippingInfo] = useState<ShippingInfo>({
        name: '',
        phone: '',
        address: '',
        note: ''
    })

    // Address management states
    const [, setCustomerInfo] = useState<CustomerInfo | null>(null)
    const [addresses, setAddresses] = useState<Address[]>([])
    const [selectedAddressIndex, setSelectedAddressIndex] = useState<number>(-1)
    const [isAddressManual, setIsAddressManual] = useState(false)
    const [isSavingAddress, setIsSavingAddress] = useState(false)

    const cartItems = useAppSelector((state: RootState) => state.cart.cartItems)
    const user = useAppSelector((state: RootState) => state.auth.user)
    const selectedItems = cartItems.filter((item) => item.checked)

    // Helper functions to resolve customer ID
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

    // Resolve customerId from multiple sources
    useEffect(() => {
        const resolveCustomerId = async () => {
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

            // 3) decode token
            const token = localStorage.getItem('token');
            if (token) {
                const payload = decodeJwt(token);
                if (payload?.customerId && isValidObjectId(payload.customerId)) {
                    setResolvedCustomerId(payload.customerId);
                    return;
                }
                if (payload?.sub) {
                    try {
                        const resp = await get<{ result: any }>(`/api/customers/username/${encodeURIComponent(payload.sub)}`);
                        const userFromApi = resp.data?.result;
                        const id = userFromApi?.id || userFromApi?._id;
                        if (id && isValidObjectId(id)) {
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

        resolveCustomerId();
    }, [user])

    // Fetch customer info including addresses
    useEffect(() => {
        const fetchCustomerInfo = async () => {
            try {
                const response = await get<{ code: number, result: CustomerInfo }>('/api/customers/info')
                if (response.data.code === 1000 && response.data.result) {
                    const info = response.data.result
                    setCustomerInfo(info)
                    setAddresses(info.addresses || [])

                    // Auto-fill name and phone from customer info
                    setShippingInfo(prev => ({
                        ...prev,
                        name: `${info.lastName} ${info.firstName}`.trim() || prev.name,
                        phone: info.phoneNumber || prev.phone
                    }))

                    // If there are addresses, select default or first one
                    if (info.addresses && info.addresses.length > 0) {
                        const defaultIndex = info.addresses.findIndex(addr => addr.isDefault)
                        const indexToSelect = defaultIndex >= 0 ? defaultIndex : 0
                        setSelectedAddressIndex(indexToSelect)
                        setShippingInfo(prev => ({
                            ...prev,
                            address: info.addresses[indexToSelect].address
                        }))
                        setIsAddressManual(false)
                    } else {
                        // No addresses, force manual input
                        setIsAddressManual(true)
                        setSelectedAddressIndex(-1)
                    }
                }
            } catch (error) {
                console.error('Failed to fetch customer info:', error)
                setIsAddressManual(true)
            }
        }

        fetchCustomerInfo()
    }, [])

    // Handle address selection change
    const handleAddressSelect = (value: string) => {
        if (value === 'manual') {
            setIsAddressManual(true)
            setSelectedAddressIndex(-1)
            setShippingInfo(prev => ({ ...prev, address: '' }))
        } else {
            const index = parseInt(value)
            setSelectedAddressIndex(index)
            setIsAddressManual(false)
            if (addresses[index]) {
                setShippingInfo(prev => ({ ...prev, address: addresses[index].address }))
            }
        }
    }

    // Save new address or set as default
    const handleSaveAddress = async (setAsDefault: boolean = false) => {
        if (!shippingInfo.address.trim()) {
            toast({ variant: 'destructive', title: 'Lỗi', description: 'Vui lòng nhập địa chỉ' })
            return
        }

        setIsSavingAddress(true)
        try {
            const response = await post<{ code: number, result: any }>('/api/customers/address/save', {
                address: shippingInfo.address,
                isDefault: setAsDefault ? 'true' : 'false'
            })

            if (response.data.code === 1000) {
                toast({
                    title: 'Thành công',
                    description: setAsDefault ? 'Đã lưu và đặt làm địa chỉ mặc định' : 'Đã lưu địa chỉ mới'
                })

                // Refresh addresses
                const infoResponse = await get<{ code: number, result: CustomerInfo }>('/api/customers/info')
                if (infoResponse.data.code === 1000 && infoResponse.data.result) {
                    const newAddresses = infoResponse.data.result.addresses || []
                    setAddresses(newAddresses)
                    setCustomerInfo(infoResponse.data.result)

                    // Select the newly saved address
                    const newIndex = newAddresses.findIndex(addr => addr.address === shippingInfo.address)
                    if (newIndex >= 0) {
                        setSelectedAddressIndex(newIndex)
                        setIsAddressManual(false)
                    }
                }
            } else {
                throw new Error('Không thể lưu địa chỉ')
            }
        } catch (error: any) {
            console.error('Failed to save address:', error)
            toast({
                variant: 'destructive',
                title: 'Lỗi',
                description: error.message || 'Không thể lưu địa chỉ'
            })
        } finally {
            setIsSavingAddress(false)
        }
    }

    // Handle success modal countdown and auto redirect
    useEffect(() => {
        let timer: NodeJS.Timeout
        if (showSuccessModal && countdown > 0) {
            timer = setTimeout(() => {
                setCountdown(prev => prev - 1)
            }, 1000)
        } else if (showSuccessModal && countdown === 0) {
            // Auto redirect to home
            navigate('/')
        }
        return () => clearTimeout(timer)
    }, [showSuccessModal, countdown, navigate])

    const subtotal = selectedItems.reduce((acc, item) => acc + item.product.priceAfterDiscount * item.quantity, 0)
    const shippingFee = subtotal >= 500000 ? 0 : 30000
    const total = subtotal + shippingFee

    const handleInputChange = (field: keyof ShippingInfo, value: string) => {
        setShippingInfo(prev => ({ ...prev, [field]: value }))
    }

    const validateForm = () => {
        if (!shippingInfo.name.trim()) {
            toast({ variant: 'destructive', title: 'Lỗi', description: 'Vui lòng nhập họ và tên' })
            return false
        }
        if (!shippingInfo.phone.trim()) {
            toast({ variant: 'destructive', title: 'Lỗi', description: 'Vui lòng nhập số điện thoại' })
            return false
        }
        if (!shippingInfo.address.trim()) {
            toast({ variant: 'destructive', title: 'Lỗi', description: 'Vui lòng nhập địa chỉ giao hàng' })
            return false
        }
        return true
    }

    const handlePlaceOrder = async () => {
        if (!validateForm()) return

        setIsLoading(true)

        try {
            if (paymentMethod === 'vnpay') {
                // Handle VNPay payment
                toast({ title: 'Đang tạo liên kết thanh toán...', description: 'Vui lòng đợi' })

                const response = await get<{
                    code: number,
                    result: {
                        status: string,
                        message: string,
                        url: string
                    }
                }>(`/api/payment/create_payment?amount=${total}`)

                if (response.data.code === 1000 && response.data.result.url) {
                    // Mở tab mới với URL thanh toán
                    window.open(response.data.result.url, '_blank')
                    // toast({
                    //     title: 'Chuyển hướng thành công!',
                    //     description: 'Trang thanh toán đã được mở trong tab mới'
                    // })
                } else {
                    throw new Error('Không thể tạo liên kết thanh toán')
                }
            } else {
                // Handle COD order
                // console.log('Place COD order', { shippingInfo, total })
                // toast({
                //     title: 'Đặt hàng thành công!',
                //     description: 'Đơn hàng của bạn đã được tiếp nhận. Bạn sẽ thanh toán khi nhận hàng.'
                // })
            }
        } catch (error: any) {
            console.error('Payment error:', error)
            toast({
                variant: 'destructive',
                title: 'Lỗi thanh toán',
                description: error.message || 'Có lỗi xảy ra khi xử lý thanh toán. Vui lòng thử lại.'
            })
        } finally {
            // Create order regardless of payment method
            console.log('=== FINALLY BLOCK STARTED ===');
            console.log('User:', user);
            console.log('User ID:', user?.id);
            console.log('Resolved Customer ID:', resolvedCustomerId);
            console.log('Selected Items:', selectedItems);

            try {
                const customerId = resolvedCustomerId || user?.id;
                console.log('Final Customer ID to use:', customerId);

                if (customerId) {
                    const orderPayload = {
                        customerId: customerId,
                        shipAddress: shippingInfo.address,
                        items: selectedItems.map(item => ({
                            productId: item.productId,
                            quantity: item.quantity
                        })),
                        totalPrice: total,
                        isPaid: paymentMethod === 'vnpay' ? 'true' : 'false',
                        orderStatus: 'DELIVERING'
                    };

                    console.log('=== CREATING ORDER ===');
                    console.log('Order Payload:', orderPayload);

                    const result = await orderApi.createOrder(orderPayload)
                    console.log('=== ORDER CREATED SUCCESSFULLY ===')
                    console.log('Order Result:', result)

                    // Backend automatically handles cart cleanup after order creation
                    // Reset cart state in frontend to reflect the changes
                    // dispatch(resetCart())

                    // Show success modal
                    setShowSuccessModal(true)
                    setCountdown(5)


                } else {
                    console.warn('=== NO CUSTOMER ID FOUND ===');
                    console.warn('Cannot create order - no customer ID available');
                    console.warn('User:', user);
                    console.warn('Resolved Customer ID:', resolvedCustomerId);

                    toast({
                        variant: 'destructive',
                        title: 'Lỗi xác thực',
                        description: 'Không thể xác định thông tin khách hàng. Vui lòng thử đăng nhập lại.'
                    });
                }
            } catch (orderError: any) {
                console.error('=== ORDER CREATION FAILED ===');
                console.error('Order Error:', orderError);
                console.error('Error Message:', orderError.message);
                console.error('Error Response:', orderError.response?.data);

                toast({
                    variant: 'destructive',
                    title: 'Lỗi tạo đơn hàng',
                    description: orderError.message || 'Không thể lưu đơn hàng vào hệ thống.'
                });
            }

            setIsLoading(false)
        }
    }

    const handleContinueShopping = () => {
        navigate('/')
    }

    if (selectedItems.length === 0) {
        return (
            <div className='container mx-auto py-8 max-w-4xl text-center'>
                <h1 className='text-2xl font-bold mb-4'>Không có sản phẩm nào được chọn</h1>
                <Button onClick={handleContinueShopping}>Tiếp tục mua sắm</Button>
            </div>
        )
    }

    const handleGoHome = () => {
        navigate('/')
    }

    return (
        <div className='container mx-auto py-8 max-w-6xl'>
            <h1 className='text-2xl font-bold mb-6'>Xác nhận đơn hàng</h1>

            <div className='grid grid-cols-1 lg:grid-cols-3 gap-6'>
                {/* Left column - Shipping Info & Payment */}
                <div className='lg:col-span-2 space-y-6'>
                    {/* Shipping Information */}
                    <div className='bg-white p-6 rounded-lg shadow-sm border'>
                        <h2 className='text-lg font-semibold mb-4'>Thông tin người nhận</h2>
                        <div className='grid grid-cols-1 md:grid-cols-2 gap-4'>
                            <div>
                                <Label htmlFor='name'>Họ và tên *</Label>
                                <Input
                                    id='name'
                                    placeholder='Nhập họ và tên'
                                    value={shippingInfo.name}
                                    onChange={(e) => handleInputChange('name', e.target.value)}
                                    className={!shippingInfo.name.trim() ? 'border-red-300' : ''}
                                />
                            </div>
                            <div>
                                <Label htmlFor='phone'>Số điện thoại *</Label>
                                <Input
                                    id='phone'
                                    placeholder='Nhập số điện thoại'
                                    value={shippingInfo.phone}
                                    onChange={(e) => handleInputChange('phone', e.target.value)}
                                    className={!shippingInfo.phone.trim() ? 'border-red-300' : ''}
                                />
                            </div>

                            {/* Address Selection */}
                            <div className='md:col-span-2'>
                                <Label htmlFor='address-select'>Chọn địa chỉ giao hàng *</Label>
                                {addresses.length > 0 ? (
                                    <select
                                        id='address-select'
                                        value={isAddressManual ? 'manual' : selectedAddressIndex.toString()}
                                        onChange={(e) => handleAddressSelect(e.target.value)}
                                        className='w-full mt-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent'
                                    >
                                        {addresses.map((addr, index) => (
                                            <option key={index} value={index.toString()}>
                                                {addr.address} {addr.isDefault && '(Mặc định)'}
                                            </option>
                                        ))}
                                        <option value='manual'>+ Nhập địa chỉ mới</option>
                                    </select>
                                ) : (
                                    <p className='text-sm text-gray-500 mt-1'>Bạn chưa có địa chỉ nào được lưu. Vui lòng nhập địa chỉ bên dưới.</p>
                                )}
                            </div>

                            {/* Address Input */}
                            <div className='md:col-span-2'>
                                <Label htmlFor='address'>
                                    {addresses.length > 0 && !isAddressManual ? 'Địa chỉ đã chọn' : 'Địa chỉ giao hàng *'}
                                </Label>
                                <Input
                                    id='address'
                                    placeholder='Nhập địa chỉ giao hàng'
                                    value={shippingInfo.address}
                                    onChange={(e) => handleInputChange('address', e.target.value)}
                                    className={!shippingInfo.address.trim() ? 'border-red-300' : ''}
                                    readOnly={addresses.length > 0 && !isAddressManual}
                                />

                                {/* Save Address Buttons */}
                                {(isAddressManual || addresses.length === 0) && shippingInfo.address.trim() && (
                                    <div className='flex gap-2 mt-2'>
                                        <Button
                                            type='button'
                                            variant='outline'
                                            size='sm'
                                            onClick={() => handleSaveAddress(false)}
                                            disabled={isSavingAddress}
                                        >
                                            {isSavingAddress ? 'Đang lưu...' : 'Lưu địa chỉ'}
                                        </Button>
                                        <Button
                                            type='button'
                                            variant='outline'
                                            size='sm'
                                            onClick={() => handleSaveAddress(true)}
                                            disabled={isSavingAddress}
                                        >
                                            {isSavingAddress ? 'Đang lưu...' : 'Lưu & Đặt làm mặc định'}
                                        </Button>
                                    </div>
                                )}
                            </div>

                            <div className='md:col-span-2'>
                                <Label htmlFor='note'>Ghi chú</Label>
                                <Input
                                    id='note'
                                    placeholder='Ghi chú thêm (tùy chọn)'
                                    value={shippingInfo.note}
                                    onChange={(e) => handleInputChange('note', e.target.value)}
                                />
                            </div>
                        </div>
                    </div>

                    {/* Payment Method */}
                    <PaymentMethod
                        selectedMethod={paymentMethod}
                        onMethodChange={setPaymentMethod}
                    />

                    {/* Action Buttons */}
                    <div className='bg-white p-6 rounded-lg shadow-sm border'>
                        <div className='flex flex-col sm:flex-row gap-3'>
                            <Button
                                variant='outline'
                                onClick={handleContinueShopping}
                                className='flex-1'
                            >
                                Tiếp tục mua sắm
                            </Button>
                            <Button
                                onClick={handlePlaceOrder}
                                disabled={isLoading}
                                className='flex-1 py-3 text-lg font-medium'
                                size='lg'
                            >
                                {isLoading ? (
                                    <div className='flex items-center gap-2'>
                                        <div className='w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin'></div>
                                        Đang xử lý...
                                    </div>
                                ) : (
                                    paymentMethod === 'vnpay' ? 'Thanh toán' : 'Đặt hàng'
                                )}
                            </Button>
                        </div>
                    </div>
                </div>

                {/* Right column - Order Summary */}
                <div className='space-y-6'>
                    <OrderItems items={selectedItems} />
                    <OrderTotal
                        subtotal={subtotal}
                        shippingFee={shippingFee}
                        total={total}
                    />
                </div>
            </div>

            {/* Success Modal */}
            {showSuccessModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white rounded-lg p-8 max-w-md w-full mx-4 text-center">
                        <div className="mb-4">
                            <div className="mx-auto w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mb-4">
                                <svg className="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                                </svg>
                            </div>
                            <h3 className="text-xl font-bold text-gray-900 mb-2">
                                Đặt hàng thành công!
                            </h3>
                            <p className="text-gray-600 mb-6">
                                Cảm ơn bạn đã đặt hàng. Chúng tôi sẽ liên hệ với bạn sớm nhất có thể.
                            </p>
                            <p className="text-sm text-gray-500 mb-4">
                                Tự động về trang chủ sau {countdown} giây
                            </p>
                        </div>
                        <div className="flex gap-3 justify-center">
                            <Button
                                onClick={handleGoHome}
                                className="px-6 py-2"
                            >
                                Về trang chủ ngay
                            </Button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    )
}

export default CheckOrder