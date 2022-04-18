import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import '../../styles/StudentAss.css';
import { Link, useParams } from 'react-router-dom';
import { getCombinedAssignmentPeerReviews } from '../../../redux/features/assignmentSlice';

const StudentToDoComponent = () => {
  const dispatch = useDispatch();
  const store = useSelector((state) => state);
  const { role } = store.auth;
  const { combinedAssignmentPeerReviews, assignmentsLoaded } =
    store.assignments;
  const { courseId } = useParams();
  const teamId = '1';

  const link = `/details/${role}/${courseId}`;

  useEffect(() => {
    dispatch(getCombinedAssignmentPeerReviews({ courseId, teamId }));
  }, []);

  return (
    <h3>
      {assignmentsLoaded ? (
        <div id='assList'>
          {combinedAssignmentPeerReviews.map((assignment) => (
            <Link
              to={
                assignment.assignment_type === 'peer-review'
                  ? `${link}/${assignment.assignment_id}/peer-review/${assignment.peer_review_team}`
                  : `${link}/${assignment.assignment_id}/normal`
              }
            >
              <li id='assListItem'>
                {assignment.assignment_name +
                  '\n\n' +
                  'Due Date: ' +
                  assignment.final_due_date}
              </li>
            </Link>
          ))}
        </div>
      ) : null}
    </h3>
  );
};

export default StudentToDoComponent;
