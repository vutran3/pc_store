import { Button } from '@/components/ui/button'
import { useAppSelector } from '@/hooks'
import { formatCurrency } from '@/lib/utils'
import { RootState } from '@/redux/store'
import { CartItemWithProduct } from '@/types/Cart'

const OrderSummary = () => {
    const cartItems = useAppSelector((state: RootState) => state.cart.cartItems)
    const selectedItems = cartItems.filter((item) => item.checked)
    const subtotal = selectedItems.reduce((acc, item) => acc + item.product.priceAfterDiscount * item.quantity, 0)
    const shippingFee = subtotal >= 500000 ? 0 : 30000
    const total = subtotal + shippingFee

    return (
        <div className='bg-gray-100 p-6 rounded-lg'>
            <h2 className='text-xl font-bold mb-4'>Tóm tắt đơn hàng</h2>
            <div className='space-y-2 mb-4'>
                {selectedItems.map((item: CartItemWithProduct) => (
                    <div key={item.product.id} className='flex justify-between'>
                        <span>
                            {item.product.name} x {item.quantity}
                        </span>
                        <span>{formatCurrency(item.product.priceAfterDiscount * item.quantity)}</span>
                    </div>
                ))}
            </div>
            <div className='border-t pt-4 space-y-2'>
                <div className='flex justify-between'>
                    <span>Tạm tính</span>
                    <span>{formatCurrency(subtotal)}</span>
                </div>
                <div className='flex justify-between'>
                    <span>Phí vận chuyển</span>
                    <span>{shippingFee === 0 ? 'Miễn phí' : formatCurrency(shippingFee)}</span>
                </div>
                <div className='flex justify-between font-bold text-lg'>
                    <span>Tổng cộng</span>
                    <span>{formatCurrency(total)}</span>
                </div>
            </div>
            <div className='mt-6'>
                <h3 className='text-lg font-semibold mb-2'>Phương thức thanh toán</h3>
                <div className='space-y-2'>
                    <div className='flex items-center'>
                        <input type='radio' id='cod' name='paymentMethod' value='cod' className='mr-2' defaultChecked />
                        <label htmlFor='cod'>Thanh toán khi nhận hàng (COD)</label>
                    </div>
                    <div className='flex items-center'>
                        <input type='radio' id='vnpay' name='paymentMethod' value='vnpay' className='mr-2' />
                        <label htmlFor='vnpay'>Thanh toán qua VNPAY</label>
                    </div>
                </div>
            </div>
            <Button className='w-full mt-6'>Đặt hàng</Button>
        </div>
    )
}

export default OrderSummary
