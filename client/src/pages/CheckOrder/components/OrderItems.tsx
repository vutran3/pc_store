import { formatCurrency } from '@/lib/utils'
import { CartItemWithProduct } from '@/types/Cart'

interface OrderItemsProps {
    items: CartItemWithProduct[]
}

const OrderItems = ({ items }: OrderItemsProps) => {
    return (
        <div className='bg-white p-6 rounded-lg shadow-sm border'>
            <h2 className='text-lg font-semibold mb-4'>Sản phẩm ({items.length})</h2>
            <div className='space-y-3 max-h-60 overflow-y-auto'>
                {items.map((item) => (
                    <div key={item.product.id} className='flex items-center space-x-3 p-3 bg-gray-50 rounded-lg'>
                        <img
                            src={item.product.img}
                            alt={item.product.name}
                            className='w-12 h-12 object-cover rounded-md flex-shrink-0'
                        />
                        <div className='flex-1 min-w-0'>
                            <h3 className='font-medium text-sm text-gray-900 truncate'>{item.product.name}</h3>
                            <div className='flex items-center justify-between mt-1'>
                                <span className='text-xs text-gray-500'>SL: {item.quantity}</span>
                                <div className='text-right'>
                                    <span className='font-semibold text-sm text-blue-600'>
                                        {formatCurrency(item.product.priceAfterDiscount * item.quantity)}
                                    </span>
                                    {item.product.originalPrice > item.product.priceAfterDiscount && (
                                        <div className='text-xs text-gray-400 line-through'>
                                            {formatCurrency(item.product.originalPrice * item.quantity)}
                                        </div>
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}

export default OrderItems