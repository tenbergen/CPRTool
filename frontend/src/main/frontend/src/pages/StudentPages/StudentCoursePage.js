import { useEffect, useState } from 'react';
import './styles/StudentCourseStyle.css';
import SidebarComponent from '../../components/SidebarComponent';
import StudentTeamComponent from '../../components/StudentComponents/CoursePage/StudentTeamComponent';
import StudentToDoComponent from '../../components/StudentComponents/CoursePage/StudentToDoComponent';
import StudentSubmittedComponent from '../../components/StudentComponents/CoursePage/StudentSubmittedComponent';
import { useLocation, useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import {getCourseDetailsAsync, getStudentCoursesAsync} from '../../redux/features/courseSlice';
import { getCurrentCourseTeamAsync } from '../../redux/features/teamSlice';
import MyTeamComponent from '../../components/StudentComponents/CoursePage/MyTeamComponent';
import uuid from 'react-uuid';
import NavigationContainerComponent
  from "../../components/NavigationComponents/NavigationContainerComponent";
import HeaderBar from "../../components/HeaderBar/HeaderBar";

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

function StudentCoursePage({chosen}) {
  const dispatch = useDispatch();
  const location = useLocation();
  const { courseId } = useParams();
  const { lakerId } = useSelector((state) => state.auth);
  const { currentTeamId, teamLoaded } = useSelector((state) => state.teams);

  const [chosenComponent, setChosenComponent] = useState(chosen);

  const components = ['To Do', 'Submitted', 'My Team'];

  useEffect(() => {
    dispatch(getCourseDetailsAsync(courseId));
    dispatch(getCurrentCourseTeamAsync({ courseId, lakerId }));
    dispatch(getStudentCoursesAsync(lakerId))
  }, [courseId, lakerId, dispatch]);

  useEffect(() => {
    setChosenComponent(chosen);
  }, [chosen]);

  return (
    <div className="page-container">
      <HeaderBar/>
      <div className='scp-container'>
        <NavigationContainerComponent/>
        {/*<AssBarComponent />*/}
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
                          active={t === chosenComponent}
                          onClick={() => setChosenComponent(t)}
                      />
                  ))}
                </div>
                <div>
                  {chosenComponent === 'To Do' && <StudentToDoComponent />}
                  {chosenComponent === 'Submitted' && <StudentSubmittedComponent />}
                  {chosenComponent === 'My Team' && <MyTeamComponent />}
                </div>
              </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default StudentCoursePage;
