import axios from 'axios';

const authURL = `${process.env.REACT_APP_URL}/auth`;
const url = `${authURL}/token/refresh`;

let refresh = false;

axios.interceptors.response.use(
  (res) => res,
  async (error) => {
    if (error.response.status === 401 && !refresh) {
      refresh = true;
      const response = await axios.post(url);

      if (response.status == 200) {
        axios.defaults.headers.common[
          'Authorization'
        ] = `Bearer ${refres_token}`;
      }

      return axios(error.config); // When the token is expired, this will get the new refreshed token and resend the failed request
    }
    refresh = false;
    return error;
  }
);
