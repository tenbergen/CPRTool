import { useEffect, useState } from 'react';
import './styles/ProfessorCourseStyle.css';
import SidebarComponent from '../../components/SidebarComponent';
import ProfessorRosterComponent from '../../components/ProfessorComponents/CoursesPage/ProfessorRosterComponent';
import { useParams } from 'react-router-dom';
import ProfessorEditCourseComponent from '../../components/ProfessorComponents/CoursesPage/ProfessorEditCourseComponent';
import ProfessorAssignmentComponent from '../../components/ProfessorComponents/CoursesPage/ProfessorAssignmentComponent';
import { useDispatch } from 'react-redux';
import { getCourseDetailsAsync } from '../../redux/features/courseSlice';
import CourseBarComponent from '../../components/CourseBarComponent';
import ProfessorTeamComponent from '../../components/ProfessorComponents/CoursesPage/ProfessorTeamComponent';
import Loader from '../../components/LoaderComponenets/Loader';
import uuid from 'react-uuid';

const CourseComponent = ({ active, component, onClick }) => {
  return (
    <p
      onClick={onClick}
      className={
        active
          ? 'kumba-30 pcp-component-link-clicked'
          : 'kumba-30 pcp-component-link'
      }
    >
      {component}
    </p>
  );
};

function ProfessorCoursePage() {
  const [isLoading, setIsLoading] = useState(false);
  let dispatch = useDispatch();
  let { courseId } = useParams();

  const components = ['Assignments', 'Roster', 'Teams', 'Manage'];
  const [chosen, setChosen] = useState('Assignments');

  useEffect(() => {
    setIsLoading(true);
    dispatch(getCourseDetailsAsync(courseId));
    setTimeout(() => setIsLoading(false), 200);
  }, [dispatch, courseId]);

  return (
    <div>
      {isLoading ? (
        <Loader />
      ) : (
        <div className='pcp-parent'>
          <SidebarComponent />
          <div className='pcp-container'>
            <CourseBarComponent title={'Courses'} />
            <div className='pcp-components'>
              <div className='pcp-component-links'>
                {components.map(
                  (t) =>
                    t && (
                      <CourseComponent
                        key={uuid()}
                        component={t}
                        active={t === chosen}
                        onClick={() => setChosen(t)}
                      />
                    )
                )}
              </div>
              <div>
                {chosen === 'Assignments' && <ProfessorAssignmentComponent />}
                {chosen === 'Roster' && <ProfessorRosterComponent />}
                {chosen === 'Teams' && <ProfessorTeamComponent />}
                {chosen === 'Manage' && <ProfessorEditCourseComponent />}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default ProfessorCoursePage;
