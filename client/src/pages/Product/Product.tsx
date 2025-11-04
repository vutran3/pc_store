import { useState, useEffect } from 'react';
import { SlidersHorizontal } from 'lucide-react';
import { MOCK_PRODUCTS } from '@/data/mockProducts.js';
import ProductCard from './components/ProductCard';
import ProductSkeleton from './components/ProductSkeleton';
import ProductFilters from './components/ProductFilters';

const ProductsPage = () => {
    const [products, setProducts] = useState<any[]>([]);
    const [filteredProducts, setFilteredProducts] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);
    const [searchQuery, setSearchQuery] = useState('');
    const [showFilters, setShowFilters] = useState(false);
    const [sortBy, setSortBy] = useState('newest');

    useEffect(() => {
        setTimeout(() => {
            setProducts(MOCK_PRODUCTS);
            setFilteredProducts(MOCK_PRODUCTS);
            setLoading(false);
        }, 800);
    }, []);

    useEffect(() => {
        let filtered = [...products];
        if (searchQuery) {
            filtered = filtered.filter((p) =>
                p.name.toLowerCase().includes(searchQuery.toLowerCase())
            );
        }
        switch (sortBy) {
            case 'price-asc':
                filtered.sort((a, b) => a.priceAfterDiscount - b.priceAfterDiscount);
                break;
            case 'price-desc':
                filtered.sort((a, b) => b.priceAfterDiscount - a.priceAfterDiscount);
                break;
            case 'discount':
                filtered.sort((a, b) => b.discountPercent - a.discountPercent);
                break;
        }
        setFilteredProducts(filtered);
    }, [searchQuery, sortBy, products]);

    return (
        <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
            <div className="bg-gradient-to-br from-blue-600 via-blue-700 to-indigo-800 text-white">
                <div className="container mx-auto px-4 py-16">
                    <h1 className="text-4xl md:text-5xl font-bold mb-4">
                        Build Your Dream PC
                    </h1>
                    <p className="text-blue-100 max-w-2xl text-lg">
                        High-performance custom PCs built with premium components.
                    </p>
                </div>
            </div>

            <div className="container mx-auto px-4 py-8">
                <div className="flex gap-8">
                    <ProductFilters searchQuery={searchQuery} setSearchQuery={setSearchQuery} />

                    <div className="flex-1">
                        <div className="lg:hidden mb-4">
                            <button
                                onClick={() => setShowFilters(!showFilters)}
                                className="w-full flex items-center justify-center gap-2 px-4 py-3 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-300 font-medium hover:bg-gray-50 dark:hover:bg-gray-700"
                            >
                                <SlidersHorizontal className="w-4 h-4" />
                                Bộ lọc
                            </button>
                        </div>

                        <div className="flex items-center justify-between mb-6 bg-white dark:bg-gray-800 p-4 rounded-xl border border-gray-200 dark:border-gray-700">
                            <p className="text-gray-600 dark:text-gray-400">
                                Hiển thị{' '}
                                <span className="font-semibold text-gray-800 dark:text-gray-100">
                                    {filteredProducts.length}
                                </span>{' '}
                                sản phẩm
                            </p>

                            <select
                                value={sortBy}
                                onChange={(e) => setSortBy(e.target.value)}
                                className="px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-700 dark:text-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                            >
                                <option value="newest">Mới nhất</option>
                                <option value="price-asc">Giá thấp đến cao</option>
                                <option value="price-desc">Giá cao đến thấp</option>
                                <option value="discount">Giảm giá nhiều nhất</option>
                            </select>
                        </div>

                        {loading ? (
                            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                                {Array.from({ length: 8 }).map((_, i) => (
                                    <ProductSkeleton key={i} />
                                ))}
                            </div>
                        ) : filteredProducts.length === 0 ? (
                            <div className="text-center py-16 bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700">
                                <p className="text-gray-500 dark:text-gray-400 text-lg">
                                    Không tìm thấy sản phẩm nào
                                </p>
                            </div>
                        ) : (
                            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                                {filteredProducts.map((p) => (
                                    <ProductCard key={p.id} product={p} />
                                ))}
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ProductsPage;