import { createAsyncThunk, createSlice } from '@reduxjs/toolkit'
import React from "react";
import jwtDecode from "jwt-decode";
import axios from "axios";

const authURL = `${process.env.REACT_APP_URL}/auth`

export const getTokenAsync = createAsyncThunk(
    'auth/getTokenAsync',
    async ()=> {
        const url = `${authURL}/token/generate`
        //console.log(url)
        const google_token = localStorage.getItem("google_token")
        const axiosAuthInstance = axios.create({
            headers: {
                "Authorization" : `Bearer ${google_token}`
            }
        });
        const jwt_token = await axiosAuthInstance.post(url)
            .then(res => {
                localStorage.setItem("jwt_token", res.data)
                return true
            })
            .catch(e => {
                console.log(e)
                return false
            })
        return { jwt_token }
    }
)

const getUserInformation = () => {
    try {
        let decoded = jwtDecode(localStorage.getItem("jwt_token"))
        console.log(decoded)
        return {
            isAuthenticated: true,
            user_given_name: decoded.name,
            role: decoded.roles[0]
        }
    } catch (e) {
        return {
            isAuthenticated: false,
        }
    }
}

const authSlice = createSlice({
    name: "auth",
    initialState: {
        isAuthenticated: null,
        user_given_name: null,
        role: null
    },
    reducers: {
        setUserInformation: getUserInformation
    },
    extraReducers: (builder) => {
        builder.addCase(getTokenAsync.fulfilled, getUserInformation)
    }
})

export const { setUserInformation } = authSlice.actions

export default authSlice.reducer