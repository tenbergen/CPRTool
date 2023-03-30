import { useEffect, useState } from 'react';
import './styles/TeacherAss.css';
import { useDispatch, useSelector } from 'react-redux';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { getSubmittedAssignmentsAsync } from '../redux/features/submittedAssignmentSlice';
import {
  getAssignmentDetailsAsync,
  getCourseAssignmentsAsync,
} from '../redux/features/assignmentSlice';
import uuid from 'react-uuid';
import LogoutButton from "./GlobalComponents/LogoutButton";

const GradeAssBarLink = ({ active, assignment, onClick }) => {
  const { role } = useSelector((state) => state.auth);
  const normalStyle = { backgroundColor: '#f6f6f6', border: '1px solid lightgrey' };
  const clickedStyle = { backgroundColor: 'white', border: '1px solid lightgrey' };
  const { courseId } = useParams();

  return (
    <Link
      to={`/${role}/${courseId}/${assignment.assignment_id}`}
      onClick={onClick}
    >
      <tr>
        <td style={active ? clickedStyle : normalStyle}>
          <div className='colorForTable' />
          <p className='courseText inter-18-bold'> {assignment.assignment_name} </p>
        </td>
      </tr>
    </Link>
  );
};

const GradeAssBarComponent = () => {
  const dispatch = useDispatch();
  const { courseAssignments } = useSelector((state) => state.assignments);
  const { courseId, assignmentId } = useParams();
  const navigate = useNavigate();

  const [chosen, setChosen] = useState(parseInt(assignmentId));

  useEffect(() => {
    dispatch(getCourseAssignmentsAsync(courseId));
  }, [courseId, dispatch]);

  const onAssClick = (assignment) => {
    setChosen(assignment.assignment_id);
    let assignmentId = assignment.assignment_id;
    dispatch(getSubmittedAssignmentsAsync({ courseId, assignmentId }));
    dispatch(getAssignmentDetailsAsync({ courseId, assignmentId }));
  };

  const onCourseClick = () => {
    navigate(`/professor/${courseId}`);
  };

  return (
    <div className='abc-parent'>
      <div className='abc-title'>
        <h2 className='inter-28-bold'> Assignment </h2>
      </div>
      <div className='inter-18-bold abc-assignments'>
        {courseAssignments.map(
          (assignment) =>
            assignment && (
              <GradeAssBarLink
                key={uuid()}
                onClick={() => onAssClick(assignment)}
                active={assignment.assignment_id === chosen}
                assignment={assignment}
              />
            )
        )}
      </div>
      <LogoutButton/>
    </div>
  );
};

export default GradeAssBarComponent;
