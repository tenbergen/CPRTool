import React from 'react';
import "./styles/LoginPage.css"
import {useNavigate} from "react-router-dom";

function LoginPage() {
    let navigate = useNavigate()

    const handleClick = (e) => {
        // e.preventDefault();
        // window.location.href = 'localhost:13126/login';
        navigate("/teacherDashboard")
    }

    return (
        <div id="box">
            <div className="googleButton">
                <button
                    type="button"
                    onClick={handleClick}
                >
                    <img className = {"google"} src={require('./img/Google__G__Logo.svg.png')}/> Login With Google</button>
            </div>
        </div>
    );
}

export default LoginPage;
