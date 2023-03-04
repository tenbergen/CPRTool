import { useNavigate, useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import axios from 'axios';
import './styles/AssignmentTile.css';
import { getCourseAssignmentsAsync } from '../redux/features/assignmentSlice';

const assignmentUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`;

const AssignmentTile = ({ assignment, submitted }) => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const title =
    assignment.assignment_type === 'peer-review' ? 'Peer Review' : 'Assignment';

  const { role } = useSelector((state) => state.auth);
  const { currentTeamId } = useSelector((state) => state.teams);
  const { courseId } = useParams();
  const link = `/details/${role}/${courseId}/${assignment.assignment_id}`;

  const onFileClick = async () => {
    const fileName =
      assignment.assignment_type === 'peer-review'
        ? assignment.peer_review_rubric
        : assignment.assignment_instructions;
    const assignmentId = assignment.assignment_id;
    const url = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}/assignments/${assignmentId}/download/${fileName}`;
    await axios
      .get(url, { responseType: 'blob' })
      .then((res) => downloadFile(res.data, fileName));
  };

  const downloadFile = (blob, fileName) => {
    const fileURL = URL.createObjectURL(blob);
    const href = document.createElement('a');
    href.href = fileURL;
    href.download = fileName;
    href.click();
  };

  const confirmDelete = async () => {
    let confirmAction = window.confirm(
      'Are you sure to delete this assignment?'
    );
    if (confirmAction) {
      await deleteAssignment();
    }
  };

  const deleteAssignment = async () => {
    const url = `${assignmentUrl}/${courseId}/assignments/${assignment.assignment_id}/remove`;
    await axios.delete(url);
    dispatch(getCourseAssignmentsAsync(courseId));
    alert('Assignment successfully deleted.');
  };

  const onTileClick = () => {
    const tileLink = submitted
      ? role === 'student'
        ? `/details/student/${courseId}/${assignment.assignment_id}/${currentTeamId}/submitted`
        : `${link}/${assignment.team_name}/submitted`
      : role === 'student'
      ? assignment.assignment_type === 'peer-review'
        ? `${link}/peer-review/${assignment.peer_review_team}`
        : `${link}/normal`
      : `${link}`;

    navigate(tileLink);
  };

  return (
    <div>
      <div
        className={
          assignment.assignment_type === 'peer-review'
            ? 'ass-tile ass-tile-yellow'
            : 'ass-tile'
        }
      >
        <div className='outfit-16 ass-tile-title'>
          {' '}
          <span> {title} </span>
        </div>
        <div className='ass-tile-content'>
          <div className='ass-tile-info' onClick={onTileClick}>
            <span className='kumba-27'>
              {submitted ? (
                role === 'professor' ? (
                  <span>
                    {' '}
                    {assignment.team_name} {assignment.assignment_name}{' '}
                    Submission{' '}
                  </span>
                ) : (
                  <span> {assignment.assigment_name} </span>
                )
              ) : assignment.assignment_type === 'peer-review' ? (
                <span>
                  {' '}
                  {assignment.assignment_name} <br /> (Team{' '}
                  {assignment.peer_review_team}){' '}
                </span>
              ) : (
                <span> {assignment.assignment_name} </span>
              )}
            </span>
            <span className='kumba-25'>
              {submitted
                ? assignment.grade === -1
                  ? 'Pending'
                  : assignment.grade
                : assignment.due_date}
            </span>
          </div>
          {!submitted && (
            <div className='ass-tile-links'>
              <span className='outfit-16 ass-tile-files' onClick={onFileClick}>
                {assignment.assignment_type === 'peer-review'
                  ? assignment.peer_review_rubric
                  : assignment.assignment_instructions}
              </span>
              {role === 'professor' ? (
                <span
                  className='ass-tile-delete outfit-16'
                  onClick={confirmDelete}
                >
                  {' '}
                  Delete assignment{' '}
                </span>
              ) : null}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default AssignmentTile;
