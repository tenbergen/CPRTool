import {
    LOGIN_SUCCESS,
    LOGIN_ERROR
} from '../actions/types';

const initialState = {
    token: localStorage.getItem("token"),
    isAuthenticated: null,
    user: null
}

export default function(state = initialState, action) {
    switch (action.type) {
        case 'LOGIN_SUCCESS':
            return {
                ...state,
                ...action.payload, // returns the token and user
                isAuthenticated: true,
            }
        case 'LOGIN_ERROR':
            return {
                ...state,
                token: null,
                user: null,
                isAuthenticated: false
            }
        default:
            return state;
    }
}