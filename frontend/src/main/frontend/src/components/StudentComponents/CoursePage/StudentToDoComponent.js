import React, {useEffect} from 'react';
import {useDispatch, useSelector} from 'react-redux';
import '../../styles/StudentAss.css';
import {useNavigate, useParams} from 'react-router-dom';
import {getCombinedAssignmentPeerReviews} from '../../../redux/features/assignmentSlice';
import axios from "axios";
import "./styles/StudentToDoStyle.css"

const AssignmentTile = ({assignment}) => {
    const navigate = useNavigate()
    const title =  assignment.assignment_type === 'peer-review' ? "Peer Review" : "Assignment"

    const store = useSelector((state) => state);
    const {role} = store.auth;

    const {courseId} = useParams();
    const link = `/details/${role}/${courseId}/${assignment.assignment_id}`;

    const onFileClick = async () => {
        const fileName = assignment.assignment_type === "peer-review" ? assignment.peer_review_rubric : assignment.assignment_instructions
        const assignmentId = assignment.assignment_id
        const url = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}/assignments/${assignmentId}/download/${fileName}`
        await axios.get(url, {responseType: 'blob'})
            .then(res => downloadFile(res.data, fileName))
    }

    const downloadFile = (blob, fileName) => {
        const fileURL = URL.createObjectURL(blob);
        const href = document.createElement("a");
        href.href = fileURL;
        href.download = fileName;
        href.click();
    }

    const onTileClick = () => {
        const tileLink =  assignment.assignment_type === 'peer-review'
            ? `${link}/peer-review/${assignment.peer_review_team}`
            : `${link}/normal`
        navigate(tileLink)
    }

    return (
        <div>
            <div className="ass-tile">
                <div className="outfit-16 ass-tile-title"> <span> {title} </span></div>
                <div className="ass-tile-content">
                   <div className="ass-tile-info" onClick={onTileClick}>
                       <span className="kumba-27"> {assignment.assignment_name} </span>
                       <span className="kumba-25"> {assignment.due_date} </span>
                   </div>
                    <span className="outfit-16 ass-tile-files" onClick={onFileClick}>
                        {assignment.assignment_type === 'peer-review' ? assignment.peer_review_rubric : assignment.assignment_instructions}
                    </span>
                </div>
            </div>
        </div>
    )
}

const StudentToDoComponent = () => {
    const dispatch = useDispatch();
    const store = useSelector((state) => state);
    const {combinedAssignmentPeerReviews, assignmentsLoaded} = store.assignments;
    const {courseId} = useParams();
    const {currentTeamId, teamLoaded} = useSelector((state) => state.teams)

    useEffect(() => {
        dispatch(getCombinedAssignmentPeerReviews({courseId, currentTeamId}));
    }, []);

    return (
        <h3>
            {assignmentsLoaded && teamLoaded ? (
                <div id='assList'>
                    {combinedAssignmentPeerReviews.map(assignment => (
                        <AssignmentTile assignment={assignment}/>
                    ))}
                </div>
            ) : null}
        </h3>
    );
};

export default StudentToDoComponent;
