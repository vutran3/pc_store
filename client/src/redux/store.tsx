import { configureStore } from '@reduxjs/toolkit';
import { authReducer } from '@/redux/slices';
import { productReducer } from '@/redux/slices/product';
import cartReducer from '@/redux/slices/cart';

const store = configureStore({
  reducer: {
    auth: authReducer,
    product: productReducer,
    cart: cartReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export default store;