import { ENDPOINTS } from "@/constants";
import { get } from "@/services/api.service";
import { UserResponse } from "@/types";
import { createAsyncThunk } from "@reduxjs/toolkit";
import { z } from "zod";
export const getUserInfo = createAsyncThunk(
    "user/getUserInfo",
    async ({ token }: { token: string }, { rejectWithValue }) => {
        try {
            const response = await get<UserResponse>(ENDPOINTS.USER_INFO, { token });
            return response.data;
        } catch (error) {
            if (error instanceof z.ZodError) {
                return rejectWithValue(error.errors);
            }
            return rejectWithValue((error as Error).message);
        }
    }
);
