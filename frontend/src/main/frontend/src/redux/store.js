import { configureStore } from "@reduxjs/toolkit";
import courseReducer from './features/courseSlice'
import authReducer from "./features/authSlice";

export default configureStore({
    reducer: {
        courses: courseReducer,
        auth: authReducer
    }
})
