import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { Login, Register } from "./pages";
import { RootState } from "./redux/store";
import { checkTokenValid } from "./redux/thunks/auth";

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
      <Routes>
        <Route
          path="/login"
          element={isLogin ? <Navigate to="/" replace /> : <Login />}
        />
        <Route path="/" element={<div>Home Page</div>} />
        <Route
          path="/register"
          element={isLogin ? <Navigate to="/" replace /> : <Register />}
        />
        {/* Các routes khác */}
      </Routes>
    </BrowserRouter>
  );
}

export default App;
