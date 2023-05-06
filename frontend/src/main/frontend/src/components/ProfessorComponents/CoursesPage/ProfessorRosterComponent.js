import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import '../../../components/NavigationComponents/_ProfessorNavigationComponents.css'
import '../../../pages/TeacherPages/styles/ProfessorRosterStyle.css'
import { useDispatch, useSelector } from 'react-redux'
import { base64StringToBlob } from 'blob-util'
import noStudent from '../../../assets/no-student.png'
import { CgAdd } from 'react-icons/cg'
import uuid from 'react-uuid'
import searchIcon from '../../../pages/AdminPages/search.svg'
//import { useState, useEffect } from 'react';
import editIcon from '../../../pages/AdminPages/edit.png';
import downloadIcon from '../../../../src/assets/icons/navigation/default/Bulk Download.svg'
import '../../styles/EditCourse.css'
import '../../styles/DeleteModal.css'
import '../../styles/Roster.css'
import {
  getCourseDetailsAsync, getCoursesAsync,
  getCurrentCourseStudentsAsync,
  setCurrentCourse
} from '../../../redux/features/courseSlice';
import { current } from '@reduxjs/toolkit';
import { Line } from 'react-chartjs-2';
import Table from './Table';
import axios from 'axios';
import Modal from "../../../pages/AdminPages/Modal";

function ProfessorRosterComponent() {
  const dispatch = useDispatch()
  let navigate = useNavigate()
  const courseParse = window.location.pathname;
  // const courseId = courseParse.split("/")[2];
  const url = `${process.env.REACT_APP_URL}/manage/professor/courses`

  const [showModal, setShowModal] = useState(false)
  const [studentToRemove, setStudentToRemove] = useState(undefined)
  const [searchTerm, setSearchTerm] = useState('')
  const uploadCsvUrl = `${process.env.REACT_APP_URL}/manage/professor/courses/course/student/mass-add`
  const csvFormData = new FormData()
  let { courseId } = useParams()
  const { currentCourse } = useSelector((state) => state.courses)
  const { currentCourseStudents } = useSelector((state) => state.courses)
  const updateUrl = `${process.env.REACT_APP_URL}/manage/professor/courses/course/update`


  //search shit
  const handleSearch = (event) => {
    setSearchTerm(event.target.value)
  }

  const filteredUsers = currentCourseStudents.filter((user) =>
      (user.first_name.toLowerCase().includes(searchTerm.toLowerCase()) ||
          user.last_name.toLowerCase().includes(searchTerm.toLowerCase()) ||
          user.student_id.toLowerCase().includes(searchTerm.toLowerCase()))
  )



  useEffect(() => {
    dispatch(getCurrentCourseStudentsAsync(courseId))
  }, [dispatch, courseId])

  function sayHi(){
    alert("you pressed me");
  }

  const [formData, setFormData] = useState({
    Name: '',
    Email: '',
  })

  const { Name, Email } = formData

  const OnChange = (e) =>
      setFormData({ ...formData, [e.target.name]: e.target.value })

  const fileUploadHandler = async (event) => {
    let file = event.target.files[0]
    const renamedFile = new File([file], currentCourse.course_id + '.csv', {
      type: file.type,
    })
    csvFormData.set('csv_file', renamedFile)
    await axios
        .post(uploadCsvUrl, csvFormData, {
          headers: { 'Content-Type': 'multipart/form-data' },
        })
        .then((res) => {
          window.alert('CSV successfully uploaded!')
        })
        .catch((e) => {
          console.error(e.response.data)
          window.alert('Error uploading CSV. Please try again.')
        })
    dispatch(getCourseDetailsAsync(courseId))
    navigate('/professor/' + courseId)
  }


  const handleSubmit = async () => {
    const nameArray = Name.split(' ')
    const first = nameArray[0]

    const last = nameArray[1]
    if (Name === '' || Email === '') {
      alert('Please enter both name and email for the student!')
      return
    }
    if (nameArray.length < 2) {
      alert('Please enter first and last name!')
      return
    }
    // if (!Email.includes('oswego.edu')) {
    //   alert('Please enter a valid Oswego email!');
    //   return;
    // }

    const firstLastEmail = first + '-' + last + '-' + Email

    const addStudentUrl = `${url}/${courseId}/students/${firstLastEmail}/add`


    await axios
        .post(addStudentUrl)
        .then((res) => {
          alert('Successfully added student.')
          dispatch(getCourseDetailsAsync(courseId))
        })
        .catch((e) => {
          console.error(e)
          alert('Error adding student.')
        })
    setFalse()
    setFormData({ ...formData, Name: '', Email: '' })
    dispatch(getCurrentCourseStudentsAsync(courseId))
  }

  const deleteStudent = async (Email) => {
    const deleteStudentUrl = `${url}/${courseId}/students/${Email.student_id}/delete`
    await axios
        .delete(deleteStudentUrl)
        .then((res) => {
          alert('Successfully deleted student.')
          dispatch(getCurrentCourseStudentsAsync(courseId))
        })
        .catch((e) => {
          console.error(e)
          alert('Error deleting student.')
        })
    dispatch(getCourseDetailsAsync(courseId))
  }

  const setTrue = () => {
    setAddStudentShow(true)
  }
  const setFalse = () => setAddStudentShow(false)

  const onRosterClick = async (studentID) => {
    const url = `${process.env.REACT_APP_URL}/assignments/student/${courseId}/${studentID}/course-assignment-files-student`

    await axios
        .get(url, { responseType: 'blob' })
        .then((res) => {
          prepareStudentFile(res['headers']['content-disposition'], res.data.text())
        })
  }

  const prepareStudentFile = (teamDataName, teamData) => {
    var filename = ''
    var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/
    var matches = filenameRegex.exec(teamDataName)
    if (matches != null && matches[1]) {
      filename = matches[1].replace(/['"]/g, '')
    }
    teamData.then((res) => {
      downloadFile(base64StringToBlob(res, 'application/zip'), filename)
    })
  }

  const downloadFile = (blob, fileName) => {
    const fileURL = URL.createObjectURL(blob)
    const href = document.createElement('a')
    href.href = fileURL
    href.download = fileName
    href.click()
  }
  const [userList, setUserList] = useState([
    { assignment: 'Assignment 1', name: 'Danny Dimes', team: 'New York Giants', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
    { assignment: 'Assignment 1', name: 'Saquads Barkley', team: 'New York Giants', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
    { assignment: 'Assignment 1', name: 'Gardner Minshew', team: 'Indianapolis Colts', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
    { assignment: 'Assignment 2', name: 'Danny Dimes', team: 'New York Giants', grade: '93', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
    { assignment: 'Assignment 2', name: 'Saquads Barkley', team: 'New York Giants', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
    { assignment: 'Assignment 2', name: 'Gardner Minshew', team: 'Indianapolis Colts', grade: '6', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
    { assignment: 'Assignment 2', name: 'Caleb Williams', team: 'Indianapolis Colts', grade: '100', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
    { assignment: 'Assignment 2', name: 'Patty Mahomes', team: 'Kansas City Chiefs', grade: '100', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
    { assignment: 'Assignment 3', name: 'Carl Wheezer', team: 'New York Jets', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
    { assignment: 'Assignment 3', name: 'Perry P', team: 'New York Jets', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
    { assignment: 'Assignment 3', name: 'Larry Lobster', team: 'Baltimore Ravens', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
    { assignment: 'Assignment 3', name: 'Danny Dimes', team: 'New York Giants', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
    { assignment: 'Assignment 4', name: 'Jimmy Neut', team: 'Tennessee Titans', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
    { assignment: 'Assignment 4', name: 'Big Chungus', team: 'Tennessee Titans', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
    { assignment: 'Assignment 4', name: 'Dak Prescott', team: 'Syracuse BenchWarmers', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
    { assignment: 'Assignment 4', name: 'Danny Dimes', team: 'New York Giants', grade: '92', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
    { assignment: 'Assignment 5', name: 'John Bones', team: 'New York Giants', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
    { assignment: 'Assignment 5', name: 'Danny Dimes', team: 'New York Giants', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
    { assignment: 'Assignment 6', name: 'Danny Dimes', team: 'New York Giants', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
  ]);

  const [userStatsList, setUserStatsList] = useState([
    { name: 'Danny Dimes', grade1: '30', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
    { name: 'Saquads Barkley', grade1: '91', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
    { name: 'Gardner Minshew', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
    { name: 'Caleb Williams', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
    { name: 'Patty Mahomes', grade1: '20', grade2: '93', grade3: '40', grade4: '92', grade5: '95', grade6: '100' },
    { name: 'Carl Wheezer', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
    { name: 'Perry P', grade1: '95', grade2: '93', grade3: '100', grade4: '92', grade5: '95', grade6: '100' },
    { name: 'Larry Lobster', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
    { name: 'Big Chungus', grade1: '95', grade2: '93', grade3: '100', grade4: '92', grade5: '95', grade6: '100' },
    { name: 'Dak Prescott', grade1: '13', grade2: '93', grade3: '95', grade4: '92', grade5: '13', grade6: '100' },
    { name: 'John Bones', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
  ]);

  const [selectedAssignment, setSelectedAssignment] = useState("all");
  const [selectedStudent, setSelectedStudent] = useState("all");
  const [selectedVisualStudent, setSelectedVisualStudent] = useState("all");
  const [selectedTeam, setSelectedTeam] = useState("all");
  //const courseId = window.location.pathname;
  //const course = courseId.split("/")[2];
  const getAllAssignmentsUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}/assignments`;
  const getAllStudentsUrl = `${process.env.REACT_APP_URL}/course/professor/courses/${courseId}/students`;
  const [showAddStudentModal, setAddStudentShow] = useState(false)
  const [showEditStudentModal, setEditStudentShow] = useState(false)

  const editStudentModal = (user) => {
    return (

        <div id="deleteModal">
          <div id="modalContent">
               <span id="deleteSpan">
                  Enter the student's name and email.
               </span>
            <div className="add-student-wrapper">
              <label>Name:</label>
              <input
                  type="text"
                  className="rosterInput"
                  name="Name"
                  value={Name}
                  required
                  onChange={(e) => OnChange(e)}
              />
              <label>Email:</label>
              <input
                  type="text"
                  className="rosterInput"
                  name="Email"
                  value={Email}
                  required
                  onChange={(e) => OnChange(e)}
              />
              <button id="addStudentButton" onClick={ () => {deleteStudent(user); handleSubmit(); }}>
                Confirm Edit Student
              </button>
              <button id="ecc-delete-button-cancel" className="inter-16-medium-white"
                      onClick={() => setEditStudentShow(false)}>Cancel
              </button>
            </div>
          </div>

        </div>

    )
  }

  const addStudentModal = () => {
    return (

        <div id="deleteModal">
          <div id="modalContent">
            <span id="deleteSpan" style={{height: '25px'}}>
              Enter the student's name and email.
            </span>
            <div className="pecc-add-student-wrapper">
              <div style={{marginBottom: '6%'}}>
                <label>Name:</label>
                <input
                    type="text"
                    className="rosterInput"
                    name="Name"
                    value={Name}
                    required
                    onChange={(e) => OnChange(e)}
                />
              </div>
              <div>
                <label>Email:</label>
                <input
                    type="text"
                    className="rosterInput"
                    name="Email"
                    value={Email}
                    required
                    onChange={(e) => OnChange(e)}
                />
              </div>
            </div>
            <div>
              <button id="addStudentButton" onClick={handleSubmit}>
                Add Student
              </button>
              <button id="ecc-delete-button-cancel" className="inter-16-medium-white" style={{}}
                      onClick={() => setAddStudentShow(false)}>Cancel
              </button>
            </div>
          </div>
        </div>

    )
  }


  const addStudent = () => {
    return (

        <div className="add-student-container">
          <div className="add-student-wrapper">
            <label>Name:</label>
            <input
                type="text"
                className="rosterInput"
                name="Name"
                value={Name}
                required
                onChange={(e) => OnChange(e)}
            />
            <label>Email:</label>
            <input
                type="text"
                className="rosterInput"
                name="Email"
                value={Email}
                required
                onChange={(e) => OnChange(e)}
            />
            <div id="addStudentButton" onClick={handleSubmit}>
              Add Student
            </div>
          </div>
        </div>

    )
  }


  const searchStudent = {
    searchStudents: async function(courseID, searchText) {
      const SearchStudentUrl = `${url}/course/${courseId}/students`
      const response = await fetch(SearchStudentUrl);
      const data = await response.json();
      const filteredData = data.filter(student => student.name.includes(searchText));
      return filteredData.map(student => student.name);
    }
  };

  const editStudent = async (studentId) => {
    const nameArray = Name.split(' ')
    const first = nameArray[0]

    const last = nameArray[1]
    if (Name === '' || Email === '') {
      alert('Please enter both name and email for the student!')
      return
    }
    if (nameArray.length < 2) {
      alert('Please enter first and last name!')
      return
    }
    const deleteStudentUrl = `${url}/${courseId}/students/${studentId}/delete`
    await axios
        .delete(deleteStudentUrl)
        .then(async (res) => {
          const addStudentUrl = `${url}/${courseId}/students/create`
          await axios
              .post(addStudentUrl, {
                Name: Name,
                Email: Email,
              })
              .then((res) => {
                alert('Successfully edited student.')
                dispatch(getCurrentCourseStudentsAsync(courseId))
              })
              .catch((e) => {
                console.error(e)
                alert('Error editing student.')
              })
        })
        .catch((e) => {
          console.error(e)
          alert('Error deleting student.')
        })
    dispatch(getCourseDetailsAsync(courseId))
  }

  const uniqueAssignments = userList
      .filter((user, index, arr) => arr.findIndex(u => u.assignment === user.assignment) === index)
      .map(user => user.assignment);

  const uniqueNames = userList
      .filter((user, index, arr) => arr.findIndex(u => u.name === user.name) === index)
      .map(user => user.name);

  const uniqueStatsNames = userStatsList
      .filter((user, index, arr) => arr.findIndex(u => u.name === user.name) === index)
      .map(user => user.name);

  const uniqueTeams = userList
      .filter((user, index, arr) => arr.findIndex(u => u.team === user.team) === index)
      .map(user => user.team);

  axios.get(getAllStudentsUrl)
      .then(response => console.log(response.data))
      .catch(error => console.error("error getting students from getAllStudentsURL"));

  axios.get(getAllAssignmentsUrl)
      .then(response => {
        const getAllSubmissions = response.data;
        for (let i = 1; i <= response.data.length; i++) {
          axios.get(`${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}/assignments/${i}`)
              .then(response2 => {
                console.log(response2.data)
              })
        }
      })
      .catch(error => console.error("error getting assignments from getAllAssignmentsURL"));

  return (
      <div className="page-container">
        <div className='admin-container'>
          <div className='user-roles'>
            <h2>Roster</h2>
            {(
                <>
                {/*added*/}
                <div className='search-filter-add'>
                  {/**/}
                  <div className='dropdowns-div'>
                    <div className='search-bar'>
                      {/**!/*/}
                      <label>Search</label>
                      <div className='assignment-dropdown'>
                        <input
                            type="text"
                            value={searchTerm}
                            onChange={handleSearch}
                            placeholder="Search.."
                            title="Search"
                        />
                        <button className="professor-roster-search-button"><img className="search-icon" src={searchIcon}/></button>

                        {/*<input type="text" id="mySearch" onKeyUp={searchStudent} placeholder="Search.."*/}
                        {/*       title="Type in a category">*/}
                        {/*</input>*/}
                      </div>
                    </div>
                    <div className="student-dropdown">
                      <div className='search-bar'>
                        {/**!/*/}
                        <label>Filter</label>
                        {/*<div className='assignment-dropdown'>*/}
                        <select name="assignment" id="role"
                                onChange={(e) => setSearchTerm(e.target.value)}>
                          <option value="all">All</option>
                          {currentCourseStudents.map(item => {
                            return (<option value = {item.student_id}>{item.first_name + item.last_name}</option>);
                          })}
                        </select>
                        {/*</div>*/}
                      </div>
                    </div>
                    <div className="team-dropdown">
                      {/*Test to see if i can just toss this component in instead of calling const onClick*/}
                      {/*  <label>*/}
                      {/*    Roster Upload*/}
                      {/*  </label>*/}

                      {/*  <button className='csv-download-button'  onClick={(event) => {*/}
                      {/*    fileUploadHandler(event);}}>Roster Upload</button>*/}
                      <div className='professor-roster-csv-download-button'>
                      <input
                          type="button"
                          className="professor-roster-csv-download-button"
                          value="Upload CSV"
                      />
                      <input
                          onChange={(event) => {
                            fileUploadHandler(event);
                          }}
                                  type="file"
                                  name="course_csv"
                                  accept=".csv"
                      />
                      </div>


                      {/*<input className='professor-roster-csv-download-button'*/}
                        {/*    onChange={(event) => {*/}
                        {/*      fileUploadHandler(event);*/}
                        {/*    }}*/}
                        {/*     type="file"*/}
                        {/*     name="course_csv"*/}
                        {/*     accept=".csv"*/}

                        {/*/>*/}
                    </div>

                    <div className="team-dropdown">
                      {/*Test, to see if i can just toss this component in instead of calling const onClick*/}
                      <button className='add-student-button' onClick={() => setAddStudentShow(true)}>Add Student</button>
                      {/*div to show the add student shit*/}
                      <div>{showAddStudentModal ? addStudentModal() : null}</div>
                    </div>
                    {/*<div className='csv-download-button-div'>*/}
                    {/*  <button className='csv-download-button'>CSV Download</button>*/}
                    {/*</div>*/}
                  </div>
                </div>
                  <div>
                    <div className='user-list'>
                      <div className='user-item header'>
                        <div>Name</div>
                        <div>Laker Net ID</div>
                        <div>Major</div>
                        <div>Team</div>
                        <div>Actions</div>
                      </div>
                      <div className='all-user-items'>

                        {filteredUsers.map((user) => (

                            <div key={user.id} className='user-item'>
                              <div className='name-div'>{user.first_name} {" "} {user.last_name}</div>
                              {/*<div className='studentID-div'>{user.team}</div>*/}
                              <div className='laker-div'>{user.student_id}</div>
                              <div>{"Computer Science"}</div>
                              <div className='team-div'>{user.team}</div>
                              <div className='action-div'>

                                <div>
                                  <button style={{backgroundColor: 'transparent'}} onClick={() => onRosterClick(user.student_id)}>
                                    <img src={downloadIcon} />
                                  </button>
                                </div>

                                <div className='edit-container'>
                                  <button className='edit-button' onClick={() => setEditStudentShow(true)}><img className='edit-icon' src={editIcon} /></button>
                                <div>{showEditStudentModal ? editStudentModal(user) : null}</div>
                              </div>
                              <div className='delete-container'>
                                <button className='delete-button' onClick={() => {setShowModal(true); setStudentToRemove(user)}}>X</button>
                                {showModal && (
                                  <div className="roster-delete-modal">
                                    <div className="roster-modal-content">
                                      <h2 className="roster-modal-head">Confirm</h2>
                                      <form>
                                        <label className="roster-confirm-remove">Are you sure you want to
                                          remove {studentToRemove.first_name} {studentToRemove.last_name}?</label>
                                      </form>
                                      <div className="roster-remove-user-buttons">
                                        <button className="roster-remove-user-popup-button" onClick={() => {
                                          setShowModal(false)
                                          deleteStudent(studentToRemove)
                                        }}>Yes
                                        </button>
                                        <button className="roster-cancel-user-delete-button"
                                          onClick={() => setShowModal(false)}>No
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
          </div>
        </div>
      </div>
  );
}

export default ProfessorRosterComponent
