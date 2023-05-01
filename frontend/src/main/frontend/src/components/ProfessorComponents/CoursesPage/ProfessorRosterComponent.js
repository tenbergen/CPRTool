
// import axios from 'axios'
//import '../../styles/Roster.css'
import '../../../components/NavigationComponents/_ProfessorNavigationComponents.css'
import '../../../pages/TeacherPages/styles/ProfessorRosterStyle.css' //ProfessorGradesStyling.css'
import { useDispatch, useSelector } from 'react-redux'
import { base64StringToBlob } from 'blob-util'
import { useParams } from 'react-router-dom'
import noStudent from '../../../assets/no-student.png'
import { CgAdd } from 'react-icons/cg'
import uuid from 'react-uuid'
//import { useState, useEffect } from 'react';
import downloadIcon from '../../../../src/assets/icons/White_Download.svg'
import {
  getCourseDetailsAsync,
  getCurrentCourseStudentsAsync,
  setCurrentCourse
} from '../../../redux/features/courseSlice';
import { current } from '@reduxjs/toolkit';
import { Line } from 'react-chartjs-2';
import Table from './Table';
import axios from 'axios';
import Modal from "../../../pages/AdminPages/Modal";
import editIcon from "../../../pages/AdminPages/edit.png";
import { useEffect, useState } from 'react'



/*
const ProfessorRosterComponent = () => {
  const dispatch = useDispatch()
  const courseParse = window.location.pathname;
  const courseId = courseParse.split("/")[2];
  const url = `${process.env.REACT_APP_URL}/manage/professor/courses`
  const { currentCourseStudents } = useSelector((state) => state.courses)



  useEffect(() => {
    dispatch(getCurrentCourseStudentsAsync(courseId))
  }, [dispatch, courseId])

  const [formData, setFormData] = useState({
    Name: '',
    Email: '',
  })

  const { Name, Email } = formData
  const OnChange = (e) =>
    setFormData({ ...formData, [e.target.name]: e.target.value })

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
          <button id="addStudentButton" onClick={handleSubmit}>
            Add Student
          </button>
        </div>
      </div>
    )
  }

  const [show, setShow] = useState(false)
  const setTrue = () => {
    setShow(true)
  }
  const setFalse = () => setShow(false)

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
*/
//   return (
//     <div>
//       <div className="RosterPage">
//         <div id="roster">
//           <div className="roster-wrapper">
//             {currentCourseStudents.length > 0 ? (
//               <div>
//                 <table className="rosterTable">
//                   {/*<thead>*/}
//                   {/*<tr>*/}
//                   {/*  <th className="rosterHeader">Name</th>*/}
//                   {/*  <th className="rosterHeader">Laker Net ID</th>*/}
//                   {/*  <th className="rosterHeader">Major</th>*/}
//                   {/*  <th className="rosterHeader">Team</th>*/}
//                   {/*  <th className="rosterHeader">Actions</th>*/}
//                   {/*  <th className="rosterHeader"></th>*/}
//                   {/*</tr>*/}
//                   {/*</thead>*/}
//                   <div>
//                     <div className='user-list'>
//                       <div className='user-item header'>
//                         <div>Name</div>
//                         <div>Laker Net ID</div>
//                         <div>Major</div>
//                         <div>Team</div>
//                         <div>Actions</div>
//                       </div>
//                       <div className='all-user-items'>
//                         {currentCourseStudents.map((student) => (
//                             <div key={student.student_id} className='user-item'>
//                               <div className='name-div'>{student.name}</div>
//                               <div className='team-div'>{student.lakerId}</div>
//                               <div className='grade-div'>{student.student_id}</div>
//                               <div className='submit-div'>{student.team}</div>
//                               <div>
//                                 <div className='edit-container'>
//                                   <button className='edit-button'><img className='edit-icon'
//                                                                        src={editIcon}/></button>
//                                 </div>
//                                 <div className='delete-container'>
//                                   <button className='delete-button'>X</button>
//                                 </div>
//                               </div>
//                             </div>
//                         ))}
//                       </div>
//                     </div>
//                   </div>
//                   {/*<tbody>*/}
//                   {/*{currentCourseStudents.map(*/}
//                   {/*  (student) =>*/}
//                   {/*    student && (*/}
//                   {/*      <tr key={uuid()}>*/}
//                   {/*        <th className="rosterComp">*/}
//                   {/*          {student.first_name*/}
//                   {/*            ? student.first_name + ' ' + student.last_name*/}
//                   {/*            : ''}*/}
//                   {/*        </th>*/}
//                   {/*        <th className="rosterComp">{student.student_id}</th>*/}
//                   {/*        <th className="rosterComp">Computer Science</th>*/}
//                   {/*        <th className="rosterComp">*/}
//                   {/*          {student.team !== null ? student.team : ''}*/}
//                   {/*        </th>*/}
//                   {/*        <th className="rosterComp">*/}
//                   {/*          <svg className={'bulk-download-icon-default'} alt={'Bulk Download For Student'}*/}
//                   {/*               style={{ cursor: 'pointer' }} onClick={() => onRosterClick(student.student_id)}>>*/}
//                   {/*          </svg>*/}
//                   {/*        </th>*/}
//                   {/*        <th className="rosterComp">*/}
//                   {/*          <div className="crossMark-wrapper">*/}
//                   {/*            <div*/}
//                   {/*              onClick={() => deleteStudent(student)}*/}
//                   {/*              className="crossMark"*/}
//                   {/*            >*/}
//                   {/*              X*/}
//                   {/*            </div>*/}
//                   {/*          </div>*/}
//                   {/*        </th>*/}
//                   {/*      </tr>*/}
//                   {/*    )*/}
//                   {/*)}*/}
//                   {/*</tbody>*/}
//                 </table>
//               </div>
//             ) : (
//               <div className="no-student-container">
//                 <div className="no-student-wrapper">
//                   <div className="no-student-img-wrapper">
//                     <img
//                       className="no-student-img"
//                       src={noStudent}
//                       alt="no_students"
//                       onClick={() => {
//                         setTrue()
//                       }}
//                     />
//                   </div>
//                   <div className="no-student-header">No Students Added</div>
//                   <div className="no-student-description">
//                     Click the plus button to add a student
//                   </div>
//                 </div>
//               </div>
//             )}
//             {show ? (
//               addStudent()
//             ) : (
//               <div className="plus-button-container">
//                 <div className="plus-button-wrapper">
//                   <CgAdd
//                     onClick={() => setTrue()}
//                     className="plus-button"
//                     size="50px"
//                     color="#4a7dfc"
//                   />
//                 </div>
//               </div>
//             )}
//           </div>
//         </div>
//       </div>
//     </div>
//   )
// }




function ProfessorRosterComponent() {
  const dispatch = useDispatch()
  const courseParse = window.location.pathname;
  const courseId = courseParse.split("/")[2];
  const url = `${process.env.REACT_APP_URL}/manage/professor/courses`
  const [showModal, setShowModal] = useState(false)
  const [studentToRemove, setStudentToRemove] = useState(undefined)
  const { currentCourseStudents } = useSelector((state) => state.courses)

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


  const handleSubmit = async () => {
    const nameArray = Name.split(' ')
    const first = nameArray[0]
    console.log("firstName: "+first)

    const last = nameArray[1]
    console.log("lastName: " + last)
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
    console.log("email: " + firstLastEmail)

    const addStudentUrl = `${url}/${courseId}/students/${firstLastEmail}/add`
    console.log("courseID: "+ courseId)

    console.log("url in header: " + url + ", generated url after adding email: " +  addStudentUrl)

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

  const [searchTerm, setSearchTerm] = useState('');
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
            Enter the students name and email.
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
               <span id="deleteSpan">
            Enter the students name and email.
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
                <button id="addStudentButton" onClick={handleSubmit}>
                  Add Student
                </button>
                <button id="ecc-delete-button-cancel" className="inter-16-medium-white"
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
            <button id="addStudentButton" onClick={handleSubmit}>
              Add Student
            </button>
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
    console.log("firstName: "+first)

    const last = nameArray[1]
    console.log("lastName: " + last)
    if (Name === '' || Email === '') {
      alert('Please enter both name and email for the student!')
      return
    }
    if (nameArray.length < 2) {
      alert('Please enter first and last name!')
      return
    }
    console.log("Student ID: " + studentId)
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
        //console.log(response.data);
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
                        <input type="text" id="mySearch" onKeyUp={searchStudent} placeholder="Search.."
                               title="Type in a category">
                        </input>
                      </div>
                    </div>
                    <div className="student-dropdown">
                      <div className='search-bar'>
                        {/**!/*/}
                        <label>Filter</label>
                        {/*<div className='assignment-dropdown'>*/}
                        <select name="assignment" id="role"
                                onChange={(e) => setSelectedAssignment(e.target.value)}>
                          <option value="all">All</option>
                          {uniqueAssignments.map(item => {
                            return (<option key={item} value={item}>{item}</option>);
                          })}
                        </select>
                        {/*</div>*/}
                      </div>
                    </div>
                    <div className="team-dropdown">
                      {/*Test to see if i can just toss this component in instead of calling const onClick*/}
                      <button className='csv-download-button' >Upload CSV</button>
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
                        {currentCourseStudents.map((user) => (
                            <div key={user.id} className='user-item'>
                              <div className='name-div'>{user.first_name} {" "} {user.last_name}</div>
                              {/*<div className='studentID-div'>{user.team}</div>*/}
                              <div className='laker-div'>{user.student_id}</div>
                              <div className='student-div'>{"Computer Science"}</div>
                              <div className='team-div'>{user.team}</div>
                              <div className='action-div'>
                                <div className='edit-container'>
                                  <button className='bulk-download-button' onClick={() => onRosterClick(user.student_id)}>
                                    <img className='edit-icon' src={downloadIcon} />
                                  </button>
                                </div>

                                <div className='edit-container'>
                                  <button className='edit-button' onClick={() => setEditStudentShow(true)}><img className='edit-icon' src={editIcon} /></button>
                                  <div>{showEditStudentModal ? editStudentModal(user) : null}</div>
                                </div>
                                <div className='delete-container'>
                                  <button className='delete-button' onClick={()=> deleteStudent(user)}>X</button>
                                </div>
                              </div>
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


/*import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import '../../styles/Roster.css'
import classes from '../../styles/Roster.module.css'
import '../../../components/NavigationComponents/_ProfessorNavigationComponents.css'

import { useDispatch, useSelector } from 'react-redux'
import {
  getCourseDetailsAsync, getCoursesAsync,
  getCurrentCourseStudentsAsync,
} from '../../../redux/features/courseSlice'
import { useParams } from 'react-router-dom'
import noStudent from '../../../assets/no-student.png'
import { CgAdd } from 'react-icons/cg'
import uuid from 'react-uuid'
import { base64StringToBlob } from 'blob-util'
const uploadCsvUrl = `${process.env.REACT_APP_URL}/manage/professor/courses/course/student/mass-add`
const updateUrl = `${process.env.REACT_APP_URL}/manage/professor/courses/course/update`

const ProfessorRosterComponent = () => {
  let navigate = useNavigate()
  const dispatch = useDispatch()
  const courseParse = window.location.pathname;
  const courseId = courseParse.split("/")[2];
  let { courseID } = useParams()
  const url = `${process.env.REACT_APP_URL}/manage/professor/courses`
  const { currentCourseStudents } = useSelector((state) => state.courses)
  const csvFormData = new FormData()

  useEffect(() => {
    dispatch(getCurrentCourseStudentsAsync(courseId))
  }, [dispatch, courseId])

  const [formData, setFormData] = useState({
    Name: '',
    Email: '',
  })



  const { Name, Email } = formData
  const OnChange = (e) =>
      setFormData({ ...formData, [e.target.name]: e.target.value })

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
            <button id="addStudentButton" onClick={handleSubmit}>
              Add Student
            </button>
          </div>
        </div>
    )
  }

  const editStudent = async (studentId, newName, newEmail) => {
    const deleteStudentUrl = `${url}/${courseId}/students/${studentId}/delete`
    await axios
        .delete(deleteStudentUrl)
        .then(async (res) => {
          const addStudentUrl = `${url}/${courseId}/students/create`
          await axios
              .post(addStudentUrl, {
                Name: newName,
                Email: newEmail,
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

  const [show, setShow] = useState(false)
  const setTrue = () => {
    setShow(true)
  }
  const setFalse = () => setShow(false)

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
  //
  // const searchStudent = currentCourseStudents.filter((student) => {
  //   return (
  //       student &&
  //       (student.first_name
  //           ? student.first_name.toLowerCase().includes(searchTerm.toLowerCase())
  //           : '') ||
  //       (student.last_name
  //           ? student.last_name.toLowerCase().includes(searchTerm.toLowerCase())
  //           : '') ||
  //       (student.student_id
  //           ? student.student_id.toLowerCase().includes(searchTerm.toLowerCase())
  //           : '') ||
  //       (student.team !== null
  //           ? student.team.toString().includes(searchTerm.toLowerCase())
  //           : '')
  //   );
  // });

  // const filterStudent = {
  //   filterStudents: async function(courseId) {
  //     const studentData = useSelector(state => state.courses.currentCourseStudents);
  //     const studentNames = studentData.map(student => student.first_name + ' ' + student.last_name);
  //     const selectedStudents = {};
  //     const dropdown = document.createElement('select');
  //     dropdown.addEventListener('change', () => {
  //       const selectedOptions = Array.from(dropdown.options).filter(option => option.selected);
  //       const selectedNames = selectedOptions.map(option => option.value);
  //       studentCheckboxes.forEach(checkbox => {
  //         checkbox.checked = selectedNames.includes(checkbox.value);
  //         if (checkbox.checked) {
  //           selectedStudents[checkbox.value] = true;
  //         } else {
  //           delete selectedStudents[checkbox.value];
  //         }
  //       });
  //     });
  //     const studentCheckboxes = studentNames.map(name => {
  //       const checkbox = document.createElement('input');
  //       checkbox.type = 'checkbox';
  //       checkbox.value = name;
  //       checkbox.addEventListener('change', event => {
  //         if (event.target.checked) {
  //           selectedStudents[name] = true;
  //         } else {
  //           delete selectedStudents[name];
  //         }
  //         const selectedNames = Object.keys(selectedStudents);
  //         const selectedOptions = Array.from(dropdown.options).filter(option => selectedNames.includes(option.value));
  //         selectedOptions.forEach(option => option.selected = true);
  //       });
  //       const label = document.createElement('label');
  //       label.textContent = name;
  //       label.insertBefore(checkbox, label.firstChild);
  //       document.body.appendChild(label);
  //       return checkbox;
  //     });
  //     const button = document.createElement('button');
  //     button.style.display = 'none';
  //     const filteredStudents = new Promise(resolve => {
  //       const filterData = () => {
  //         const filteredData = studentData.filter(
  //             student => selectedStudents[student.first_name + ' ' + student.last_name]
  //         );
  //         resolve(filteredData);
  //       };
  //       dropdown.addEventListener('change', filterData);
  //       studentCheckboxes.forEach(checkbox => checkbox.addEventListener('change', filterData));
  //     });
  //     document.body.appendChild(dropdown);
  //     document.body.appendChild(button);
  //     return filteredStudents;
  //   },
  // };

  const fileChangeHandler = (event) => {
    let file = event.target.files[0];
    const renamedFile = new File([file], currentCourseStudents.course_id + '.csv', {
      type: file.type,
    });
    csvFormData.set('csv_file', renamedFile);
  };

  return (
      <div>
        <div className="RosterPage">
          <div id="roster">
            <div className={classes.container}>
              {currentCourseStudents.length > 0 ? (
                  <div>
                    <table className={classes.table} cellSpacing={0}>
                      <thead>
                      <tr>
                        <th className={classes.tableHeader}>Name</th>
                        <th className={classes.tableHeader}>Laker Net ID</th>
                        <th className={classes.tableHeader}>Major</th>
                        <th className={classes.tableHeader}>Team</th>
                        <th className={classes.tableHeader}>Actions</th>
                        <th className={classes.tableHeader}></th>
                      </tr>
                      </thead>
                      <tbody>
                      {currentCourseStudents.map(
                          (student) =>
                              student && (
                                  <tr key={uuid()}>
                                    <th className="rosterComp">
                                      {student.first_name
                                          ? student.first_name + ' ' + student.last_name
                                          : ''}
                                    </th>
                                    <th className="rosterComp">{student.student_id}</th>
                                    <th className="rosterComp">Computer Science</th>
                                    <th className="rosterComp">
                                      {student.team_id !== null ? student.team_id : ''}
                                    </th>
                                    <th className="rosterComp">
                                      <svg className={'bulk-download-icon-default'} alt={'Bulk Download For Student'}
                                           style={{ cursor: 'pointer' }} onClick={() => onRosterClick(student.student_id)}>>
                                      </svg>
                                    </th>
                                    <th className="rosterComp">
                                      <div className="crossMark-wrapper">
                                        <div
                                            onClick={() => deleteStudent(student)}
                                            className="crossMark"
                                        >
                                          X
                                        </div>
                                        <div
                                            onClick={() => editStudent(student)}
                                            className="crossMark"
                                        >
                                          X
                                        </div>
                                        <div className="ecc-file-upload">
                                          <label>
                                            {' '}
                                            <span className="inter-20-bold"> Roster Upload </span>{' '}
                                          </label>
                                          <input
                                              onChange={fileChangeHandler}
                                              type="file"
                                              name="course_csv"
                                              accept=".csv"/>
                                        </div>

                                      </div>
                                    </th>
                                  </tr>
                              )
                      )}
                      </tbody>
                    </table>
                  </div>
              ) : (
                  <div className="no-student-container">
                    <div className="no-student-wrapper">
                      <div className="no-student-img-wrapper">
                        <img
                            className="no-student-img"
                            src={noStudent}
                            alt="no_students"
                            onClick={() => {
                              setTrue()
                            }}
                        />
                      </div>
                      <div className="no-student-header">No Students Added</div>
                      <div className="no-student-description">
                        Click the plus button to add a student
                      </div>
                    </div>
                  </div>
              )}
              {show ? (
                  addStudent()
              ) : (
                  <div className="plus-button-container">
                    <div className="plus-button-wrapper">
                      <CgAdd
                          onClick={() => setTrue()}
                          className="plus-button"
                          size="50px"
                          color="#4a7dfc"
                      />
                    </div>
                  </div>
              )}
            </div>
          </div>
        </div>
      </div>
  )
}

export default ProfessorRosterComponent

 */
