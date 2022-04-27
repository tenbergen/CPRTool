import {createAsyncThunk, createSlice} from '@reduxjs/toolkit';
import axios from 'axios';
import React from 'react';
import {refreshTokenAsync} from './authSlice';

const viewCourseUrl = `${process.env.REACT_APP_URL}/view/professor`;

export const getStudentCoursesAsync = createAsyncThunk(
    'courses/getStudentCoursesAsync',
    async (studentId, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync());
        const courses = await axios.get(`${viewCourseUrl}/${studentId}/courses`)
            .then((res) => {
                if (res.data != null) return res.data.filter(course => course !== null).slice(0).reverse();
            })
            .catch((e) => {
                console.log(e.response);
                return []
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
                if (res.data != null) return res.data.filter(course => course !== null).slice(0).reverse();
                return [];
            })
            .catch((e) => {
                console.log(e.response);
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
        const currentCourse = await axios.get(url).then((res) => {
            return res.data;
        });
        return {currentCourse};
    }
);

export const getCurrentCourseStudentsAsync = createAsyncThunk(
    'courses/getCurrentCourseStudentsAsync',
    async (courseId, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync());
        const url = `${process.env.REACT_APP_URL}/view/professor/courses/${courseId}/students`
        let finalStudentArray = []

        const students = await axios.get(url)
            .then(res => {
                return res.data
            })
            .catch(e => {
                console.log(e.response)
                return []
            })

        const teamUrl = `${process.env.REACT_APP_URL}/teams/team/get/all/${courseId}`
        const teams = await axios.get(teamUrl)
            .then(res => {
                return res.data
            })
            .catch(e => {
                console.log(e.response)
                return []
            })

        if (teams.length < 1) {
            finalStudentArray.push(...students)
            return {finalStudentArray}
        }

        // if there are teams
        students.map(student => {
            let inTeam = false
            for (let i = 0; i < teams.length; i++) {
                let currentTeam = teams[i]
                if (currentTeam.team_members.includes(student.student_id)) {
                    finalStudentArray.push({...student, team:  currentTeam.team_id})
                    inTeam = true
                    break;
                }
            }
            if (!inTeam) finalStudentArray.push({...student, team:  null})
        })

        return {finalStudentArray}
    }
)

const courseSlice = createSlice({
    name: 'courseSlice',
    initialState: {
        courses: [],
        currentCourse: null,
        coursesLoaded: false,
        currentCourseStudents: [],
        currentCourseStudentsLoaded: false,
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
        [getCoursesAsync.pending]: (state) => {
            state.coursesLoaded = false;
        },
        [getCoursesAsync.fulfilled]: (state, action) => {
            state.courses = action.payload.courses;
            state.coursesLoaded = true;
        },
        [getStudentCoursesAsync.pending]: (state) => {
            state.coursesLoaded = false;
        },
        [getStudentCoursesAsync.fulfilled]: (state, action) => {
            state.courses = action.payload.courses;
            state.coursesLoaded = true;
        },
        [getCourseDetailsAsync.fulfilled]: (state, action) => {
            state.currentCourseLoaded = true;
            state.currentCourse = action.payload.currentCourse;
        },
        [getCourseDetailsAsync.pending]: (state) => {
            state.currentCourseLoaded = false;
        },
        [getCurrentCourseStudentsAsync.pending]: (state) => {
            state.currentCourseStudentsLoaded = false;
        },
        [getCurrentCourseStudentsAsync.fulfilled]: (state, action) => {
            state.currentCourseStudents = action.payload.finalStudentArray
            state.currentCourseStudentsLoaded = true;
        }
    },
});

export const {addCourse, setCurrentCourse} = courseSlice.actions;

export default courseSlice.reducer;
