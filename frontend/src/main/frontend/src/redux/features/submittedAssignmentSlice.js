import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { refreshTokenAsync } from './authSlice';
import axios from 'axios';

const getAssignmentUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`;
const submittedAssignmentUrl = `${process.env.REACT_APP_URL}/assignments/student`;

const getPeerReviews = async (courseId, assignmentId, lakerId) => {
  const peerReviewUrl = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/reviews-of/${lakerId}`;
  const peerReviews = await axios
    .get(peerReviewUrl)
    .then((res) => {
      if (res.data.length > 0) return res.data;
      return [];
    })
    .catch((e) => {
      console.error(e.response.data);
      return [];
    });
  return peerReviews;
};

// ---- PROF SUBMITTED LIST ---- //
const getSubmittedAssignments = async (courseId, assignmentId) => {
  const url = `${submittedAssignmentUrl}/${courseId}/${assignmentId}/submissions`;
  const submittedAssignments = await axios
    .get(url)
    .then((res) => {
      if (res.data.length > 0) return res.data;
      return [];
    })
    .catch((e) => {
      console.error(e.response.data);
      return [];
    });
  return submittedAssignments;
};

export const getSubmittedAssignmentsAsync = createAsyncThunk(
  'submittedAssignments/getSubmittedAssignmentsAsync',
  async (values, thunkAPI) => {
    thunkAPI.dispatch(refreshTokenAsync());
    const { courseId, assignmentId } = values;
    const submittedAssignments = await getSubmittedAssignments(
      courseId,
      assignmentId
    );
    return { submittedAssignments };
  }
);

// ---- STUDENT SUBMITTED LIST ---- //
const getStudentSubmittedAssignments = async (courseId, lakerId) => {
  const url = `${submittedAssignmentUrl}/${courseId}/${lakerId}/submissions`;
  const submittedAssignments = await axios
    .get(url)
    .then((res) => {
      if (res.data.length > 0) return res.data;
      return [];
    })
    .catch((e) => {
      console.error(e.response);
      return [];
    });
  return submittedAssignments;
};

export const getStudentSubmittedAssignmentsAsync = createAsyncThunk(
  'submittedAssignments/getStudentSubmittedAssignmentsAsync',
  async (values, thunkAPI) => {
    thunkAPI.dispatch(refreshTokenAsync());
    const { courseId, lakerId } = values;
    const submittedAssignments = await getStudentSubmittedAssignments(
      courseId,
      lakerId
    );
    return { submittedAssignments };
  }
);

// ---- SUBMITTED ASSIGNMENT DETAILS ---- //
export const getSubmittedAssignmentDetailsAsync = createAsyncThunk(
  'submittedAssignments/getSubmittedAssignmentDetailsAsync',
  async (values, thunkAPI) => {
    thunkAPI.dispatch(refreshTokenAsync());
    const { courseId, assignmentId, teamId } = values;
    const submittedAssignment = await getSubmittedAssignmentDetails(
      courseId,
      assignmentId,
      teamId
    );
    return { submittedAssignment };
  }
);

const getSubmittedAssignmentDetails = async (
  courseId,
  assignmentId,
  teamId
) => {
  // get assignment details
  const assUrl = `${getAssignmentUrl}/${courseId}/assignments/${assignmentId}`;
  const details = await axios.get(assUrl).then((res) => {
    return res.data;
  });

  const lakerId = await axios
    .get(`${process.env.REACT_APP_URL}/teams/team/${courseId}/get/${teamId}`)
    .then((res) => {
      return res.data.team_lead;
    })
    .catch((e) => {
      console.error(e.response.data);
    });

  // team file name
  const submissionDetailUrl = `${process.env.REACT_APP_URL}/assignments/student/${courseId}/${assignmentId}/${lakerId}/submission`;
  const submissionDetail = await axios
    .get(submissionDetailUrl)
    .then((res) => {
      return res.data[0];
    })
    .catch((e) => {
      console.error(e.response.data);
    });

  // get reviews for that student
  const peerReviews = await getPeerReviews(courseId, assignmentId, lakerId);
  const grade =
    submissionDetail.grade === -1 ? 'Pending' : submissionDetail.grade;

  return {
    ...details,
    ...submissionDetail,
    peer_reviews: peerReviews,
    grade: grade,
  };
};

const submittedAssignmentSlice = createSlice({
  name: 'submittedAssignmentSlice',
  initialState: {
    courseSubmittedAssignments: [],
    studentSubmittedAssignments: [],
    currentSubmittedAssignment: null,
    currentSubmittedAssignmentLoaded: false,
    assignmentsLoaded: false,
  },
  extraReducers: {
    [getSubmittedAssignmentsAsync.pending]: (state) => {
      state.assignmentsLoaded = false;
    },
    [getSubmittedAssignmentsAsync.fulfilled]: (state, action) => {
      state.courseSubmittedAssignments = action.payload.submittedAssignments;
      state.assignmentsLoaded = true;
    },
    [getStudentSubmittedAssignmentsAsync.pending]: (state) => {
      state.assignmentsLoaded = false;
    },
    [getStudentSubmittedAssignmentsAsync.fulfilled]: (state, action) => {
      state.courseSubmittedAssignments = action.payload.submittedAssignments;
      state.assignmentsLoaded = true;
    },
    [getSubmittedAssignmentDetailsAsync.pending]: (state) => {
      state.currentSubmittedAssignmentLoaded = false;
    },
    [getSubmittedAssignmentDetailsAsync.fulfilled]: (state, action) => {
      state.currentSubmittedAssignment = action.payload.submittedAssignment;
      state.currentSubmittedAssignmentLoaded = true;
    },
  },
});

export default submittedAssignmentSlice.reducer;
