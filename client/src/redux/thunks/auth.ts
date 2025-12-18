import { ENDPOINTS } from "@/constants";
import { post } from "@/services/api.service";
import {
    CheckTokenValidResponse,
    LoginCredentials,
    LoginResponse,
    LogoutResponse,
    RegisterCredentials,
    RegisterResponse
} from "@/types";
import { createAsyncThunk } from "@reduxjs/toolkit";
import { z } from "zod";

export const login = createAsyncThunk("auth/login", async (credentials: LoginCredentials, { rejectWithValue }) => {
    try {
        const response = await post<LoginResponse>(ENDPOINTS.LOGIN, credentials);
        if (!response.data?.result?.token) {
            throw new Error("Token không hợp lệ");
        }
        return response.data;
    } catch (error) {
        if (error instanceof z.ZodError) {
            return rejectWithValue(error.errors);
        }
        return rejectWithValue((error as Error).message);
    }
});

// Thunk để đăng ký người dùng
export const register = createAsyncThunk(
    "auth/register",
    async (credentials: RegisterCredentials, { rejectWithValue }) => {
        try {
            const response = await post<RegisterResponse>(ENDPOINTS.REGISTER, credentials);
            return response.data;
        } catch (error) {
            if (error instanceof z.ZodError) {
                return rejectWithValue(error.errors);
            }
            return rejectWithValue((error as Error).message);
        }
    }
);

// Thunk check token valid
export const checkTokenValid = createAsyncThunk("auth/checkTokenValid", async (token: string, { rejectWithValue }) => {
    try {
        const response = await post<CheckTokenValidResponse>(ENDPOINTS.INTROSPECT, { token });

        if (!response.data?.result?.valid) {
            throw new Error("Token không hợp lệ");
        }

        return response.data;
    } catch (error) {
        if (error instanceof z.ZodError) {
            return rejectWithValue(error.errors);
        }
        return rejectWithValue((error as Error).message);
    }
});

export const logout = createAsyncThunk("auth/logout", async (token: string, { rejectWithValue }) => {
    try {
        const response = await post<LogoutResponse>(ENDPOINTS.LOGOUT, { token });
        return response.data;
    } catch (error) {
        if (error instanceof z.ZodError) {
            return rejectWithValue(error.errors);
        }
        return rejectWithValue((error as Error).message);
    }
});
