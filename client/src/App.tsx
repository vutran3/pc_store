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
import AIChatModal from "./components/AIChatModal";
import SellerChatModal from "./components/SellerChatModal";
import AdminChatTab from "./components/AdminChatTab";
import { useAppSelector } from "./hooks";
import { RootState } from "./redux/store";

import MessagesPage from "./pages/Messages";
import SocketClient from "./SocketClient";

function App() {
    const roles = useAppSelector((state: RootState) => state.user.info?.roles || []);
    const isAdmin = roles.some((r: any) => r.name === "ADMIN");
    const isLogin = useAppSelector((state: RootState) => state.auth.isLogin);

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
                    {isAdmin && <Route path="/messages" element={<MessagesPage />} />}
                    <Route path="*" element={<NotFound />} />
                </Routes>
                {
                    isLogin && (
                        <SocketClient />
                    )
                }
            </ProtectedRoutes>
            <Toaster />
            {/* User thường: có AIChatModal và SellerChatModal. Admin: chỉ có tab Tin nhắn */}
            {!isAdmin && <AIChatModal />}
            {!isAdmin && <SellerChatModal />}
        </BrowserRouter>
    );
}

export default App;
