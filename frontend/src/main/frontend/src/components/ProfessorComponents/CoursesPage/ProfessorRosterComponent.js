import { useEffect, useState } from 'react'
import axios from 'axios'
import '../../styles/Roster.css'
import '../../../components/NavigationComponents/_ProfessorNavigationComponents.css'
import { useDispatch, useSelector } from 'react-redux'
import {
  getCourseDetailsAsync,
  getCurrentCourseStudentsAsync,
} from '../../../redux/features/courseSlice'
import { useParams } from 'react-router-dom'
import noStudent from '../../../assets/no-student.png'
import { CgAdd } from 'react-icons/cg'
import uuid from 'react-uuid'
import { base64StringToBlob } from 'blob-util'

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

  return (
    <div>
      <div className="RosterPage">
        <div id="roster">
          <div className="roster-wrapper">
            {currentCourseStudents.length > 0 ? (
              <div>
                <table className="rosterTable">
                  <thead>
                  <tr>
                    <th className="rosterHeader">Name</th>
                    <th className="rosterHeader">Laker Net ID</th>
                    <th className="rosterHeader">Major</th>
                    <th className="rosterHeader">Team</th>
                    <th className="rosterHeader">Actions</th>
                    <th className="rosterHeader"></th>
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
