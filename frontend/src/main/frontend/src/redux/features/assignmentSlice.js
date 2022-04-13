import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import axios from "axios";
import {refreshTokenAsync} from "./authSlice";

const getAssignmentUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`

const getAssignments = async (courseId) => {
    const courseAssignments = await axios.get(`${getAssignmentUrl}/${courseId}/assignments`)
        .then(res => {
            if(res.data != null) return res.data
            return []
        })
        .catch(e => {
            console.log(e)
            return []
        })
    return courseAssignments
}

export const getCourseAssignmentsAsync = createAsyncThunk(
    'assignments/getCourseAssignmentsAsync',
    async (courseId, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync())
        const courseAssignments = await getAssignments(courseId)
        return { courseAssignments }
    }
)

export const getCombinedAssignmentPeerReviews = createAsyncThunk(
    'assignments/getCombinedAssignmentPeerReviews',
    async (courseId, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync())
        const courseAssignments = await getAssignments(courseId)
        console.log(courseAssignments)
        const peerReviews = [{"assignment_name": "something", "due_date": "2022-01-21"}]
        const combined = [...courseAssignments, ...peerReviews]
        combined.sort(function (a, b)  {
            if (a.due_date < b.due_date) { return -1; }
            if (a.due_date > b.due_date) { return 1; }
            return 0;
        });
        console.log(combined)
        return { combined }
    }
)

export const getAssignmentFilesAsync = createAsyncThunk(
    'assignments/getAssignmentFilesAsync',
    async (values, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync())
        const { courseId, assignment_id } = values;
        const url = `${getAssignmentUrl}/${courseId}/assignments/${assignment_id}/view-files`
        const currentAssignmentFiles = await axios.get(url)
            .then(res => {
                console.log(res.data)
                return res.data
            })
            .catch(e => {
                console.log(e)
            })
        return { currentAssignmentFiles }
    }
)

export const getAssignmentDetailsAsync = createAsyncThunk(
    'assignments/getAssignmentDetailsAsync',
    async (values, thunkAPI)=> {
        thunkAPI.dispatch(refreshTokenAsync())
        let { courseId, assignmentId } = values;
        const url = `${getAssignmentUrl}/${courseId}/assignments/${assignmentId}`
        console.log(url)
        const currentAssignment = await axios.get(url)
            .then(res => {
                console.log(res.data)
                return res.data
            })
            .catch(e => {
                console.log(e)
            })
        return { currentAssignment }
    }
)

const assignmentSlice = createSlice({
    name: "assignmentSlice",
    initialState: {
        courseAssignments: [],
        combinedAssignmentPeerReviews: [],
        currentAssignment: null,
        currentAssignmentFiles: [],
        currentAssignmentLoaded: false
    },
    reducers: {
        setCurrentAssignment: (state, action) => {
            state.currentAssignment = action.payload
        }
    },
    extraReducers: {
        [getCourseAssignmentsAsync.fulfilled]: (state, action) => {
            state.courseAssignments = action.payload.courseAssignments
        },
        [getAssignmentDetailsAsync.fulfilled]: (state, action) => {
            state.currentAssignment = action.payload.currentAssignment
            state.currentAssignmentLoaded = true
        },
        [getAssignmentDetailsAsync.pending]: (state) => {
            state.currentAssignmentLoaded = false
        },
        [getAssignmentFilesAsync.fulfilled]: (state, action) => {
            state.currentAssignmentFiles = action.payload.currentAssignmentFiles
        },
        [getCombinedAssignmentPeerReviews.fulfilled]: (state, action) => {
            state.combinedAssignmentPeerReviews = action.payload.combined
        }
    }
})

export const { setCurrentAssignment } = assignmentSlice.actions
export default assignmentSlice.reducer