import { useEffect, useState } from 'react';
import './AdminStyle.css';
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
import editIcon from './edit.png';
import searchIcon from './search.svg';
import plusIcon from './plus.png';
import Modal from './Modal';

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

  const [courseList, setCourseList] = useState([
    { Course: 'CSC 212 - Principles of Programming', Instructor: 'Perry', CRN: '101233', Year: '2022', Semester: 'Spring'},
    { Course: 'CSC 480 - Software Design', Instructor: 'Danny Dimes', CRN: '231193', Year: '2023', Semester: 'Fall'},
    { Course: 'CSC 241 - Extreme Java', Instructor: 'Danny Dimes', CRN: '235790', Year: '2024', Semester: 'Fall'},
    { Course: 'HCI 509 - Research Methods', Instructor: 'Danny Dimes', CRN: '345356', Year: '2023', Semester: 'Winter'},
    { Course: 'ISC 210 - Informatics', Instructor: 'Danny Dimes', CRN: '123573', Year: '2023', Semester: 'Fall'},
    { Course: 'HCI 530 - Data Visualization', Instructor: 'Danny Dimes', CRN: '9111123', Year: '2023', Semester: 'Fall'},
    { Course: 'CSC 431 - Microprocessors', Instructor: 'Danny Dimes', CRN: '567234', Year: '2023', Semester: 'Summer'},
    { Course: 'ACC 201 - Principles of Accounting I', Instructor: 'Danny Dimes', CRN: '991808', Year: '2023', Semester: 'Fall'},
    { Course: 'HCI 550 - Project I', Instructor: 'Danny Dimes', CRN: '123456', Year: '2023', Semester: 'Summer'},
    { Course: 'HCI 551 - Project II', Instructor: 'Danny Dimes', CRN: '651322', Year: '2023', Semester: 'Fall'},
    { Course: 'ISC 246 - Database Management', Instructor: 'Danny Dimes', CRN: '312332', Year: '2023', Semester: 'Summer'},
    { Course: 'MUS 101 - World of Music', Instructor: 'Danny Dimes', CRN: '722987', Year: '2024', Semester: 'Winter'},
  ]);

  const [profanityList, setProfanityList] = useState([
    { profanity: 'heck', excludedCourses: []},
    { profanity: 'darn', excludedCourses: []},
    { profanity: 'shoot', excludedCourses: []},
    { profanity: 'flip', excludedCourses: ['234234']},
    { profanity: 'frick', excludedCourses: ['532552']},
    { profanity: 'gee wizz', excludedCourses: []},
    { profanity: 'kumquat', excludedCourses: []},
    { profanity: 'meow', excludedCourses:[]},
    { profanity: 'nuts', excludedCourses: ['235235', '239353']},
    { profanity: 'profanity1', excludedCourses: []},
    { profanity: 'profanity2', excludedCourses: []},
    { profanity: 'profanity3', excludedCourses: []},
  ]);

  const [searchTerm, setSearchTerm] = useState('');
  const [selectedRole, setSelectedRole] = useState("all");
  const [selectedYear, setSelectedYear] = useState("all");
  const [selectedSemester, setSelectedSemester] = useState("all");
  const [openModal, setOpenModal] = useState(false);
  const [selectedCourse, setSelectedCourse] = useState([]);

  const handleSearch = (event) => {
    setSearchTerm(event.target.value);
  };

  const filteredUsers = userList.filter((user) =>
    user.name.toLowerCase().includes(searchTerm.toLowerCase()) &&
    (selectedRole === "all" || user.role === selectedRole)
  );

  const filteredCourses = courseList.filter((course) =>
    (course.Course.toLowerCase().includes(searchTerm.toLowerCase()) ||
    course.Instructor.toLowerCase().includes(searchTerm.toLowerCase()) ||
    course.CRN.toLowerCase().includes(searchTerm.toLowerCase())) &&
    (selectedYear === "all" || course.Year === selectedYear) &&
    (selectedSemester === "all" || course.Semester === selectedSemester)
  );

  const filteredProfanities = profanityList.filter((profanity) =>
    (profanity.profanity.toLowerCase().includes(searchTerm.toLowerCase()))
  );


  const [filterType, setFilterType] = useState(null);

  const handleFilterClick = (type) => {
    setFilterType(type);
  }

  const [page, setPage] = useState("roles")

  const changePage = (page) => {
    setPage(page)
  }

  const changeCourse = (selectedCourse) => {
    setSelectedCourse(selectedCourse)
  }

  return (
    <><Modal open={openModal} courseArr={selectedCourse} onClose={() => setOpenModal(false)} />
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
              <div className='search-icon-div'>
                <input
                  type='text'
                  value={searchTerm}
                  onChange={handleSearch} />
                <button className='search-button'><img className='search-icon' src={searchIcon} /></button>
              </div>
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
                        <div className='edit-container'>
                          <button className='edit-button'><img className='edit-icon' src={editIcon} /></button>
                        </div>
                        <div className='delete-container'>
                          <button className='delete-button'>X</button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div></>
        )}
        {page === "courses" && (
          <><div className='search-year-semester'>
            <div className='search-bar-courses'>
              <label>Search</label>
              <div className='search-icon-div'>
                <input
                  type='text'
                  value={searchTerm}
                  onChange={handleSearch} />
                <button className='search-button'><img className='search-icon' src={searchIcon} /></button>
              </div>
            </div>
            <div className="dropdown-year">
              <label for="role-filter">Year</label>
              <select name="role" id="role" onChange={(e) => setSelectedYear(e.target.value)}>
                <option value="all">All</option>
                <option value="2022">2022</option>
                <option value="2023">2023</option>
                <option value="2024">2024</option>
              </select>
            </div>
            <div className="dropdown-semester">
              <label for="role-filter">Semester</label>
              <select name="role" id="role" onChange={(e) => setSelectedSemester(e.target.value)}>
                <option value="all">All</option>
                <option value="Spring">Spring</option>
                <option value="Summer">Summer</option>
                <option value="Fall">Fall</option>
                <option value="Winter">Winter</option>
              </select>
            </div>
          </div><div>
              <div className='user-list'>
                <div className='user-item header'>
                  <div className='courses-list'>Courses</div>
                  <div>Instructor</div>
                  <div>CRN</div>
                  <div>Year</div>
                  <div>Semester</div>
                  <div>Actions</div>
                </div>
                <div className='all-user-items'>
                  {filteredCourses.map((course) => (
                    <div key={course.id} className='user-item'>
                      <div className='courses-list'>{course.Course}</div>
                      <div>{course.Instructor}</div>
                      <div>{course.CRN}</div>
                      <div>{course.Year}</div>
                      <div>{course.Semester}</div>
                      <div>
                        <div className='edit-container'>
                          <button className='edit-button'><img className='edit-icon' src={editIcon} /></button>
                        </div>
                        <div className='delete-container'>
                          <button className='delete-button'>X</button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div></>
        )}
        {page === "profanity" && (
          <><div className='search-year-semester'>
            <div className='search-bar-courses'>
              <label>Search</label>
              <div className='search-icon-div'>
                <input
                  type='text'
                  value={searchTerm}
                  onChange={handleSearch} />
                <button className='search-button'><img className='search-icon' src={searchIcon} /></button>
              </div>
            </div>
            <div className="add-profanity">
              <label>Add Profanity</label>
              <div className='profanity-icon-div'>
                <input
                  type='text' />
                <button className='plus-profanity-button'><img className='plus-icon' src={plusIcon} /></button>
              </div>
            </div>
          </div><div>
              <div className='user-list'>
                <div className='user-item header'>
                  <div>Profanity</div>
                  <div>Courses Excluded From</div>
                  <div>Actions</div>
                </div>
                <div className='all-user-items'>
                  {filteredProfanities.map((profanity) => (
                    <div key={profanity.id} className='user-item'>
                      <div>{profanity.profanity}</div>
                      <div className='excluded-courses-div'>
                        <div><button className='excluded-courses-button' onClick={() => {setOpenModal(true); changeCourse(profanity.excludedCourses)}}>{profanity.excludedCourses.length}</button></div>
                      </div>
                      <div>
                        <div className='edit-container'>
                          <button className='edit-button'><img className='edit-icon' src={editIcon} /></button>
                        </div>
                        <div className='delete-container'>
                          <button className='delete-button'>X</button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div></>
        )}
      </div>
    </div></>
  );
}

export default AdminInterface;