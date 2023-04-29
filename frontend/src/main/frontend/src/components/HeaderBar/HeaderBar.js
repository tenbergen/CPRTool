import './HeaderBar.css'
import React from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useSelector } from 'react-redux'
import jwt_decode from 'jwt-decode'

const HeaderBar = () => {
  const role = useSelector((state) => state.auth.role)
  const alt_role = localStorage.getItem('alt_role')
  const admin_access_token = jwt_decode(localStorage.getItem('jwt_token')).groups.includes('admin')

  let navigate = useNavigate()

  function showDropdown () {
    document.getElementById('dropdown-options').classList.toggle('show')
  }

  const studentView = () => {
    localStorage.setItem('alt_role', 'student')
    //since the professor can change views from any page now, it is necessary to redirect them back to the route url
    navigate('/')
    window.location.reload(false)
  }

  const professorView = () => {
    localStorage.removeItem('alt_role')
    //since the professor can change views from any page now, it is necessary to redirect them back to the route url
    navigate('/')
    window.location.reload(false)
  }

  const adminView = () => {
    localStorage.removeItem('alt_role')
    //since the professor can change views from any page now, it is necessary to redirect them back to the route url
    navigate('/professor/admin')
    window.location.reload(false)
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
        <div id="homeButtonDiv">
          <Link to={`/${role}`}>
            <div>
              <button id="logo"></button>
            </div>
          </Link>
        </div>
      </div>
      <div className="about-and-admin-buttons-container">
        <Link to={`/about`}>
          <div>
            <button id="aboutPageLinkIcon"></button>
          </div>
        </Link>
        {role === 'professor' || (alt_role && alt_role === 'student')
            ? (
                <div className="admin-dropdown">
                  <button id="admin-button" onClick={() => showDropdown()}></button>
                  <div id="dropdown-options" className="dropdown-content">
                    {admin_access_token ? <a href="#" onClick={adminView}>Admin</a> : null}
                    <a href="#" onClick={professorView}>Instructor</a>
                    <a href="#" onClick={studentView}>Student</a>
                  </div>
                </div>
            ) : (
                <div/>
            )
        }
      </div>
    </div>
  )
}

export default HeaderBar