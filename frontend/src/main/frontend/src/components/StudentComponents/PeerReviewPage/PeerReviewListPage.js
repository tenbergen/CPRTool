import {useDispatch, useSelector} from "react-redux";
import {Link, useNavigate, useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import './styles/PeerReviewListStyle.css'
import axios from 'axios';
import {
    getCombinedAssignmentPeerReviews
} from '../../../redux/features/assignmentSlice';
import HeaderBar from "../../HeaderBar/HeaderBar";
import NavigationContainerComponent from "../../NavigationComponents/NavigationContainerComponent";
import * as React from "react";
import {act} from "react-dom/test-utils";
import uuid from "react-uuid";

const PeerReviewListPage = () => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { role } = useSelector((state) => state.auth);
    const { currentTeamId } = useSelector((state) => state.teams);
    const { assignmentId, courseId } = useParams();
    const { lakerId } = useSelector((state) => state.auth);
    const [givenPeerReviews, setGivenPeerReviews] = useState([]);
    const [receivedPeerReviews, setReceivedPeerReviews] = useState([]);
    const [assignmentName, setAssignmentName] = useState("")
    const getAssignmentUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`;
    const [assignments, setAssignments] = useState([])
    const [teamName, setTeamName] = useState("")
    const [activeState, setActiveState] = useState("given")

    useEffect(async () => {
        dispatch(
            getCombinedAssignmentPeerReviews({ courseId, currentTeamId, lakerId })
        );

        await axios
            .get(`${getAssignmentUrl}/${courseId}/assignments`)
            .then((res) => {
                if (res.data.length > 0) setAssignments(res.data)
            })
            .catch((e) => {
                console.error(e.response);
            });

        const teamUrl = `${process.env.REACT_APP_URL}/teams/team/get/${courseId}/${lakerId}`
        const currentTeam = await axios
            .get(teamUrl)
            .then((res) => {
                return res.data
            })
            .catch((e) => {
                console.error(e.response.data);
                return [];
            });
        await setTeamName(currentTeam.team_id)
        await console.log("Team Name:")
        await console.log(currentTeam.team_id)

        const peerReviewUrl = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/reviews-of/${lakerId}`;
        const givenPeerReviewUrl = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/peer-reviews-given/${currentTeam.team_id}`;
        const givenPeerReviewsList = await axios
            .get(givenPeerReviewUrl)
            .then((res) => {
                return res.data;
            })
            .catch((e) => {
                console.error(e.response.data);
                return [];
            });
        setGivenPeerReviews(givenPeerReviewsList.data);

        const receivedPeerReviewUrl = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/peer-reviews-received/${currentTeam.team_id}`;
        const receivedPeerReviewsList = await axios
            .get(receivedPeerReviewUrl)
            .then((res) => {
                return res.data;
            })
            .catch((e) => {
                console.error(e.response.data);
                return [];
            });
        setReceivedPeerReviews(receivedPeerReviewsList.data);

        await (assignments).forEach((assignment) => {
            if(assignment.assignment_id.toString() === assignmentId){
                setAssignmentName(assignment.assignment_name)
                setTeamName(assignment.team_name)
                setTeamName(assignment.team_name)
            }
        })

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

    const setPRGivenActive = () => {
        if(activeState === "received"){
            const prGivenButton = document.getElementById("prGivenButton")
            const prReceivedButton = document.getElementById("prReceivedButton")

            prGivenButton.classList.remove("pr-button-inactive-state")
            prGivenButton.classList.add("pr-button-active-state")
            prReceivedButton.classList.remove("pr-button-active-state")
            prReceivedButton.classList.add("pr-button-inactive-state")

            setActiveState("given")
        }
    };

    const setPRReceivedActive = () => {
        if(activeState === "given"){
            const prGivenButton = document.getElementById("prGivenButton")
            const prReceivedButton = document.getElementById("prReceivedButton")

            prGivenButton.classList.remove("pr-button-active-state")
            prGivenButton.classList.add("pr-button-inactive-state")
            prReceivedButton.classList.remove("pr-button-inactive-state")
            prReceivedButton.classList.add("pr-button-active-state")

            setActiveState("received")
        }
    };

    return (
        <div className="prl-page-container">
            <HeaderBar/>
            <div className='prl-body-container'>
                <NavigationContainerComponent/>
                <div className='prl-main-container'>
                    <h2 className="inter-28-medium">
                        Peer Reviews - {assignmentName}
                    </h2>
                    <div className="given-and-received-buttons">
                        <div id="prGivenButton" className="pr-button-active-state" onClick={() => setPRGivenActive()}>PR Given</div>
                        <div id="prReceivedButton" className="pr-button-inactive-state" onClick={() => setPRReceivedActive()}>PR Received</div>
                    </div>
                    {activeState === "given"
                        ? (
                            <div className="pr-all-tiles-container">
                                {/*{givenPeerReviews.map(*/}
                                {/*    (pr) =>*/}
                                {/*        pr && (*/}
                                {/*            <div id="REPLACE THIS WITH ACTUAL TILE"/>*/}
                                {/*        )*/}
                                {/*)}*/}

                                <div className='pr-individual-tile'>
                                    <div className="inter-20-medium-white team-tile-title">
                                        {' '}
                                        <span>Team</span>
                                    </div>
                                    <div className="pr-individual-tile-content">
                                        <div className="pr-individual-tile-info">
                                        <span className='inter-24-bold'>
                                            SomeName
                                        </span>
                                            <div className="pr-individual-tile-main-info-container">
                                                <div className="members-count-container">
                                                    <span className="members-count inter-24-medium">5/5</span>
                                                    <span className="inter-12-light-italic">Team Members</span>
                                                </div>
                                                <button
                                                    id="joinTeamButton"
                                                    key={uuid()}
                                                    onClick={() => {


                                                    }}
                                                >Join</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ) : (
                            <div className="pr-all-tiles-container">
                                <h2>Received</h2>
                            </div>


                        )
                    }
                </div>
            </div>
        </div>
    );
}

export default PeerReviewListPage