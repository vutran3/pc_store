// src/redux/thunks/cart.ts

import { createAsyncThunk } from '@reduxjs/toolkit';
import { cartApi } from '@/services/api/cartApi';
import { get } from '@/services/api.service';
import ENDPOINT from '@/constants/endpoint';
import { Cart, CartItem, CartItemWithProduct } from '@/types/Cart';

type ProductAny = any;

interface ApiResponse<T> {
  code?: number;
  result: T;
}

// Fetch cart items (with product details)
export const fetchCart = createAsyncThunk<CartItemWithProduct[], string, { rejectValue: string }>(
  'cart/fetchCart',
  async (customerId, { rejectWithValue }) => {
    try {
      const cartItems: any[] = await cartApi.getCartItems(customerId);
      const itemsWithProducts = await Promise.all(
        cartItems.map(async (item) => {
          try {
            if (item?.product && typeof item.product === 'object') {
              const product = item.product;
              product.priceAfterDiscount = Number(product.priceAfterDiscount ?? 0);
              product.originalPrice = product.originalPrice !== undefined ? Number(product.originalPrice) : undefined;
              return {
                productId: item.productId ?? product.id ?? product._id,
                quantity: Number(item.quantity ?? 0),
                product,
              } as CartItemWithProduct;
            } else {
              const resp = await get<{ code?: number; result: any }>(`${ENDPOINT.PRODUCTS}/${item.productId}`);
              const product = resp.data?.result ?? null;
              if (!product) return null;
              product.priceAfterDiscount = Number(product.priceAfterDiscount ?? 0);
              product.originalPrice = product.originalPrice !== undefined ? Number(product.originalPrice) : undefined;
              return {
                productId: item.productId,
                quantity: Number(item.quantity ?? 0),
                product,
              } as CartItemWithProduct;
            }
          } catch (err) {
            console.error('Error fetching product for cart item', item, err);
            return null;
          }
        })
      );

      return itemsWithProducts.filter(Boolean) as CartItemWithProduct[];
    } catch (err: any) {
      return rejectWithValue(err?.message || 'Không thể tải giỏ hàng');
    }
  }
);

export const addToCart = createAsyncThunk<any, { customerId: string; productId: string; quantity: number }, { rejectValue: string }>(
  'cart/addToCart',
  async ({ customerId, productId, quantity }, { rejectWithValue }) => {
    try {
      return await cartApi.addToCart(customerId, productId, quantity);
    } catch (err: any) {
      return rejectWithValue(err?.message || 'Không thể thêm sản phẩm vào giỏ');
    }
  }
);

export const increaseQuantity = createAsyncThunk<any, { customerId: string; productId: string }, { rejectValue: string }>(
  'cart/increaseQuantity',
  async ({ customerId, productId }, { rejectWithValue }) => {
    try {
      return await cartApi.increaseQuantity(customerId, productId);
    } catch (err: any) {
      return rejectWithValue(err?.message || 'Không thể tăng số lượng');
    }
  }
);

export const decreaseQuantity = createAsyncThunk<any, { customerId: string; productId: string }, { rejectValue: string }>(
  'cart/decreaseQuantity',
  async ({ customerId, productId }, { rejectWithValue }) => {
    try {
      return await cartApi.decreaseQuantity(customerId, productId);
    } catch (err: any) {
      return rejectWithValue(err?.message || 'Không thể giảm số lượng');
    }
  }
);

export const removeFromCart = createAsyncThunk<any, { customerId: string; productId: string }, { rejectValue: string }>(
  'cart/removeFromCart',
  async ({ customerId, productId }, { rejectWithValue }) => {
    try {
      return await cartApi.deleteItem(customerId, productId);
    } catch (err: any) {
      return rejectWithValue(err?.message || 'Không thể xóa sản phẩm');
    }
  }
);

export const clearCart = createAsyncThunk<string, string, { rejectValue: string }>(
  'cart/clearCart',
  async (customerId, { rejectWithValue }) => {
    try {
      return await cartApi.clearCart(customerId);
    } catch (err: any) {
      return rejectWithValue(err?.message || 'Không thể xóa giỏ hàng');
    }
  }
);