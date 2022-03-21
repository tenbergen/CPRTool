import React, {useEffect} from 'react';
import './styles/LoginPage.css';
import { useDispatch } from 'react-redux';
import { authenticateUser } from '../../redux/features/authSlice';

function LoginPage() {
    const loginURL = `${process.env.REACT_APP_URL}/login`
    console.log(loginURL)
    const dispatch = useDispatch();
    const queryParams = new URLSearchParams(window.location.search);

    useEffect(() => {
        const token = queryParams.get('token');
        if (token != null) {
            localStorage.setItem("jwt_token", token)
            dispatch(authenticateUser())
        }
    },[])

    return (
        <div id='bigBox'>
                <div id='box'>
                    <div className='welcome'>Welcome!</div>
                    <a href={loginURL}>
                        <button className='googleButton' type='login'>
                            <img
                                alt="google login img"
                                className={'google'}
                                src={require('../img/google_logo.png')}
                            />{' '}
                            Login With Google
                        </button>
                    </a>
                    <a href="" className="started">Get started as an Instructor</a>
                </div>
        </div>
    );
}

export default LoginPage;
