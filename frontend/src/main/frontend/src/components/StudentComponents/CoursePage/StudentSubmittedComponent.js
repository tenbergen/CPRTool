import {useDispatch, useSelector} from "react-redux";
import "../../styles/StudentAss.css"
import {Link, useParams} from "react-router-dom";
import React, {useEffect} from "react";
import { getSubmittedAssignmentsAsync } from "../../../redux/features/assignmentSlice";

const StudentSubmittedComponent = () => {
    const dispatch = useDispatch()
    const { courseSubmittedAssignments, assignmentsLoaded } = useSelector((state) => state.assignments)
    const { lakerId } = useSelector((state) => state.auth)
    const { courseId } = useParams()
    const { currentTeamId, teamLoaded } = useSelector((state) => state.teams)

    useEffect(() => {
        dispatch(getSubmittedAssignmentsAsync({courseId, currentTeamId, lakerId}))
    }, [])

    return (
        <h3>
            {teamLoaded && assignmentsLoaded ? (
                <div id='assList'>
                    {courseSubmittedAssignments.map(assignment => (
                        <div className='assListItem'>
                            <Link to={`/details/student/${courseId}/${assignment.assignment_id}/${currentTeamId}/submitted`}>
                                <li>
                                    <br/>
                                    <div className='ass-title'>
                                        {assignment.assignment_name}
                                        <span className="span1-ap">
                                            {assignment.grade === -1 ? "Pending" : assignment.grade}
                                        </span>
                                        <br/>
                                    </div>
                                </li>
                            </Link>
                        </div>
                    ))}
                </div>
            ) : null}
        </h3>
    );
}

export default StudentSubmittedComponent