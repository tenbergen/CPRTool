import React, {useEffect} from 'react';
import {useDispatch, useSelector} from 'react-redux';
import '../../styles/StudentAss.css';
import {useParams} from 'react-router-dom';
import {getCombinedAssignmentPeerReviews} from '../../../redux/features/assignmentSlice';
import AssignmentTile from "../../AssignmentTile";

const StudentToDoComponent = () => {
    const dispatch = useDispatch();
    const store = useSelector((state) => state);
    const {combinedAssignmentPeerReviews, assignmentsLoaded} = store.assignments;
    const {currentTeamId, teamLoaded} = store.teams
    const {courseId} = useParams();

    useEffect(() => {
        dispatch(getCombinedAssignmentPeerReviews({courseId, currentTeamId}));
    }, []);

    return (
        <h3>
            {assignmentsLoaded && teamLoaded ? (
                <div id='assList'>
                    {combinedAssignmentPeerReviews.map(assignment => (
                        <AssignmentTile
                            assignment={assignment}
                        />
                    ))}
                </div>
            ) : null}
        </h3>
    );
};

export default StudentToDoComponent;
