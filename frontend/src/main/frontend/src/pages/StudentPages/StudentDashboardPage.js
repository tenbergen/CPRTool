import { useEffect } from 'react';
import { Link } from 'react-router-dom';
import SidebarComponent from '../../components/SidebarComponent';
import './styles/StudentDashboardStyle.css';
import { useDispatch, useSelector } from 'react-redux';
import Loader from '../../components/LoaderComponenets/Loader';
import {
  getCourseDetailsAsync,
  getStudentCoursesAsync,
} from '../../redux/features/courseSlice';
import uuid from 'react-uuid';
import noCourse from '.././../assets/no-course.png';

function StudentDashboardPage() {
  const dispatch = useDispatch();
  const { courses, coursesLoaded } = useSelector((state) => state.courses);
  const { user_given_name, lakerId } = useSelector((state) => state.auth);
  const alt_role = localStorage.getItem('alt_role');

  useEffect(() => {
    dispatch(getStudentCoursesAsync(lakerId));
  }, [dispatch, lakerId]);

  const courseClickHandler = (course) => {
    dispatch(getCourseDetailsAsync(course.course_id));
  };

  const changeView = () => {
    localStorage.removeItem('alt_role');
    window.location.reload(false);
  };

  return (
    <div>
      {!coursesLoaded ? (
        <Loader />
      ) : (
        <div className={'StudentDashboard'}>
          <SidebarComponent />
          <div id='student'>
            <div className='welcome-banner-st'>
              <div className='kumba-35 student-welcome'>
                {' '}
                Hello, {user_given_name}!
              </div>
              {alt_role && alt_role === 'student' ? (
                <div className='btn-wrapper'>
                  <button className='prof-view-btn' onClick={changeView}>
                    Back to Professor View
                  </button>
                </div>
              ) : null}
            </div>

            {courses.length === 0 ? (
              <div className='no-course-wrapper'>
                <img
                  className='no-course-img'
                  src={noCourse}
                  alt='No Courses'
                />
                <div className='no-course-head'>No courses to see here</div>
                <div className='no-course-description'>
                  Inform your instructor to add you to a course
                </div>
              </div>
            ) : (
              <div id='courseList'>
                {courses.map((course) => (
                  <Link
                    key={uuid()}
                    to={'/details/student/' + course.course_id}
                    onClick={() => courseClickHandler(course)}
                  >
                    <li className='courseListItem'>
                      <span className='outfit-16 pdp-coursename'>
                        {course.course_id}
                      </span>
                      <span className='kumba-25'>{course.course_name}</span>
                    </li>
                  </Link>
                ))}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}

export default StudentDashboardPage;
