import { PRIVATE_ROUTES } from "@/constants/routes";
import { Login } from "@/pages";
import { RootState } from "@/redux/store";
import { checkTokenValid } from "@/redux/thunks/auth";
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useLocation } from "react-router-dom";

function ProtectedRoutes({ children }: { children: any }) {
    const { pathname } = useLocation();
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
        return null;
    }

    const isPrivateRoute = PRIVATE_ROUTES.includes(pathname);

    if ((isLogin && isPrivateRoute) || !isPrivateRoute) return children;

    return <Login />;
}

export default ProtectedRoutes;
