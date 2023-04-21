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
    //added
    const { courseSubmittedAssignments, submittedAssignmentsLoaded } = useSelector(
        (state) => state.submittedAssignments
    );
    const { currentTeamId, teamLoaded } = store.teams;
    const { courseId } = useParams();
    const { lakerId } = store.auth;


    //need to get the submitted peer-reviews as well***
    useEffect(() => {
        dispatch(
            //maybe try the getcourseAssignments export thing in the asignment slice?
            getCombinedAssignmentPeerReviews({ courseId, currentTeamId, lakerId })
        );
    }, [courseId, currentTeamId, lakerId, dispatch]);



    //added
    useEffect(() => {
        dispatch(getStudentSubmittedAssignmentsAsync({ courseId, lakerId }));
    }, [courseId, lakerId, dispatch]);



    //filter for peer-reviews only
    const filteredPeerReviews = combinedAssignmentPeerReviews.filter(checkPeerReview);

    const filteredSubmittedReviews = courseSubmittedAssignments.filter(checkPeerReview);

    function checkPeerReview(assignment){
        //if the assigment has peer reviews associated w it, then keep them
        return true;//assignment.has_peer_review;//.assignment_type !== 'peer-review';
    }


    return (
        <div>
            { assignmentsLoaded && teamLoaded ? (
                <div id='assList'>
                    <div>
                        {filteredSubmittedReviews.map((assignment) => (
                            assignment.peer_review_due_date !== null && assignment.reviews !== null
                                ? <PeerReviewTile key={uuid()} assignment={assignment} submitted={true}/>
                                : console.log("submitted assignment: "+assignment.assignment_id + " was not a peer review ")
                        ))}

                        {filteredPeerReviews.map((assignment) => (
                            assignment.has_peer_review && assignment.assignment_type !== 'peer-review'
                                ? <PeerReviewTile key={uuid()} assignment={assignment} submitted={false}/>
                                : console.log("non-submitted assignment: "+assignment.assignment_id + " was not a peer review ")
                        ))}
                    </div>

                </div>
            ) : null}
        </div>
    );
};
export default PeerReviewComponent;
