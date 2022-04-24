import React, {useEffect} from 'react';
import SidebarComponent from "../../components/SidebarComponent";
import "./styles/AssignmentPageStyle.css"
import {useDispatch, useSelector} from "react-redux";
import {useParams} from "react-router-dom";
import {getSubmittedAssignmentDetailsAsync} from "../../redux/features/assignmentSlice";
import SubmittedAssignmentComponent from "../../components/StudentComponents/AssignmentPage/SubmittedAssignmentComponent";
import SubmittedAssBarComponent from "../../components/SubmittedAssBarComponent";

function SubmittedAssignmentPage() {
    const dispatch = useDispatch()
    const {currentSubmittedAssignment, currentSubmittedAssignmentLoaded} = useSelector((state) => state.assignments)
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
                    <SubmittedAssBarComponent/>
                    <div className='ap-component'>
                        {currentSubmittedAssignmentLoaded ?
                            <SubmittedAssignmentComponent
                                currentSubmittedAssignment={currentSubmittedAssignment}
                            /> : null}
                    </div>
                </div>
            </div>
        </div>
    )
}

export default SubmittedAssignmentPage;