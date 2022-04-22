import React, {useEffect} from 'react';
import SidebarComponent from "../../components/SidebarComponent";
import AssBarComponent from "../../components/AssBarComponent";
import "./styles/AssignmentPageStyle.css"
import {useDispatch, useSelector} from "react-redux";
import {useParams} from "react-router-dom";
import {getSubmittedAssignmentDetailsAsync} from "../../redux/features/assignmentSlice";
import SubmittedAssignmentComponent from "../../components/StudentComponents/AssignmentPage/SubmittedAssignmentComponent";

function SubmittedAssignmentPage() {
    const dispatch = useDispatch()
    const {currentSubmittedAssignment, currentAssignmentLoaded} = useSelector((state) => state.assignments)
    const {lakerId} = useSelector((state) => state.auth)
    const {courseId, assignmentId, currentTeamId} = useParams()

    useEffect(() => {
        dispatch(getSubmittedAssignmentDetailsAsync({courseId, assignmentId, lakerId, currentTeamId}));
    }, [])

    return (
        <div>
            <div className='ap-parent'>
                <SidebarComponent/>
                <div className='ap-container'>
                    <AssBarComponent/>
                    <div className='ap-component'>
                        <SubmittedAssignmentComponent
                            currentAssignmentLoaded={currentAssignmentLoaded}
                            currentSubmittedAssignment={currentSubmittedAssignment}
                        />
                    </div>
                </div>
            </div>
        </div>
    )
}

export default SubmittedAssignmentPage;