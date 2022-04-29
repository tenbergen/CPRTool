import {useDispatch, useSelector} from "react-redux";
import axios from "axios";
import "../../../pages/StudentPages/styles/AssignmentPageStyle.css";
import {useNavigate, useParams} from "react-router-dom";
import React, {useEffect, useState} from "react";
import {getAssignmentDetailsAsync} from "../../../redux/features/assignmentSlice";

const StudentPeerReviewComponent = () => {
    const dispatch = useDispatch()
    const navigate = useNavigate()
    const {courseId, assignmentId, teamId} = useParams()

    const {currentAssignment, currentAssignmentLoaded} = useSelector((state) => state.assignments)
    const {currentTeamId, teamLoaded} = useSelector((state) => state.teams)

    const [grade, setGrade] = useState(undefined)
    const feedbackFileFormData = new FormData()

    const onFeedbackFileHandler = (e) => {
        let file = e.target.files[0];
        feedbackFileFormData.set("file", file)
    }

    useEffect( () => {
        dispatch(getAssignmentDetailsAsync({ courseId, assignmentId}))
    },[])

    const downloadFile = (blob, fileName) => {
        const fileURL = URL.createObjectURL(blob);
        const href = document.createElement("a");
        href.href = fileURL;
        href.download = fileName;
        href.click();
    }

    const onFileClick = async (fileName) => {
        const url = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}/assignments/${assignmentId}/peer-review/download/${fileName}`

        await axios.get(url, {responseType: 'blob'})
            .then(res => downloadFile(res.data, fileName))
    }

    const onTeamFileClick = async () => {
        const url = `${process.env.REACT_APP_URL}/assignments/student/courses/${courseId}/assignments/${assignmentId}/${currentTeamId}/download`

        await axios.get(url, {responseType: 'blob'})
            .then(res => downloadFile(res.data, teamId))

    }

    const handleSubmit = async () => {
        console.log(feedbackFileFormData)
        console.log(grade)

        const submitAssUrl = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/${currentTeamId}/${teamId}/${grade}/upload`

        console.log(submitAssUrl)
        await axios.post(submitAssUrl, feedbackFileFormData)
            .then(res => {
                console.log(res)
                alert("Successfully uploaded peer review")
                navigate(`/details/student/${courseId}`)
            })
            .catch(e => {
                console.log(e.response)
                alert("Error uploading peer review")
            })
        setGrade(undefined)
    }

    return (
        <div>
            { currentAssignmentLoaded && teamLoaded ? (
                <div>
                    <h2 className="kumba-30">Team {teamId} Peer Review</h2>
                    <div className="ap-assignmentArea">
                        <h3>
                            <span className="outfit-25"> Instructions: </span>
                            <span className="outfit-25 span1-ap">Due: {currentAssignment.peer_review_due_date}</span>
                            <br/>
                            <p className="outfit-18">
                                {currentAssignment.peer_review_instructions}
                            </p>
                            <br/>

                            <span className="outfit-25"> Rubric: </span>
                            <span className="outfit-18 p2" onClick={() => onFileClick(currentAssignment.peer_review_rubric)}>
                                {currentAssignment.peer_review_rubric}
                            </span>
                            <br/><br/>

                            <span className="outfit-25"> Template: </span>
                            <span className="outfit-18 p2" onClick={() => onFileClick(currentAssignment.peer_review_template)}>
                                {currentAssignment.peer_review_template}
                            </span>
                            <br/><br/>

                            <span className="outfit-25"> Team Files: </span>
                            <span className="outfit-18 p2" onClick={onTeamFileClick}>
                                {teamId}Files
                            </span>
                            <br/><br/><br/>

                            <div className="input-field">
                                <label> Grade: </label>
                                <input
                                    type="number"
                                    min="0"
                                    name="peer_review_grade"
                                    value={grade}
                                    required
                                    onChange={e => setGrade(e.target.value)}
                                />
                            </div>
                            <br/>

                            <div className="ap-assignment-files">
                                <label className="outfit-25"> Feedback: </label>
                                <input
                                    type="file"
                                    name="assignment_files"
                                    accept=".pdf,.docx"
                                    onChange={onFeedbackFileHandler}
                                    required
                                />
                            </div>
                            <div className="ap-button">
                                <div className="ap-button">
                                    <button className="green-button" onClick={handleSubmit}> Submit</button>
                                </div>
                            </div>
                        </h3>
                    </div>
                </div>) : null
            }
        </div>
    )
}

export default StudentPeerReviewComponent;