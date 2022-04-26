import {useDispatch, useSelector} from "react-redux";
import axios from "axios";
import "../../../pages/StudentPages/styles/AssignmentPageStyle.css";
import {useNavigate, useParams} from "react-router-dom";
import React, {useEffect, useState} from "react";
import {getAssignmentDetailsAsync} from "../../../redux/features/assignmentSlice";

const StudentPeerReviewComponent = () => {
    const dispatch = useDispatch()
    const navigate = useNavigate()
    const {courseId, assignmentId, teamName} = useParams()

    const {currentAssignment, currentAssignmentLoaded} = useSelector((state) => state.assignments)
    const {currentTeamId, teamLoaded} = useSelector((state) => state.teams)

    const destTeamName = currentTeamId

    const [grade, setGrade] = useState(0)
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
        const srcTeamName = currentTeamId
        console.log(srcTeamName)
        const url = `${process.env.REACT_APP_URL}/assignments/student/courses/${courseId}/assignments/${assignmentId}/${currentTeamId}/download`

        await axios.get(url, {responseType: 'blob'})
            .then(res => downloadFile(res.data, teamName))

    }

    const handleSubmit = async () => {
        const srcTeamName = teamName
        console.log(feedbackFileFormData)
        console.log(grade)

        const submitAssUrl = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/${srcTeamName}/${destTeamName}/${grade}/upload`

        await axios.post(submitAssUrl, feedbackFileFormData)
            .then(res => {
                console.log(res)
                alert("Successfully uploaded assignment")
                navigate(`/details/student/${courseId}`)
            })
            .catch(e => {
                console.log(e)
                alert("Error uploading assignment")
            })
    }

    return (
        <div>
            { currentAssignmentLoaded && teamLoaded ? (
                <div>
                    <h2>Team {teamName} {currentAssignment.assignment_name} Peer Review </h2>
                    <div className="ap-assignmentArea">
                        <h3> Instructions:
                            <span className="span1-ap">Due Date: {currentAssignment.peer_review_due_date}</span>
                            <br/>
                            <p>
                                {currentAssignment.peer_review_instructions}
                            </p>
                            <br/><br/>
                            Rubric: <p className={"p2"}
                                onClick={() => onFileClick(currentAssignment.peer_review_rubric)}> {currentAssignment.peer_review_rubric} </p>
                            <br/>
                            Template: <p className={"p2"}
                                onClick={() => onFileClick(currentAssignment.peer_review_template)}> {currentAssignment.peer_review_template} </p>
                            <br/>
                            Team Files: <p className={"p2"}
                                onClick={() => onTeamFileClick(teamName)}> {teamName} File
                            </p>
                            <br/>
                            <div>
                                <div>
                                    <label> <b> Grade: </b></label>
                                    <input
                                        type="number"
                                        min="0"
                                        name="peer_review_grade"
                                        value={grade}
                                        required
                                        onChange={e => setGrade(e.target.value)}
                                    />
                                </div>
                                <br/><br/>
                                <div className="ap-assignment-files">
                                    <label> <b> Feedback: </b></label>
                                    <input
                                        type="file"
                                        accept=".pdf,.docx"
                                        required
                                        name="peer_review_grade"
                                        onChange={onFeedbackFileHandler}
                                    />
                                </div>
                                <div className="ap-button">
                                    <button onClick={handleSubmit}> Submit</button>
                                </div>
                            </div>
                        </h3>
                    </div>
                </div> ): null
            }
        </div>
    )
}

export default StudentPeerReviewComponent;