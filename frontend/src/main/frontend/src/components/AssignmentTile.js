import { useNavigate, useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import axios from 'axios';
import './styles/AssignmentTile.css';
import { getCourseAssignmentsAsync } from '../redux/features/assignmentSlice';

const assignmentUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`;

const AssignmentTile = ({ assignment, submitted }) => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  let title = "";
  {assignment.assignment_type === "peer-review" ?
   title = "Peer-Review"
  : title = "Assignment #" + assignment.assignment_id}
  // const title = "Assignment #" + assignment.assignment_id;
  const { role } = useSelector((state) => state.auth);
  const { currentTeamId } = useSelector((state) => state.teams);
  const course = window.location.pathname;
  const courseId = course.split("/")[2];
  const link = `/${role}/${courseId}/${assignment.assignment_id}`;

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
        ? `/student/${courseId}/${assignment.assignment_id}/${currentTeamId}/submitted`
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
                ? 'ass-tile-yellow'
                : 'ass-tile'
          }
      >
        <div className='inter-20-medium-white ass-tile-title'>
          <span> {title} </span>
        </div>
        <div className='ass-tile-content' onClick={onTileClick}>
          <div className='ass-tile-info' >
            <span className='inter-24-bold'>
                {assignment.team_name}
              {assignment.assignment_name}
              <br />

                <span className = 'inter-14-medium-black'>
                    {'Due Date: '}
                  {assignment.assignment_type === 'peer-review' ?
                      assignment.peer_review_due_date :
                      assignment.due_date}
                </span>

            </span>

            <span className='inter-20-medium'>
              {submitted
                  ? assignment.grade === -1
                      ? "Pending PR"
                      : "Graded"
                  : 'Assigned'}

            </span>

          </div>
          {!submitted && (
              <div className='ass-tile-links'>
              <span className='inter-16-bold-blue ass-tile-files' onClick={onFileClick}>
                {assignment.assignment_type === 'peer-review'
                    ? assignment.peer_review_rubric
                    : assignment.assignment_instructions}
              </span>
              </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default AssignmentTile;
