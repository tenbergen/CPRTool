import { configureStore } from "@reduxjs/toolkit";
import courseReducer from './features/courseSlice'
import authReducer from "./features/authSlice";
import assignmentReducer from "./features/assignmentSlice";

export default configureStore({
    reducer: {
        courses: courseReducer,
        auth: authReducer,
        assignments: assignmentReducer
    }
})
