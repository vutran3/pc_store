import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { fetchProducts } from '../thunks/product';

export interface Supplier {
  name: string;
  address: string;
}

export interface Product {
  id: string;
  name: string;
  img: string;
  priceAfterDiscount: number;
  originalPrice: number;
  discountPercent: number;
  priceDiscount: number;
  inStock: number;
  supplier: Supplier;
  updateDetail: boolean;
}

export interface Pageable {
  pageNumber: number;
  pageSize: number;
  sort: {
    empty: boolean;
    unsorted: boolean;
    sorted: boolean;
  };
  offset: number;
  paged: boolean;
  unpaged: boolean;
}

export interface ProductsResponse {
  content: Product[];
  pageable: Pageable;
  totalElements: number;
  totalPages: number;
  last: boolean;
  size: number;
  number: number;
  sort: {
    empty: boolean;
    unsorted: boolean;
    sorted: boolean;
  };
  numberOfElements: number;
  first: boolean;
  empty: boolean;
}

export interface ProductState {
  products: Product[];
  pagination: {
    currentPage: number;
    totalPages: number;
    totalElements: number;
    pageSize: number;
    first: boolean;
    last: boolean;
  };
  loading: boolean;
  error: string | null;
}

const initialState: ProductState = {
  products: [],
  pagination: {
    currentPage: 0,
    totalPages: 0,
    totalElements: 0,
    pageSize: 10,
    first: true,
    last: false,
  },
  loading: false,
  error: null,
};

const productSlice = createSlice({
  name: 'product',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    resetProducts: (state) => {
      state.products = [];
      state.pagination = initialState.pagination;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchProducts.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchProducts.fulfilled, (state, action: PayloadAction<ProductsResponse>) => {
        state.loading = false;
        state.products = action.payload.content;
        state.pagination = {
          currentPage: action.payload.number,
          totalPages: action.payload.totalPages,
          totalElements: action.payload.totalElements,
          pageSize: action.payload.size,
          first: action.payload.first,
          last: action.payload.last,
        };
      })
      .addCase(fetchProducts.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string || 'Không thể tải danh sách sản phẩm';
      });
  },
});

export const { clearError, resetProducts } = productSlice.actions;
export const productReducer = productSlice.reducer;
