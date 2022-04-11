import React, {useEffect} from 'react';
import SidebarComponent from "../../components/SidebarComponent";
import AssBarComponent from "../../components/AssBarComponent";
import "./styles/AssignmentPageStyle.css"
import {useDispatch, useSelector} from "react-redux";
import {useParams} from "react-router-dom";
import {getAssignmentDetailsAsync, getAssignmentFilesAsync} from "../../redux/features/assignmentSlice";
import axios from "axios";

function AssignmentPage() {
    const dispatch = useDispatch()
    const { currentAssignment, currentAssignmentLoaded, currentAssignmentFiles } = useSelector((state) => state.assignments)
    const { courseId, assignmentId } = useParams()
    console.log(courseId)
    console.log(assignmentId)

    useEffect(() => {
        const assignment_id = assignmentId
        dispatch(getAssignmentDetailsAsync({ courseId, assignmentId }));
        dispatch(getAssignmentFilesAsync({courseId, assignment_id}))
    }, [])

    const onAssignmentClick = async () => {
        const fileName = currentAssignmentFiles[0]
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

    return (
        <div>
            { currentAssignmentLoaded ?
                <div className="ap-parent">
                    <SidebarComponent/>
                    <div className="ap-container">
                        <AssBarComponent/>
                        <div className="ap-component">
                            <h2>{currentAssignment.assignment_name}</h2>
                            <div className="ap-assignmentArea">
                                <div className="ap-component-links">
                                    <h3> Instructions: <br/><br/>{currentAssignment.instructions}
                                        <br/> <br/> <br/>
                                         Files: <div onClick={onAssignmentClick}> {currentAssignmentFiles[0]} </div>
                                    </h3>
                                    <h3> Due Date: {currentAssignment.due_date}</h3>
                                </div>
                            </div>
                        </div>
                    </div>
                </div> : null
            }
        </div>
    )
}

export default AssignmentPage;