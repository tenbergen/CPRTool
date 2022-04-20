import React, {useEffect} from 'react';
import {useDispatch, useSelector} from 'react-redux';
import '../../styles/StudentAss.css';
import {Link, useParams} from 'react-router-dom';
import {getCombinedAssignmentPeerReviews} from '../../../redux/features/assignmentSlice';
import axios from "axios";

const StudentToDoComponent = () => {
    const dispatch = useDispatch();
    const store = useSelector((state) => state);
    const {role} = store.auth;
    const {combinedAssignmentPeerReviews, assignmentsLoaded} =
        store.assignments;
    const {courseId} = useParams();
    const {currentTeamId, teamLoaded} = useSelector((state) => state.teams)
    const ass = "Assignment"
    const peer = "Peer Review"
    const link = `/details/${role}/${courseId}`;

    useEffect(() => {
        dispatch(getCombinedAssignmentPeerReviews({courseId, currentTeamId}));
    }, []);

    const onAssClick = async (assignment) => {
        const fileName = assignment.assignment_instructions
        const assignmentId = assignment.assignment_id
        const url = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}/assignments/${assignmentId}/download/${fileName}`
        await axios.get(url, {responseType: 'blob'})
            .then(res => downloadFile(res.data, fileName))
    }

    const onPeerClick = async (assignment) => {
        const fileName = assignment.peer_review_rubric
        const assignmentId = assignment.assignment_id
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
        <h3>
            {assignmentsLoaded && teamLoaded ? (
                <div id='assList'>
                    {combinedAssignmentPeerReviews.map((assignment) => (
                        <div className='assListItem'>
                            <Link
                                to={
                                    assignment.assignment_type === 'peer-review'
                                        ? `${link}/${assignment.assignment_id}/peer-review/${assignment.peer_review_team}`
                                        : `${link}/${assignment.assignment_id}/normal`
                                }
                            >
                                <li>
                                    <span className='ass-span-student'>
                                        {
                                            assignment.assignment_type === 'peer-review'
                                            ? peer
                                            : ass
                                        }
                                    </span>
                                    <br></br>
                                    <div className='ass-title'>
                                        {assignment.assignment_name}
                                        <span className="span1-ap">Due Date: {
                                            assignment.assignment_type === "peer-review"
                                                ? assignment.peer_review_due_date
                                                : assignment.due_date
                                        }
                                        </span>
                                        <br></br>
                                    </div>
                                </li>
                            </Link>
                            {
                                assignment.assignment_type === 'peer-review'
                                    ? <div className='ass-instructions' onClick={() => onPeerClick(assignment)}>
                                        {assignment.peer_review_rubric}
                                    </div>
                                    : <div className='ass-instructions' onClick={() => onAssClick(assignment)}>
                                        {assignment.assignment_instructions}
                                    </div>
                            }
                        </div>
                    ))}
                </div>
            ) : null}
        </h3>
    );
};

export default StudentToDoComponent;
