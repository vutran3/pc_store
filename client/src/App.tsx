import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Login, NotFound, Register } from "./pages";
import Home from "./pages/Home";
import ProductsPage from "./pages/Product/Product";
import ProductDetail from "./pages/Product/ProductDetail";
import ScrollToTop from "./components/ScrollToTop";
import ProtectedRoutes from "./config/routers/ProtectedRoutes";
import Cart from "./pages/Cart/Cart";
import { Toaster } from "./components/ui/toaster";
import { Customer, OrderPage, Product } from "./pages/Admin";
import About from "./pages/About";
import Header from "./components/layout/Header";
import Order from "./pages/Order";
import OrderDetail from "./pages/Order/[id]";
import { useEffect } from "react";
import { del, get } from "./services/api.service";
import { ENDPOINTS } from "./constants";
import { useDispatch, useSelector } from "react-redux";
import { clearCart } from "./redux/slices/cart";
import ENDPOINT from "./constants/endpoint";
import { RootState } from "./redux/store";

function App() {
    const dispatch = useDispatch();

    const { info: user } = useSelector((state: RootState) => state.user);
    const clearCartApi = async (customerId: string) => {
        const token = localStorage.getItem("token");
        const headers = token ? { Authorization: `Bearer ${token}` } : undefined;
        await del(ENDPOINT.CART.DELETE_ALL, { customerId }, headers);
    };

    useEffect(() => {
        const paymentId = localStorage.getItem("paymentId");
        let timerId = null;

        if (paymentId && user?.id) {
            timerId = setInterval(() => {
                get(`${ENDPOINTS.PAYMENT_STATUS}/${paymentId}`).then((res) => {
                    const status = res.data;
                    if (status && status === "approved") {
                        localStorage.removeItem("paymentId");
                        clearCartApi(user?.id);
                        dispatch(clearCart());
                    }
                });
            }, 1000);
        }

        return () => {
            if (timerId) clearInterval(timerId);
        };
    }, [user]);

    return (
        <BrowserRouter>
            <ScrollToTop />
            <Header />
            <ProtectedRoutes>
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/products" element={<ProductsPage />} />
                    <Route path="/products/:id" element={<ProductDetail />} />
                    <Route path="/cart" element={<Cart />} />
                    <Route path="/order" element={<Order />} />
                    <Route path="/order/:id" element={<OrderDetail />} />
                    <Route path="/about" element={<About />} />
                    <Route path="admin/products" element={<Product />} />
                    <Route path="admin/customers" element={<Customer />} />
                    <Route path="admin/orders" element={<OrderPage />} />
                    <Route path="*" element={<NotFound />} />
                </Routes>
            </ProtectedRoutes>
            <Toaster />
        </BrowserRouter>
    );
}

export default App;
