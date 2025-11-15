// src/redux/slices/cart.ts

import { createSlice } from '@reduxjs/toolkit';
import { CartState } from '@/types/cart.types';
import {
  fetchCart,
  addToCart,
  increaseQuantity,
  decreaseQuantity,
  removeFromCart,
  clearCart,
} from '@/redux/thunks/cart';

const initialState: CartState = {
  cart: null,
  cartItems: [],
  totalQuantity: 0,
  totalPrice: 0,
  loading: false,
  error: null,
};

const cartSlice = createSlice({
  name: 'cart',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    resetCart: () => initialState,
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchCart.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchCart.fulfilled, (state, action) => {
        state.loading = false;
        state.cartItems = action.payload;
        state.totalQuantity = action.payload.reduce((sum, item) => sum + item.quantity, 0);
        state.totalPrice = action.payload.reduce(
          (sum, item) => sum + (item.product?.priceAfterDiscount ?? 0) * item.quantity,
          0
        );
      })
      .addCase(fetchCart.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      .addCase(addToCart.fulfilled, (state, action) => {
        state.cart = action.payload;
      })
      .addCase(increaseQuantity.fulfilled, (state, action) => {
        const productId = (action.meta.arg as any).productId;
        const item = state.cartItems.find((i) => i.productId === productId);
        if (item) {
          item.quantity += 1;
          state.totalQuantity += 1;
          state.totalPrice += item.product.priceAfterDiscount;
        }
      })
      .addCase(decreaseQuantity.fulfilled, (state, action) => {
        const productId = (action.meta.arg as any).productId;
        const item = state.cartItems.find((i) => i.productId === productId);
        if (item && item.quantity > 1) {
          item.quantity -= 1;
          state.totalQuantity -= 1;
          state.totalPrice -= item.product.priceAfterDiscount;
        } else if (item && item.quantity === 1) {
          state.cartItems = state.cartItems.filter((i) => i.productId !== productId);
        }
      })
      .addCase(removeFromCart.fulfilled, (state, action) => {
        const productId = (action.meta.arg as any).productId;
        const item = state.cartItems.find((i) => i.productId === productId);
        if (item) {
          state.totalQuantity -= item.quantity;
          state.totalPrice -= (item.product.priceAfterDiscount ?? 0) * item.quantity;
          state.cartItems = state.cartItems.filter((i) => i.productId !== productId);
        }
      })
      .addCase(clearCart.fulfilled, (state) => {
        state.cartItems = [];
        state.totalQuantity = 0;
        state.totalPrice = 0;
      });
  },
});

export const { clearError, resetCart } = cartSlice.actions;
export const cartReducer = cartSlice.reducer;
export default cartSlice.reducer;