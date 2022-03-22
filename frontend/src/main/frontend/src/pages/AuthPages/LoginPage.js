import React, {useEffect} from 'react';
import './styles/LoginPage.css';
import { useDispatch } from 'react-redux';
import { authenticateUser } from '../../redux/features/authSlice';
import GoogleLogin from "react-google-login";

function LoginPage() {
    const loginURL = `${process.env.REACT_APP_URL}/login`
    console.log(loginURL)
    const dispatch = useDispatch();
    const queryParams = new URLSearchParams(window.location.search);

    // useEffect(() => {
    //     const token = queryParams.get('token');
    //     if (token != null) {
    //         localStorage.setItem("jwt_token", token)
    //         dispatch(authenticateUser())
    //     }
    // },[])

    const handleFailure = (result) => {
        console.log("Login error: " + result.details)
    };

    const handleLogin = async (googleData) => {
        console.log(googleData)
        // const res = await fetch('PUT_ENDPOINT_HERE', {
        //     method: 'POST',
        //     body: JSON.stringify({
        //         token: googleData.tokenId,
        //     }),
        //     headers: {
        //         'Content-Type': 'application/json',
        //     },
        // });
        // const data = await res.json();
        // console.log(data)
    };

    return (
        <div id='bigBox'>
            <div id='box'>
                <div className='welcome'>Welcome!</div>
                <GoogleLogin
                    className='googleButton'
                    clientId="334840295190-jcieqjl5rdmcjur8qj9dg3uhtiicu1r6.apps.googleusercontent.com"
                    buttonText="Log in with Google"
                    onSuccess={handleLogin}
                    onFailure={handleFailure}
                    cookiePolicy={'single_host_origin'}
                />
                <a href="" className="started">Get started as an Instructor</a>
            </div>
        </div>
    );
}

export default LoginPage;
