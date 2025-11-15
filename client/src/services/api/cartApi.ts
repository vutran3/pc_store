// src/services/api/cartApi.ts

import { get, post, del } from '@/services/api.service';
import ENDPOINT from '@/constants/endpoint';
import { Cart, CartItem } from '@/types/cart.types';

interface ApiResponse<T> {
  code?: number;
  result: T;
  message?: string;
}

export const cartApi = {
  getCartItemsCount: async (customerId: string): Promise<number> => {
    const res = await get<ApiResponse<number>>(ENDPOINT.CART.COUNT, { customerId });
    return res.data.result;
  },

  createCart: async (customerId: string): Promise<Cart> => {
    const res = await post<ApiResponse<Cart>>(ENDPOINT.CART.CREATE(customerId));
    return res.data.result;
  },

  addToCart: async (customerId: string, productId: string, quantity: number): Promise<Cart> => {
    const res = await post<ApiResponse<Cart>>(
      ENDPOINT.CART.ADD(customerId),
      null,
      { productId, quantity } // send as query params
    );
    return res.data.result;
  },

  increaseQuantity: async (customerId: string, productId: string): Promise<Cart> => {
    const res = await post<ApiResponse<Cart>>(ENDPOINT.CART.INCREASE, null, { customerId, productId });
    return res.data.result;
  },

  decreaseQuantity: async (customerId: string, productId: string): Promise<Cart> => {
    const res = await post<ApiResponse<Cart>>(ENDPOINT.CART.DECREASE, null, { customerId, productId });
    return res.data.result;
  },

  deleteItem: async (customerId: string, productId: string): Promise<Cart> => {
    const token = localStorage.getItem('token');
    const headers = token ? { Authorization: `Bearer ${token}` } : undefined;
    const res = await del<ApiResponse<Cart>>(ENDPOINT.CART.DELETE_ITEM, { customerId, productId }, headers);
    return res.data.result;
  },

  clearCart: async (customerId: string): Promise<string> => {
    const token = localStorage.getItem('token');
    const headers = token ? { Authorization: `Bearer ${token}` } : undefined;
    const res = await del<ApiResponse<string>>(ENDPOINT.CART.DELETE_ALL, { customerId }, headers);
    return res.data.result;
  },

  getProductIds: async (customerId: string): Promise<string[]> => {
    const res = await get<ApiResponse<string[]>>(ENDPOINT.CART.GET_PRODUCT_IDS(customerId));
    return res.data.result;
  },

  getCartItems: async (customerId: string): Promise<CartItem[]> => {
    const res = await get<ApiResponse<CartItem[]>>(ENDPOINT.CART.GET_ITEMS(customerId));
    return res.data.result;
  },
};