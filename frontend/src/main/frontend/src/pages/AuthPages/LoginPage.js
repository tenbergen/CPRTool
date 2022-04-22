import React, { useState, useEffect } from 'react';
import './styles/LoginPage.css';
import { useDispatch } from 'react-redux';
import { getTokenAsync } from '../../redux/features/authSlice';
import GoogleLogin from 'react-google-login';
import { useNavigate } from 'react-router-dom';
import Loader from '../../components/LoaderComponenets/Loader';

function LoginPage() {
  const [isLoading, setIsLoading] = useState(false);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const REACT_APP_CLIENT_ID = `${process.env.REACT_APP_CLIENT_ID}`;

  useEffect(() => {
    setIsLoading(true);
    setTimeout(() => setIsLoading(false), 775);
  }, []);

  const handleFailure = (result) => {
    console.log(result);
    navigate('/unauthenticated');
  };

  const handleLogin = async (googleData) => {
    localStorage.setItem('google_token', googleData.tokenId);
    dispatch(getTokenAsync());
  };

  return (
    <div>
      {isLoading ? (
        <Loader />
      ) : (
        <div className='bigBox'>
          <div id='box'>
            <div className='kumba-40 welcome'>Welcome!</div>
            <GoogleLogin
              className='googleButton'
              clientId={REACT_APP_CLIENT_ID}
              buttonText='Log in with Google'
              onSuccess={handleLogin}
              onFailure={handleFailure}
              hostedDomain={'oswego.edu'}
              cookiePolicy={'single_host_origin'}
              prompt='select_account'
            />
            <a href='' className='started'>
              Get started as an Instructor
            </a>
          </div>
        </div>
      )}
    </div>
  );
}

export default LoginPage;
