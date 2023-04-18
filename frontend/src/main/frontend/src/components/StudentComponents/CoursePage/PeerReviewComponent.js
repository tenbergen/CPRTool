import { useDispatch, useSelector } from 'react-redux';
import '../../styles/PeerReviewPage.css';
import { useParams } from 'react-router-dom';
import { useEffect } from 'react';
import {
    getStudentSubmittedAssignmentsAsync,
    getSubmittedAssignmentDetailsAsync
} from '../../../redux/features/submittedAssignmentSlice';
import AssignmentTile from '../../AssignmentTile';
import uuid from 'react-uuid';
import noData from '../../../assets/no-data.png';
import {
    getAssignmentDetailsAsync,
    getCombinedAssignmentPeerReviews,
    getCourseAssignmentsAsync
} from "../../../redux/features/assignmentSlice";
import PeerReviewTile from "../../PeerReviewTile";



const PeerReviewComponent = () => {
    const dispatch = useDispatch();
    const store = useSelector((state) => state);
    const { combinedAssignmentPeerReviews, assignmentsLoaded } =
        store.assignments;
    const { currentTeamId, teamLoaded } = store.teams;
    const { courseId } = useParams();
    const { lakerId } = store.auth;

    const { courseSubmittedAssignments, submittedAssignmentsLoaded } = useSelector(
        (state) => state.submittedAssignments
    );

    //grab submitted assignments, then filter for peer review ones
    useEffect(() => {
        dispatch(getStudentSubmittedAssignmentsAsync({ courseId, lakerId }));
    }, [courseId, lakerId, dispatch]);

    //need to get the submitted peer-reviews as well***
    useEffect(() => {
        dispatch(
            getCombinedAssignmentPeerReviews({ courseId, currentTeamId, lakerId })
        );
    }, [courseId, currentTeamId, lakerId, dispatch]);

    //filter for peer-reviews only
    const filteredPeerReviews = combinedAssignmentPeerReviews.filter(checkPeerReview);
    //const filteredSubmittedAssignments = currentSubmittedAssignment.filter(checkPeerReview);

    function checkPeerReview(assignment){
        return assignment.assignment_type !== 'peer-review';
    }

    /*{
    assignmentType === 'peer-review' ? (
              <StudentPeerReviewComponent />
            ) : (
              <RegularAssignmentComponent />
            )}*/

    return (
        <h3>
            { assignmentsLoaded && teamLoaded ? (
                <div id='assList'>
                    <div>

                        {filteredPeerReviews.map((assignment) => (
                            <PeerReviewTile key={uuid()} assignment={assignment} submitted={false}/>

                        ))}

                        {courseSubmittedAssignments.map((assignment) => (
                            <PeerReviewTile
                                key={uuid()}
                                assignment={assignment}
                                submitted={true}
                            />
                        ))}
                    </div>

                </div>
            ) : null}
        </h3>
    );
};
export default PeerReviewComponent;