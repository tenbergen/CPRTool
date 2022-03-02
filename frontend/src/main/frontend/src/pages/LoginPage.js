import React from 'react';
import "./styles/LoginPage.css"
import {useDispatch} from "react-redux";
import {authenticateUser} from "../redux/slices/authSlice";

function LoginPage() {
    const dispatch = useDispatch()

    const handleClick = () => {
        dispatch(authenticateUser())
    }

    // useEffect() -- checks for url param using useSearchParams() from React-Router
        // check for token param
            // if not null -- authenticateUser(token)

    return (
        <div id="box">
            <div className="googleButton">
                <button
                    type="button"
                    onClick={handleClick}>
                    <img className = {"google"} src={require('./img/Google__G__Logo.svg.png')}/> Login With Google</button>
            </div>
        </div>
    );
}

export default LoginPage;
