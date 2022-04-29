import {createAsyncThunk, createSlice} from "@reduxjs/toolkit";
import {refreshTokenAsync} from "./authSlice";
import axios from "axios";

const getAssignmentUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`
const submittedAssignmentUrl = `${process.env.REACT_APP_URL}/assignments/student`

export const getSubmittedAssignmentsAsync = createAsyncThunk(
    'submittedAssignments/getSubmittedAssignmentsAsync',
    async (values, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync())
        const {courseId, assignmentId} = values;
        const url = `${submittedAssignmentUrl}/${courseId}/${assignmentId}/submissions`
        const submittedAssignments = await axios.get(url)
            .then(res => {
                console.log(res)
                if (res.data.length > 0) return res.data
                return []
            })
            .catch(e => {
                console.log(e.response.data)
                return []
            })

        const detailedSubmittedAssignments = await getSubmittedTileDetails(submittedAssignments)
        return {detailedSubmittedAssignments}
    }
)

export const getStudentSubmittedAssignmentsAsync = createAsyncThunk(
    'submittedAssignments/getStudentSubmittedAssignmentsAsync',
    async (values, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync())
        const {courseId, teamId, lakerId} = values
        const url = `${submittedAssignmentUrl}/${courseId}/${lakerId}/submissions`
        const submittedAssignments = await axios.get(url)
            .then(res => {
                console.log(res.data)
                return res.data
            })
            .catch(e => {
                console.log(e.response)
                return []
            })
        console.log(submittedAssignments)
        const detailedSubmittedAssignments = await getSubmittedTileDetails(submittedAssignments)
        return {detailedSubmittedAssignments}
    }
)

const getSubmittedTileDetails = async (submittedAssignments) => {
    const detailsSubmittedAssignments = [];
    console.log(submittedAssignments)

    for (let i = 0; i < submittedAssignments.length; i++) {
        const assignment = submittedAssignments[i]
        const assignmentId = assignment.assignment_id
        const lakerId = assignment.members.pop()
        const courseId = assignment.course_id

        // get grade
        // const gradeUrl = `${process.env.REACT_APP_URL}/view/professor/courses/${courseId}/assignments/${assignmentId}/students/${lakerId}/grade`
        // const grade = await axios.get(gradeUrl)
        //     .then(res => {
        //         console.log(res.data.grade)
        //         if (res.data !== null && res.data.grade !== -1) return res.data.grade
        //         return "Pending"
        //     })

        detailsSubmittedAssignments.push({...assignment, grade: "Pending"})
    }

    return detailsSubmittedAssignments
}

export const getSubmittedAssignmentDetailsAsync = createAsyncThunk(
    'submittedAssignments/getSubmittedAssignmentDetailsAsync',
    async (values, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync())
        const {courseId, assignmentId, teamId} = values
        console.log(values)
        const submittedAssignment = await getSubmittedAssignmentDetails(courseId, assignmentId, teamId)
        return {submittedAssignment}
    }
)

const getSubmittedAssignmentDetails = async (courseId, assignmentId, teamId) => {
    // get assignment details
    const assUrl = `${getAssignmentUrl}/${courseId}/assignments/${assignmentId}`
    const details = await axios.get(assUrl)
        .then(res => {
            console.log(res.data)
            return res.data
        })

    const lakerId = await axios.get(`http://moxie.cs.oswego.edu:13125/teams/team/${courseId}/get/${teamId}`)
        .then(res =>{
            console.log(res.data)
            return res.data.team_lead
        })
        .catch(e => {
            console.log(e.response.data)
        })

    // team file name
    const submissionDetailUrl = `${process.env.REACT_APP_URL}/assignments/student/${courseId}/${assignmentId}/${lakerId}/submission`
    const submissionDetail = await axios.get(submissionDetailUrl)
        .then(res => {
            console.log("Submission data: " + {...res.data[0]})
            return res.data[0]
        })
        .catch(e => {
            console.log(e.response.data)
        })

    // get reviews for that student
    // const peerReviewGradesUrl = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/${teamId}/getTeamGrades`
    const peerReviewUrl = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/reviews-of/${lakerId}`
    const peerReviews = await axios.get(peerReviewUrl)
        .then(res => {
            console.log(res.data)
            if (res.data.length > 0) return res.data
            return []
        })
        .catch(e => {
            console.log(e.response.data)
            return []
        })

    const gradeUrl = `${process.env.REACT_APP_URL}/view/professor/courses/${courseId}/assignments/${assignmentId}/students/${lakerId}/grade`
    const grade = await axios.get(gradeUrl)
        .then(res => {
            console.log(res.data)
            if (res.data.grade === -1) return "Pending"
            return res.data.grade
        })
        .catch(e => {
            console.log(e.response.data)
        })

    console.log({...details, ...submissionDetail, peer_reviews: peerReviews, grade: grade})
    return {...details, ...submissionDetail, peer_reviews: peerReviews, grade: grade}
}

const submittedAssignmentSlice = createSlice({
    name: "submittedAssignmentSlice",
    initialState: {
        courseSubmittedAssignments: [],
        studentSubmittedAssignments: [],
        currentSubmittedAssignment: null,
        currentSubmittedAssignmentLoaded: false,
        assignmentsLoaded: false
    },
    extraReducers: {
        [getSubmittedAssignmentsAsync.pending]: (state) => {
            state.assignmentsLoaded = false
        },
        [getSubmittedAssignmentsAsync.fulfilled]: (state, action) => {
            state.courseSubmittedAssignments = action.payload.detailedSubmittedAssignments
            state.assignmentsLoaded = true
        },
        [getStudentSubmittedAssignmentsAsync.pending]: (state) => {
            state.assignmentsLoaded = false
        },
        [getStudentSubmittedAssignmentsAsync.fulfilled]: (state, action) => {
            state.courseSubmittedAssignments = action.payload.detailedSubmittedAssignments
            state.assignmentsLoaded = true
        },
        [getSubmittedAssignmentDetailsAsync.pending]: (state) => {
            state.currentSubmittedAssignmentLoaded = false
        },
        [getSubmittedAssignmentDetailsAsync.fulfilled]: (state, action) => {
            state.currentSubmittedAssignment = action.payload.submittedAssignment
            state.currentSubmittedAssignmentLoaded = true
        }
    }
})

export default submittedAssignmentSlice.reducer