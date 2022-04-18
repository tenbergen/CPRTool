import React from 'react-dom';
import { useEffect, useState } from 'react';
import './styles/StudentCourseStyle.css';
import SidebarComponent from '../../components/SidebarComponent';
import StudentToDoComponent from '../../components/StudentComponents/CoursePage/StudentToDoComponent';
import CourseBarComponent from '../../components/CourseBarComponent';
import StudentSubmittedComponent from '../../components/StudentComponents/CoursePage/StudentSubmittedComponent';
import { useParams } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { getCourseDetailsAsync } from '../../redux/features/courseSlice';
import Loader from '../../components/LoaderComponenets/Loader';

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
  const [isLoading, setIsLoading] = useState(false);
  let dispatch = useDispatch();
  let { courseId } = useParams();

  const components = ['To Do', 'Submitted'];
  const [chosen, setChosen] = useState('To Do');

  useEffect(() => {
    setIsLoading(true);
    dispatch(getCourseDetailsAsync(courseId));
    setTimeout(() => setIsLoading(false), 200);
  }, []);

  return (
    <div>
      {isLoading ? (
        <Loader />
      ) : (
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
                {chosen === 'To Do' && <StudentToDoComponent />}
                {chosen === 'Submitted' && <StudentSubmittedComponent />}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default StudentCoursePage;
