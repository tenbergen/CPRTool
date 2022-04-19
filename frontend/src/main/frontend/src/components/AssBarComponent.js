import React, {useEffect, useState} from 'react';
import './styles/AssBar.css';
import {useDispatch, useSelector} from 'react-redux';
import {Link, useParams} from 'react-router-dom';
import {getCombinedAssignmentPeerReviews} from '../redux/features/assignmentSlice';

const AssBarLink = ({active, assignment, onClick}) => {
    const {role} = useSelector((state) => state.auth);
    const normalStyle = {backgroundColor: 'rgba(255, 255, 255, 0.25)'};
    const clickedStyle = {backgroundColor: 'white'};
    const {courseId} = useParams();
    const link = `/details/${role}/${courseId}`;

    return (
        <Link
            to={
                assignment.assignment_type === 'peer-review'
                    ? `${link}/${assignment.assignment_id}/peer-review/${assignment.peer_review_team}`
                    : `${link}/${assignment.assignment_id}/normal`
            }
            onClick={onClick}
        >
            <tr>
                <td style={active ? clickedStyle : normalStyle}>
                    <div className='colorForTable'/>
                    <p className='courseText'> {assignment.assignment_name} </p>
                </td>
            </tr>
        </Link>
    );
};

const AssBarComponent = () => {
    const dispatch = useDispatch();
    const {combinedAssignmentPeerReviews} = useSelector(
        (state) => state.assignments
    );
    const {courseId, assignmentId, assignmentType, teamName} = useParams();
    const teamId = '1';

    const curr =
        assignmentType === 'peer-review'
            ? `${assignmentId}-peer-review-${teamName}`
            : parseInt(assignmentId);
    const [chosen, setChosen] = useState(curr);

    useEffect(() => {
        dispatch(getCombinedAssignmentPeerReviews({courseId, teamId}));
    }, []);

    const onAssClick = (assignment) => {
        const curr =
            assignment.assignment_type === 'peer-review'
                ? `${assignment.assignment_id}-${assignment.assignment_type}-${assignment.peer_review_team}`
                : parseInt(assignment.assignment_id);
        setChosen(curr);
    };

    return (
        <div className='abc-parent'>
            <h2> Assignments </h2>
            <div className='abc-assignments'>
                {combinedAssignmentPeerReviews.map((assignment) => (
                    <AssBarLink
                        onClick={() => onAssClick(assignment)}
                        active={assignment.final_id === chosen}
                        assignment={assignment}
                    />
                ))}
            </div>
        </div>
    );
};

export default AssBarComponent;
