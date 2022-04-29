import React, {useEffect, useState} from 'react';
import {useDispatch, useSelector} from 'react-redux';
import '../../styles/TeamSubmission.css';
import {useParams} from 'react-router-dom';
import axios from "axios";
import {getSubmittedAssignmentsAsync} from "../../../redux/features/submittedAssignmentSlice";
import AssignmentTile from "../../AssignmentTile";

const ProfessorAllSubmissionsComponent = () => {
    const dispatch = useDispatch();
    const {courseId, assignmentId} = useParams();
    const { courseSubmittedAssignments, assignmentsLoaded } = useSelector((state) => state.submittedAssignments)
    const [assignedTeamCount, setAssignedTeamCount] = useState()

    useEffect(async () => {
        dispatch(getSubmittedAssignmentsAsync({courseId, assignmentId}))
    }, []);

    const distribute = async () => {
        const url = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/assign/${assignedTeamCount}`
        await axios.get(url)
            .then(res => {
                console.log(res)
                alert("Assignments successfully distributed for peer review!")
            })
            .catch(e => {
                console.log(e.response.data)
                alert(`Error: Teams for this assignment cannot review more than ${courseSubmittedAssignments.length - 1} team(s).`)
            })
        setAssignedTeamCount(undefined)
    }

    return (
        <div>
            {assignmentsLoaded ? (
                <div className="psc-container">
                    <div id='assList'>
                        {courseSubmittedAssignments.map(assignment => (
                            <AssignmentTile
                                assignment={assignment}
                                submitted={true}/>
                        ))}
                    </div>
                    <div className="input-field" style={{marginLeft: "5%", marginBottom: "5%"}}>
                        <span className="outfit-16"> Number of teams to review: </span>
                        <input
                            type="number"
                            value={assignedTeamCount}
                            onChange={(e) => setAssignedTeamCount(e.target.value)}
                            style={{marginRight: "2%"}}/>
                        <button
                            className="green-button outfit-16"
                            style={{padding: "2%", fontSize: "14px"}}
                            onClick={distribute}> Distribute Peer Reviews </button>
                    </div>
                </div>
            ) : null}
        </div>
    );
};

export default ProfessorAllSubmissionsComponent;
