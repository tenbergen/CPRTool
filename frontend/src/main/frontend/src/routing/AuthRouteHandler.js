import { Outlet } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import LoginPage from '../pages/AuthPages/LoginPage';
import { useEffect } from 'react';
import {
  refreshTokenAsync,
  setUserInformation,
} from '../redux/features/authSlice';
import axios from 'axios';

const AuthRouteHandler = () => {
  const dispatch = useDispatch();
  const { isAuthenticated } = useSelector((state) => state.auth);

  useEffect(() => {
    dispatch(refreshTokenAsync());
    let token = localStorage.getItem('jwt_token');
    if (token) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    }
    dispatch(setUserInformation());
  }, []);

  return <div>{isAuthenticated ? <Outlet /> : <LoginPage />}</div>;
};

export default AuthRouteHandler;
