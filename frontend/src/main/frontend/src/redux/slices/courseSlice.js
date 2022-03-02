import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import axios from "axios";
import React from "react";

export const getCoursesAsync = createAsyncThunk(
    'courses/getCoursesAsync',
    async () => {
        const courses = await axios.get("http://moxie.cs.oswego.edu:13128/view/professor/courses").then(res => {
            return res.data
        })
        return { courses }
    });

const courseSlice = createSlice({
    name: 'courses',
    initialState: [],
    reducers: {
        addCourse: (state, action) => {
            state.push(action.payload)
        }
    },
    extraReducers: {
        [getCoursesAsync.fulfilled]: (state, action) => {
            return action.payload.courses
        }
    }
})

export const { addCourse } = courseSlice.actions

export default courseSlice.reducer