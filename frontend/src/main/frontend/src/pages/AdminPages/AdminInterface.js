import { useEffect, useState } from 'react';
import '../TeacherPages/styles/ProfessorCourseStyle.css';
import SidebarComponent from '../../components/SidebarComponent';
import { useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { getCourseDetailsAsync } from '../../redux/features/courseSlice';
import GradeAssBarComponent from '../../components/GradeAssBarComponent';
import ProfessorAllSubmissionsComponent from '../../components/ProfessorComponents/AssignmentPage/ProfessorAllSubmissionsComponent';
import ProfessorEditAssignmentComponent from '../../components/ProfessorComponents/AssignmentPage/ProfessorEditAssignmentComponent';
import { getAssignmentDetailsAsync } from '../../redux/features/assignmentSlice';
import Loader from '../../components/LoaderComponenets/Loader';
import uuid from 'react-uuid';
import editIcon from './edit.png'

function AdminInterface() {

  const [userList, setUserList] = useState([
    { netID: 'ddimes', name: 'Danny Dimes', role: 'Admin' },
    { netID: 'quads', name: 'Saquads Barkley', role: 'Teacher' },
    { netID: 'ern', name: 'Ernie Spinkleton', role: 'Student' },
    { netID: 'ern', name: 'Ernie Spinkleton', role: 'Student' },
    { netID: 'ern', name: 'Ernie Spinkleton', role: 'Student' },
    { netID: 'ern', name: 'Ernie Spinkleton', role: 'Student' },
    { netID: 'ern', name: 'Ernie Spinkleton', role: 'Teacher' },
    { netID: 'ern', name: 'Ernie Spinkleton', role: 'Student' },
    { netID: 'ern', name: 'Ernie Spinkleton', role: 'Teacher' },
    { netID: 'ern', name: 'Ernie Spinkleton', role: 'Student' },
    { netID: 'ern', name: 'Ernie Spinkleton', role: 'Student' },
    { netID: 'ern', name: 'Ernie Spinkleton', role: 'Student' },
    { netID: 'ern', name: 'Ernie Spinkleton', role: 'Admin' },
    { netID: 'ern', name: 'Ernie Spinkleton', role: 'Student' },
    { netID: 'ern', name: 'Ernie Spinkleton', role: 'Student' },
    { netID: 'ern', name: 'Ernie Spinkleton', role: 'Student' },
    { netID: 'ern', name: 'Ernie Spinkleton', role: 'Student' },
    { netID: 'ern', name: 'Ernie Spinkleton', role: 'Student' },
    { netID: 'ern', name: 'Ernie Spinkleton', role: 'Student' },
    { netID: 'perryp', name: 'Perry Platypus', role: 'Student' },
    { netID: 'ern', name: 'Ernie Spinkleton', role: 'Student' },
  ]);

  const [searchTerm, setSearchTerm] = useState('');
  const [selectedRole, setSelectedRole] = useState("all");

  const handleSearch = (event) => {
    setSearchTerm(event.target.value);
  };

  const filteredUsers = userList.filter((user) =>
    user.name.toLowerCase().includes(searchTerm.toLowerCase()) &&
    (selectedRole === "all" || user.role === selectedRole)
  );


  const [filterType, setFilterType] = useState(null);

  const handleFilterClick = (type) => {
    setFilterType(type);
  }
  
  const [page, setPage] = useState("roles")

  const changePage = (page) => {
    setPage(page)
  }

  return (
    <div className='admin-container'>
      <div className='sidebar'>
        <h1>Admin</h1>
      </div>
      <div className='user-roles'>
        <h2>Admin</h2>
        <div className='admin-tabs'>
          <button className='user-roles-tab' onClick={() => changePage('roles')} style={{ backgroundColor: page === "roles" ? "#4a7dfc" : "#E6E6E6", color: page === "roles" ? "white" : "black" }}>User Roles</button>
          <button className='courses-tab' onClick={() => changePage('courses')} style={{ backgroundColor: page === "courses" ? "#4a7dfc" : "#E6E6E6", color: page === "courses" ? "white" : "black" }}>Courses</button>
          <button className='profanity-settings-tab' onClick={() => changePage('profanity')} style={{ backgroundColor: page === "profanity" ? "#4a7dfc" : "#E6E6E6", color: page === "profanity" ? "white" : "black" }}>Profanity Settings</button>
        </div>
        {page === "roles" && (
          <><div className='search-filter-add'>
            <div className='search-bar'>
              <label>Search</label>
              <input
                type='text'
                value={searchTerm}
                onChange={handleSearch} />
            </div>
            <div className="dropdown">
              <label for="role-filter">Role Filter</label>
              <select name="role" id="role" onChange={(e) => setSelectedRole(e.target.value)}>
                <option value="all">All</option>
                <option value="Admin">Admin</option>
                <option value="Teacher">Teacher</option>
                <option value="Student">Student</option>
              </select>
            </div>
            <div className='add-user-button-div'>
              <button className='add-user-button'>Add User +</button>
            </div>
          </div><div>
              <div className='user-list'>
                <div className='user-item header'>
                  <div>Name</div>
                  <div>Laker Net ID</div>
                  <div>Role</div>
                  <div>Actions</div>
                </div>
                <div className='all-user-items'>
                  {filteredUsers.map((user) => (
                    <div key={user.id} className='user-item'>
                      <div>{user.name}</div>
                      <div>{user.netID}</div>
                      <div>{user.role}</div>
                      <div>
                        <button className='edit-button'><img className='edit-icon' src={editIcon}/></button>
                        <button className='delete-button'>X</button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div></>
        )}
        {page === "courses" && (
          <div>courses</div>
        )}
        {page === "profanity" && (
          <div>profanity</div>
        )}
      </div>
    </div>
  );
}

export default AdminInterface;
