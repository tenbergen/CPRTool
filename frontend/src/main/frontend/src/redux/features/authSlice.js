import { createSlice } from '@reduxjs/toolkit'
import React from "react";
import jwtDecode from "jwt-decode";

const authSlice = createSlice({
    name: "auth",
    initialState: {
        token: localStorage.getItem("token"),
        isAuthenticated: null,
        user_given_name: null,
    },
    reducers: {
        authenticateUser: () => {
            try {
                let decoded = jwtDecode(localStorage.getItem("jwt_token"))
                return {
                    isAuthenticated: true,
                    user_given_name: decoded.given_name
                }
            } catch (e) {
                console.log(e)
                return {
                    isAuthenticated: false,
                }
            }
        }
    }
})

export const { authenticateUser } = authSlice.actions

export default authSlice.reducer