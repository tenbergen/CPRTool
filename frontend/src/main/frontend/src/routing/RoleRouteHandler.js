import { useDispatch, useSelector } from 'react-redux';
import { Outlet } from 'react-router-dom';
import React, { useEffect } from 'react';
import {
  refreshTokenAsync,
  setUserInformation,
} from '../redux/features/authSlice';
import axios from 'axios';

const RoleRouteHandler = ({ allowedRoles }) => {
  const dispatch = useDispatch();
  const { role } = useSelector((state) => state.auth);

  useEffect(() => {
    dispatch(refreshTokenAsync());
    let token = localStorage.getItem('jwt_token');
    if (token) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    }
    dispatch(setUserInformation());
  }, []);

  return (
    <div>
      {allowedRoles.includes(role) ? (
        <Outlet />
      ) : (
        <p> You are unauthorized to view this page </p>
      )}
    </div>
  );
};

export default RoleRouteHandler;
