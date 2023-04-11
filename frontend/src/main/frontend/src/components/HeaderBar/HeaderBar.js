import './HeaderBar.css'
import React from 'react';
import { Link } from 'react-router-dom';
import {useSelector} from "react-redux";

const HeaderBar = () => {
    const role = useSelector((state) => state.auth.role);
    const alt_role = localStorage.getItem('alt_role');

    function showDropdown() {
        document.getElementById("dropdown-options").classList.toggle("show");
    }

    const studentView = () => {
        localStorage.setItem("alt_role", "student");
        window.location.reload(false);
    };

    const professorView = () => {
        localStorage.removeItem('alt_role');
        window.location.reload(false);
    };

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
                <Link to={`/${role}`}>
                    <div >
                        <button id="logo"></button>
                    </div>
                </Link>
            </div>
            {role === 'professor' || (alt_role && alt_role === 'student')
                ? (
                    <div className="admin-dropdown">
                        <button id="admin-button" onClick={() => showDropdown()}></button>
                        <div id="dropdown-options" className="dropdown-content">
                            <Link to={`/professor/admin`}>
                                Admin
                            </Link>
                            <a href="#"onClick={professorView}>Instructor</a>
                            <a href="#" onClick={studentView}>Student</a>
                        </div>
                    </div>
                ) : (
                    <div/>
                )
            }
        </div>
    )
}

export default HeaderBar;