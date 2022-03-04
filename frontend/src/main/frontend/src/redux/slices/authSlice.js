import { createSlice } from '@reduxjs/toolkit'
import React from "react";
import jwtDecode from "jwt-decode";

const authSlice = createSlice({
    name: "auth",
    initialState: {
        token: localStorage.getItem("token"),
        isAuthenticated: null,
        user: null,
    },
    reducers: {
        authenticateUser: (state, action) => {
            try {
                let decoded = jwtDecode(localStorage.getItem("jwt_token"))
                return {
                    isAuthenticated: true,
                    user: decoded.first_name
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