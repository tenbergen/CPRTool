import React, {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {
    getSubmittedAssignmentDetailsAsync,
    getStudentSubmittedAssignmentsAsync,
    getSubmittedAssignmentsAsync
} from "../redux/features/submittedAssignmentSlice";

const SubAssBarLink = ({active, assignment, onClick}) => {
    const normalStyle = {backgroundColor: 'rgba(255, 255, 255, 0.25)'};
    const clickedStyle = {backgroundColor: 'white'};

    return (
        <div onClick={onClick}>
            <tr>
                <td style={active ? clickedStyle : normalStyle}>
                    <div className='colorForTable'/>
                    <p className='kumba-25 courseText'> {assignment.assigment_name} </p>
                </td>
            </tr>
        </div>
    );
};

const SubmittedAssBarComponent = () => {
    const navigate = useNavigate()
    const dispatch = useDispatch();
    const {courseSubmittedAssignments, assignmentsLoaded} = useSelector((state) => state.submittedAssignments);
    const {courseId, assignmentId, teamId} = useParams();
    const [chosen, setChosen] = useState(assignmentId + teamId);
    const {lakerId, role} = useSelector((state) => state.auth);

    useEffect(() => {
        role === "professor"
            ? dispatch(getSubmittedAssignmentsAsync({courseId, assignmentId}))
            : dispatch(getStudentSubmittedAssignmentsAsync({courseId, teamId, lakerId}))
    }, []);

    const onSubAssClick = (assignment) => {
        const teamId =  assignment.team_name
        const chosen = assignment.assignment_id + teamId
        setChosen(chosen);
        dispatch(getSubmittedAssignmentDetailsAsync({courseId, assignmentId, lakerId, teamId}))
        navigate(`/details/${role}/${courseId}/${assignment.assignment_id}/${assignment.team_name}/submitted`)
    };

    return (
        <div className='abc-parent'>
            <h2 className="kumba-30"> Assignments </h2>
            <div className='abc-assignments'>
                {assignmentsLoaded ?
                    courseSubmittedAssignments.map(assignment => (
                        <SubAssBarLink
                            active={assignment.assignment_id + assignment.team_name  === chosen}
                            assignment={assignment}
                            onClick={() => onSubAssClick(assignment)}
                        />
                )) : null}
            </div>
        </div>
    )
}

export default SubmittedAssBarComponent