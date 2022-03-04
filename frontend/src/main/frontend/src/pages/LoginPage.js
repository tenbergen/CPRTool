import React, {useEffect} from 'react';
import './styles/LoginPage.css';
import { useDispatch } from 'react-redux';
import { authenticateUser } from '../redux/slices/authSlice';

function LoginPage() {
  const dispatch = useDispatch();
  const queryParams = new URLSearchParams(window.location.search);

  useEffect(() => {
    const token = queryParams.get('token');
    if (token != null) {
      localStorage.setItem("jwt_token", token)
      dispatch(authenticateUser(token))
    }
  },[])

  return (
    <div id='box'>
      <div className='googleButton'>
        <a href='http://moxie.cs.oswego.edu:13126/login'>
          <button type='button'>
            <img
                className={'google'}
                src={require('./img/Google__G__Logo.svg.png')}
            />{' '}
            Login With Google
          </button>
        </a>
      </div>
    </div>
  );
}

export default LoginPage;
