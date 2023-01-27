import { useEffect, useState } from 'react';
import './styles/LoginPage.css';
import { useDispatch } from 'react-redux';
import { getTokenAsync } from '../../redux/features/authSlice';
import {GoogleLogin} from '@react-oauth/google';
import { useNavigate } from 'react-router-dom';
import Loader from '../../components/LoaderComponenets/Loader';

function LoginPage() {
  const [isLoading, setIsLoading] = useState(false);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleFailure = () => {
    navigate('/unauthenticated');
  }

  const handleLogin = async (googleData) => {
    console.log(googleData)
    localStorage.setItem('google_token', googleData.credential);
    dispatch(getTokenAsync());
  };

  useEffect(() => {
    setIsLoading(true);
    setTimeout(() => setIsLoading(false), 775);
    return () => {
      setIsLoading(false);
    };
  }, []);

  return (
      <div>
        {isLoading ? (
            <Loader />
        ) : (
            <div className='bigBox'>
              <div id='box'>
                <div className='kumba-40 welcome'>Welcome!</div>
                <div className='googleButton'>
                  <GoogleLogin
                      text={'signin_with'}
                      onSuccess={handleLogin}
                      onError={handleFailure}
                      hosted_domain={'oswego.edu'}
                      size={'large'}
                      cookiePolicy={'single_host_origin'}
                      prompt='select_account'
                  />
                </div>
              </div>
            </div>
        )}
      </div>

  );
}

export default LoginPage;
