import { useNavigate } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import OrderSummary from './components/OrderSummary'
import ShippingInfoForm from './components/ShippingInfoForm'

const Checkout = () => {
    const navigate = useNavigate()

    const handleContinueToOrder = () => {
        navigate('/check-order')
    }

    return (
        <div className='container mx-auto py-8'>
            <h1 className='text-3xl font-bold mb-6'>Thông tin đặt hàng</h1>
            <div className='grid grid-cols-1 md:grid-cols-3 gap-8'>
                <div className='md:col-span-2 space-y-6'>
                    <ShippingInfoForm />
                    <div className='bg-white p-6 rounded-lg shadow-sm border'>
                        <Button
                            onClick={handleContinueToOrder}
                            className='w-full py-3 text-lg font-medium'
                            size='lg'
                        >
                            Tiếp tục đặt hàng
                        </Button>
                    </div>
                </div>
                <div>
                    <OrderSummary />
                </div>
            </div>
        </div>
    )
}

export default Checkout
