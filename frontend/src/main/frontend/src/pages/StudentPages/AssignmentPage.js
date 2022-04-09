import React, { useEffect } from 'react';
import SidebarComponent from "../../components/SidebarComponent";
import AssBarComponent from "../../components/AssBarComponent";
import "./styles/AssignmentPageStyle.css"
import {useDispatch, useSelector} from "react-redux";
import {useParams} from "react-router-dom";
import {getAssignmentDetailsAsync} from "../../redux/features/assignmentSlice";

function AssignmentPage() {
    const dispatch = useDispatch()
    const { currentAssignment, currentAssignmentLoaded } = useSelector((state) => state.assignments)
    const { courseId, assignmentId } = useParams()

    useEffect( () => {
        dispatch(getAssignmentDetailsAsync({ courseId, assignmentId}));
    }, [])

    return (
        <div>
            <div className="ap-parent">
                <SidebarComponent/>
                <div className="ap-container">
                    <AssBarComponent/>
                    <div className="ap-component">
                        <h2>{currentAssignment.assignment_name}</h2>
                        <div className="ap-assignmentArea">
                            <div className="ap-component-links">
                                <h3> Instructions: <br/> <br/> <br/>{currentAssignment.instructions}</h3>
                                <h3> Due Date: {currentAssignment.due_date}</h3>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default AssignmentPage;
