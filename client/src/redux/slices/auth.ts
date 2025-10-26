import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { CheckTokenValidResponse, LoginResponse } from '../../types/Auth';
import { checkTokenValid, login } from '../thunks/auth';

interface AuthState {
  user: { id: string } | null;
  token: string | null;
  status: 'idle' | 'loading' | 'succeeded' | 'failed';
  isLogin: boolean;
  error: string | null;
}

const initialState: AuthState = {
  user: null,
  token: localStorage.getItem('token'),
  status: 'idle',
  isLogin: false,
  error: null,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logout: (state) => {
      state.user = null;
      state.token = null;
      state.status = 'idle';
      state.isLogin = false;
      state.error = null;
      localStorage.removeItem('token');
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => {
        state.status = 'loading';
        state.error = null;
      })
      .addCase(login.fulfilled, (state, action: PayloadAction<LoginResponse>) => {
        state.status = 'succeeded';
        state.token = action.payload.result.token;
        state.isLogin = true;
        state.error = null;
        localStorage.setItem('token', action.payload.result.token);
      })
      .addCase(login.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload as string;
        state.isLogin = false;
        state.token = null;
        localStorage.removeItem('token');
      })
      .addCase(checkTokenValid.pending, (state) => {
        state.status = 'loading';
      })
      .addCase(checkTokenValid.fulfilled, (state, action: PayloadAction<CheckTokenValidResponse>) => {
        state.status = 'succeeded';
        if (action.payload.result.valid) {
          state.isLogin = true;
        } else {
          state.isLogin = false;
          state.token = null;
          localStorage.removeItem('token');
        }
      })
      .addCase(checkTokenValid.rejected, (state) => {
        state.status = 'failed';
        state.isLogin = false;
        state.token = null;
        localStorage.removeItem('token');
      });
  },
});

export const { logout } = authSlice.actions;
export default authSlice.reducer;
