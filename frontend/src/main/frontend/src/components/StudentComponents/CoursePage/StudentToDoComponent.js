import React, {useEffect} from 'react';
import {useDispatch, useSelector} from 'react-redux';
import '../../styles/StudentAss.css';
import {Link, useParams} from 'react-router-dom';
import {getCombinedAssignmentPeerReviews} from '../../../redux/features/assignmentSlice';

const StudentToDoComponent = () => {
    const dispatch = useDispatch();
    const store = useSelector((state) => state);
    const {role} = store.auth;
    const {combinedAssignmentPeerReviews, assignmentsLoaded} =
        store.assignments;
    const {courseId} = useParams();
    const {currentTeamId, teamLoaded} = useSelector((state) => state.teams)

    const link = `/details/${role}/${courseId}`;

    useEffect(() => {
        dispatch(getCombinedAssignmentPeerReviews({courseId, currentTeamId}));
    }, []);

    return (
        <h3>
            {assignmentsLoaded && teamLoaded ? (
                <div id='assList'>
                    {combinedAssignmentPeerReviews.map((assignment) => (
                        <div className='assListItem'>
                            <Link
                                to={
                                    assignment.assignment_type === 'peer-review'
                                        ? `${link}/${assignment.assignment_id}/peer-review/${assignment.peer_review_team}`
                                        : `${link}/${assignment.assignment_id}/normal`
                                }
                            >
                                <li>
                                    <div className='ass-title'>
                                        {assignment.assignment_name}
                                        <span className="span1-ap">Due Date: {
                                            assignment.assignment_type === "peer-review"
                                                ? assignment.peer_review_due_date
                                                : assignment.due_date
                                        }
                                        </span>
                                        <br></br>
                                    </div>
                                </li>
                            </Link>
                        </div>
                    ))}
                </div>
            ) : null}
        </h3>
    );
};

export default StudentToDoComponent;
