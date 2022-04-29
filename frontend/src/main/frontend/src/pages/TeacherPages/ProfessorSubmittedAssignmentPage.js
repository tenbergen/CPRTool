import React, {useEffect} from 'react';
import '../../components/styles/FinalGrade.css';
import {useDispatch, useSelector} from 'react-redux';
import {useParams} from 'react-router-dom';
import axios from 'axios';
import SidebarComponent from "../../components/SidebarComponent";
import SubmittedAssBarComponent from "../../components/SubmittedAssBarComponent";
import {getSubmittedAssignmentDetailsAsync} from "../../redux/features/submittedAssignmentSlice";

function ProfessorSubmittedAssignmentPage() {
    const dispatch = useDispatch();
    const {currentSubmittedAssignment, currentSubmittedAssignmentLoaded} = useSelector((state) => state.submittedAssignments)
    const {courseId, assignmentId, teamId} = useParams();

    useEffect( () => {
        dispatch(getSubmittedAssignmentDetailsAsync({courseId, assignmentId, teamId}))
    }, []);

    const downloadFile = (blob, fileName) => {
        const fileURL = URL.createObjectURL(blob);
        const href = document.createElement('a');
        href.href = fileURL;
        href.download = fileName;
        href.click();
    };

    const onFileClick = async (fileName) => {
        const url = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}/assignments/${assignmentId}/peer-review/download/${fileName}`

        await axios.get(url, {responseType: 'blob'})
            .then((res) => downloadFile(res.data, fileName));
    };

    const onFeedBackClick = async (teamName, fileName) => {
        const url = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/${teamName}/${teamId}/download`;

        await axios.get(url, {responseType: 'blob'})
            .then((res) => downloadFile(res.data, fileName));
    };

    return (
        <div>
            <div className='scp-parent'>
                <SidebarComponent/>
                <div className='scp-container'>
                    <SubmittedAssBarComponent/>
                    <div className='scp-component'>
                        <div>
                            {currentSubmittedAssignmentLoaded ?
                                <div className="sac-parent">
                                    <h2 className="assignment-name">{currentSubmittedAssignment.assignment_name}</h2>
                                    <div className="sac-content">
                                        <div>
                                            <span className="sac-title"> Instructions </span>
                                            <span className="sac-date sac-title">Due Date: {currentSubmittedAssignment.due_date}</span>
                                            <br/>
                                            <p>
                                                <span className="sac-text"> {currentSubmittedAssignment.instructions} </span>
                                            </p>
                                        </div>
                                        <br/>

                                        <div>
                                            <div className="ap-assignment-files">
                                                <span className="sac-title"> Rubric: </span>
                                                <span
                                                    className="sac-filename"
                                                    onClick={() => onFileClick(currentSubmittedAssignment.peer_review_rubric)}>
                                                        {currentSubmittedAssignment.peer_review_rubric}
                                                </span>
                                            </div>

                                            <div className="ap-assignment-files">
                                                <span className="sac-title">Template:</span>
                                                <span
                                                    className="sac-filename"
                                                    onClick={() => onFileClick(currentSubmittedAssignment.peer_review_template)}>
                                                    {currentSubmittedAssignment.peer_review_template}
                                                </span>
                                            </div>

                                            <div className="ap-assignment-files">
                                                <span className="sac-title"> Team Files: </span>
                                                <span
                                                    className="sac-filename"
                                                    onClick={() => onFeedBackClick(teamId)}>
                                                    {currentSubmittedAssignment.submission_name}
                                                </span>
                                            </div>
                                        </div>
                                        <br/>
                                        <div>
                                            <div>
                                                <span className="sac-title"> Peer reviews: </span>
                                                <div className='peerReviewList'>
                                                    {currentSubmittedAssignment.peer_reviews !== null ?
                                                        currentSubmittedAssignment.peer_reviews.map(peerReview => (
                                                            <li className='psa-peerReviewListItem'>
                                                                <b> {peerReview.reviewed_by} </b>
                                                                <div>
                                                                    <span> {peerReview.grade === -1 ? "Pending" : peerReview.grade} </span>
                                                                    &nbsp;
                                                                    <span
                                                                        className="psa-sac-filename"
                                                                        onClick={() => onFeedBackClick(peerReview.reviewed_by, peerReview.submission_name)}>
                                                                        View feedback
                                                                    </span>
                                                                </div>

                                                            </li>
                                                        )) : null}
                                                </div>
                                            </div>
                                        </div>
                                        <br/><br/>
                                        <div>
                                            <span className="sac-title"> Grade: {currentSubmittedAssignment.grade}</span>
                                        </div>
                                    </div>
                                </div> : null }
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ProfessorSubmittedAssignmentPage;