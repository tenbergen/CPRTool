import React, {useEffect, useState} from 'react';
import './styles/TeacherAss.css';
import {useDispatch, useSelector} from 'react-redux';
import {Link, useNavigate, useParams} from 'react-router-dom';
import {getSubmittedAssignmentsAsync} from "../redux/features/submittedAssignmentSlice";
import {getAssignmentDetailsAsync, getCourseAssignmentsAsync} from "../redux/features/assignmentSlice";

const GradeAssBarLink = ({active, assignment, onClick}) => {
    const {role} = useSelector((state) => state.auth);
    const normalStyle = {backgroundColor: 'rgba(255, 255, 255, 0.25)'};
    const clickedStyle = {backgroundColor: 'white'};
    const {courseId} = useParams();

    return (
        <Link
            to={`/details/${role}/${courseId}/${assignment.assignment_id}`}
            onClick={onClick}>
            <tr>
                <td style={active ? clickedStyle : normalStyle}>
                    <div className='colorForTable'/>
                    <p className='courseText kumba-25'> {assignment.assignment_name} </p>
                </td>
            </tr>
        </Link>
    );
};

const GradeAssBarComponent = () => {
    const dispatch = useDispatch();
    const {courseAssignments} = useSelector((state) => state.assignments);
    const {courseId, assignmentId} = useParams();
    const navigate = useNavigate()

    const [chosen, setChosen] = useState(parseInt(assignmentId));

    useEffect(() => {
        dispatch(getCourseAssignmentsAsync(courseId));
    }, []);

    const onAssClick = (assignment) => {
        setChosen(assignment.assignment_id);
        let assignmentId = assignment.assignment_id;
        dispatch(getSubmittedAssignmentsAsync({courseId, assignmentId}));
        dispatch(getAssignmentDetailsAsync({ courseId, assignmentId}))
    };

    const onCourseClick = () => {
        navigate(`/details/professor/${courseId}`)
    }

    return (
        <div className='abc-parent'>
            <div className="abc-title">
                <span className="outfit-16 link" style={{fontSize: "11px"}} onClick={onCourseClick}>{courseId}</span>
                <h2 className="kumba-30"> Assignments </h2>
            </div>
            <div className='abc-assignments'>
                {courseAssignments.map((assignment) => (
                    <GradeAssBarLink
                        onClick={() => onAssClick(assignment)}
                        active={assignment.assignment_id === chosen}
                        assignment={assignment}
                    />
                ))}
            </div>
        </div>
    );
};

export default GradeAssBarComponent;
