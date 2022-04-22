import React, {useEffect, useState} from "react";
import {Link, useParams} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {getSubmittedAssignmentDetailsAsync, getSubmittedAssignmentsAsync} from "../redux/features/assignmentSlice";

const SubAssBarLink = ({active, assignment, onClick, teamName}) => {
    const {role} = useSelector((state) => state.auth);
    const normalStyle = {backgroundColor: 'rgba(255, 255, 255, 0.25)'};
    const clickedStyle = {backgroundColor: 'white'};
    const {courseId} = useParams();

    return (
        <Link
            to={`/details/${role}/${courseId}/${assignment.assignment_id}/${teamName}/submitted`}
            onClick={onClick}>
            <tr>
                <td style={active ? clickedStyle : normalStyle}>
                    <div className='colorForTable'/>
                    <p className='courseText'> {assignment.assignment_name} </p>
                </td>
            </tr>
        </Link>
    );
};

const SubmittedAssBarComponent = () => {
    const dispatch = useDispatch();
    const {courseSubmittedAssignments} = useSelector((state) => state.assignments);
    const {courseId, assignmentId, currentTeamId} = useParams();
    const [chosen, setChosen] = useState(parseInt(assignmentId));
    const {lakerId} = useSelector((state) => state.auth);

    useEffect(() => {
        dispatch(getSubmittedAssignmentsAsync({courseId, currentTeamId, lakerId}))
    }, []);

    const onSubAssClick = (assignment) => {
        const assignmentId = assignment.assignment_id
        setChosen(assignmentId);
        dispatch(getSubmittedAssignmentDetailsAsync({courseId, assignmentId, lakerId, currentTeamId}))
    };

    return (
        <div className='abc-parent'>
            <h2> Assignments </h2>
            <div className='abc-assignments'>
                {courseSubmittedAssignments.map((assignment) => (
                    <SubAssBarLink
                        onClick={() => onSubAssClick(assignment)}
                        active={assignment.assignment_id === chosen}
                        assignment={assignment}
                        teamName={currentTeamId}
                    />
                ))}
            </div>
        </div>
    )
}

export default SubmittedAssBarComponent