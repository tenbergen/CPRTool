import {useDispatch, useSelector} from "react-redux";
import {useNavigate, useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import axios from 'axios';
import {
    getCombinedAssignmentPeerReviews
} from '../../../redux/features/assignmentSlice';

const PeerReviewListPage = () => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { role } = useSelector((state) => state.auth);
    const { currentTeamId, teamName } = useSelector((state) => state.teams);
    const { assignmentId, courseId } = useParams();
    const { lakerId } = useSelector((state) => state.auth);
    const [givenPeerReviews, setGivenPeerReviews] = useState([]);
    const [receivedPeerReviews, setReceivedPeerReviews] = useState([]);

    useEffect(async () => {
        dispatch(
            getCombinedAssignmentPeerReviews({ courseId, currentTeamId, lakerId })
        );

        console.log(teamName)

        // const peerReviewUrl = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/reviews-of/${lakerId}`;
        const givenPeerReviewUrl = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/peer-reviews-given/${teamName}`;
        const givenPeerReviewsList = await axios
            .get(givenPeerReviewUrl)
            .then((res) => {
                if (res.data.length > 0) return res.data;
                return [];
            })
            .catch((e) => {
                console.error(e.response.data);
                return [];
            });
        setGivenPeerReviews(givenPeerReviewsList.data);

        const receivedPeerReviewUrl = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/peer-reviews-received/${teamName}`;
        const receivedPeerReviewsList = await axios
            .get(receivedPeerReviewUrl)
            .then((res) => {
                if (res.data.length > 0) return res.data;
                return [];
            })
            .catch((e) => {
                console.error(e.response.data);
                return [];
            });
        setReceivedPeerReviews(receivedPeerReviewsList.data);
    }, [courseId, currentTeamId, lakerId, dispatch]);

    const onFileClick = async () => {
        const fileName =
            givenPeerReviews[0].assignment_type = 'peer-review';
        const assignmentId = givenPeerReviews[0].assignment_id;
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

    };

    return (
        <div>

        </div>
    );
}

export default PeerReviewListPage