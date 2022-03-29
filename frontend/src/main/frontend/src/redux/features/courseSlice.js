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

export const getCourseDetailsAsync = createAsyncThunk(
    'courses/getCourseDetailAsync',
    async (courseId )=> {
        const url = `${viewCourseURL}/${courseId}`
        console.log(url)
        const currentCourse = await axios.get(url).then(res => {
            return res.data
        })
        console.log(currentCourse)
        return { currentCourse }
    }
)

const courseSlice = createSlice({
    name: 'courseSlice',
    initialState: {
        courses: [],
        currentCourse: null,
        currentCourseLoaded: false
    },
    reducers: {
        addCourse: (state, action) => {
            state.courses.push(action.payload)
        }
    },
    extraReducers: {
        [getCoursesAsync.fulfilled]: (state, action) => {
            state.courses = action.payload.courses
        },
        [getCourseDetailsAsync.fulfilled]: (state, action) => {
            state.currentCourseLoaded = true
            state.currentCourse = action.payload.currentCourse
        },
        [getCourseDetailsAsync.pending]: (state) => {
            state.currentCourseLoaded = false
        }
    }
})

export const { addCourse } = courseSlice.actions

export default courseSlice.reducer