import { formatCurrency } from '@/lib/utils'

interface OrderTotalProps {
    subtotal: number
    shippingFee: number
    total: number
}

const OrderTotal = ({ subtotal, shippingFee, total }: OrderTotalProps) => {
    return (
        <div className='bg-white p-6 rounded-lg shadow-sm border'>
            <h2 className='text-lg font-semibold mb-4'>Chi tiết thanh toán</h2>
            <div className='space-y-3'>
                <div className='flex justify-between text-gray-600'>
                    <span>Tạm tính:</span>
                    <span>{formatCurrency(subtotal)}</span>
                </div>
                <div className='flex justify-between text-gray-600'>
                    <span>Phí vận chuyển:</span>
                    <span className='text-green-600'>
                        {shippingFee === 0 ? 'Miễn phí' : formatCurrency(shippingFee)}
                    </span>
                </div>
                <div className='border-t pt-3'>
                    <div className='flex justify-between text-xl font-bold'>
                        <span>Tổng cộng:</span>
                        <span className='text-blue-600'>{formatCurrency(total)}</span>
                    </div>
                </div>
                {shippingFee === 0 && (
                    <div className='text-sm text-green-600 bg-green-50 p-2 rounded'>
                        🎉 Đã đạt miễn phí vận chuyển cho đơn hàng từ 500.000đ
                    </div>
                )}
            </div>
        </div>
    )
}

export default OrderTotal