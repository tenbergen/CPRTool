import {createAsyncThunk, createSlice} from "@reduxjs/toolkit";
import axios from "axios";
import {refreshTokenAsync} from "./authSlice";

const getAssignmentUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`
const getStudentAssignmentUrl = `${process.env.REACT_APP_URL}/assignments/student/courses`

const getAssignments = async (courseId) => {
    return await axios.get(`${getAssignmentUrl}/${courseId}/assignments`)
        .then(res => {
            if (res.data != null) return res.data
            return []
        })
        .catch(e => {
            console.log(e.response)
            return []
        })
}

const getToDos = async (courseId, lakerId) => {
    const assignmentsArr = []

    const url = `${getStudentAssignmentUrl}/${courseId}/${lakerId}/to-dos`
    const toDos = await axios.get(url)
        .then(res => {
            if (res.data.length > 0) return res.data
            return []
        })
        .catch(e => {
            console.log(e.response)
            return []
        })

    toDos.map(todo => {
        assignmentsArr.push({
            ...todo,
            final_due_date: todo.due_date,
            assignment_type: "normal",
            final_id: todo.assignment_id
        })
    })

    return assignmentsArr
}

const getPeerReviews = async (courseId, teamId) => {
    const peerReviewAssignments = []
    const assignments = await getAssignments(courseId);
    console.log(assignments)
    assignments.map(assignment => {
        if (assignment.assigned_teams !== null) {
            const teams = assignment.assigned_teams[teamId]
            teams.map(team => {
                if (!assignment.completed_teams[team].includes(teamId)) {
                    const final_id = `${assignment.assignment_id}-peer-review-${team}`
                    peerReviewAssignments.push({
                        ...assignment,
                        peer_review_team: team,
                        assignment_type: "peer-review",
                        final_due_date: assignment.peer_review_due_date,
                        final_id: final_id
                    })
                }
            })
        }
    })
    console.log(peerReviewAssignments)
    return peerReviewAssignments
}

const getCombined = (courseAssignments, peerReviews) => {
    const combined = [...courseAssignments, ...peerReviews]

    combined.sort(function (a, b) {
        if (a.final_due_date < b.final_due_date) {return -1;}
        if (a.final_due_date > b.final_due_date) {return 1;}
        return 0;
    });

    return combined
}

export const getCourseAssignmentsAsync = createAsyncThunk(
    'assignments/getCourseAssignmentsAsync',
    async (courseId, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync())
        const courseAssignments = await getAssignments(courseId)
        return {courseAssignments}
    }
)

export const getCombinedAssignmentPeerReviews = createAsyncThunk(
    'assignments/getCombinedAssignmentPeerReviews',
    async (value, thunkAPI) => {
        const {courseId, currentTeamId, lakerId} = value
        thunkAPI.dispatch(refreshTokenAsync())
        const courseAssignments = await getToDos(courseId, lakerId)
        const peerReviews = await getPeerReviews(courseId, currentTeamId);
        const combined = getCombined(courseAssignments, peerReviews)
        return {combined}
    }
)

export const getAssignmentDetailsAsync = createAsyncThunk(
    'assignments/getAssignmentDetailsAsync',
    async (values, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync())
        let {courseId, assignmentId} = values;
        const url = `${getAssignmentUrl}/${courseId}/assignments/${assignmentId}`
        const currentAssignment = await axios.get(url)
            .then(res => {
                return res.data
            })
            .catch(e => {
                console.log(e.response)
            })
        return {currentAssignment}
    }
)

const assignmentSlice = createSlice({
    name: "assignmentSlice",
    initialState: {
        courseAssignments: [],
        combinedAssignmentPeerReviews: [],
        currentAssignment: null,
        currentAssignmentLoaded: false,
        assignmentFilesLoaded: false,
        assignmentsLoaded: false
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
        [getCombinedAssignmentPeerReviews.pending]: (state) => {
            state.assignmentsLoaded = false
        },
        [getCombinedAssignmentPeerReviews.fulfilled]: (state, action) => {
            state.assignmentsLoaded = true
            state.combinedAssignmentPeerReviews = action.payload.combined
        },
    }
})

export const {setCurrentAssignment} = assignmentSlice.actions
export default assignmentSlice.reducer