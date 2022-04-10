import React, { useEffect } from "react";
import "../../styles/TeacherAss.css"
import {useDispatch, useSelector} from "react-redux";
import {Link, useParams} from "react-router-dom";
import { getCourseAssignmentsAsync } from "../../../redux/features/assignmentSlice";
import axios from "axios";

const assignmentUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`

const ProfessorAssignmentComponent = () => {
    const dispatch = useDispatch()
    const { courseId } = useParams();
    const { courseAssignments, currentAssignmentLoaded } = useSelector((state) => state.assignments)

    useEffect(  () => {
        dispatch(getCourseAssignmentsAsync(courseId))
    }, [])

    console.log(courseAssignments);

    const deleteAssignment = async (assignment) => {
        const url = `${assignmentUrl}/${courseId}/assignments/${assignment.assignment_id}/remove`
        await axios.delete(url).then(res => {
            console.log(res)
        })
        dispatch(getCourseAssignmentsAsync(courseId))
        if (currentAssignmentLoaded) alert("Assignment successfully deleted.")
    }

    return  (
        <div className={"TeacherAss"}>
            <div id="ass">
                <div id="assList">
                    {courseAssignments.map(assignment =>
                        <div className="assListItem">
                            <Link to={`/details/professor/${courseId}/${assignment.assignment_id}/grade`}>
                                <li>
                                    {assignment.assignment_name}
                                    <span1>Due Date: {assignment.due_date}</span1>
                                    <br></br>
                                </li>
                            </Link>
                            <div>
                                <p>
                                    {/*{assignment}*/}
                                    <br></br>
                                    <span2 onClick={() => deleteAssignment(assignment)}>Delete Assignment</span2>
                                </p>
                            </div>
                        </div>
                    )}
                </div>
                <div id="assAddClass">
                    <Link to="create/assignment">
                        <button id="assAddButton">
                            Create new assignment
                        </button>
                    </Link>
                </div>
            </div>
        </div>
    )
}

export default ProfessorAssignmentComponent;