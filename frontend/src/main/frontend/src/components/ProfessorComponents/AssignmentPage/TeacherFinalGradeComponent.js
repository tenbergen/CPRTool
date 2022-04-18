import React, { useEffect } from 'react';
import '../../styles/FinalGrade.css';
import { useDispatch, useSelector } from 'react-redux';
import { useParams } from 'react-router-dom';
import { getAssignmentDetailsAsync } from '../../../redux/features/assignmentSlice';
import { getPeerReviewFilesAsync } from '../../../redux/features/peerReviewSlice';
import axios from 'axios';

function TeacherFinalGradeComponent() {
  const dispatch = useDispatch();
  const { currentAssignment, currentAssignmentLoaded } = useSelector(
    (state) => state.assignments
  );
  const { courseId, assignmentId } = useParams();
  const { peerReviewFiles, peerReviewTeamFiles } = useSelector(
    (state) => state.peerReviews
  );

  useEffect(() => {
    dispatch(getAssignmentDetailsAsync({ courseId, assignmentId }));
    dispatch(getPeerReviewFilesAsync({ courseId, assignmentId }));
  }, []);

  const onDownloadClick2 = async (fileName) => {
    return `${process.env.REACT_APP_URL}/manage/courses/${courseId}/assignments/${assignmentId}/download/${fileName}`;
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

  return currentAssignmentLoaded ? (
    <div className='ap-component'>
      <h2>{currentAssignment.assignment_name}</h2>
      <div className='ap-assignmentArea'>
        <div className='ap-component-links'>
          <h3>
            {' '}
            Instructions: <br /> <br />
            {currentAssignment.instructions}
          </h3>
          <h3 id='dueDate' className='dueDate'>
            Due Date: {currentAssignment.due_date}
          </h3>
          <div className='ap-teams'>
            <label className='fileLables'>
              {' '}
              <b>
                {' '}
                Rubric:{' '}
                <div onClick={() => onFileClick(peerReviewFiles[0])}>
                  {' '}
                  {peerReviewFiles[0]}{' '}
                </div>{' '}
              </b>{' '}
            </label>
            <br />
            <label className='fileLables'>
              {' '}
              <b>
                {' '}
                Template:{' '}
                <div onClick={() => onFileClick(peerReviewFiles[1])}>
                  {' '}
                  {peerReviewFiles[1]}{' '}
                </div>{' '}
              </b>{' '}
            </label>
            <br />
            <label className='fileLables'>
              {' '}
              <b> Team Files: {/*peerReviewTeamFiles*/}</b>{' '}
            </label>
            <br />
            <label>
              {' '}
              <b> Peer Review: </b>{' '}
            </label>
            <table className='teamTable'>
              <td>
                <div className='colorForTable' />
                <div className='teamName'>Team Testers</div>
              </td>
            </table>
            <div className='cap-assignment-info'>
              <label>
                {' '}
                <b> Grade: </b>{' '}
              </label>
              <input
                type='number'
                min='0'
                name='Grade'
                // value={Grade}
                required
                // onChange={(e) => OnChange(e)}
              />
            </div>
            <div className='cap-assignment-files'>
              <label>
                {' '}
                <b> Feedback: </b>{' '}
              </label>
              <input
                type='file'
                name='Feedback'
                // value={Feedback}
                required
                //onChange={(e) => OnChange(e)}
              />
            </div>
          </div>
          <button className='submitButton'>Submit</button>
        </div>
      </div>
    </div>
  ) : null;
}

export default TeacherFinalGradeComponent;
