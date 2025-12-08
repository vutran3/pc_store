// src/services/api/orderApi.ts

import { post } from '@/services/api.service';

interface OrderItem {
    productId: string;
    quantity: number;
}

interface CreateOrderPayload {
    customerId: string;
    shipAddress: string;
    items: OrderItem[];
    totalPrice: number;
    isPaid: string;
    orderStatus: string;
}

interface ApiResponse<T> {
    code?: number;
    result: T;
    message?: string;
}

export const orderApi = {
    createOrder: async (payload: CreateOrderPayload) => {
        const res = await post<ApiResponse<any>>('/api/orders', payload);
        return res.data;
    }
};
