import axios from 'axios';

const authURL = `${process.env.REACT_APP_URL}/auth`;
const url = `${authURL}/token/refresh`;

const token = localStorage.getItem("jwt_token")
if (token) {
    axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
}

const refresh_token = localStorage.getItem("refresh_token")
let refresh = false;

axios.interceptors.response.use(
(res) => res,
async (error) => {
    if (error.response.status === 401 && !refresh) {
        refresh = true;
        const axiosAuthInstance = axios.create({
            headers: {
                "Authorization" : `Bearer ${refresh_token}`
            }
        });
        const response = await axiosAuthInstance.post(url);

        if (response.status === 200) {
            axios.defaults.headers.common["Authorization"] = `Bearer ${response.data}`;
        }

        return axios(error.config); // When the token is expired, this will get the new refreshed token and resend the failed request
    }
    refresh = false;
    return error;
})


