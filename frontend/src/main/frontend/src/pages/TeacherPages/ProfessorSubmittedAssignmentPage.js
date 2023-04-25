import React, { useState, useEffect } from 'react';
import './styles/ProfessorSubmittedAssignmentPage.css';
import {useDispatch, useSelector} from 'react-redux';
import {useParams} from 'react-router-dom';
import axios from 'axios';
import {getSubmittedAssignmentDetailsAsync} from '../../redux/features/submittedAssignmentSlice';
import uuid from 'react-uuid';
import {base64StringToBlob} from "blob-util";
import HeaderBar from "../../components/HeaderBar/HeaderBar";
import NavigationContainerComponent from "../../components/NavigationComponents/NavigationContainerComponent";


function ProfessorSubmittedAssignmentPage() {
  const dispatch = useDispatch();
  const { currentSubmittedAssignment, currentSubmittedAssignmentLoaded } =
    useSelector((state) => state.submittedAssignments);
  const { courseId, assignmentId, teamId } = useParams();
  const [reviews, setReviews] = useState([]);
  const [teamSubmission, setTeamSubmission] = useState('')

  const getReviews = async (courseId, teamId, assignmentId) => {

    const currentTeam = await axios
        .get(`${process.env.REACT_APP_URL}/teams/team/${courseId}/get/${teamId}`)
        .then(r => {
          return r.data;
        })

    return await axios
        .get(`${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/reviews-of/${currentTeam.team_lead}`)
        .then(r => {
          return r.data;
        });
  }

  const getTeamSubmission = async (courseId, teamId, assignmentId) => {

    return await axios
        .get(`${process.env.REACT_APP_URL}/assignments/student/${courseId}/${assignmentId}/submissions`)
        .then(r => {
          return r.data.find(e => e.team_name === teamId);
        })
  }

  useEffect( () => {
    const fetchReviews = async () => {
      const data = await getReviews(courseId, teamId, assignmentId);
      setReviews(data);
    }

    fetchReviews();
  }, [courseId, teamId, assignmentId]);

  useEffect( () => {
    const fetchTeamSubmission = async () => {
      const data = await getTeamSubmission(courseId, teamId, assignmentId);
      setTeamSubmission(data);
    }

    fetchTeamSubmission();
  }, [courseId, teamId, assignmentId]);

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

  const onTemplateClick = async () => {
    const templateFileName = currentSubmittedAssignment.peer_review_template_name;
    if(templateFileName.endsWith(".pdf")){
      downloadFile(new Blob([Uint8Array.from(currentSubmittedAssignment.peer_review_template_data.data)], {type: 'application/pdf'}), templateFileName)
    }else if(templateFileName.endsWith(".docx")){
      downloadFile(new Blob([Uint8Array.from(currentSubmittedAssignment.peer_review_template_data.data)], {type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'}), templateFileName)
    }else{
      downloadFile(new Blob([Uint8Array.from(currentSubmittedAssignment.peer_review_template_data.data)], {type: 'application/zip'}), templateFileName)
    }
  };

  const onRubricFileClick = async () => {
    const rubricFileName = currentSubmittedAssignment.rubric_name;
    if(rubricFileName.endsWith(".pdf")){
      downloadFile(new Blob([Uint8Array.from(currentSubmittedAssignment.rubric_data.data)], {type: 'application/pdf'}), rubricFileName)
    }else if(rubricFileName.endsWith(".docx")){
      downloadFile(new Blob([Uint8Array.from(currentSubmittedAssignment.rubric_data.data)], {type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'}), rubricFileName)
    }else{
      downloadFile(new Blob([Uint8Array.from(currentSubmittedAssignment.rubric_data.data)], {type: 'application/zip'}), rubricFileName)
    }
  };

  const onTeamFileClick = async () => {
    if (!teamSubmission || !teamSubmission.submission_name) {
      console.error('teamSubmission or teamSubmission.submission_name is undefined');
      return;
    }
    const teamFileName = teamSubmission.submission_name;
    if(teamFileName.endsWith(".pdf")){
      downloadFile(new Blob([Uint8Array.from(currentSubmittedAssignment.submission_data.data)], {type: 'application/pdf'}), teamFileName)
    }else if(teamFileName.endsWith(".docx")){
      downloadFile(new Blob([Uint8Array.from(currentSubmittedAssignment.submission_data.data)], {type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'}), teamFileName)
    }else{
      downloadFile(new Blob([Uint8Array.from(currentSubmittedAssignment.submission_data.data)], {type: 'application/zip'}), teamFileName)
    }
  }

  function onDownloadButtonClick(reviewSubmission){
    if(!reviewSubmission || reviewSubmission === ''){
      console.error('no review submission found!');
      return;
    }
    const reviewFileName = reviewSubmission.submission_name;
    if(reviewFileName.endsWith(".pdf")){
      downloadFile(new Blob([Uint8Array.from(reviewSubmission.submission_data.data)], {type: 'application/pdf'}), reviewFileName)
    }else if(reviewFileName.endsWith(".docx")){
      downloadFile(new Blob([Uint8Array.from(reviewSubmission.submission_data.data)], {type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'}), reviewFileName)
    }else{
      downloadFile(new Blob([Uint8Array.from(reviewSubmission.submission_data.data)], {type: 'application/zip'}), reviewFileName)
    }
  }

  return (
    <div className="page-container">
      <HeaderBar/>
      <div className='scp-container'>
        <NavigationContainerComponent/>
        <div className='scp-component'>
          <div>
            {currentSubmittedAssignmentLoaded ? (
              <div className='sac-parent'>
                <h2 className='team-name'>
                  {teamId} Submission
                </h2>
                <div className='sac-content'>
                  <div className='inter-20-medium-white ass-tile-title'> {' '}
                    <span> {currentSubmittedAssignment.assignment_name} </span>
                  </div>
                  <div className='ass-tile-content' >
                    <span className='inter-24-bold'> {currentSubmittedAssignment.assignment_name} </span>
                    <span className='inter-20-medium span1-ap'>
                      Due: {currentSubmittedAssignment.due_date}
                    </span>
                    <br /> <br /> <br />
                    <p className='inter-20-medium' >Instructions:</p>
                    <p className='inter-16-medium-black'>{currentSubmittedAssignment.instructions}</p>
                    <br />
                    <br />
                    <span className='inter-20-bold'> Rubric: </span>
                    <span className='inter-16-bold-blue p2' >
                      <button className='blue-button-small-pr' onClick={onRubricFileClick} >
                        {' '}
                        Rubric{' '}
                      </button>
                    </span>
                    <span className='inter-16-bold-blue p2' >
                      <button className='blue-button-small-pr' onClick={onTemplateClick} >
                        {' '}
                        Template{' '}
                      </button>
                    </span>
                    <span className='inter-16-bold-blue p2' >
                      <button className='blue-button-small-pr' onClick={onTeamFileClick}>
                        {' '}
                        Team Download{' '}
                      </button>
                    </span>
                  </div>
                  <br />
                  <div>
                    <div>
                      <span className='sac-title'> Peer reviews: </span>
                      <table className="instructorPRTable">
                        <thead>
                          <tr>
                            <th className="instructorPRHeader">Team Name</th>
                            <th className="instructorPRHeader">Given Score</th>
                            <th className="instructorPRHeader">Download Feedback</th>
                          </tr>
                        </thead>
                        <tbody>
                        {reviews.map((review) => (
                            review && (
                                <tr key={uuid()}>
                                  <th className="instructorPRComp">{review.reviewed_by}</th>
                                  <th className="instructorPRComp">{review.grade}</th>
                                  <th className="instructorPRComp">
                                    <svg
                                        className={'bulk-download-icon-default'}
                                        alt={'Bulk Download For Student'}
                                        style={{ cursor: 'pointer' }}
                                    ></svg>
                                    <button onClick={() => onDownloadButtonClick(review)}></button>
                                  </th>
                                </tr>
                            )))}
                        </tbody>
                      </table>
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
  );
}

export default ProfessorSubmittedAssignmentPage;
