import {createAsyncThunk, createSlice} from "@reduxjs/toolkit";
import axios from "axios";
import {refreshTokenAsync} from "./authSlice";

const peerReviewUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`

export const getPeerReviewFilesAsync = createAsyncThunk(
    'peerReviewSlice/getPeerReviewFilesAsync',
    async (values, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync())
        const { courseId, assignmentId } = values
        const url = `${peerReviewUrl}/${courseId}/assignments/${assignmentId}/peer-review/view-files`
        console.log(url)
        const peerReviewFiles = await axios.get(url)
            .then(res => {
                console.log(res.data)
                if (res.data != null) return res.data
                return []
            })
            .catch(e => {
                console.log(e)
                return []
            })
        return { peerReviewFiles }
    }
)

const peerReviewSlice = createSlice({
    name: 'peerReviewSlice',
    initialState: {
        peerReviewFiles: [],
        peerReviewTeamFiles: [],
        peerReviewFeedback: null,
        peerReviewFeedbackFiles: []
    },
    reducers: {
        peerReviewFeedback: (state, action) => {
            state.peerReviewFeedback = action.payload
        },
    },
    extraReducers: {
        [getPeerReviewFilesAsync.fulfilled]: (state, action) => {
            state.peerReviewFiles = action.payload.peerReviewFiles
        }
    }
})

export default peerReviewSlice.reducer