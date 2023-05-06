import './LogoutButtonStyle.css'
import React from 'react';
import {useNavigate} from "react-router-dom";

const LogoutButton = () => {

    let navigate = useNavigate();

    const handleLogout = () => {
        localStorage.clear();
        navigate('/');
        window.location.reload(false);
    };

    return (
        <div>
            <button id="logout-button" onClick={handleLogout}>
                <div id="logout-icon"/>
                <p className="inter-24-medium">Logout</p>
            </button>
        </div>
    )
}

export default LogoutButton;