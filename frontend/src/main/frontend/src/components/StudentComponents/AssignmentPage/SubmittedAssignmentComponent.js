import React from "react";
import axios from "axios";
import {useParams} from "react-router-dom";
import '../../styles/SubmittedAssignmentComponent.css'

const SubmittedAssignmentComponent = ({currentAssignmentLoaded, currentSubmittedAssignment}) => {

    const {courseId, assignmentId} = useParams()

    const onAssignmentClick = async (filename) => {
        const url = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}/assignments/${assignmentId}/download/${filename}`

        await axios.get(url, {responseType: 'blob'})
            .then(res => downloadFile(res.data, filename))
    }

    const downloadFile = (blob, fileName) => {
        const fileURL = URL.createObjectURL(blob);
        const href = document.createElement("a");
        href.href = fileURL;
        href.download = fileName;
        href.click();
    }

    return (
        <div>
            {currentAssignmentLoaded ?
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
                                <span className="sac-filename" onClick={onAssignmentClick}>
                                    {currentSubmittedAssignment.peer_review_rubric}
                                </span>
                            </div>

                            <div className="ap-assignment-files">
                                <span className="sac-title">Template:</span>
                                <span className="sac-filename" onClick={onAssignmentClick}>
                                    {currentSubmittedAssignment.peer_review_template}
                                </span>
                            </div>

                            <div className="ap-assignment-files">
                                <span className="sac-title">Team Files:</span>
                                <span className="sac-filename" onClick={onAssignmentClick}>
                                    {currentSubmittedAssignment.team_file}
                                </span>
                            </div>
                        </div>
                        <br/>
                        <div>
                            <div>
                                <span className="sac-title"> Peer reviews: </span>
                                <div className='peerReviewList'>
                                    {currentSubmittedAssignment.peer_reviews.map(peerReview => (
                                        <li className='peerReviewListItem'>
                                            <b> {peerReview.grade === -1 ? "Pending" : peerReview.grade}
                                            </b> <span className="sac-filename"> {peerReview.submission_name} </span>
                                        </li>
                                    ))}
                                </div>
                            </div>
                        </div>
                        <br/><br/>
                        <div>
                            <span className="sac-title"> Grade: {currentSubmittedAssignment.grade}</span>
                        </div>
                    </div>
                </div> : null
            }
        </div>
    )
}

export default SubmittedAssignmentComponent;