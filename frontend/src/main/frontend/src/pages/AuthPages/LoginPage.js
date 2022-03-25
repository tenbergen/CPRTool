import React from 'react';
import './styles/LoginPage.css';
import { useDispatch } from 'react-redux';
import { getTokenAsync } from '../../redux/features/authSlice';
import GoogleLogin from "react-google-login";

function LoginPage() {
    const dispatch = useDispatch();
    const REACT_APP_CLIENT_ID = `${process.env.REACT_APP_CLIENT_ID}`

    const handleFailure = (result) => {
        console.log("Login error: " + result.details)
    };

    const handleLogin = async (googleData) => {
        localStorage.setItem("google_token", googleData.tokenId)
        dispatch(getTokenAsync())
    };

    return (
        <div id='bigBox'>
            <div id='box'>
                <div className='welcome'>Welcome!</div>
                <GoogleLogin
                    className='googleButton'
                    clientId={REACT_APP_CLIENT_ID}
                    buttonText="Log in with Google"
                    onSuccess={handleLogin}
                    onFailure={handleFailure}
                    hostedDomain={"oswego.edu"}
                    cookiePolicy={'single_host_origin'}
                />
                <a href="" className="started">Get started as an Instructor</a>
            </div>
        </div>
    );
}

export default LoginPage;
