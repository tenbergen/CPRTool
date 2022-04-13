import jwtDecode from "jwt-decode";
import axios from "axios";

const authURL = `${process.env.REACT_APP_URL}/auth`
const url = `${authURL}/token/refresh`;
const refresh_token = localStorage.getItem("refresh_token")

export const reduxInterceptor = async () => {
    const access_token = localStorage.getItem("jwt_token")

    const currentTime = new Date()
    const utcMilliseconds = currentTime.getTime()
    const utcSeconds = Math.round(utcMilliseconds / 1000)

    if (jwtDecode(refresh_token).exp <= utcSeconds) {
        alert("Session expired!")
        localStorage.clear()
    } else if (jwtDecode(access_token).exp <= utcSeconds) {
        const axiosAuthInstance = axios.create({
            headers: {
                "Authorization": `Bearer ${refresh_token}`
            }
        });

        await axiosAuthInstance.post(url)
            .then(res => {
                if (res.status === 200) {
                    axios.defaults.headers.common["Authorization"] = `Bearer ${res.data.access_token}`;
                    localStorage.setItem("jwt_token", res.data.access_token)
                    console.log("refreshed!")
                }
            })
            .catch(e => {
                console.log(e)
            })
    }
}