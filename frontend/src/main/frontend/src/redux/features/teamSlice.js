import {createAsyncThunk, createSlice} from "@reduxjs/toolkit";
import {refreshTokenAsync} from "./authSlice";
import axios from "axios";

export const getCurrentCourseTeamAsync = createAsyncThunk(
    'team/getCurrentCourseTeamAsync',
    async (values, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync())
        const {courseId, lakerId} = values
        const teamId = await axios.get(`${process.env.REACT_APP_URL}/teams/team/get/${courseId}/${lakerId}`)
            .then(res => {
                console.log(res.data.team_id)
                return res.data.team_id
            })
            .catch(e => {
                console.log(e)
                return null
            })
        return {teamId}
    }
)

const teamSlice = createSlice({
    name: "teamSlice",
    initialState: {
        currentTeamId: null,
        teamLoaded: false
    },
    reducers: {},
    extraReducers: {
        [getCurrentCourseTeamAsync.pending]: (state) => {
            state.teamLoaded = false
        },
        [getCurrentCourseTeamAsync.fulfilled]: (state, action) => {
            state.currentTeamId = action.payload.teamId
            console.log(action.payload.teamId)
            state.teamLoaded = true
        }
    }
})

export default teamSlice.reducer