import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import axios from "axios";
import React from "react";

const viewCourseURL = `${process.env.REACT_APP_URL}/view/professor/courses`
export const getCoursesAsync = createAsyncThunk(
    'courses/getCoursesAsync',
    async () => {
        const courses = await axios.get(viewCourseURL).then(res => {
            return res.data
        })
        return { courses }
    });

const courseSlice = createSlice({
    name: 'courseSlice',
    initialState: {
        courses: [],
        currentCourse: null
    },
    reducers: {
        addCourse: (state, action) => {
            state.courses.push(action.payload)
        },
        setCurrentCourse: (state, action) => {
            state.currentCourse = action.payload
        }
    },
    extraReducers: {
        [getCoursesAsync.fulfilled]: (state, action) => {
            state.courses = action.payload.courses
        }
    }
})

export const { addCourse, setCurrentCourse } = courseSlice.actions

export default courseSlice.reducer