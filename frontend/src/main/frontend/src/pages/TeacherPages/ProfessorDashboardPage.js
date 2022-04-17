import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import SidebarComponent from '../../components/SidebarComponent';
import './styles/ProfessorDashboardStyle.css';
import Loader from '../../components/LoaderComponenets/Loader';
import { useDispatch, useSelector } from 'react-redux';
import {
  getCourseDetailsAsync,
  getCoursesAsync,
} from '../../redux/features/courseSlice';

function ProfessorDashboardPage() {
  const [isLoading, setIsLoading] = useState(false);
  const dispatch = useDispatch();
  const courses = useSelector((state) => state.courses.courses);
  const user = useSelector((state) => state.auth.user_given_name);

  useEffect(() => {
    setIsLoading(true);
    dispatch(getCoursesAsync());
    const timer = setTimeout(() => setIsLoading(false), 900);
  }, []);

  const studentView = () => {
    localStorage.setItem('alt_role', 'student');
    window.location.reload(false);
  };

  const originalView = () => {
    localStorage.removeItem('alt_role');
    window.location.reload(false);
  };

  const onCourseClick = (course) => {
    dispatch(getCourseDetailsAsync(course.course_id));
  };

  return (
    <div>
      {isLoading ? (
        <Loader />
      ) : (
        <div className={'TeacherDashboard'}>
          <SidebarComponent />
          <div id='teacher'>
            <h1>Hello {user}</h1>
            <div>
              <button onClick={studentView} style={{ marginRight: '10px' }}>
                {' '}
                Student View
              </button>
              <button onClick={originalView}> Original View</button>
            </div>
            <div id='proCourseList'>
              {courses.map((course) => (
                <Link
                  to={'/details/professor/' + course.course_id}
                  onClick={() => onCourseClick(course)}
                >
                  <li className='courseListItem'>
                    {course.course_id + '\n\n' + course.course_name}
                  </li>
                </Link>
              ))}
            </div>
            <div id='addClass'>
              <Link to='/create/course'>
                <button id='addButton'>Create new course</button>
              </Link>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default ProfessorDashboardPage;
