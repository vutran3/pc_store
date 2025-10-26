const BASE_URL = import.meta.env.VITE_API_URL;


const ENDPOINT = {
    LOGIN: `${BASE_URL}/api/auth/log-in`,
    REGISTER: `${BASE_URL}/api/auth/customers/register`,
    LOGOUT: `${BASE_URL}/api/auth/logout`,
    REFRESH_TOKEN: `${BASE_URL}/api/auth/refresh`,
    INTROSPECT: `${BASE_URL}/api/auth/introspect`,
}

export default ENDPOINT;
