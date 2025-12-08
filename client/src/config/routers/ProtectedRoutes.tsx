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
    console.log(pathname);
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

    const isPrivateRoute = PRIVATE_ROUTES.find((route: string) => {
        // Loại bỏ phần tử rỗng khi split
        const pathParts = pathname.split("/").filter(Boolean);
        const routeParts = route.split("/").filter(Boolean);

        // Nếu route có wildcard ở cuối (vd: /products/*)
        if (routeParts[routeParts.length - 1] === "*") {
            // So sánh các phần trước wildcard
            const routeWithoutWildcard = routeParts.slice(0, -1);
            return routeWithoutWildcard.every((part, index) => pathParts[index] === part);
        }

        // So sánh exact match
        if (pathParts.length === routeParts.length) {
            return routeParts.every((routePart: string, index) => pathParts[index] === routePart);
        }

        return false;
    });

    console.log("isPrivateRoute:", isPrivateRoute, "pathname:", pathname);

    // Nếu không phải private route, luôn cho phép truy cập
    if (!isPrivateRoute) return children;

    // Nếu là private route và đã đăng nhập, cho phép truy cập
    if (isLogin && isPrivateRoute) return children;

    // Nếu là private route nhưng chưa đăng nhập, redirect về login
    return <Login />;
}

export default ProtectedRoutes;
