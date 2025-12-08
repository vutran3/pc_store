import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'

const ShippingInfoForm = () => {
    return (
        <div className='bg-white p-6 rounded-lg shadow-md'>
            <h2 className='text-xl font-bold mb-4'>Thông tin giao hàng</h2>
            <div className='grid grid-cols-1 gap-4'>
                <div>
                    <Label htmlFor='name'>Họ và tên</Label>
                    <Input id='name' placeholder='Nhập họ và tên' />
                </div>
                <div>
                    <Label htmlFor='phone'>Số điện thoại</Label>
                    <Input id='phone' placeholder='Nhập số điện thoại' />
                </div>
                <div>
                    <Label htmlFor='address'>Địa chỉ</Label>
                    <Input id='address' placeholder='Nhập địa chỉ' />
                </div>
                <div>
                    <Label htmlFor='note'>Ghi chú</Label>
                    <Input id='note' placeholder='Ghi chú thêm (tùy chọn)' />
                </div>
            </div>
        </div>
    )
}

export default ShippingInfoForm
