import React, {useEffect, useState} from "react";
import "./styles/AssBar.css"
import {useDispatch, useSelector} from "react-redux";
import {Link, useParams} from "react-router-dom";
import { getAssignmentFilesAsync, getCourseAssignmentsAsync, setCurrentAssignment } from "../redux/features/assignmentSlice";

const AssBarLink = ( {active, assignment, onClick})  => {
    const { role } = useSelector((state) => state.auth)
    const normalStyle = { backgroundColor: "rgba(255, 255, 255, 0.25)" }
    const clickedStyle = { backgroundColor: "white" }
    const { courseId } = useParams()

    return (
        <Link to={`/details/${role}/${courseId}/${assignment.assignment_id}`} onClick={onClick}>
            <tr>
                <td style={active ? clickedStyle : normalStyle} >
                    <div className="colorForTable"/>
                    <p className="courseText"> {assignment.assignment_name} </p>
                </td>
            </tr>
        </Link>
    );
}

const AssBarComponent = () => {
    const dispatch = useDispatch()
    const { courseAssignments }  = useSelector((state) => state.assignments)
    const { courseId, assignmentId } = useParams()

    const [chosen, setChosen] = useState(parseInt(assignmentId));

    useEffect(() => {
        dispatch(getCourseAssignmentsAsync(courseId))
    },[])

    const onAssClick = (assignment) =>{
        const assignment_id = assignment.assignment_id
        console.log(assignment_id)
        setChosen(assignment_id)
        dispatch(setCurrentAssignment(assignment))
        dispatch(getAssignmentFilesAsync({courseId, assignment_id}))
    }

    return (
        <div className="abc-parent">
            <h2> Assignments </h2>
            <div className="abc-assignments">
                {courseAssignments.map(assignment =>
                    <AssBarLink
                        onClick={()=> onAssClick(assignment)}
                        active={assignment.assignment_id === chosen}
                        assignment={assignment}/>
                )}
            </div>
        </div>
    );
}

export default AssBarComponent;