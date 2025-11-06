import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { Login, Register } from "./pages";
import { RootState } from "./redux/store";
import { checkTokenValid } from "./redux/thunks/auth";
import Home from "./pages/Home";
import Header from "./components/Header";
import ProductsPage from "./pages/Product/Product";
import ProductDetail from "./pages/Product/ProductDetail";
import ScrollToTop from "./components/ScrollToTop";

function App() {
  const { isLogin, token } = useSelector((state: RootState) => state.auth);
  const [isChecking, setIsChecking] = useState(true);
  const dispatch = useDispatch();

  useEffect(() => {
    const checkAuth = async () => {
      if (token) {
        await dispatch(checkTokenValid(token) as any);
      }
      setIsChecking(false);
    };

    checkAuth();
  }, [dispatch, token]);

  if (isChecking) {
    return null; // hoặc loading spinner
  }

  return (
    <BrowserRouter>
      <ScrollToTop />
      <Header />
      <Routes>
        <Route
          path="/login"
          element={isLogin ? <Navigate to="/" replace /> : <Login />}
        />
        <Route path="/" element={<Home />} />
        <Route path="/register" element={isLogin ? <Navigate to="/" replace /> : <Register />} />
        <Route path="/products" element={<ProductsPage />} />
        <Route path="/products/:id" element={<ProductDetail />} />
        {/* Các routes khác */}
      </Routes>
    </BrowserRouter>
  );
}

export default App;
