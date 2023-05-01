import { useEffect, useState } from 'react';
import './styles/CourseBar.css';
import { useDispatch, useSelector } from 'react-redux';
import {
  getCourseDetailsAsync,
  getCoursesAsync,
  getCurrentCourseStudentsAsync,
  getStudentCoursesAsync,
} from '../redux/features/courseSlice';
import {Link, useParams} from 'react-router-dom';
import { setUserInformation } from '../redux/features/authSlice';
import {
  getCombinedAssignmentPeerReviews,
  getCourseAssignmentsAsync,
} from '../redux/features/assignmentSlice';
import { getCurrentCourseTeamAsync } from '../redux/features/teamSlice';
import uuid from 'react-uuid';
import LogoutButton from './GlobalComponents/LogoutButton'

const CourseBarLink = ({ active, course, onClick }) => {
  const role = useSelector((state) => state.auth.role);

  return (
    <Link to={`/${role}/${course.course_id}`} onClick={onClick}>
      <div className={active ? 'clickedStyle' : 'normalStyle'}>
        <div className='colorForTable' />
        <div className='course_info'>
          <p className='inter-20-extralight course_id'>
            {' '}
            {course.abbreviation}-{course.course_section}{' '}
          </p>
          <p className='inter-24-bold course_text'> {course.course_name} </p>
        </div>
      </div>
    </Link>
  );
};

// const AdminButton = ({ active, course, onClick }) => {
//   const role = useSelector((state) => state.auth.role);

//   return (
//     <Link to={`/details/${role}/${course.course_id}`} onClick={onClick}>
//       <div className={active ? 'clickedStyle' : 'normalStyle'}>
//         <div className='colorForTable' />
//         <div className='course_info'>
//           <p className='inter-24-bold course_text'> {"Admin Page"} </p>
//         </div>
//       </div>
//     </Link>
//   );
// };

const CourseBarComponent = ({ title }) => {
  const dispatch = useDispatch();
  const { role, lakerId, dataLoaded } = useSelector((state) => state.auth);
  const { courses } = useSelector((state) => state.courses);
  const { courseId } = useParams();
  const [chosen, setChosen] = useState(courseId);
  const teamId = '1';

  useEffect(() => {
    dispatch(setUserInformation());
    dataLoaded && role === 'professor'
      ? dispatch(getCoursesAsync())
      : dispatch(getStudentCoursesAsync(lakerId));
  }, [lakerId, dataLoaded, role, dispatch]);

  const onCourseClick = (course) => {
    const courseId = course.course_id;
    setChosen(courseId);
    dispatch(getCourseDetailsAsync(courseId));
    dispatch(getCurrentCourseStudentsAsync(courseId));
    dispatch(getCourseAssignmentsAsync(courseId));
    dispatch(getCurrentCourseTeamAsync({ courseId, lakerId }));
    role === 'professor'
      ? dispatch(getCourseAssignmentsAsync(courseId))
      : dispatch(
          getCombinedAssignmentPeerReviews({
            courseId,
            teamId,
          })
        );
  };

  return (
    <div className='cbc-parent'>
      <h2 className='inter-36-bold'> {title} </h2>
      {courses.length < 1 ? (
        <p className='inter-16' style={{ marginLeft: '10%' }}>
          {' '}
          Create your first course
        </p>
      ) : null}

      <div className='cbc-courses'>
        <div>
          {courses.map(
            (course) =>
              course && (
                <CourseBarLink
                  key={uuid()}
                  onClick={() => onCourseClick(course)}
                  active={course.course_id === chosen}
                  course={course}
                />
              )
          )}
        </div>
      </div>
      <LogoutButton/>
    </div>
  );
};

export default CourseBarComponent;
