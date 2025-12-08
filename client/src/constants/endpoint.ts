// src/constants/endpoint.ts

const BASE_URL = import.meta.env.VITE_API_URL;

const ENDPOINT = {
    LOGIN: `${BASE_URL}/api/auth/log-in`,
    REGISTER: `${BASE_URL}/api/customers/register`,
    LOGOUT: `${BASE_URL}/api/auth/logout`,
    REFRESH_TOKEN: `${BASE_URL}/api/auth/refresh`,
    INTROSPECT: `${BASE_URL}/api/auth/introspect`,

    PRODUCTS: `${BASE_URL}/api/products`,
    PRODUCT_DETAIL: `${BASE_URL}/api/products/id`,

    CART: {
        COUNT: `${BASE_URL}/api/cart/countOfItems`,
        CREATE: (customerId: string) => `${BASE_URL}/api/cart/createCart/${customerId}`,
        ADD: (customerId: string) => `${BASE_URL}/api/cart/${customerId}/addCart`,
        INCREASE: `${BASE_URL}/api/cart/increaseQuantity`,
        DECREASE: `${BASE_URL}/api/cart/decreaseQuantity`,
        DELETE_ITEM: `${BASE_URL}/api/cart/deleteItem`,
        DELETE_ALL: `${BASE_URL}/api/cart/deleteCart`,
        GET_PRODUCT_IDS: (customerId: string) => `${BASE_URL}/api/cart/productIds/${customerId}`,
        GET_ITEMS: (customerId: string) => `${BASE_URL}/api/cart/items/${customerId}`
    }
};

export const buildProductsUrl = (page: number = 0, size: number = 10) => {
    return `${ENDPOINT.PRODUCTS}?page=${page}&size=${size}`;
};

export const buildProductDetailUrl = (id: string) => {
    return `${ENDPOINT.PRODUCT_DETAIL}?id=${id}`;
};

export default ENDPOINT;
