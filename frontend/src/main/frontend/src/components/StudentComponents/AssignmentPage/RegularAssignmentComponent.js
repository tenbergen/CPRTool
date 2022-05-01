import React, {useEffect} from 'react';
import "../../../pages/StudentPages/styles/AssignmentPageStyle.css";
import {useDispatch, useSelector} from "react-redux";
import {useNavigate, useParams} from "react-router-dom";
import axios from "axios";
import {getAssignmentDetailsAsync } from "../../../redux/features/assignmentSlice";
import {getCurrentCourseTeamAsync} from "../../../redux/features/teamSlice";

const RegularAssignmentComponent = () => {
    const dispatch = useDispatch()
    const navigate = useNavigate()
    const { currentAssignment, currentAssignmentLoaded } = useSelector((state) => state.assignments)
    const { lakerId } = useSelector((state) => state.auth)
    const { courseId, assignmentId } = useParams()
    const { currentTeamId } = useSelector((state) => state.teams)
    const assignmentFileFormData = new FormData()

    useEffect(() => {
        dispatch(getCurrentCourseTeamAsync({courseId, lakerId}))
        dispatch(getAssignmentDetailsAsync({courseId, assignmentId}))
    }, [])

    const assignmentFileHandler = (event) => {
        let file = event.target.files[0];
        assignmentFileFormData.set("file", file)
        console.log(assignmentFileFormData.get("file"))
    }

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
                alert("Successfully uploaded assignment")
                navigate(`/details/student/${courseId}`, {state: {initialComponent: "Submitted"}})
            })
            .catch(e => {
                console.log(e.response)
                alert("Error uploading assignment")
            })
    }

    return (
        <div>
            { currentAssignmentLoaded &&
                <div>
                    <h2 className="kumba-30">{currentAssignment.assignment_name}</h2>
                    <div className="ap-assignmentArea">
                        <h3>
                            <span className="outfit-25"> Instructions: </span>
                            <span className="outfit-25 span1-ap">Due: {currentAssignment.due_date}</span>
                            <br/>
                            <p className="outfit-18">
                                {currentAssignment.instructions}
                            </p>
                            <br/><br/>
                            <span className="outfit-25"> Files: </span>
                            <span className="outfit-18 p2" onClick={onAssignmentClick}>
                                {currentAssignment.assignment_instructions}
                            </span>
                            <br/><br/>
                            <div className="ap-assignment-files">
                                <input
                                    type="file"
                                    name="assignment_files"
                                    accept=".pdf,.docx"
                                    onChange={(e) => assignmentFileHandler(e)}
                                    required
                                />
                            </div>
                            <div className="ap-button">
                                <button className="green-button" onClick={handleSubmit}> Submit </button>
                            </div>
                        </h3>
                    </div>
                </div>
            }
        </div>
    )
}

export default RegularAssignmentComponent;

