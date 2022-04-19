import {createAsyncThunk, createSlice} from "@reduxjs/toolkit";
import axios from "axios";
import {refreshTokenAsync} from "./authSlice";

const getAssignmentUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`

const getAssignments = async (courseId) => {
    return await axios.get(`${getAssignmentUrl}/${courseId}/assignments`)
        .then(res => {
            if (res.data != null) return res.data
            return []
        })
        .catch(e => {
            console.log(e)
            return []
        })
}

const combine = (assignments, peerReviews) => {
    const combined = [...assignments, ...peerReviews]

    combined.sort(function (a, b)  {
        if (a.final_due_date < b.final_due_date) { return -1; }
        if (a.final_due_date > b.final_due_date) { return 1; }
        return 0;
    });

    console.log(combined)

    return combined
}

const getCombined = (courseAssignments, courseId, teamName) => {
    const peerReviewAssignments = []
    const assignmentsArr = []

    courseAssignments.map(async assignment => {
        assignmentsArr.push({...assignment, final_due_date: assignment.due_date, assignment_type: "normal", final_id: assignment.assignment_id})
        console.log(assignment)
        if (assignment.assigned_teams) {
            const teams = assignment.assigned_teams[teamName]
            teams.map(team => {
                const final_id = `${assignment.assignment_id}-peer-review-${team}`
                console.log(final_id)
                peerReviewAssignments.push({...assignment, peer_review_team: team, assignment_type: "peer-review", final_due_date: assignment.peer_review_due_date, final_id: final_id})
            })
        }
    })

    return combine(assignmentsArr, peerReviewAssignments)
}

export const getCourseAssignmentsAsync = createAsyncThunk(
    'assignments/getCourseAssignmentsAsync',
    async (courseId, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync())
        const courseAssignments = await getAssignments(courseId)
        return { courseAssignments }
    }
)

export const getSubmittedAssignmentsAsync = createAsyncThunk(
    'assignments/getSubmittedAssignmentsAsync',
    async (values, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync())
        const { courseId, currentTeamId, lakerId } = values
        const url = `${process.env.REACT_APP_URL}/assignments/student/${courseId}/${currentTeamId}/submissions`
        const submittedAssignments = await axios.get(url)
            .then(res => {
                console.log(res.data)
                return res.data
            })
            .catch(e => {
                console.log(e)
            })
        await getSubmittedAssignmentDetails(submittedAssignments, lakerId)
        return { submittedAssignments }
    }
)

const getSubmittedAssignmentDetails = async (submittedAssignments, lakerId) => {
    const submittedAssignmentDetails = []
    submittedAssignments.map(async assignment => {
        const courseId = assignment.course_id
        const assignmentId = assignment.assignment_id
        const teamId = assignment.team_name
        // get assignment details -- assignments/professor/courses/{courseID}/assignments/{assignmentID}

        //{courseID}/assignments/{assignmentID}
        const assUrl = `${getAssignmentUrl}/${courseId}/assignments/${assignmentId}`
        const details = await axios.get(assUrl)
            .then(res => {
                console.log(res.data)
            })

        const assignmentDetails = {...details, }
        // // team file name -- assignments/student/{course_id}/{assignment_id}/{student_id}/submission
        const teamUrl = `${process.env.REACT_APP_URL}/assignments/student/${courseId}/${assignmentId}/${teamId}/submission`
        await axios.get(teamUrl)
            .then(res => {
                console.log(res)
            })

        // // downloading file (not needed here) -- assignments/student/courses/{courseID}/assignments/{assignmentID}/{teamName}/download
        //
        // // get reviews for that student -- peer-review/assignments/{course_id}/{assignment_id}/reviews-of/{student_id}
        // const peerReviewUrl = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/reviews-of/${lakerId}`
        // await axios.get(peerReviewUrl)
        //     .then(res => {
        //         console.log(res)
        //     })
        //
        // // get grade -- view/professor/courses/{courseID}/assignments/{assignmentID}/students/{studentID}/grade
        // const gradeUrl = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/reviews-of/${lakerId}`
        // await axios.get(gradeUrl)
        //     .then(res => {
        //         console.log(res)
        //     })
    })
}

export const getSubmittedAssignmentDetailsAsync = createAsyncThunk(
    'assignments/getSubmittedAssignmentDetailsAsync',
    async (values, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync())
        const { courseId, assignmentId, lakerId } = values
        const url = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/${lakerId}/graded-assignment`
        const submittedAssignment = await axios.get(url)
            .then(res => {
                console.log(res)
                return res.data
            })
            .catch(e => {
                console.log(e)
            })
        return { submittedAssignment }
    }
)

export const getCombinedAssignmentPeerReviews = createAsyncThunk(
    'assignments/getCombinedAssignmentPeerReviews',
    async (value, thunkAPI) => {
        const { courseId, teamId } = value
        thunkAPI.dispatch(refreshTokenAsync())
        const courseAssignments = await getAssignments(courseId)
        console.log(courseAssignments)
        const combined = getCombined(courseAssignments, courseId, teamId)
        return { combined }
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
        courseSubmittedAssignments: [],
        combinedAssignmentPeerReviews: [],
        currentAssignment: null,
        currentAssignmentLoaded: false,
        currentSubmittedAssignment: null,
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
        [getSubmittedAssignmentsAsync.fulfilled]: (state, action) => {
            state.courseSubmittedAssignments = action.payload.submittedAssignments
        },
        [getSubmittedAssignmentDetailsAsync.fulfilled]: (state, action) => {
            state.currentSubmittedAssignment = action.payload.submittedAssignment
        }
    }
})

export const { setCurrentAssignment } = assignmentSlice.actions
export default assignmentSlice.reducer