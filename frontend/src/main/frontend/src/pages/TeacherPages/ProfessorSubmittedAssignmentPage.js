import { useEffect } from 'react';
import '../../components/styles/FinalGrade.css';
import { useDispatch, useSelector } from 'react-redux';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import SidebarComponent from '../../components/SidebarComponent';
import SubmittedAssBarComponent from '../../components/SubmittedAssBarComponent';
import { getSubmittedAssignmentDetailsAsync } from '../../redux/features/submittedAssignmentSlice';
import uuid from 'react-uuid';
function ProfessorSubmittedAssignmentPage() {
  const dispatch = useDispatch();
  const { currentSubmittedAssignment, currentSubmittedAssignmentLoaded } =
    useSelector((state) => state.submittedAssignments);
  const { courseId, assignmentId, teamId } = useParams();

  useEffect(() => {
    dispatch(
      getSubmittedAssignmentDetailsAsync({ courseId, assignmentId, teamId })
    );
  }, [assignmentId, courseId, dispatch, teamId]);

  const downloadFile = (blob, fileName) => {
    const fileURL = URL.createObjectURL(blob);
    const href = document.createElement('a');
    href.href = fileURL;
    href.download = fileName;
    href.click();
  };

  const onTemplateClick = async (fileName) => {
    if(fileName.endsWith(".pdf")){
      downloadFile(new Blob([Uint8Array.from(currentAssignment.peer_review_template_data.data)], {type: 'application/pdf'}), fileName)
    }else if(fileName.endsWith(".docx")){
      downloadFile(new Blob([Uint8Array.from(currentAssignment.peer_review_template_data.data)], {type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'}), fileName)
    }else{
      downloadFile(new Blob([Uint8Array.from(currentAssignment.peer_review_template_data.data)], {type: 'application/zip'}), fileName)
    }
  };

  const onRubricFileClick = async (fileName) => {
    if(fileName.endsWith(".pdf")){
      downloadFile(new Blob([Uint8Array.from(currentAssignment.rubric_data.data)], {type: 'application/pdf'}), fileName)
    }else if(fileName.endsWith(".docx")){
      downloadFile(new Blob([Uint8Array.from(currentAssignment.rubric_data.data)], {type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'}), fileName)
    }else{
      downloadFile(new Blob([Uint8Array.from(currentAssignment.rubric_data.data)], {type: 'application/zip'}), fileName)
    }
  };

  const onTeamFileClick = async () => {
    const url = `${process.env.REACT_APP_URL}/assignments/student/courses/${courseId}/assignments/${assignmentId}/${teamId}/download`;

    await axios
        .get(url, { responseType: 'blob' })
        .then((res) => prepareTeamFile(res["headers"]["content-disposition"], res.data.text()));
  }


  return (
    <div>
      <div className='scp-parent'>
        <SidebarComponent />
        <div className='scp-container'>
          <SubmittedAssBarComponent />
          <div className='scp-component'>
            <div>
              {currentSubmittedAssignmentLoaded ? (
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
                            onRubricFileClick(currentSubmittedAssignment.rubric_name)
                          }
                        >
                          onRubricFileClick(currentSubmittedAssignment.rubric_name)
                        </span>
                      </div>

                      <div className='ap-assignment-files'>
                        <span className='sac-title'>Template:</span>
                        <span
                          className='sac-filename'
                          onClick={() =>
                            onTemplateClick(currentSubmittedAssignment.peer_review_template_name)
                          }
                        >
                          {currentSubmittedAssignment.peer_review_template_name}
                        </span>
                      </div>

                      <div className='ap-assignment-files'>
                        <span className='sac-title'> Team Files: </span>
                        <span
                          className='sac-filename'
                          onClick={() => onTeamFileClick()}
                        >
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
                                (peerReview) =>
                                  peerReview && (
                                    <li
                                      key={uuid()}
                                      className='psa-peerReviewListItem'
                                    >
                                      <b> {peerReview.reviewed_by} </b>
                                      <div>
                                        <span>
                                          {' '}
                                          {peerReview.grade === -1
                                            ? 'Pending'
                                            : peerReview.grade}{' '}
                                        </span>
                                        &nbsp;
                                        <span
                                          className='psa-sac-filename'
                                          onClick={() =>
                                            onFeedBackClick(
                                              peerReview.reviewed_by,
                                              peerReview.submission_name
                                            )
                                          }
                                        >
                                          View feedback
                                        </span>
                                      </div>
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
              ) : null}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ProfessorSubmittedAssignmentPage;
