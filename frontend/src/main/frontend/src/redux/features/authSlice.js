import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import jwtDecode from 'jwt-decode';
import axios from 'axios';
import { reduxInterceptor } from '../interceptors/reduxInterceptor';

const authURL = `${process.env.REACT_APP_URL}/auth`;

export const getTokenAsync = createAsyncThunk(
  'auth/getTokenAsync',
  async () => {
    const url = `${authURL}/token/generate`;
    const google_token = localStorage.getItem('google_token');
    const axiosAuthInstance = axios.create({
      headers: {
        Authorization: `Bearer ${google_token}`,
      },
    });
    const jwt_token = await axiosAuthInstance
      .post(url)
      .then((res) => {
        localStorage.setItem('jwt_token', res.data.access_token);
        localStorage.setItem('refresh_token', res.data.refresh_token);
        return true;
      })
      .catch((e) => {
        console.log(e);
        return false;
      });
    return { jwt_token };
  }
);

export const refreshTokenAsync = createAsyncThunk(
  'auth/refreshTokenAsync',
  async () => {
    await reduxInterceptor();
  }
);

const getUserInformation = () => {
  const alt_role = localStorage.getItem('alt_role');

  let token = localStorage.getItem('jwt_token');
  if (token) {
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  }

  try {
    let decoded = jwtDecode(localStorage.getItem('jwt_token'));
    return {
      isAuthenticated: true,
      user_given_name: decoded.full_name.split(' ')[0],
      email: decoded.upn,
      lakerId: decoded.laker_id,
      role: alt_role != null ? alt_role : decoded.groups[0],
      dataLoaded: true,
    };
  } catch (e) {
    return {
      isAuthenticated: false,
      dataLoaded: true,
    };
  }
};

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    isAuthenticated: null,
    user_given_name: null,
    email: null,
    lakerId: null,
    role: null,
    dataLoaded: false,
  },
  reducers: {
    setUserInformation: getUserInformation,
  },
  extraReducers: (builder) => {
    builder.addCase(getTokenAsync.fulfilled, getUserInformation);
  },
});

export const { setUserInformation } = authSlice.actions;

export default authSlice.reducer;
