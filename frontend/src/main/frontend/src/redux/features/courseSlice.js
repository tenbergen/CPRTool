import {createAsyncThunk, createSlice} from '@reduxjs/toolkit';
import axios from 'axios';
import React from 'react';
import {refreshTokenAsync} from './authSlice';

const viewCourseUrl = `${process.env.REACT_APP_URL}/view/professor`;

export const getStudentCoursesAsync = createAsyncThunk(
    'courses/getStudentCoursesAsync',
    async (studentId, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync());
        const distinctCourses = []
        const courses = []
        await axios.get(`${viewCourseUrl}/${studentId}/courses`)
            .then((res) => {
                console.log(res.data);
                if (res.data != null)
                    res.data.map(course => {
                        if (course !== null && !distinctCourses.includes(course.course_id)) {
                            distinctCourses.push(course.course_id)
                            courses.push(course)
                        }
                    })
            })
            .catch((e) => {
                console.log(e);
            });
        return {courses};
    }
);

export const getCoursesAsync = createAsyncThunk(
    'courses/getCoursesAsync',
    async (_, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync());
        const courses = await axios
            .get(`${viewCourseUrl}/courses`)
            .then((res) => {
                if (res.data != null) return res.data;
                return [];
            })
            .catch((e) => {
                console.log(e);
                return [];
            });
        return {courses};
    }
);

export const getCourseDetailsAsync = createAsyncThunk(
    'courses/getCourseDetailAsync',
    async (courseId, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync());
        const url = `${viewCourseUrl}/courses/${courseId}`;
        console.log(url);
        const currentCourse = await axios.get(url).then((res) => {
            return res.data;
        });
        console.log(currentCourse);
        return {currentCourse};
    }
);

const courseSlice = createSlice({
    name: 'courseSlice',
    initialState: {
        courses: [],
        currentCourse: null,
        currentCourseLoaded: false,
    },
    reducers: {
        addCourse: (state, action) => {
            state.courses.push(action.payload);
        },
        setCurrentCourse: (state, action) => {
            state.currentCourse = action.payload;
        },
    },
    extraReducers: {
        [getCoursesAsync.fulfilled]: (state, action) => {
            state.courses = action.payload.courses;
        },
        [getStudentCoursesAsync.fulfilled]: (state, action) => {
            state.courses = action.payload.courses;
        },
        [getCourseDetailsAsync.fulfilled]: (state, action) => {
            state.currentCourseLoaded = true;
            state.currentCourse = action.payload.currentCourse;
        },
        [getCourseDetailsAsync.pending]: (state) => {
            state.currentCourseLoaded = false;
        },
    },
});

export const {addCourse, setCurrentCourse} = courseSlice.actions;

export default courseSlice.reducer;
