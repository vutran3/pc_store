import { ENDPOINTS } from "@/constants";
import { post } from "@/services/api.service";
import { CheckTokenValidResponse, Credentials, LoginResponse, RegisterCredentials, RegisterResponse } from "@/types/Auth";
import { createAsyncThunk } from "@reduxjs/toolkit";

// Thunk để login người dùng
export const login = createAsyncThunk(
  "auth/login",
  async (credentials: Credentials, { rejectWithValue }) => {
    try {
      const response = await post<LoginResponse>(
        ENDPOINTS.LOGIN,
        credentials
      );

      if (!response.data?.result?.token) {
        throw new Error('Token không hợp lệ');
      }

      return response.data;
    } catch (error: any) {
      console.error('Login error:', error.response?.data || error.message);
      return rejectWithValue(
        error.response?.data?.message ||
        'Đăng nhập thất bại, vui lòng thử lại'
      );
    }
  }
);

// Thunk để đăng ký người dùng
export const register = createAsyncThunk(
  "auth/register",
  async (credentials: RegisterCredentials, { rejectWithValue }) => {
    try {
      const response = await post<RegisterResponse>(
        ENDPOINTS.REGISTER,
        credentials
      );
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || error.message);
    }
  }
);

// Thunk check token valid
export const checkTokenValid = createAsyncThunk(
  "auth/checkTokenValid",
  async (token: string, { rejectWithValue }) => {
    try {
      const response = await post<CheckTokenValidResponse>(
        ENDPOINTS.INTROSPECT,
        { token }
      );

      if (!response.data?.result?.valid) {
        throw new Error('Token không hợp lệ');
      }

      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || error.message);
    }
  }
);
