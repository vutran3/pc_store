// axios.config.ts
import { isTokenValid } from '@/utils/token';
import axios from 'axios';

const instance = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
    withCredentials: true,  // thử đổi thành true
    headers: {
        "ngrok-skip-browser-warning": "true"
    }
});

// Request interceptor
instance.interceptors.request.use(
    config => {
        // Log request để debug
        console.log('Request:', {
            url: config.url,
            method: config.method,
            data: config.data,
            headers: config.headers
        });

        const token = localStorage.getItem('token');
        if (token && isTokenValid(token)) {
            config.headers['Authorization'] = `Bearer ${token}`;
        } else {
            localStorage.removeItem('token');
        }
        return config;
    },
    error => {
        console.error('Request Error:', error);
        return Promise.reject(error);
    }
);

// Response interceptor
instance.interceptors.response.use(
    response => {
        // Log response để debug
        console.log('Response:', {
            status: response.status,
            data: response.data
        });
        return response;
    },
    error => {
        console.error('Response Error:', {
            status: error.response?.status,
            data: error.response?.data,
            message: error.message
        });
        return Promise.reject(error);
    }
);

export default instance;
