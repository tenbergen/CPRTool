import { useEffect, useState } from 'react';
import './styles/ProfessorSubmittedAssignmentPage.css';
import { useDispatch, useSelector } from 'react-redux';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import SubmittedAssBarComponent from '../../components/SubmittedAssBarComponent';
import { getSubmittedAssignmentDetailsAsync } from '../../redux/features/submittedAssignmentSlice';
import uuid from 'react-uuid';
import {base64StringToBlob} from "blob-util";
import HeaderBar from "../../components/HeaderBar/HeaderBar";
import NavigationContainerComponent from "../../components/NavigationComponents/NavigationContainerComponent";

async function initializeData(courseId, teamId, assignmentId) {
  const currentTeam = await axios
      .get(`${process.env.REACT_APP_URL}/teams/team/${courseId}/get/${teamId}`)
      .then(r => {
        return r.data;
      })

  const reviewers = await axios
      .get(`${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/peer-review-team-reviewers/${teamId}`)
      .then(r => {
        return r.data;
      })

  const reviews = await axios
      .get(`${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/reviews-of/${currentTeam.team_lead}`)
      .then(r => {
        return r.data;
      })

  return {currentTeam, reviewers, reviews}
}

function ProfessorSubmittedAssignmentPage() {
  const dispatch = useDispatch();
  const { currentSubmittedAssignment, currentSubmittedAssignmentLoaded } =
    useSelector((state) => state.submittedAssignments);
  const { courseId, assignmentId, teamId } = useParams();
  const [currentTeam, setCurrentTeam] = useState(undefined);
  var [reviewers, setReviewers] = useState({});
  var [reviews, setReviews] = useState({});

  initializeData(courseId, teamId, assignmentId).then(r => {
    setCurrentTeam(r.currentTeam);
    setReviewers(r.reviewers);
    setReviews(r.reviews);
  });
  console.log(reviewers);

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
      downloadFile(new Blob([Uint8Array.from(currentSubmittedAssignment.peer_review_template_data.data)], {type: 'application/pdf'}), fileName)
    }else if(fileName.endsWith(".docx")){
      downloadFile(new Blob([Uint8Array.from(currentSubmittedAssignment.peer_review_template_data.data)], {type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'}), fileName)
    }else{
      downloadFile(new Blob([Uint8Array.from(currentSubmittedAssignment.peer_review_template_data.data)], {type: 'application/zip'}), fileName)
    }
  };

  const onRubricFileClick = async (fileName) => {
    if(fileName.endsWith(".pdf")){
      downloadFile(new Blob([Uint8Array.from(currentSubmittedAssignment.rubric_data.data)], {type: 'application/pdf'}), fileName)
    }else if(fileName.endsWith(".docx")){
      downloadFile(new Blob([Uint8Array.from(currentSubmittedAssignment.rubric_data.data)], {type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'}), fileName)
    }else{
      downloadFile(new Blob([Uint8Array.from(currentSubmittedAssignment.rubric_data.data)], {type: 'application/zip'}), fileName)
    }
  };

  const onTeamFileClick = async (fileName) => {
    if(fileName.endsWith(".pdf")){
      downloadFile(new Blob([Uint8Array.from(currentSubmittedAssignment.submission_data.data)], {type: 'application/pdf'}), fileName)
    }else if(fileName.endsWith(".docx")){
      downloadFile(new Blob([Uint8Array.from(currentSubmittedAssignment.submission_data.data)], {type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'}), fileName)
    }else{
      downloadFile(new Blob([Uint8Array.from(currentSubmittedAssignment.submission_data.data)], {type: 'application/zip'}), fileName)
    }
  }

  const getReviewSubmission = team => {
    reviews.map( (t) => {
      if(t.key === team){
        return t;
      }
    })
    console.error(`no match for ${team}`)
  }

  const prepareFeedbackFile = (feedbackDataName, feedbackData) => {
    var filename = ""
    var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
    var matches = filenameRegex.exec(feedbackDataName);
    if (matches != null && matches[1]) {
      filename = matches[1].replace(/['"]/g, '');
    }
    feedbackData.then((res) => {
      if(filename.endsWith(".pdf")){
        downloadFile(base64StringToBlob(res, 'application/pdf'), filename)
      }else{
        downloadFile(base64StringToBlob(res, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'), filename)
      }
    })
  };


  const onFeedBackClick = async (teamName) => {
    const url = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/${teamName}/${teamId}/download`;

    await axios
        .get(url, { responseType: 'blob' })
        .then((res) => prepareFeedbackFile(res["headers"]["content-disposition"], res.data.text()))
        .catch((e) => {
          alert(`Error : ${e.response.data}`);
        });
  };


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
                        <button className='blue-button-small-pr'>
                          {' '}
                          Team Download{' '}
                        </button>
                      </span>
                    </div>
                    <br />
                    <div>
                      <div>
                        <span className='sac-title'> Peer reviews: </span>
                        <table className="rosterTable">
                          <thead>
                            <tr>
                              <th className="rosterHeader">Team Name</th>
                              <th className="rosterHeader">Given Score</th>
                              <th className="rosterHeader">Download Feedback</th>
                            </tr>
                          </thead>
                          <tbody>
                            {reviewers.map(
                              (team) =>
                                team && (
                                  <tr key={uuid()}>
                                    <th className="rosterComp">
                                      {team}
                                    </th>
                                    <th className="rosterComp">{getReviewSubmission(team).grade}</th>
                                    <th className="rosterComp">
                                      <svg className={'bulk-download-icon-default'} alt={'Bulk Download For Student'}
                                           style={{ cursor: 'pointer' }} >
                                      </svg>
                                    </th>
                                  </tr>
                                )
                          )}
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
