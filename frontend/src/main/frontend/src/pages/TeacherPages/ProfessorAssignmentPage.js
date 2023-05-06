import { useEffect, useState } from 'react';
import './styles/ProfessorCourseStyle.css';
import { useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import {getCourseDetailsAsync, getCoursesAsync} from '../../redux/features/courseSlice';
import GradeAssBarComponent from '../../components/GradeAssBarComponent';
import ProfessorAllSubmissionsComponent from '../../components/ProfessorComponents/AssignmentPage/ProfessorAllSubmissionsComponent';
import ProfessorEditAssignmentComponent from '../../components/ProfessorComponents/AssignmentPage/ProfessorEditAssignmentComponent';
import { getAssignmentDetailsAsync } from '../../redux/features/assignmentSlice';
import Loader from '../../components/LoaderComponenets/Loader';
import uuid from 'react-uuid';
import HeaderBar from "../../components/HeaderBar/HeaderBar";
import NavigationContainerComponent from "../../components/NavigationComponents/NavigationContainerComponent";

const AssComponent = ({ active, component, onClick }) => {
  return (
    <p
      onClick={onClick}
      className={
        active
          ? 'inter-24-medium scp-component-link-clicked'
          : 'inter-24-medium scp-component-link'
      }
    >
      {component}
    </p>
  );
};



function ProfessorAssignmentPage() {
  let dispatch = useDispatch();

  const courseParse = window.location.pathname;
  const courseId = courseParse.split("/")[2];

  let { assignmentId } = useParams();
  const { currentCourseLoaded } = useSelector((state) => state.courses);

  const components = ['All Submissions', 'Needs Grading', 'Edit'];
  const [chosen, setChosen] = useState('All Submissions');

  useEffect(() => {
    dispatch(getCoursesAsync());
    dispatch(getCourseDetailsAsync(courseId));
  }, [courseId, dispatch]);
  const onComponentClick = (component) => {
    dispatch(getAssignmentDetailsAsync({ courseId, assignmentId }));
    setChosen(component);
  };

  return (
    <div>
      {currentCourseLoaded ? (
          <div className="page-container">
            <HeaderBar/>
            <div className='scp-container'>
              <NavigationContainerComponent/>
              <div className='scp-component'>
                <div className='scp-component-links'>
                  {components.map(
                      (t) =>
                          t && (
                              <AssComponent
                                  key={uuid()}
                                  component={t}
                                  active={t === chosen}
                                  onClick={() => onComponentClick(t)}
                              />
                          )
                  )}
                </div>
                <div>
                  {chosen === 'All Submissions' && (
                      <ProfessorAllSubmissionsComponent />
                  )}
                  {chosen === 'Needs Grading' && (
                      <ProfessorAllSubmissionsComponent />
                  )}
                  {chosen === 'Edit' && <ProfessorEditAssignmentComponent />}
                </div>
              </div>
            </div>
          </div>
      ) : (
        <Loader />
      )}
    </div>
  );
}

export default ProfessorAssignmentPage;
