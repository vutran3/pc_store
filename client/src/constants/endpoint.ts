const BASE_URL = import.meta.env.VITE_API_URL;


const ENDPOINT = {
    LOGIN: `${BASE_URL}/api/auth/log-in`,
    REGISTER: `${BASE_URL}/api/auth/customers/register`,
    LOGOUT: `${BASE_URL}/api/auth/logout`,
    REFRESH_TOKEN: `${BASE_URL}/api/auth/refresh`,
    INTROSPECT: `${BASE_URL}/api/auth/introspect`,
    PRODUCTS: `${BASE_URL}/api/products`,
    PRODUCT_DETAIL: `${BASE_URL}/api/products/id`,
}

// Helper function để tạo URL với query parameters
export const buildProductsUrl = (page: number = 0, size: number = 10) => {
    return `${ENDPOINT.PRODUCTS}?page=${page}&size=${size}`;
}

export const buildProductDetailUrl = (id: string) => {
    return `${ENDPOINT.PRODUCT_DETAIL}?id=${id}`;
}

export default ENDPOINT;
