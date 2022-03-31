import { useDispatch, useSelector } from "react-redux";
import { Outlet } from "react-router-dom";
import React, { useEffect } from "react";
import { setUserInformation } from "../redux/features/authSlice";

const RoleRouteHandler = ({ allowedRoles }) => {
    const dispatch = useDispatch()
    const { role } = useSelector((state) => state.auth);

    useEffect(() => {
        dispatch(setUserInformation())
    }, [])

    return (
        <div>
            {
                allowedRoles.includes(role)
                    ? <Outlet/>
                    : <p> You are unauthorized to view this page </p>
            }
        </div>
    );

}

export default RoleRouteHandler;