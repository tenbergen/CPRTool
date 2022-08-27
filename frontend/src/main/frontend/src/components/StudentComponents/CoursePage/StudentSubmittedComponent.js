import { useDispatch, useSelector } from 'react-redux';
import '../../styles/StudentAss.css';
import { useParams } from 'react-router-dom';
import { useEffect } from 'react';
import { getStudentSubmittedAssignmentsAsync } from '../../../redux/features/submittedAssignmentSlice';
import AssignmentTile from '../../AssignmentTile';
import uuid from 'react-uuid';

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
          {courseSubmittedAssignments.map((assignment) => (
            <AssignmentTile
              key={uuid()}
              assignment={assignment}
              submitted={true}
            />
          ))}
        </div>
      ) : null}
    </h3>
  );
};

export default StudentSubmittedComponent;
