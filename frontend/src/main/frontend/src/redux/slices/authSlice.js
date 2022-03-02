import { createSlice } from '@reduxjs/toolkit'
import React from "react";

const authSlice = createSlice({
    name: "auth",
    initialState: {
        token: localStorage.getItem("token"),
        isAuthenticated: null,
        user: null
    },
    reducers: {
        authenticateUser: (state, action) => {
            return {
                ...state,
                ...action.payload, // returns the token and user
                isAuthenticated: true,
            }
        }
    }
})

export const { authenticateUser } = authSlice.actions

export default authSlice.reducer