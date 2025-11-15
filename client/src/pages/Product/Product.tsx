import { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { SlidersHorizontal } from 'lucide-react';
import { fetchProducts } from '@/redux/thunks/product';
import { AppDispatch, RootState } from '@/redux/store';
import ProductCard from './components/ProductCard';
import ProductSkeleton from './components/ProductSkeleton';
import ProductFilters from './components/ProductFilters';

const ProductsPage = () => {
    const dispatch = useDispatch<AppDispatch>();
    const { products, loading, error, pagination } = useSelector((state: RootState) => state.product);

    const [filteredProducts, setFilteredProducts] = useState<any[]>([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [showFilters, setShowFilters] = useState(false);
    const [sortBy, setSortBy] = useState('newest');

    useEffect(() => {
        dispatch(fetchProducts({ page: 0, size: 10 }));
    }, [dispatch]);

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

    if (error) {
        return (
            <div className="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center">
                <div className="text-center">
                    <p className="text-red-500 mb-4">{error}</p>
                    <button
                        onClick={() => dispatch(fetchProducts({ page: 0, size: 10 }))}
                        className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                    >
                        Thử lại
                    </button>
                </div>
            </div>
        );
    }

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
                                trong tổng số{' '}
                                <span className="font-semibold text-gray-800 dark:text-gray-100">
                                    {pagination.totalElements}
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
                            <>
                                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                                    {filteredProducts.map((p) => (
                                        <ProductCard key={p.id} product={p} />
                                    ))}
                                </div>

                                {/* Pagination controls */}
                                {pagination.totalPages > 1 && (
                                    <div className="flex justify-center mt-8">
                                        <div className="flex gap-2">
                                            {!pagination.first && (
                                                <button
                                                    onClick={() => dispatch(fetchProducts({
                                                        page: pagination.currentPage - 1,
                                                        size: pagination.pageSize
                                                    }))}
                                                    className="px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700"
                                                >
                                                    Trước
                                                </button>
                                            )}

                                            <span className="px-4 py-2 bg-blue-600 text-white rounded-lg">
                                                {pagination.currentPage + 1} / {pagination.totalPages}
                                            </span>

                                            {!pagination.last && (
                                                <button
                                                    onClick={() => dispatch(fetchProducts({
                                                        page: pagination.currentPage + 1,
                                                        size: pagination.pageSize
                                                    }))}
                                                    className="px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700"
                                                >
                                                    Tiếp
                                                </button>
                                            )}
                                        </div>
                                    </div>
                                )}
                            </>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ProductsPage;