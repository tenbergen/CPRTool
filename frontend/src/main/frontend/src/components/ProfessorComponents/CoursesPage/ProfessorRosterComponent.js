import { useEffect, useState } from 'react';
import axios from 'axios';
import '../../styles/Roster.css';
import { useDispatch, useSelector } from 'react-redux';
import {
  getCourseDetailsAsync,
  getCurrentCourseStudentsAsync,
} from '../../../redux/features/courseSlice';
import { useParams } from 'react-router-dom';
import noStudent from '../../../assets/no-student.png';
import { CgAdd } from 'react-icons/cg';
import uuid from 'react-uuid';

const ProfessorRosterComponent = () => {
  const dispatch = useDispatch();
  const { courseId } = useParams();
  const url = `${process.env.REACT_APP_URL}/manage/professor/courses`;
  const { currentCourseStudents } = useSelector((state) => state.courses);

  useEffect(() => {
    dispatch(getCurrentCourseStudentsAsync(courseId));
  }, [dispatch, courseId]);

  const [formData, setFormData] = useState({
    Name: '',
    Email: '',
  });

  const { Name, Email } = formData;
  const OnChange = (e) =>
    setFormData({ ...formData, [e.target.name]: e.target.value });

  const handleSubmit = async () => {
    const nameArray = Name.split(' ');
    const first = nameArray[0];
    const last = nameArray[1];
    if (Name === '' || Email === '') {
      alert('Please enter both name and email for the student!');
      return;
    }
    if (nameArray.length < 2) {
      alert('Please enter first and last name!');
      return;
    }
    if (!Email.includes('oswego.edu')) {
      alert('Please enter a valid Oswego email!');
      return;
    }

    const firstLastEmail = first + '-' + last + '-' + Email;
    const addStudentUrl = `${url}/${courseId}/students/${firstLastEmail}/add`;
    await axios
      .post(addStudentUrl)
      .then((res) => {
        console.log(res.data);
        alert('Successfully added student.');
        dispatch(getCourseDetailsAsync(courseId));
      })
      .catch((e) => {
        console.log(e);
        alert('Error adding student.');
      });
    setFalse();
    setFormData({ ...formData, Name: '', Email: '' });
    dispatch(getCurrentCourseStudentsAsync(courseId));
  };

  const deleteStudent = async (Email) => {
    const deleteStudentUrl = `${url}/${courseId}/students/${Email.student_id}/delete`;
    await axios
      .delete(deleteStudentUrl)
      .then((res) => {
        console.log(res);
        alert('Successfully deleted student.');
        dispatch(getCurrentCourseStudentsAsync(courseId));
      })
      .catch((e) => {
        console.log(e);
        alert('Error deleting student.');
      });
    dispatch(getCourseDetailsAsync(courseId));
  };

  const addStudent = () => {
    return (
      <div className='add-student-container'>
        <div className='add-student-wrapper'>
          <label>Name:</label>
          <input
            type='text'
            className='rosterInput'
            name='Name'
            value={Name}
            required
            onChange={(e) => OnChange(e)}
          />
          <label>Email:</label>
          <input
            type='text'
            className='rosterInput'
            name='Email'
            value={Email}
            required
            onChange={(e) => OnChange(e)}
          />
          <button id='addStudentButton' onClick={handleSubmit}>
            Add Student
          </button>
        </div>
      </div>
    );
  };

  const [show, setShow] = useState(false);
  const setTrue = () => {
    setShow(true);
  };
  const setFalse = () => setShow(false);

  return (
    <div>
      <div className='RosterPage'>
        <div id='roster'>
          <div className='roster-wrapper'>
            {currentCourseStudents.length > 0 ? (
              <div>
                <table className='rosterTable'>
                  <thead>
                    <tr>
                      <th className='rosterHeader'>Name</th>
                      <th className='rosterHeader'>Email</th>
                      <th className='rosterHeader'>Team</th>
                      <th className='rosterHeader'></th>
                    </tr>
                  </thead>
                  <tbody>
                    {currentCourseStudents.map(
                      (student) =>
                        student && (
                          <tr key={uuid()}>
                            <th className='rosterComp'>
                              {student.first_name
                                ? student.first_name + ' ' + student.last_name
                                : ''}
                            </th>
                            <th className='rosterComp'>{student.student_id}</th>
                            <th className='rosterComp'>
                              {student.team !== null ? student.team : ''}
                            </th>
                            <th className='rosterComp'>
                              <div className='crossMark-wrapper'>
                                <div
                                  onClick={() => deleteStudent(student)}
                                  className='crossMark'
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
              <div className='no-student-container'>
                <div className='no-student-wrapper'>
                  <div className='no-student-img-wrapper'>
                    <img
                      className='no-student-img'
                      src={noStudent}
                      alt='no_students'
                      onClick={() => {
                        setTrue();
                      }}
                    />
                  </div>
                  <div className='no-student-header'>No Students Added</div>
                  <div className='no-student-description'>
                    Click the plus button to add a student
                  </div>
                </div>
              </div>
            )}
            {show ? (
              addStudent()
            ) : (
              <div className='plus-button-container'>
                <div className='plus-button-wrapper'>
                  <CgAdd
                    onClick={() => setTrue()}
                    className='plus-button'
                    size='50px'
                    color='#6c63ff'
                  />
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfessorRosterComponent;
