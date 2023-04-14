import { createSlice } from '@reduxjs/toolkit';

const initialState = {
    open: false,
    year: (new Date()).getFullYear().toString() + " \u2304",
    semester: (new Date).getMonth() < 2 ? "Winter" : (new Date).getMonth() < 5 ? "Spring" : (new Date).getMonth() < 8 ? "Summer" : "Fall",
    chosen: null,
    teamId: '1',
};

const coursesTileSlice = createSlice({
    name: 'coursesTile',
    initialState,
    reducers: {
        setOpen: (state, action) => {
            state.open = action.payload;
        },
        setYear: (state, action) => {
            state.year = action.payload;
        },
        setSemester: (state, action) => {
            state.semester = action.payload;
        },
        setChosen: (state, action) => {
            state.chosen = action.payload;
        },
    },
});

export const { setOpen, setYear, setSemester, setChosen } = coursesTileSlice.actions;
export default coursesTileSlice.reducer;
