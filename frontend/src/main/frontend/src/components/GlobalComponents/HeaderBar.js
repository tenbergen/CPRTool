import './HeaderBar.css'
import React from 'react';
import {useNavigate} from "react-router-dom";

const HeaderBar = () => {

    const handleLogoClick = () => {
        window.location.replace('/')
    }

    return (
        <div className="headerBar">
            <div >
                <button id="logo" onClick={() => handleLogoClick()}></button>
            </div>
            <div >
                <button id="admin-button"></button>
            </div>
        </div>
    )
}

export default HeaderBar;