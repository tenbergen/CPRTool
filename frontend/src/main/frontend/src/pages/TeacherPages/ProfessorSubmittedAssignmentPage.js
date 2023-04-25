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
  const [reviewers, setReviewers] = useState([]);
  const [reviews, setReviews] = useState([]);
  const [teamSubmission, setTeamSubmission] = useState([]);


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

  useEffect(() => {
    const fetchReviewers = async () => {
      const data = await getReviewers(courseId, teamId, assignmentId);
      setReviewers(data);
    };

    fetchReviewers();
  }, [courseId, teamId, assignmentId]);

  useEffect( () => {
    const fetchReviews = async () => {
      const data = await getReviews(courseId, teamId, assignmentId);
      setReviewers(data);
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

  const getReviewers = async (courseId, teamId, assignmentId) => {
    return await axios
        .get(`${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/peer-review-team-reviewers/${teamId}`)
        .then(r => {
          console.log(r);
          return r.data;
        })
        .catch((e) => {
          console.error(e)
          alert('Error getting reviewers')
        });
  }

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
    console.log(teamSubmission)
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

  const getReviewSubmission = team => {
    reviews.map( (t) => {
      if(t.key === team){
        console.log(t);
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
                        <table className="rosterTable">
                          <thead>
                            <tr>
                              <th className="rosterHeader">Team Name</th>
                              <th className="rosterHeader">Given Score</th>
                              <th className="rosterHeader">Download Feedback</th>
                            </tr>
                          </thead>
                          <tbody>
                          {reviewers.map((team) => (
                              team && (
                                  <tr key={uuid()}>
                                    <th className="rosterComp">{team}</th>
                                    <th className="rosterComp">{getReviewSubmission(team).data.grade}</th>
                                    <th className="rosterComp">
                                      <svg
                                          className={'bulk-download-icon-default'}
                                          alt={'Bulk Download For Student'}
                                          style={{ cursor: 'pointer' }}
                                      ></svg>
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
