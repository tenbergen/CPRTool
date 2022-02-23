import {
    LOGIN_SUCCESS,
    LOGIN_ERROR
} from './types';
import axios from "axios";

export const authenticateUser = () => {
    return (dispatch) => {
        dispatch({
            type: "LOGIN_SUCCESS",
        });
    }
}

// export const authenticateUser = () => (dispatch, getState) => {
//     axios.get("http://localhost:13126/login", tokenConfig(getState))
//         .then(res => dispatch({
//             type: LOGIN_SUCCESS,
//             payload: res.data
//         }))
//         .catch(err => {
//             dispatch({
//                 type: LOGIN_ERROR
//             })
//         })
// }
//
// export const tokenConfig = getState => {
//     const token = getState().auth.token;
//
//     const config = {
//         headers: {
//             "Content-type": "applications/json"
//         }
//     }
//
//     if (token) {
//         config.headers['x-auth-token'] = token;
//     }
//
//     return config;
// }