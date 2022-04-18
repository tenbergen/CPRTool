import React from 'react-dom';
import { useEffect, useState } from 'react';
import './styles/StudentCourseStyle.css';
import SidebarComponent from '../../components/SidebarComponent';
import StudentTeamComponent from '../../components/StudentComponents/CoursePage/StudentTeamComponent';
import StudentToDoComponent from '../../components/StudentComponents/CoursePage/StudentToDoComponent';
import CourseBarComponent from '../../components/CourseBarComponent';
import StudentSubmittedComponent from '../../components/StudentComponents/CoursePage/StudentSubmittedComponent';
import { useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { getCourseDetailsAsync } from '../../redux/features/courseSlice';
import axios from 'axios';

const CourseComponent = ({ active, component, onClick }) => {
  return (
    <p
      onClick={onClick}
      className={active ? 'scp-component-link-clicked' : 'scp-component-link'}
    >
      {component}
    </p>
  );
};

function StudentCoursePage() {
  let dispatch = useDispatch();
  let { courseId } = useParams();
  let { lakerId } = useSelector((state) => state.auth);
  const components = ['To Do', 'Submitted'];
  const [chosen, setChosen] = useState('To Do');

  var inTeam = false;
  axios
    .get(`${process.env.REACT_APP_URL}/teams/team/get/${courseId}/${lakerId}`)
    .then((res) => {
      if (res.status != 200) {
        components.unshift('Teams');
        setChosen('Teams');
      }
    });

  useEffect(() => {
    dispatch(getCourseDetailsAsync(courseId));
  }, []);

  return (
    <div>
      <div className='scp-parent'>
        <SidebarComponent />
        <div className='scp-container'>
          <CourseBarComponent />
          <div className='scp-component'>
            <div className='scp-component-links'>
              {components.map((t) => (
                <CourseComponent
                  key={t}
                  component={t}
                  active={t === chosen}
                  onClick={() => setChosen(t)}
                />
              ))}
            </div>
            <div>
              {chosen === 'Teams' && <StudentTeamComponent />}
              {chosen === 'To Do' && <StudentToDoComponent />}
              {chosen === 'Submitted' && <StudentSubmittedComponent />}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default StudentCoursePage;
