import { useEffect } from "react";
import {useDispatch, useSelector} from "react-redux";
import "../../styles/TeamSubmission.css"
import {Link, useParams} from "react-router-dom";
import { getCourseAssignmentsAsync, setCurrentAssignment } from "../../../redux/features/assignmentSlice";

const ToDoComponent = () => {
    const dispatch = useDispatch()
    const store = useSelector((state) => state)
    const { role } = store.auth
    const { courseAssignments } = store.assignments
    const { courseId } = useParams()

    useEffect(() => {
        dispatch(getCourseAssignmentsAsync(courseId))
    },[])

    const assignmentClickHandler = (assignment) => {
        dispatch(setCurrentAssignment(assignment))
    }

    const teamsubs = ["Team A : Assignment 1", "Team B: Assignment 1"]

    return (
        <h3>
            <div id="assList">
                {teamsubs.map(teamsub =>
                    <Link to={''}
                          onClick={() => assignmentClickHandler(teamsub)}>
                        <li id="assListItem">{ teamsub + "\n\n" + "Grade : " + "Pending"}</li>
                    </Link>
                )}
            </div>
        </h3>

    )
}

export default ToDoComponent
