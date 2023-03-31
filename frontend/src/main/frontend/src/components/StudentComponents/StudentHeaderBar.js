import './StudentHeaderBar.css'
import React from 'react';

const StudentHeaderBar = () => {

    const handleLogoClick = () => {
        window.location.replace('/')
    }

    return (
        <div className="headerBar">
            <div >
                <button id="logo" onClick={() => handleLogoClick()}></button>
            </div>
        </div>
    )
}

export default StudentHeaderBar;