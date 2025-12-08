interface PaymentMethodProps {
    selectedMethod: string
    onMethodChange: (method: string) => void
}

const PaymentMethod = ({ selectedMethod, onMethodChange }: PaymentMethodProps) => {
    return (
        <div className='bg-white p-6 rounded-lg shadow-sm border'>
            <h2 className='text-lg font-semibold mb-4'>Phương thức thanh toán</h2>
            <div className='space-y-3'>
                <label className='flex items-center p-4 border rounded-lg cursor-pointer hover:bg-gray-50'>
                    <input
                        type='radio'
                        name='paymentMethod'
                        value='cod'
                        checked={selectedMethod === 'cod'}
                        onChange={(e) => onMethodChange(e.target.value)}
                        className='w-4 h-4 text-blue-600 mr-3'
                    />
                    <div className='flex-1'>
                        <div className='font-medium'>Thanh toán khi nhận hàng (COD)</div>
                        <div className='text-sm text-gray-500'>Thanh toán bằng tiền mặt khi nhận hàng</div>
                    </div>
                </label>

                <label className='flex items-center p-4 border rounded-lg cursor-pointer hover:bg-gray-50'>
                    <input
                        type='radio'
                        name='paymentMethod'
                        value='vnpay'
                        checked={selectedMethod === 'vnpay'}
                        onChange={(e) => onMethodChange(e.target.value)}
                        className='w-4 h-4 text-blue-600 mr-3'
                    />
                    <div className='flex-1'>
                        <div className='font-medium'>Thanh toán qua VNPAY</div>
                        <div className='text-sm text-gray-500'>Thanh toán trực tuyến qua VNPAY</div>
                    </div>
                    <img
                        src='https://vnpay.vn/s1/statics.vnpay.vn/2023/9/06ncktiwd6dc1694418196384.png'
                        alt='VNPAY'
                        className='w-12 h-8 object-contain'
                    />
                </label>
            </div>
        </div>
    )
}

export default PaymentMethod