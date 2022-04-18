import { useDispatch, useSelector } from 'react-redux';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { getPeerReviewFilesAsync } from '../../../redux/features/peerReviewSlice';

const StudentPeerReviewComponent = () => {
  const dispatch = useDispatch();
  const { currentAssignment } = useSelector((state) => state.assignments);
  const { peerReviewFiles } = useSelector((state) => state.peerReviews);

  const { courseId, assignmentId } = useParams();
  const teamName = 'YeeHaw';
  const [grade, setGrade] = useState();

  const feedbackFileFormData = new FormData();

  useEffect(() => {
    dispatch(getPeerReviewFilesAsync({ courseId, assignmentId }));
  }, []);

  const onFeedbackFileHandler = (e) => {
    let file = e.target.files[0];
    feedbackFileFormData.set('file', file);
  };

  const downloadFile = (blob, fileName) => {
    const fileURL = URL.createObjectURL(blob);
    const href = document.createElement('a');
    href.href = fileURL;
    href.download = fileName;
    href.click();
  };

  const onFileClick = async (fileName) => {
    const url = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}/assignments/${assignmentId}/peer-review/download/${fileName}`;

    await axios
      .get(url, { responseType: 'blob' })
      .then((res) => downloadFile(res.data, fileName));
  };

  return (
    <div>
      <h2>
        {' '}
        {teamName} {currentAssignment.assignment_name} Peer Review{' '}
      </h2>
      <div className='ap-assignmentArea'>
        <div className='ap-component-links'>
          <h3>
            {' '}
            Instructions: <br /> <br /> {currentAssignment.instructions}
            <br /> <br /> <br />
            Rubric:{' '}
            <div onClick={() => onFileClick(peerReviewFiles[0])}>
              {' '}
              {peerReviewFiles[0]}{' '}
            </div>
            <br />
            Template:{' '}
            <div onClick={() => onFileClick(peerReviewFiles[1])}>
              {' '}
              {peerReviewFiles[1]}{' '}
            </div>
            <br />
            Team Files: <div> </div>
            <br />
            <form>
              <div>
                <label>
                  {' '}
                  <b> Grade </b>
                </label>
                <input
                  type='number'
                  min='0'
                  required
                  name='peer_review_grade'
                  value={grade}
                  onChange={(e) => setGrade(e)}
                />
              </div>
              <div>
                <label>
                  {' '}
                  <b> Feedback </b>
                </label>
                <input
                  type='file'
                  accept='.pdf,.zip'
                  required
                  name='peer_review_grade'
                  onChange={onFeedbackFileHandler}
                />
              </div>
            </form>
          </h3>
          <h3> Due Date: {currentAssignment.due_date}</h3>
        </div>
      </div>
    </div>
  );
};

export default StudentPeerReviewComponent;
