import { useNavigate, useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import './styles/AssignmentTile.css';


//page to route to
//PeerReviewGRComponent

const PeerReviewTile = ({ assignment, submitted }) => {
    //const dispatch = useDispatch();
    const navigate = useNavigate();

    const title =
        "Assignment #" + assignment.assignment_id;

    //const assID = assignment.assignment_id;

    //const { role } = useSelector((state) => state.auth);
    const { currentTeamId } = useSelector((state) => state.teams);
    //const { assignmentShit } = useSelector((state) => state.assignments);
    const { courseId } = useParams();
    //const link = `/${role}/${courseId}/${assignment.assignment_id}`;


    const onTileClick = () => {
        //"student/:courseId/peer-review/:assignmentId/:teamId/pr-list"
        //if the peer reviews have been distrubutes, link to this page, else alert that the peer reviews are not
        //yet distributed


        let tileLink;
        //if the assignment is a peer review(is an assignment that hasnt been submitted)
        if (assignment.has_peer_review)
            //if the non submitted assignment has a peer review, there are peer reviews
            //accessible, route to the correct page(the page w all the peer reviews)
            if(assignment.reviews_per_team > 0) {
                tileLink = `student/${courseId}/peer-review/${assignment.assignment_id}/${currentTeamId}/pr-list`;
                navigate(tileLink);
            }
            else
                alert("peer reviews are not yet completed");
        //if the assignment is a submitted assignment
        else{
            if(assignment.reviews !== null) {
                tileLink = `/student/${courseId}/peer-review/${assignment.assignment_id}/${currentTeamId}/pr-list`;
                navigate(tileLink);
            }
            else
                alert("peer reviews are not yet completed");
        }

    };

    return (
        <div>
            <div
                className={
                    assignment.assignment_type === 'peer-review'
                        ? 'ass-tile'
                        : 'ass-tile'
                }
            >
                <div className='inter-20-medium-white ass-tile-title'>
                    {/*{' '}*/}
                    <span> {title} </span>
                </div>

                <div className='ass-tile-content' onClick={onTileClick}>
                    <div className='ass-tile-info' >
            <span className='inter-24-bold'>
                {''}
                {assignment.type === 'team_submission' ?
                    assignment.assigment_name:
                    assignment.assignment_name
                }
                <br />
                <span className = 'inter-14-medium-black'>
                    {'Due Date: '}
                    {/*edited*/}
                    {assignment.type === 'team_submission' ?
                        assignment.peer_review_due_date :
                        assignment.due_date}
                </span>

            </span>

                        <span className='inter-20-medium'>
              {submitted
                  ? assignment.grade === -1
                      ? "Awaiting PR"
                      : "Graded"
                  : 'Assigned(Awaiting Assignment Completion)'}


            </span>

                    </div>
                    {!submitted && (
                        <div className='ass-tile-links'>
                            {/*<span className='inter-16-bold-blue ass-tile-files' onClick={onFileClick}>*/}
                            {/*  {assignment.assignment_type === 'peer-review'*/}
                            {/*      ? assignment.peer_review_rubric*/}
                            {/*      : assignment.assignment_instructions}*/}
                            {/*</span>*/}
              <span className='inter-16-bold-blue ass-tile-files' >
              </span>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default PeerReviewTile;
