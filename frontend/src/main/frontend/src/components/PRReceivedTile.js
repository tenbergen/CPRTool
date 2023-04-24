import { useNavigate, useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import axios from 'axios';
import './styles/AssignmentTile.css';
import { useEffect } from 'react';
import {
    getCombinedAssignmentPeerReviews
} from '../redux/features/assignmentSlice';

const PRTile = ({ peerReview, submitted }) => {
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const { role } = useSelector((state) => state.auth);
    const { currentTeamId } = useSelector((state) => state.teams);
    const { courseId } = useParams();
    const { lakerId } = useSelector((state) => state.auth);
    const link = `/${role}/${courseId}/${peerReview.assignment_id}`;

    useEffect(() => {
        dispatch(
            getCombinedAssignmentPeerReviews({ courseId, currentTeamId, lakerId })
        );
    }, [courseId, currentTeamId, lakerId, dispatch]);

    const onFileClick = async () => {
        const fileName =
            peerReview.assignment_type = 'peer-review';
        const assignmentId = peerReview.assignment_id;
        const url = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}/assignments/${assignmentId}/download/${fileName}`;
        await axios
            .get(url, { responseType: 'blob' })
            .then((res) => downloadFile(res.data, fileName));
    };

    const downloadFile = (blob, fileName) => {
        const fileURL = URL.createObjectURL(blob);
        const href = document.createElement('a');
        href.href = fileURL;
        href.download = fileName;
        href.click();
    };

    const onButtonClick = () => {
        const tileLink = submitted
            ? role === 'student'
                ? `/student/${courseId}/${peerReview.assignment_id}/${currentTeamId}/submitted`
                : `${link}/${peerReview.team_name}/submitted`
            : role === 'student'
                ? peerReview.assignment_type === 'peer-review'
                    ? `${link}/peer-review/${peerReview.peer_review_team}`
                    : `${link}/normal`
                : `${link}`;

        navigate(tileLink);
    };

    return (
        <div>
            <div
                className={
                    peerReview.assignment_type === 'peer-review'
                        ? 'ass-tile'
                        : 'ass-tile'
                }
            >
                <div className='inter-20-medium-white ass-tile-title'>
                    {/*{' '}*/}
                    <span> {title} </span>
                </div>
                <div className='ass-tile-content' onClick={onButtonClick}>
                    <div className='ass-tile-info' >
            <span className='inter-24-bold'>
                {'Assignment Details: '}
                {peerReview.assignment_name}
                {console.log(assignment.assignment_name)}
                <br />

                <span className = 'inter-14-medium-black'>
                    {'Due Date: '}
                    {peerReview.due_date}
                    {console.log(assignment.due_date)}
                    {submitted
                        ? peerReview.grade === -1
                            ? 'Pending'
                            : peerReview.grade
                        : peerReview.due_date}
                </span>

            </span>

                        <span className='inter-20-medium'>
              {submitted
                  ? peerReview.grade === -1
                      ? 'Assigned'
                      : "Graded"
                  : "Pending PR"}

            </span>

                    </div>
                    {!submitted && (
                        <div className='ass-tile-links'>
              <span className='inter-16-bold-blue ass-tile-files' onClick={onFileClick}>
                {peerReview.assignment_type === 'peer-review'
                    ? peerReview.peer_review_rubric
                    : peerReview.assignment_instructions}
              </span>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default PRTile;