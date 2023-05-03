import { useEffect, useState, ChangeEvent } from 'react'
import './AdminStyle.css'
import editIcon from './edit.png'
import searchIcon from './search.svg'
import plusIcon from './plus.png'
import Modal from './Modal'
import axios from 'axios'
import HeaderBar from '../../components/HeaderBar/HeaderBar'
import LogoutButton from '../../components/GlobalComponents/LogoutButton'
import AdminProfanityComponent from './AdminProfanityComponent'
import AdminNavigationContainerComponent from '../../components/NavigationComponents/AdminNavigationContainerComponent'

function AdminInterface () {

  // const [courseList, setCourseList] = useState([
  //   { Course: 'CSC 212 - Principles of Programming', Instructor: 'Perry', CRN: '101233', Year: '2022', Semester: 'Spring' },
  //   { Course: 'CSC 480 - Software Design', Instructor: 'Danny Dimes', CRN: '231193', Year: '2023', Semester: 'Fall' },
  //   { Course: 'CSC 241 - Extreme Java', Instructor: 'Danny Dimes', CRN: '235790', Year: '2024', Semester: 'Fall' },
  //   { Course: 'HCI 509 - Research Methods', Instructor: 'Danny Dimes', CRN: '345356', Year: '2023', Semester: 'Winter' },
  //   { Course: 'ISC 210 - Informatics', Instructor: 'Danny Dimes', CRN: '123573', Year: '2023', Semester: 'Fall' },
  //   { Course: 'HCI 530 - Data Visualization', Instructor: 'Danny Dimes', CRN: '9111123', Year: '2023', Semester: 'Fall' },
  //   { Course: 'CSC 431 - Microprocessors', Instructor: 'Danny Dimes', CRN: '567234', Year: '2023', Semester: 'Summer' },
  //   { Course: 'ACC 201 - Principles of Accounting I', Instructor: 'Danny Dimes', CRN: '991808', Year: '2023', Semester: 'Fall' },
  //   { Course: 'HCI 550 - Project I', Instructor: 'Danny Dimes', CRN: '123456', Year: '2023', Semester: 'Summer' },
  //   { Course: 'HCI 551 - Project II', Instructor: 'Danny Dimes', CRN: '651322', Year: '2023', Semester: 'Fall' },
  //   { Course: 'ISC 246 - Database Management', Instructor: 'Danny Dimes', CRN: '312332', Year: '2023', Semester: 'Summer' },
  //   { Course: 'MUS 101 - World of Music', Instructor: 'Danny Dimes', CRN: '722987', Year: '2024', Semester: 'Winter' },
  // ]);

  const [profanityList, setProfanityList] = useState([
    { profanity: 'heck', excludedCourses: [] },
    { profanity: 'darn', excludedCourses: [] },
    { profanity: 'shoot', excludedCourses: [] },
    { profanity: 'flip', excludedCourses: ['234234'] },
    { profanity: 'frick', excludedCourses: ['532552'] },
    { profanity: 'gee wizz', excludedCourses: [] },
    { profanity: 'kumquat', excludedCourses: [] },
    { profanity: 'meow', excludedCourses: [] },
    { profanity: 'nuts', excludedCourses: ['235235', '239353'] },
    { profanity: 'profanity1', excludedCourses: [] },
    { profanity: 'profanity2', excludedCourses: [] },
    { profanity: 'profanity3', excludedCourses: [] },
  ])

  const [searchTerm, setSearchTerm] = useState('')
  const [selectedRole, setSelectedRole] = useState('all')
  const [selectedUserRole, setSelectedUserRole] = useState('')
  const [selectedYear, setSelectedYear] = useState('all')
  const [selectedSemester, setSelectedSemester] = useState('all')
  const [openModal, setOpenModal] = useState(false)
  const [selectedCourse, setSelectedCourse] = useState([])
  const [showModal, setShowModal] = useState(false)
  const [showDeleteModal, setShowDeleteModal] = useState(false)
  const [showEditModal, setShowEditModal] = useState(false)
  const [showDeleteCourseModal, setShowDeleteCourseModal] = useState(false)
  const getUsersUrl = `${process.env.REACT_APP_URL}/manage/admin/views/users`
  const getCoursesUrl = `${process.env.REACT_APP_URL}/manage/admin/views/courses`

  let [userList, setUserList] = useState([])
  let [courseList, setCourseList] = useState([])

  useEffect(() => {
    axios.get(getUsersUrl)
      .then(response => {
        setUserList(response.data)
        console.log(response.data)
      })
      .catch(error => console.error(error.message))
  }, [])

  useEffect(() => {
    axios.get(getCoursesUrl)
      .then(response => {
        console.log(response.data)
        setCourseList(response.data)

      })
      .catch(error => console.error(error.message))
  }, [])

  function refreshPage () {
    window.location.reload(false)
  }

  const handleSearch = (event) => {
    setSearchTerm(event.target.value)
  }

  const filteredUsers = userList.filter((user) =>
    (user.first_name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.last_name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.user_id.toLowerCase().includes(searchTerm.toLowerCase())) &&
    (selectedRole === 'all' || user.role === selectedRole)
  )

  const filteredCourses = courseList.filter((course) =>
    (course.course_name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      course.abbreviation.toLowerCase().includes(searchTerm.toLowerCase()) ||
      course.course_section.toLowerCase().includes(searchTerm.toLowerCase()) ||
      course.professor_id.toLowerCase().includes(searchTerm.toLowerCase()) ||
      course.crn.toLowerCase().includes(searchTerm.toLowerCase())) &&
    (selectedYear === 'all' || course.year === selectedYear) &&
    (selectedSemester === 'all' || course.semester === selectedSemester)
  )

  const filteredProfanities = profanityList.filter((profanity) =>
    (profanity.profanity.toLowerCase().includes(searchTerm.toLowerCase()))
  )

  const [filterType, setFilterType] = useState(null)

  const handleFilterClick = (type) => {
    setFilterType(type)
  }

  const [page, setPage] = useState('roles')

  const changePage = (page) => {
    setPage(page)
  }

  const changeCourse = (selectedCourse) => {
    setSelectedCourse(selectedCourse)
  }

  const [deleteName, setDeleteName] = useState('')

  const getDeleteUser = (firstName, lastName) => {
    setDeleteName(firstName + ' ' + lastName)
  }

  const [editName, setEditName] = useState('')

  const getEditUser = (firstName, lastName) => {
    setEditName(firstName + ' ' + lastName)
  }

  const [deleteCourseName, setDeleteCourseName] = useState('')

  const getDeleteCourse = (course) => {
    setDeleteCourseName(course)
  }

  const [currentRole, setCurrentRole] = useState('')

  const getCurrentRole = (role) => {
    setCurrentRole(role)
  }

  const [courseID, setCourseID] = useState('')

  const getCourseID = (courseid) => {
    setCourseID(courseid)
  }

  const [currentID, setCurrentID] = useState('')

  const getCurrentID = (ID) => {
    setCurrentID(ID)
  }

  const [editRole, setEditRole] = useState('')

  const getEditRole = (role) => {
    setEditRole(role)
  }

  const addUser = async (userFirstName, userLastName, userID, role) => {
    if (role === 'student') {
      const url = `${process.env.REACT_APP_URL}/manage/admin/add/student/${userID}/${userFirstName}/${userLastName}`
      await axios
        .post(url)
        .then((res) => {
          alert('Succesfully added user')
          refreshPage()
          console.log(res.data)
        })
        .catch((e) => {
          alert(e.message)
        })
      console.log(userID)
      console.log(userFirstName)
      console.log(userLastName)
      console.log(url)
    } else if (role === 'admin') {
      const url = `${process.env.REACT_APP_URL}/manage/admin/add/admin/${userID}/${userFirstName}/${userLastName}`
      await axios
        .post(url)
        .then((res) => {
          alert('Successfully added user')
          refreshPage()
          console.log(res.data)
        })
        .catch((e) => {
          alert(e.message)
        })
      console.log(userID)
      console.log(userFirstName)
      console.log(userLastName)
      console.log(url)
    } else if (role === 'professor') {
      const url = `${process.env.REACT_APP_URL}/manage/admin/add/professor/${userID}/${userFirstName}/${userLastName}`
      await axios
        .post(url)
        .then((res) => {
          alert('Succesfully added user')
          refreshPage()
          console.log(res.data)
        })
        .catch((e) => {
          alert(e.message)
        })
      console.log(userID)
      console.log(userFirstName)
      console.log(userLastName)
      console.log(url)
    }
    setFirstName('')
    setLastName('')
    setLakerID('')
    setSelectedUserRole('')
  }

  const [firstName, setFirstName] = useState('')
  const handleFirstName = (event) => {
    setFirstName(event.target.value)
  }

  const [lastName, setLastName] = useState('')
  const handleLastName = (event) => {
    setLastName(event.target.value)
  }

  const [lakerID, setLakerID] = useState('')
  const handleLakerID = (event) => {
    setLakerID(event.target.value)
  }

  const deleteUser = async (userID, role) => {
    if (role === 'student') {
      const url = `${process.env.REACT_APP_URL}/manage/admin/delete/student/${userID}`
      await axios
        .delete(url)
        .then((res) => {
          alert('Succesfully deleted user')
          refreshPage()
          console.log(res.data)
        })
        .catch((e) => {
          alert(e.message)
        })
      console.log(userID)
      console.log(url)
    } else if (role === 'admin') {
      const url = `${process.env.REACT_APP_URL}/manage/admin/delete/admin/${userID}`
      await axios
        .delete(url)
        .then((res) => {
          alert('Succesfully deleted user')
          refreshPage()
          console.log(res.data)
        })
        .catch((e) => {
          alert(e.message)
        })
      console.log(userID)
      console.log(url)
    } else if (role === 'professor') {
      const url = `${process.env.REACT_APP_URL}/manage/admin/delete/professor/${userID}`
      await axios
        .delete(url)
        .then((res) => {
          alert('Succesfully deleted user')
          refreshPage()
          console.log(res.data)
        })
        .catch((e) => {
          alert(e.message)
        })
      console.log(userID)
      console.log(url)
    }
    setFirstName('')
    setLastName('')
    setLakerID('')
    setSelectedUserRole('')
  }

  const deleteCourse = async (courseID) => {
    const url = `${process.env.REACT_APP_URL}/manage/admin/delete/course/${courseID}`
    await axios
      .delete(url)
      .then((res) => {
        alert('Succesfully deleted course')
        refreshPage()
        console.log(res.data)
      })
      .catch((e) => {
        alert(e.message)
      })
    console.log(courseID)
  }

  const editUser = async (userID, role, newRole) => {
    if (role === 'admin' && newRole === 'professor') {
      const url = `${process.env.REACT_APP_URL}/manage/admin/roles/demote/adminToProfessor/${userID}`
      await axios
        .post(url)
        .then((res) => {
          alert('Succesfully edited user')
          refreshPage()
          console.log(res.data)
        })
        .catch((e) => {
          alert(e.message)
        })
      console.log(userID)
      console.log(url)
    } else if (role === 'professor' && newRole === 'student') {
      const url = `${process.env.REACT_APP_URL}/manage/admin/roles/demote/professorToStudent/${userID}`
      await axios
        .post(url)
        .then((res) => {
          alert('Succesfully edited user')
          refreshPage()
          console.log(res.data)
        })
        .catch((e) => {
          alert(e.message)
        })
      console.log(userID)
      console.log(url)
    } else if (role === 'professor' && newRole === 'admin') {
      const url = `${process.env.REACT_APP_URL}/manage/admin/roles/promote/professorToAdmin/${userID}`
      await axios
        .post(url)
        .then((res) => {
          alert('Succesfully edited user')
          refreshPage()
          console.log(res.data)
        })
        .catch((e) => {
          alert(e.message)
        })
      console.log(userID)
      console.log(url)
    } else if (role === 'student' && newRole === 'professor') {
      const url = `${process.env.REACT_APP_URL}/manage/admin/roles/promote/studentToProfessor/${userID}`
      await axios
        .post(url)
        .then((res) => {
          alert('Succesfully edited user')
          refreshPage()
          console.log(res.data)
        })
        .catch((e) => {
          alert(e.message)
        })
      console.log(userID)
      console.log(url)
    }
    setFirstName('')
    setLastName('')
    setLakerID('')
    setSelectedUserRole('')
  }

  const uniqueYears = courseList
    .filter((course, index, arr) => arr.findIndex(u => u.year === course.year) === index)
    .map(course => course.year)

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100vh' }}><Modal open={openModal}
                                                                                      courseArr={selectedCourse}
                                                                                      onClose={() => setOpenModal(false)}/>
      <HeaderBar/>
      <div className="admin-page-container">
        <AdminNavigationContainerComponent/>
        <div className="admin-user-roles">
          <h2>Admin</h2>
          <div className="admin-tabs">
            <button className="user-roles-tab" onClick={() => changePage('roles')} style={{
              backgroundColor: page === 'roles' ? '#4a7dfc' : '#E6E6E6',
              color: page === 'roles' ? 'white' : 'black'
            }}>User Roles
            </button>
            <button className="courses-tab" onClick={() => changePage('courses')} style={{
              backgroundColor: page === 'courses' ? '#4a7dfc' : '#E6E6E6',
              color: page === 'courses' ? 'white' : 'black'
            }}>Courses
            </button>
            <button className="profanity-settings-tab" onClick={() => changePage('profanity')} style={{
              backgroundColor: page === 'profanity' ? '#4a7dfc' : '#E6E6E6',
              color: page === 'profanity' ? 'white' : 'black'
            }}>Profanity Settings
            </button>
          </div>

          {page === 'roles' && (
            <>
              <div className="search-filter-add">
                <div className="search-bar">
                  <label>Search</label>
                  <div className="search-icon-div">
                    <input
                      type="text"
                      value={searchTerm}
                      onChange={handleSearch}/>
                    <button className="admin-search-button"><img className="search-icon" src={searchIcon}/></button>
                  </div>
                </div>
                <div className="admin-page-dropdown">
                  <label for="role-filter">Role Filter</label>
                  <select name="admin-role" id="admin-role" onChange={(e) => setSelectedRole(e.target.value)}>
                    <option value="all">All</option>
                    <option value="admin">Admin</option>
                    <option value="professor">Professor</option>
                    <option value="student">Student</option>
                  </select>
                </div>
                <div className="admin-add-user-button-div">
                  <button className="admin-add-user-button" onClick={() => setShowModal(true)}>Add User +</button>

                  {showModal && (
                    <div className="modal">
                      <div className="modal-content">
                        <h2 className="modal-head">Add User</h2>
                        <form>
                          <div className="add-user-name">
                            <label>First Name</label>
                            <input className="add-user-fields"
                                   type="text"
                                   id="firstName"
                                   name="firstName"
                                   onChange={handleFirstName}/>
                          </div>
                          <div className="add-user-name">
                            <label>Last Name</label>
                            <input className="add-user-fields"
                                   type="text"
                                   id="lastName"
                                   name="lastName"
                                   onChange={handleLastName}/>
                          </div>
                          <div className="add-user-email">
                            <label>Laker Net ID</label>
                            <input className="add-user-fields"
                                   type="text"
                                   id="lakerID"
                                   name="lakerID"
                                   onChange={handleLakerID}/>
                          </div>
                          <div className="add-user-dropdown">
                            <label for="role-filter">Role</label>
                            <select name="role" id="add-user-role" defaultValue="Select Role"
                                    onChange={(e) => setSelectedUserRole(e.target.value)}>
                              <option disabled={true} value="Select Role">--Select Role--</option>
                              <option value="admin">Admin</option>
                              <option value="professor">Professor</option>
                              <option value="student">Student</option>
                            </select>
                          </div>
                        </form>
                        <div className="add-user-buttons">
                          <button className="add-user-popup-button" onClick={() => {
                            addUser(firstName, lastName, lakerID, selectedUserRole)
                            setShowModal(false)
                          }}>Add
                          </button>
                          <button className="cancel-user-button" onClick={() => setShowModal(false)}>Cancel</button>
                        </div>
                      </div>
                    </div>
                  )}

                </div>
              </div>
              <div>
                <div className="admin-user-list">
                  <div className="user-item header">
                    <div>Name</div>
                    <div>Laker Net ID</div>
                    <div>Role</div>
                    <div>Actions</div>
                  </div>
                  <div className="admin-all-user-items">
                    {filteredUsers.map((user) => (
                      <div key={user.id} className="user-item">
                        <div className="name-list">
                          <div>{user.first_name}</div>
                          <div>{user.last_name}</div>
                        </div>
                        <div>{user.user_id}</div>
                        <div>{user.role}</div>
                        <div>
                          <div className="admin-edit-container">
                            <button className="admin-edit-button" onClick={() => {
                              setShowEditModal(true)
                              getEditUser(user.first_name, user.last_name)
                              getCurrentID(user.user_id)
                              getCurrentRole(user.role)
                            }}>
                              <img className="admin-edit-icon" src={editIcon}/>
                            </button>

                            {showEditModal && (
                              <div className="edit-modal">
                                <div className="modal-content">
                                  <h2 className="modal-head">Edit {editName}</h2>
                                  <form>
                                    <div className="add-user-dropdown">
                                      <label for="role-filter">Role</label>
                                      <select name="role" id="add-user-role" defaultValue="Select Role"
                                              onChange={(e) => setEditRole(e.target.value)}>
                                        <option disabled={true} value="Select Role">--Select Role--</option>
                                        <option value="admin">Admin</option>
                                        <option value="professor">Professor</option>
                                        <option value="student">Student</option>
                                      </select>
                                    </div>
                                  </form>
                                  <div className="add-user-buttons">
                                    <button className="add-user-popup-button" onClick={() => {
                                      editUser(currentID, currentRole, editRole)
                                      setShowEditModal(false)
                                    }}>Save Changes
                                    </button>
                                    <button className="cancel-user-button"
                                            onClick={() => setShowEditModal(false)}>Cancel
                                    </button>
                                  </div>
                                </div>
                              </div>
                            )}

                          </div>
                          <div className="admin-delete-container">
                            <button className="admin-delete-button" onClick={() => {
                              setShowDeleteModal(true)
                              getDeleteUser(user.first_name, user.last_name)
                              getCurrentID(user.user_id)
                              getCurrentRole(user.role)
                            }}>
                              X
                            </button>
                            {showDeleteModal && (
                              <div className="delete-modal">
                                <div className="modal-content">
                                  <h2 className="modal-head">Confirm</h2>
                                  <form>
                                    <label className="confirm-remove">Are you sure you want to
                                      remove {deleteName}?</label>
                                  </form>
                                  <div className="remove-user-buttons">
                                    <button className="remove-user-popup-button" onClick={() => {
                                      setShowDeleteModal(false)
                                      deleteUser(currentID, currentRole)
                                    }}>Yes
                                    </button>
                                    <button className="cancel-user-delete-button"
                                            onClick={() => setShowDeleteModal(false)}>No
                                    </button>
                                  </div>
                                </div>
                              </div>
                            )}

                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </>
          )}
          {page === 'courses' && (
            <>
              <div className="search-year-semester">
                <div className="search-bar-courses">
                  <label>Search</label>
                  <div className="search-icon-div">
                    <input
                      type="text"
                      value={searchTerm}
                      onChange={handleSearch}/>
                    <button className="admin-search-button"><img className="search-icon" src={searchIcon}/></button>
                  </div>
                </div>
                <div className="dropdown-year">
                  <label for="role-filter">Year</label>
                  <select name="role" id="admin-courses-role" onChange={(e) => setSelectedYear(e.target.value)}>
                    <option value="all">All</option>
                    {uniqueYears.map(item => {
                      return (<option key={item} value={item}>{item}</option>)
                    })}
                  </select>
                </div>
                <div className="dropdown-semester">
                  <label for="role-filter">Semester</label>
                  <select name="role" id="admin-courses-role" onChange={(e) => setSelectedSemester(e.target.value)}>
                    <option value="all">All</option>
                    <option value="Spring">Spring</option>
                    <option value="Summer">Summer</option>
                    <option value="Fall">Fall</option>
                    <option value="Winter">Winter</option>
                  </select>
                </div>
              </div>
              <div>
                <div className="admin-user-list">
                  <div className="user-item header">
                    <div className="courses-list">Courses</div>
                    <div>Instructor</div>
                    <div>CRN</div>
                    <div>Year</div>
                    <div>Semester</div>
                    <div>Actions</div>
                  </div>
                  <div className="admin-all-user-items">
                    {filteredCourses.map((course) => (
                      <div key={course.id} className="user-item">
                        <div className="courses-list">
                          <div className="abbreviation-section-container">
                            <div>{course.abbreviation} -</div>
                          </div>
                          <div>{course.course_name}</div>
                        </div>
                        <div>{course.professor_id}</div>
                        <div>{course.crn}</div>
                        <div>{course.year}</div>
                        <div>{course.semester}</div>
                        <div>
                          <div className="delete-container">
                            <button className="delete-button" onClick={() => {
                              setShowDeleteCourseModal(true)
                              getDeleteCourse(course.crn)
                              getCourseID(course.course_id)
                            }}>X
                            </button>
                            {showDeleteCourseModal && (
                              <div className="delete-modal">
                                <div className="modal-content">
                                  <h2 className="modal-head">Confirm</h2>
                                  <form>
                                    <label className="confirm-remove">Are you sure you want to
                                      remove {deleteCourseName}?</label>
                                  </form>
                                  <div className="remove-user-buttons">
                                    <button className="remove-user-popup-button" onClick={() => {
                                      setShowDeleteCourseModal(false)
                                      deleteCourse(courseID)
                                    }}>Yes
                                    </button>
                                    <button className="cancel-user-delete-button"
                                            onClick={() => setShowDeleteCourseModal(false)}>No
                                    </button>
                                  </div>
                                </div>
                              </div>
                            )}
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </>
          )}
          {page === 'profanity' && (
            <AdminProfanityComponent/>
          )}
        </div>
      </div>
    </div>
  )
}

export default AdminInterface