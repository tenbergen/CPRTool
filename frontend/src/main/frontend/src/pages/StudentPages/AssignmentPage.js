import React, { useEffect } from 'react';
import SidebarComponent from '../../components/SidebarComponent';
import AssBarComponent from '../../components/AssBarComponent';
import './styles/AssignmentPageStyle.css';
import { useDispatch } from 'react-redux';
import { useParams } from 'react-router-dom';
import { getAssignmentDetailsAsync } from '../../redux/features/assignmentSlice';
import RegularAssignmentComponent from "../../components/StudentComponents/AssignmentPage/RegularAssignmentComponent";
import StudentPeerReviewComponent from "../../components/StudentComponents/AssignmentPage/StudentPeerReviewComponent";

function AssignmentPage() {
    const dispatch = useDispatch();
    const { courseId, assignmentId, assignmentType } = useParams();

    useEffect(() => {
        dispatch(getAssignmentDetailsAsync({ courseId, assignmentId }));
    }, []);

    return (
        <div>
            <div className='ap-parent'>
                <SidebarComponent />
                <div className='ap-container'>
                    <AssBarComponent />
                    <div className='ap-component'>
                        {assignmentType === 'peer-review'
                            ? <StudentPeerReviewComponent/>
                            : <RegularAssignmentComponent/>
                        }
                    </div>
                </div>
            </div>
        </div>
    );
}

export default AssignmentPage;