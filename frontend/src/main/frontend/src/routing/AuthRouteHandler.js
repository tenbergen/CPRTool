import { Outlet } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import LoginPage from "../pages/AuthPages/LoginPage";
import { useEffect } from "react";
import { setUserInformation } from "../redux/features/authSlice";

const AuthRouteHandler = () => {
    const dispatch = useDispatch()
    const { isAuthenticated } = useSelector((state) => state.auth);

    useEffect(() => {
        dispatch(setUserInformation())
    }, [])

    return (
        <div>
            {
                isAuthenticated
                    ? <Outlet/>
                    : <LoginPage/>
            }
        </div>
    )
}

export default AuthRouteHandler