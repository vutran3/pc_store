import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { ShoppingCart, Heart, Star, Truck, Shield, Plus, Minus } from 'lucide-react';
import { MOCK_PRODUCTS } from '@/data/mockProducts';
import ProductDetailSkeleton from './components/ProductDetailSkeleton';
import ImageModal from '@/components/ImageModal';

export default function ProductDetail() {
    const { id } = useParams();
    const [quantity, setQuantity] = useState(1);
    const [loading, setLoading] = useState(true);
    const [product, setProduct] = useState<any>(null);
    const [isImageModalOpen, setIsImageModalOpen] = useState(false);
    
    useEffect(() => {
        const fetchProduct = async () => {
            // Giả lập loading
            await new Promise(resolve => setTimeout(resolve, 1000));
            const foundProduct = MOCK_PRODUCTS.find(p => p.id === id);
            setProduct(foundProduct);
            setLoading(false);
        };
        
        fetchProduct();
    }, [id]);

    if (loading) return <ProductDetailSkeleton />;
    if (!product) return <div>Không tìm thấy sản phẩm</div>;

    return (
        <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
            <ImageModal 
                isOpen={isImageModalOpen}
                onClose={() => setIsImageModalOpen(false)}
                imageUrl={product.img}
                alt={product.name}
            />
            <div className="container mx-auto px-4 py-8">
                <div className="bg-white dark:bg-gray-800 rounded-xl p-6 md:p-8">
                    <div className="flex gap-8">
                        {/* Ảnh sản phẩm */}
                        <div className="w-[35%]">
                            <div 
                                className="aspect-square rounded-xl overflow-hidden bg-gray-100 dark:bg-gray-900 cursor-zoom-in"
                                onClick={() => setIsImageModalOpen(true)}
                            >
                                <img 
                                    src={product.img} 
                                    alt={product.name}
                                    className="w-full h-full object-cover"
                                />
                            </div>
                        </div>

                        {/* Thông tin sản phẩm */}
                        <div className="flex-1 space-y-6">
                            <div>
                                <h1 className="text-2xl font-bold text-gray-800 dark:text-gray-100 mb-2">
                                    {product.name}
                                </h1>
                                <div className="flex items-center gap-4">
                                    <div className="flex items-center gap-1">
                                        <Star className="w-5 h-5 fill-yellow-400 text-yellow-400" />
                                        <span className="text-sm text-gray-600 dark:text-gray-400">4.8</span>
                                    </div>
                                    <span className="text-sm text-blue-600 dark:text-blue-400">
                                        {product.supplier?.name}
                                    </span>
                                </div>
                            </div>

                            <div className="border-t border-b border-gray-200 dark:border-gray-700 py-6">
                                <div className="flex items-baseline gap-3 mb-2">
                                    <span className="text-3xl font-bold text-blue-600 dark:text-blue-400">
                                        {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(product.priceAfterDiscount)}
                                    </span>
                                    {product.originalPrice && (
                                        <span className="text-lg text-gray-400 line-through">
                                            {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(product.originalPrice)}
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
                                    <span className="text-gray-600 dark:text-gray-400">Số lượng:</span>
                                    <div className="flex items-center border border-gray-300 dark:border-gray-600 rounded-lg">
                                        <button 
                                            onClick={() => setQuantity(Math.max(1, quantity - 1))}
                                            className="p-2 hover:bg-gray-100 dark:hover:bg-gray-700"
                                        >
                                            <Minus className="w-4 h-4" />
                                        </button>
                                        <span className="w-12 text-center">{quantity}</span>
                                        <button 
                                            onClick={() => setQuantity(quantity + 1)}
                                            className="p-2 hover:bg-gray-100 dark:hover:bg-gray-700"
                                        >
                                            <Plus className="w-4 h-4" />
                                        </button>
                                    </div>
                                </div>

                                <div className="flex gap-4">
                                    <button className="flex-1 bg-blue-600 hover:bg-blue-700 text-white py-3 px-6 rounded-lg font-medium transition-colors flex items-center justify-center gap-2">
                                        <ShoppingCart className="w-5 h-5" />
                                        Thêm vào giỏ hàng
                                    </button>
                                    <button className="p-3 border border-gray-300 dark:border-gray-600 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700">
                                        <Heart className="w-5 h-5" />
                                    </button>
                                </div>
                            </div>

                            <div className="space-y-4 pt-6">
                                <div className="flex items-center gap-3 text-gray-600 dark:text-gray-400">
                                    <Truck className="w-5 h-5" />
                                    <span>Giao hàng miễn phí toàn quốc</span>
                                </div>
                                <div className="flex items-center gap-3 text-gray-600 dark:text-gray-400">
                                    <Shield className="w-5 h-5" />
                                    <span>Bảo hành 24 tháng chính hãng</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Mô tả sản phẩm */}
                    <div className="mt-12">
                        <h2 className="text-xl font-bold text-gray-800 dark:text-gray-100 mb-4">
                            Mô tả sản phẩm
                        </h2>
                        <div className="prose dark:prose-invert max-w-none">
                            <p>Đây là phần mô tả chi tiết về sản phẩm...</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
