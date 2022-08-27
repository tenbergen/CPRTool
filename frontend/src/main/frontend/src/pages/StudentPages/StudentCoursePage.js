import { useEffect, useState } from 'react';
import './styles/StudentCourseStyle.css';
import SidebarComponent from '../../components/SidebarComponent';
import StudentTeamComponent from '../../components/StudentComponents/CoursePage/StudentTeamComponent';
import StudentToDoComponent from '../../components/StudentComponents/CoursePage/StudentToDoComponent';
import CourseBarComponent from '../../components/CourseBarComponent';
import StudentSubmittedComponent from '../../components/StudentComponents/CoursePage/StudentSubmittedComponent';
import { useLocation, useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { getCourseDetailsAsync } from '../../redux/features/courseSlice';
import { getCurrentCourseTeamAsync } from '../../redux/features/teamSlice';
import MyTeamComponent from '../../components/StudentComponents/CoursePage/MyTeamComponent';
import uuid from 'react-uuid';

const CourseComponent = ({ active, component, onClick }) => {
  return (
    <p
      onClick={onClick}
      className={
        active
          ? 'kumba-30 scp-component-link-clicked'
          : 'kumba-30 scp-component-link'
      }
    >
      {component}
    </p>
  );
};

function StudentCoursePage() {
  const dispatch = useDispatch();
  const location = useLocation();
  const { courseId } = useParams();
  const { lakerId } = useSelector((state) => state.auth);
  const { currentTeamId, teamLoaded } = useSelector((state) => state.teams);

  const initialState =
    location.state !== null ? location.state.initialComponent : 'To Do';
  const [chosen, setChosen] = useState(initialState);

  const components = ['To Do', 'Submitted', 'My Team'];

  useEffect(() => {
    dispatch(getCourseDetailsAsync(courseId));
    dispatch(getCurrentCourseTeamAsync({ courseId, lakerId }));
  }, [courseId, lakerId, dispatch]);

  return (
    <div>
      <div className='scp-parent'>
        <SidebarComponent />
        <div className='scp-container'>
          <CourseBarComponent title={'Courses'} />
          <div className='scp-component'>
            {/* Not in a team yet */}
            {teamLoaded && currentTeamId === null && <StudentTeamComponent />}

            {/* Already in a team */}
            {teamLoaded && currentTeamId !== null && (
              <div>
                <div className='scp-component-links'>
                  {components.map((t) => (
                    <CourseComponent
                      key={uuid()}
                      component={t}
                      active={t === chosen}
                      onClick={() => setChosen(t)}
                    />
                  ))}
                </div>
                <div>
                  {chosen === 'To Do' && <StudentToDoComponent />}
                  {chosen === 'Submitted' && <StudentSubmittedComponent />}
                  {chosen === 'My Team' && <MyTeamComponent />}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default StudentCoursePage;
