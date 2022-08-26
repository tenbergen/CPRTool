import { createSlice } from '@reduxjs/toolkit';

const peerReviewSlice = createSlice({
  name: 'peerReviewSlice',
  initialState: {
    peerReviewTeamFiles: [],
    peerReviewFeedback: null,
    peerReviewFeedbackFiles: [],
  },
  reducers: {
    peerReviewFeedback: (state, action) => {
      state.peerReviewFeedback = action.payload;
    },
  },
});

export default peerReviewSlice.reducer;
