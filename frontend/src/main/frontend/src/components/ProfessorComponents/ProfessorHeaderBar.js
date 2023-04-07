import './ProfessorHeaderBar.css'
import React from 'react';

const ProfessorHeaderBar = () => {

    const handleLogoClick = () => {
        window.location.replace('/')
    }

    function showDropdown() {
        document.getElementById("dropdown-options").classList.toggle("show");
    }

// // Close the dropdown menu if the user clicks outside of it
//     window.onclick = function(event) {
//         if (!event.target.matches('#admin-button')) {
//             var dropdowns = document.getElementsByClassName("dropdown-content");
//             var i;
//             for (i = 0; i < dropdowns.length; i++) {
//                 var openDropdown = dropdowns[i];
//                 if (openDropdown.classList.contains('show')) {
//                     openDropdown.classList.remove('show');
//                 }
//             }
//         }
//     }

    return (
        <div className="headerBar">
            <div className="header-bar-left">
                <div >
                    <button id="logo" onClick={() => handleLogoClick()}></button>
                </div>
            </div>
            <div className="admin-dropdown">
                <button id="admin-button" onClick={() => showDropdown()}></button>
                <div id="dropdown-options" className="dropdown-content">
                    <a href="#">Admin</a>
                    <a href="#">Instructor</a>
                    <a href="#">Student</a>
                </div>
            </div>
        </div>
    )
}

export default ProfessorHeaderBar;