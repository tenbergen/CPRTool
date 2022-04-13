import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import axios from "axios";
import React from "react";
import {reduxInterceptor} from "../interceptors/reduxInterceptor";

const viewCourseUrl = `${process.env.REACT_APP_URL}/view/professor`

export const getStudentCoursesAsync = createAsyncThunk(
    'courses/getStudentCoursesAsync',
    async (studentId) => {
        await reduxInterceptor()
        const courses = await axios.get(`${viewCourseUrl}/${studentId}/courses`).then(res => {
            console.log(res.data)
            return res.data.filter(course => course !== null);
        });
        return { courses }
    });

export const getCoursesAsync = createAsyncThunk(
    'courses/getCoursesAsync',
    async () => {
        await reduxInterceptor()
        const courses = await axios.get(`${viewCourseUrl}/courses`).then(res => {
            console.log(res.data)
            return res.data
        })
        return { courses }
    });

export const getCourseDetailsAsync = createAsyncThunk(
    'courses/getCourseDetailAsync',
    async (courseId )=> {
        await reduxInterceptor()
        const url = `${viewCourseUrl}/courses/${courseId}`
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
        },
        setCurrentCourse: (state, action) => {
            state.currentCourse = action.payload
        }
    },
    extraReducers: {
        [getCoursesAsync.fulfilled]: (state, action) => {
            state.courses = action.payload.courses
        },
        [getStudentCoursesAsync.fulfilled]: (state, action) => {
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

export const { addCourse, setCurrentCourse } = courseSlice.actions

export default courseSlice.reducer