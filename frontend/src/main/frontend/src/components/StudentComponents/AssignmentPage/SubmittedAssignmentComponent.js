import axios from 'axios';
import { useParams } from 'react-router-dom';
import '../../styles/SubmittedAssignmentComponent.css';
import uuid from 'react-uuid';

const SubmittedAssignmentComponent = ({ currentSubmittedAssignment }) => {
  const { courseId, assignmentId, teamId } = useParams();

  const onAssignmentFileClick = async (filename) => {
    const url = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}/assignments/${assignmentId}/peer-review/download/${filename}`;

    await axios
      .get(url, { responseType: 'blob' })
      .then((res) => downloadFile(res.data, filename))
      .catch((e) => {
        alert(`Error : ${e.response.data}`);
      });
  };

  const onTeamFileClick = async () => {
    const url = `${process.env.REACT_APP_URL}/assignments/student/courses/${courseId}/assignments/${assignmentId}/${teamId}/download`;

    await axios
      .get(url, { responseType: 'blob' })
      .then((res) =>
        downloadFile(res.data, currentSubmittedAssignment.submission_name)
      )
      .catch((e) => {
        alert(`Error : ${e.response.data}`);
      });
  };

  const onFeedbackClick = async (peerReview) => {
    const srcTeamName = peerReview.reviewed_by;
    const url = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/${srcTeamName}/${teamId}/download`;

    await axios
      .get(url, { responseType: 'blob' })
      .then((res) => downloadFile(res.data, peerReview.submission_name))
      .catch((e) => {
        alert(`Error : ${e.response.data}`);
      });
  };

  const downloadFile = (blob, fileName) => {
    const fileURL = URL.createObjectURL(blob);
    const href = document.createElement('a');
    href.href = fileURL;
    href.download = fileName;
    href.click();
  };

  return (
    <div>
      <div className='sac-parent'>
        <h2 className='assignment-name'>
          {currentSubmittedAssignment.assignment_name}
        </h2>
        <div className='sac-content'>
          <div>
            <span className='sac-title'> Instructions </span>
            <span className='sac-date sac-title'>
              Due Date: {currentSubmittedAssignment.due_date}
            </span>
            <br />
            <p>
              <span className='sac-text'>
                {' '}
                {currentSubmittedAssignment.instructions}{' '}
              </span>
            </p>
          </div>
          <br />

          <div>
            <div className='ap-assignment-files'>
              <span className='sac-title'> Rubric: </span>
              <span
                className='sac-filename'
                onClick={() =>
                  onAssignmentFileClick(
                    currentSubmittedAssignment.peer_review_rubric
                  )
                }
              >
                {currentSubmittedAssignment.peer_review_rubric}
              </span>
            </div>

            <div className='ap-assignment-files'>
              <span className='sac-title'>Template:</span>
              <span
                className='sac-filename'
                onClick={() =>
                  onAssignmentFileClick(
                    currentSubmittedAssignment.peer_review_template
                  )
                }
              >
                {currentSubmittedAssignment.peer_review_template}
              </span>
            </div>

            <div className='ap-assignment-files'>
              <span className='sac-title'>Team Files:</span>
              <span className='sac-filename' onClick={onTeamFileClick}>
                {currentSubmittedAssignment.submission_name}
              </span>
            </div>
          </div>
          <br />
          <div>
            <div>
              <span className='sac-title'> Peer reviews: </span>
              <div className='peerReviewList'>
                {currentSubmittedAssignment.peer_reviews !== null
                  ? currentSubmittedAssignment.peer_reviews.map(
                      (peerReview) => (
                        <li key={uuid()} className='peerReviewListItem'>
                          <b>
                            {' '}
                            {peerReview.grade === -1
                              ? 'Pending'
                              : peerReview.grade}
                          </b>
                          <span
                            className='sac-filename'
                            onClick={() => onFeedbackClick(peerReview)}
                          >
                            View feedback
                          </span>
                        </li>
                      )
                    )
                  : null}
              </div>
            </div>
          </div>
          <br />
          <br />
          <div>
            <span className='sac-title'>
              {' '}
              Grade: {currentSubmittedAssignment.grade}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SubmittedAssignmentComponent;
