import axios from 'axios';
import jwtDecode from 'jwt-decode';

const authURL = `${process.env.REACT_APP_URL}/auth`;
const url = `${authURL}/token/refresh`;

const refresh_token = localStorage.getItem('refresh_token');
let refresh = false;

const forceLogout = () => {
  const refresh_token = localStorage.getItem('refresh_token');

  const currentTime = new Date();
  const utcMilliseconds =
    currentTime.getTime() + currentTime.getTimezoneOffset() * 60 * 1000;
  const utcSeconds = Math.round(utcMilliseconds / 1000);

  if (jwtDecode(refresh_token).exp <= utcSeconds) {
    alert('Session expired!');
    localStorage.clear();
  }
};

axios.interceptors.response.use(
  (res) => res,
  async (error) => {
    if (error.response.status === 401 && !refresh) {
      refresh = true;

      forceLogout();

      const axiosAuthInstance = axios.create({
        headers: {
          Authorization: `Bearer ${refresh_token}`,
        },
      });

      await axiosAuthInstance
        .post(url)
        .then((res) => {
          if (res.status === 200) {
            axios.defaults.headers.common[
              'Authorization'
            ] = `Bearer ${res.data.access_token}`;
          }
        })
        .catch((e) => {
          console.error(e);
        });

      return axios(error.config); // When the token is expired, this will get the new refreshed token and resend the failed request
    }
    refresh = false;
    return error;
  }
);
