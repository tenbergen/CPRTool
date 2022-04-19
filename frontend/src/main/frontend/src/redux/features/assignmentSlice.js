import {createAsyncThunk, createSlice} from '@reduxjs/toolkit';
import axios from 'axios';
import {refreshTokenAsync} from './authSlice';

const getAssignmentUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`;

const getAssignments = async (courseId) => {
    return await axios
        .get(`${getAssignmentUrl}/${courseId}/assignments`)
        .then((res) => {
            if (res.data != null) return res.data;
            return [];
        })
        .catch((e) => {
            console.log(e);
            return [];
        });
};

const combine = (assignments, peerReviews) => {
    const combined = [...assignments, ...peerReviews];

    combined.sort(function (a, b) {
        if (a.final_due_date < b.final_due_date) {
            return -1;
        }
        if (a.final_due_date > b.final_due_date) {
            return 1;
        }
        return 0;
    });

    console.log(combined);

    return combined;
};

const getCombined = (courseAssignments, courseId, teamName) => {
    const peerReviewAssignments = [];
    const assignmentsArr = [];

    courseAssignments.map(async (assignment) => {
        assignmentsArr.push({
            ...assignment,
            final_due_date: assignment.due_date,
            assignment_type: 'normal',
            final_id: assignment.assignment_id,
        });
        const teams = assignment.assigned_teams[parseInt(teamName)];
        console.log(teamName);

        teams.map((team) => {
            const final_id = `${assignment.assignment_id}-peer-review-${team}`;
            console.log(final_id);
            peerReviewAssignments.push({
                ...assignment,
                peer_review_team: team,
                assignment_type: 'peer-review',
                final_due_date: assignment.peer_review_due_date,
                final_id: final_id,
            });
        });
    });

    return combine(assignmentsArr, peerReviewAssignments);
};

export const getCourseAssignmentsAsync = createAsyncThunk(
    'assignments/getCourseAssignmentsAsync',
    async (courseId, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync());
        const courseAssignments = await getAssignments(courseId);
        return {courseAssignments};
    }
);

export const getCombinedAssignmentPeerReviews = createAsyncThunk(
    'assignments/getCombinedAssignmentPeerReviews',
    async (value, thunkAPI) => {
        const {courseId, teamId} = value;
        thunkAPI.dispatch(refreshTokenAsync());
        const courseAssignments = await getAssignments(courseId);
        console.log(courseAssignments);
        const combined = getCombined(courseAssignments, courseId, teamId);
        return {combined};
    }
);

export const getAssignmentDetailsAsync = createAsyncThunk(
    'assignments/getAssignmentDetailsAsync',
    async (values, thunkAPI) => {
        thunkAPI.dispatch(refreshTokenAsync());
        let {courseId, assignmentId} = values;
        const url = `${getAssignmentUrl}/${courseId}/assignments/${assignmentId}`;
        console.log(url);
        const currentAssignment = await axios
            .get(url)
            .then((res) => {
                console.log(res.data);
                return res.data;
            })
            .catch((e) => {
                console.log(e);
            });
        return {currentAssignment};
    }
);

const assignmentSlice = createSlice({
    name: 'assignmentSlice',
    initialState: {
        courseAssignments: [],
        combinedAssignmentPeerReviews: [],
        currentAssignment: null,
        assignmentFilesLoaded: false,
        assignmentsLoaded: false,
    },
    reducers: {
        setCurrentAssignment: (state, action) => {
            state.currentAssignment = action.payload;
        },
    },
    extraReducers: {
        [getCourseAssignmentsAsync.fulfilled]: (state, action) => {
            state.courseAssignments = action.payload.courseAssignments;
        },
        [getAssignmentDetailsAsync.fulfilled]: (state, action) => {
            state.currentAssignment = action.payload.currentAssignment;
            state.currentAssignmentLoaded = true;
        },
        [getAssignmentDetailsAsync.pending]: (state) => {
            state.currentAssignmentLoaded = false;
        },
        [getCombinedAssignmentPeerReviews.pending]: (state) => {
            state.assignmentsLoaded = false;
        },
        [getCombinedAssignmentPeerReviews.fulfilled]: (state, action) => {
            state.assignmentsLoaded = true;
            state.combinedAssignmentPeerReviews = action.payload.combined;
        },
    },
});

export const {setCurrentAssignment} = assignmentSlice.actions;
export default assignmentSlice.reducer;
