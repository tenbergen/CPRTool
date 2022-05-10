import {useDispatch, useSelector} from "react-redux";
import "../../styles/StudentAss.css"
import {useParams} from "react-router-dom";
import React, {useEffect} from "react";
import {getStudentSubmittedAssignmentsAsync} from "../../../redux/features/submittedAssignmentSlice";
import AssignmentTile from "../../AssignmentTile";

const StudentSubmittedComponent = () => {
    const dispatch = useDispatch()
    const { courseSubmittedAssignments, assignmentsLoaded } = useSelector((state) => state.submittedAssignments)
    const { lakerId } = useSelector((state) => state.auth)
    const { courseId } = useParams()

    useEffect(() => {
        dispatch(getStudentSubmittedAssignmentsAsync({courseId, lakerId}))
    }, [])

    return (
        <h3>
            {assignmentsLoaded ? (
                <div id='assList'>
                    {courseSubmittedAssignments.map(assignment => (
                        <AssignmentTile
                            assignment={assignment}
                            submitted={true}/>
                    ))}
                </div>
            ) : null}
        </h3>
    );
}

export default StudentSubmittedComponent