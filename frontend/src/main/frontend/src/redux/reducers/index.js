import { combineReducers } from "redux";
import authReducer from './authReducer'

const reducers = combineReducers({
    // returns key-value pair of authReducer and what authReducer is returning
    authReducer: authReducer
})

export default reducers;
