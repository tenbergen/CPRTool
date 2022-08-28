import { useDispatch, useSelector } from 'react-redux';
import '../../styles/StudentAss.css';
import { useParams } from 'react-router-dom';
import { useEffect } from 'react';
import { getStudentSubmittedAssignmentsAsync } from '../../../redux/features/submittedAssignmentSlice';
import AssignmentTile from '../../AssignmentTile';
import uuid from 'react-uuid';
import noData from '../../../assets/no-data.png';

const StudentSubmittedComponent = () => {
  const dispatch = useDispatch();
  const { courseSubmittedAssignments, assignmentsLoaded } = useSelector(
    (state) => state.submittedAssignments
  );
  const { lakerId } = useSelector((state) => state.auth);
  const { courseId } = useParams();

  useEffect(() => {
    dispatch(getStudentSubmittedAssignmentsAsync({ courseId, lakerId }));
  }, [courseId, lakerId, dispatch]);

  return (
    <h3>
      {assignmentsLoaded ? (
        <div id='assList'>
          {courseSubmittedAssignments.length === 0 ? (
            <div className='no-todo-wrapper'>
              <img className='no-todo-img' src={noData} alt='No Todo' />
              <div className='no-todo-head'>No submissions to show here</div>
            </div>
          ) : (
            <div>
              {courseSubmittedAssignments.map((assignment) => (
                <AssignmentTile
                  key={uuid()}
                  assignment={assignment}
                  submitted={true}
                />
              ))}
            </div>
          )}
        </div>
      ) : null}
    </h3>
  );
};

export default StudentSubmittedComponent;
