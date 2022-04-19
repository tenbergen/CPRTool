import React, {useEffect} from 'react';
import "../../../pages/StudentPages/styles/AssignmentPageStyle.css";
import {useDispatch, useSelector} from "react-redux";
import {useParams} from "react-router-dom";
import axios from "axios";
import {getCombinedAssignmentPeerReviews} from "../../../redux/features/assignmentSlice";
import {getCurrentCourseTeamAsync} from "../../../redux/features/teamSlice";

const RegularAssignmentComponent = () => {
    const dispatch = useDispatch()
    const {currentAssignment, currentAssignmentLoaded} = useSelector((state) => state.assignments)
    const {courseId, assignmentId} = useParams()
    const {currentTeamId, teamLoaded} = useSelector((state) => state.teams)
    const {lakerId} = useSelector((state) => state.auth)
    const assignmentFileFormData = new FormData()

    const assignmentFileHandler = (event) => {
        let file = event.target.files[0];
        assignmentFileFormData.set("file", file)
        console.log(assignmentFileFormData.get("file"))
    }

    useEffect(() => {
        dispatch(getCombinedAssignmentPeerReviews({ courseId, assignmentId }))
        dispatch(getCurrentCourseTeamAsync({courseId, lakerId}))
    }, [])

    const onAssignmentClick = async () => {
        const fileName = currentAssignment.assignment_instructions
        const url = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}/assignments/${assignmentId}/download/${fileName}`
        await axios.get(url, {responseType: 'blob'})
            .then(res => downloadFile(res.data, fileName))
    }

    const downloadFile = (blob, fileName) => {
        const fileURL = URL.createObjectURL(blob);
        const href = document.createElement("a");
        href.href = fileURL;
        href.download = fileName;
        href.click();
    }

    const handleSubmit = async () => {
        const submitAssUrl = `${process.env.REACT_APP_URL}/assignments/student/courses/${courseId}/assignments/${assignmentId}/${currentTeamId}/upload`

        await axios.post(submitAssUrl, assignmentFileFormData)
            .then(res => {
                console.log(res)
            })
            .catch(e => {
                console.log(e)
                alert("Error uploading assignment")
            })
    }

    return (
        <div>
            {currentAssignmentLoaded && teamLoaded ? (
                <div>
                    <h2>{currentAssignment.assignment_name}</h2>
                    <div className="ap-assignmentArea">
                        <div>
                            <h3>
                            Instructions:
                            <span className="span1-ap">Due Date: {currentAssignment.due_date}</span>
                            <br></br>
                            <p>
                                {currentAssignment.instructions}
                            </p>
                            <br></br><br></br>
                            Files: <p className="p2"
                                      onClick={onAssignmentClick}> {currentAssignment.assignment_instructions} </p>
                            <br></br><br></br>
                            <div className="ap-assignment-files">
                                Upload:
                                <input
                                    type="file"
                                    name="assignment_files"
                                    accept=".pdf,.docx"
                                    onChange={(e) => assignmentFileHandler(e)}
                                    required
                                />
                            </div>
                            <div className="ap-button">
                                <button onClick={handleSubmit}>Submit</button>
                            </div>
                            </h3>
                        </div>
                    </div>
                </div> ): null
            }
        </div>
    )
}

export default RegularAssignmentComponent;

