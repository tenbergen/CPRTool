import React, { useEffect } from "react";
import "./styles/TeacherAss.css"
import {useDispatch, useSelector} from "react-redux";
import {Link, useParams} from "react-router-dom";
import {getCourseAssignmentsAsync} from "../redux/features/assignmentSlice";

const TeacherAssComponent = () => {
    const dispatch = useDispatch()
    const { courseId } = useParams();
    const { courseAssignments } = useSelector((state) => state.assignments)

    useEffect(  () => {
        dispatch(getCourseAssignmentsAsync(courseId))
    }, [])

    return  (
        <div className={"TeacherAss"}>
            <div id="ass">
                <div id="assList">
                    {courseAssignments.map(assignment =>
                            <Link to={`/details/professor/${courseId}/${assignment.assignment_name}/grade`}>
                            <li className="assListItem">{assignment.assignment_name}</li>
                           </Link>
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

export default TeacherAssComponent;