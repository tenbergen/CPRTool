import { configureStore } from "@reduxjs/toolkit";
import courseReducer from './slices/courseSlice'
import authReducer from "./slices/authSlice";

export default configureStore({
    reducer: {
        courses: courseReducer,
        auth: authReducer
    }
})
