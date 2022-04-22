import React from 'react-dom';
import { useEffect, useState } from 'react';
import './styles/ProfessorCourseStyle.css';
import SidebarComponent from '../../components/SidebarComponent';
import { useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { getCourseDetailsAsync } from '../../redux/features/courseSlice';
import GradeAssBarComponent from '../../components/GradeAssBarComponent';
import ProfessorSubmissionsComponent from '../../components/ProfessorComponents/AssignmentPage/ProfessorSubmissionsComponent';
import ProfessorEditAssignmentComponent from '../../components/ProfessorComponents/AssignmentPage/ProfessorEditAssignmentComponent';
import { getAssignmentDetailsAsync } from '../../redux/features/assignmentSlice';

const AssComponent = ({ active, component, onClick }) => {
  return (
    <p
      onClick={onClick}
      className={active ? 'scp-component-link-clicked' : 'scp-component-link'}
    >
      {component}
    </p>
  );
};

function ProfessorAssignmentPage() {
  let dispatch = useDispatch();
  let { courseId, assignmentId } = useParams();
  const isDataLoaded = useSelector(
    (state) => state.courses.currentCourseLoaded
  );

  const components = ['All Submissions', 'Needs Grading', 'Edit'];
  const [chosen, setChosen] = useState('All Submissions');

  useEffect(() => {
    dispatch(getCourseDetailsAsync(courseId));
  }, []);

  const onComponentClick = (component) => {
    dispatch(getAssignmentDetailsAsync({ courseId, assignmentId }));
    setChosen(component);
  };

  return (
    <div>
      {isDataLoaded ? (
        <div className='scp-parent'>
          <SidebarComponent />
          <div className='scp-container'>
            <GradeAssBarComponent />
            <div className='scp-component'>
              <div className='scp-component-links'>
                {components.map((t) => (
                  <AssComponent
                    key={t}
                    component={t}
                    active={t === chosen}
                    onClick={() => onComponentClick(t)}
                  />
                ))}
              </div>
              <div>
                {chosen === 'All Submissions' && (
                  <ProfessorSubmissionsComponent />
                )}
                {chosen === 'Needs Grading' && (
                  <ProfessorSubmissionsComponent />
                )}
                {chosen === 'Edit' && <ProfessorEditAssignmentComponent />}
              </div>
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
}

export default ProfessorAssignmentPage;
