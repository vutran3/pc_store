import { ENDPOINTS } from "@/constants";
import { del, get, post } from "@/services/api.service";
import { CartCountResponse } from "@/types";
import { createAsyncThunk } from "@reduxjs/toolkit";
import { z } from "zod";
export const getCartCount = createAsyncThunk(
    "cart/getCartCount",
    async ({ userId }: { userId: string }, { rejectWithValue }) => {
        try {
            const response = await get<CartCountResponse>(`${ENDPOINTS.CART.CART_COUNT}/items/${userId}`);
            return response.data;
        } catch (error) {
            if (error instanceof z.ZodError) {
                return rejectWithValue(error.errors);
            }
            return rejectWithValue((error as Error).message);
        }
    }
);

export const addToCart = createAsyncThunk(
    "cart/addToCart",
    async (
        { userId, productId, quantity }: { userId: string; productId: string; quantity?: number },
        { rejectWithValue }
    ) => {
        try {
            const response = await post<any>(
                `${ENDPOINTS.CART.ADD(userId)}?productId=${productId}&quantity=${quantity ?? 1}`,
                {}
            );
            return response.data;
        } catch (error: any) {
            return rejectWithValue(error.response?.data?.message || "Thêm vào giỏ hàng thất bại");
        }
    }
);

export const deleteCartItem = createAsyncThunk(
    "cart/deleteCartItem",
    async ({ userId, productId }: { userId: string; productId: string }, { rejectWithValue }) => {
        try {
            const response = await del<any>(
                `${ENDPOINTS.CART.DELETE_ITEM}?customerId=${userId}&productId=${productId}`,
                {}
            );
            return response.data;
        } catch (error: any) {
            return rejectWithValue(error.response?.data?.message || "Xóa sản phẩm trong giỏ hàng thất bại");
        }
    }
);
