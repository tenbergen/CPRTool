import { useEffect, useState } from 'react'
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
                                      {student.team !== null ? student.team : ''}
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
