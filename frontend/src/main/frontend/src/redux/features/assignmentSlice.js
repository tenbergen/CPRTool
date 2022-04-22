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

    combined.sort(function (a, b) {
        if (a.final_due_date < b.final_due_date) {
            return -1;
        }
        if (a.final_due_date > b.final_due_date) {
            return 1;
        }
        return 0;
    });

    console.log(combined)

    return combined
}

const getCombined = (courseAssignments, courseId, teamName) => {
    const peerReviewAssignments = []
    const assignmentsArr = []

    courseAssignments.map(async assignment => {
        assignmentsArr.push({
            ...assignment,
            final_due_date: assignment.due_date,
            assignment_type: "normal",
            final_id: assignment.assignment_id
        })
        console.log(assignment)
        if (assignment.assigned_teams) {
            const teams = assignment.assigned_teams[teamName]
            teams.map(team => {
                const final_id = `${assignment.assignment_id}-peer-review-${team}`
                console.log(final_id)
                peerReviewAssignments.push({
                    ...assignment,
                    peer_review_team: team,
                    assignment_type: "peer-review",
                    final_due_date: assignment.peer_review_due_date,
                    final_id: final_id
                })
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
        return {courseAssignments}
    }
)

export const getSubmittedAssignmentsAsync = createAsyncThunk(
    'assignments/getSubmittedAssignmentsAsync',
    async (values, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync())
        const {courseId, currentTeamId, lakerId} = values
        const url = `${process.env.REACT_APP_URL}/assignments/student/${courseId}/${lakerId}/submissions`
        const submittedAssignments = await axios.get(url)
            .then(res => {
                console.log(res.data)
                return res.data
            })
            .catch(e => {
                console.log(e)
            })
        const detailedSubmittedAssignments = await getSubmittedTileDetails(submittedAssignments, lakerId)
        return {detailedSubmittedAssignments}
    }
)

const getSubmittedTileDetails = async (submittedAssignments, lakerId) => {
    const detailsSubmittedAssignments = [];
    console.log(submittedAssignments)

    for (let i = 0; i < submittedAssignments.length; i++) {
        const assignment = submittedAssignments[i]
        const assignmentId = assignment.assignment_id
        const courseId = assignment.course_id

        const assUrl = `${getAssignmentUrl}/${courseId}/assignments/${assignmentId}`
        const details = await axios.get(assUrl)
            .then(res => {
                console.log(res.data)
                if (res.data !== null) return res.data
                return null
            })
            .catch(e => {
                console.log(e)
                return null
            })

        const teamUrl = `${process.env.REACT_APP_URL}/assignments/student/${courseId}/${assignmentId}/${lakerId}/submission`
        const teamFile = await axios.get(teamUrl)
            .then(res => {
                console.log(res.data)
                return res.data[0]
            })

        // get grade
        const gradeUrl = `${process.env.REACT_APP_URL}/view/professor/courses/${courseId}/assignments/${assignmentId}/students/${lakerId}/grade`
        const grade = await axios.get(gradeUrl)
            .then(res => {
                console.log(res.data.grade)
                if (res.data !== null) return res.data.grade
                // return "Pending"
            })

        detailsSubmittedAssignments.push({...details, grade: grade})
    }

    return detailsSubmittedAssignments
}

const getSubmittedAssignmentDetails = async (courseId, assignmentId, lakerId, teamId) => {

    // get assignment details
    const assUrl = `${getAssignmentUrl}/${courseId}/assignments/${assignmentId}`
    const details = await axios.get(assUrl)
        .then(res => {
            console.log(res.data)
            return res.data
        })

    // team file name
    const teamUrl = `${process.env.REACT_APP_URL}/assignments/student/${courseId}/${assignmentId}/${lakerId}/submission`
    const teamFile = await axios.get(teamUrl)
        .then(res => {
            console.log(res.data[0])
            return res.data[0]
        })

    // get reviews for that student
    const peerReviewUrl = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/reviews-of/${lakerId}`
    const peerReviews = await axios.get(peerReviewUrl)
        .then(res => {
            console.log(res.data)
            return res.data
        })
        .catch(e => {
            console.log(e)
            return null
        })

    const gradeUrl = `${process.env.REACT_APP_URL}/view/professor/courses/${courseId}/assignments/${assignmentId}/students/${lakerId}/grade`
    const grade = await axios.get(gradeUrl)
        .then(res => {
            console.log(res.data)
            if (res.data.grade === -1) return "Pending"
            return res.data.grade
        })

    console.log({team_file: teamFile, peer_reviews: peerReviews, grade: grade})
    return {...details, team_file: teamFile.submission_name, peer_reviews: peerReviews, grade: grade}
}

export const getSubmittedAssignmentDetailsAsync = createAsyncThunk(
    'assignments/getSubmittedAssignmentDetailsAsync',
    async (values, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync())
        const {courseId, assignmentId, lakerId, currentTeamId} = values
        console.log(values)
        const submittedAssignment = await getSubmittedAssignmentDetails(courseId, assignmentId, lakerId, currentTeamId)
        return {submittedAssignment}
    }
)

export const getCombinedAssignmentPeerReviews = createAsyncThunk(
    'assignments/getCombinedAssignmentPeerReviews',
    async (value, thunkAPI) => {
        const {courseId, teamId} = value
        thunkAPI.dispatch(refreshTokenAsync())
        const courseAssignments = await getAssignments(courseId)
        console.log(courseAssignments)
        const combined = getCombined(courseAssignments, courseId, teamId)
        return {combined}
    }
)

export const getAssignmentDetailsAsync = createAsyncThunk(
    'assignments/getAssignmentDetailsAsync',
    async (values, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync())
        let {courseId, assignmentId} = values;
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
        return {currentAssignment}
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
        [getSubmittedAssignmentsAsync.pending]: (state) => {
            state.assignmentsLoaded = false
        },
        [getSubmittedAssignmentsAsync.fulfilled]: (state, action) => {
            state.courseSubmittedAssignments = action.payload.detailedSubmittedAssignments
            state.assignmentsLoaded = true
        },
        [getSubmittedAssignmentDetailsAsync.pending]: (state) => {
            state.currentAssignmentLoaded = false
        },
        [getSubmittedAssignmentDetailsAsync.fulfilled]: (state, action) => {
            state.currentSubmittedAssignment = action.payload.submittedAssignment
            state.currentAssignmentLoaded = true
        }
    }
})

export const {setCurrentAssignment} = assignmentSlice.actions
export default assignmentSlice.reducer