import React from 'react';
import "./styles/LoginPage.css"
import {useDispatch} from "react-redux";
import {bindActionCreators} from "redux";
import { authCreators } from '../redux/index.js'
import {authenticateUser} from "../redux/actions/authActions";

function LoginPage() {
    const dispatch = useDispatch()

    const { authenticateUser } = bindActionCreators(authCreators, dispatch)

    const handleClick = () => {
        authenticateUser()
    }

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
