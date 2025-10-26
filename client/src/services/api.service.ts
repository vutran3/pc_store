import { AxiosResponse } from 'axios';
import instance from '../config/axios.config';

export const get = async <T>(uri: string, params?: any): Promise<AxiosResponse<T>> => {
    const res = await instance.get<T>(uri, { params });
    return res;
};

export const post = async <T>(
  uri: string,
  data?: any,
  headers?: any
): Promise<AxiosResponse<T>> => {
  try {
      console.log('Calling API:', uri, { data, headers });
      const res = await instance.post<T>(uri, data, { headers });
      return res;
  } catch (error: any) {
      console.error('API Error:', {
          uri,
          data,
          error: error.response?.data || error.message
      });
      throw error;
  }
};
export const put = async <T>(uri: string, data?: any): Promise<AxiosResponse<T>> => {
    const res = await instance.put<T>(uri, data);
    return res;
};

export const del = async <T>(uri: string, data?: any): Promise<AxiosResponse<T>> => {
    const res = await instance.delete<T>(uri, { data });
    return res;
};
export const patch = async <T>(uri: string, data?: any, headers?: any): Promise<AxiosResponse<T>> => {
    const res = await instance.patch<T>(uri, data, { headers });
    return res;
};
// Example usage functions
// export const fetchBannerData = () => api.get<{ result: BannerData[] }>(ENDPOINTS.BANNER);
// export const getCategories = () => api.get<{ result: Category[] }>(ENDPOINTS.CATEGORIES);
// export const loginUser = (credentials: Credentials) => api.post<LoginResponse>(ENDPOINTS.LOGIN, credentials);
// export const getAdvertisementById = (id: number) => api.get(`${ENDPOINTS.ADVERTISEMENT}/${id}`);
// export const getAllReviewsAdvertisementById = (id: number) => api.get(`${ENDPOINTS.REVIEWS}/${id}`);
