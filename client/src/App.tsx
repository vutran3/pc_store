import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Login, Register } from "./pages";
import Home from "./pages/Home";
import Header from "./components/Header";
import ProductsPage from "./pages/Product/Product";
import ProductDetail from "./pages/Product/ProductDetail";
import ScrollToTop from "./components/ScrollToTop";
import ProtectedRoutes from "./config/routers/ProtectedRoutes";

function App() {
    return (
        <BrowserRouter>
            <ScrollToTop />
            <Header />
            <ProtectedRoutes>
                <Routes>
                    <Route path="/login" element={<Login />} />
                    <Route path="/" element={<Home />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/products" element={<ProductsPage />} />
                    <Route path="/products/:id" element={<ProductDetail />} />
                </Routes>
            </ProtectedRoutes>
        </BrowserRouter>
    );
}

export default App;
