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
import {base64StringToBlob} from "blob-util";
import { getAssignmentDetailsAsync } from '../../../redux/features/assignmentSlice'
const PeerReviewListPage = () => {
    const dispatch = useDispatch();
    const { currentTeamId } = useSelector((state) => state.teams);
    const { courseId, assignmentId, teamId } = useParams();
    const { lakerId } = useSelector((state) => state.auth);
    const [givenPeerReviews, setGivenPeerReviews] = useState([]);
    const [receivedPeerReviews, setReceivedPeerReviews] = useState([]);
    const [assignmentName, setAssignmentName] = useState("")
    const getAssignmentUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`;
    const [assignments, setAssignments] = useState([])
    const [teamName, setTeamName] = useState("")
    const [activeState, setActiveState] = useState("given")
    const [showDetailsModal, setShowDetailsModal] = useState(false)
    const [assignment, setAssignment] = useState({})
    const navigate = useNavigate()
    const { currentAssignment, currentAssignmentLoaded } = useSelector(
        (state) => state.assignments
    )
    const [grade, setGrade] = useState(undefined)
    const feedbackFileFormData = new FormData()

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

        await (assignments).forEach((currentAssignment) => {
            if(currentAssignment.assignment_id.toString() === assignmentId){
                setAssignmentName(currentAssignment.assignment_name)
                setTeamName(currentAssignment.team_name)
                setAssignment(currentAssignment)
            }
        })

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

        const givenPeerReviewUrl = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/peer-reviews-given/${currentTeam.team_id}`;
        const givenPeerReviewsList = await axios
            .get(givenPeerReviewUrl)
            .then((res) => {
                return res;
            })
            .catch((e) => {
                console.error(e.response.data);
                return [];
            });
        setGivenPeerReviews(Array.from(givenPeerReviewsList.data));

        const receivedPeerReviewUrl = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/peer-reviews-received/${currentTeam.team_id}`;
        const receivedPeerReviewsList = await axios
            .get(receivedPeerReviewUrl)
            .then((res) => {
                return res;
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

    const downloadGivenPeerReview = async (teamGraded) => {
        const url = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/${teamName}/${teamGraded}/download`;

        await axios
            .get(url, { responseType: 'blob' })
            .then((res) => prepareFeedbackFile(res["headers"]["content-disposition"], res.data.text()))
            .catch((e) => {
                alert(`Error : ${e.response.data}`);
            });
    }

    const downloadReceivedPeerReview = async (gradingTeam) => {
        const url = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/${gradingTeam}/${teamName}/download`;

        await axios
            .get(url, { responseType: 'blob' })
            .then((res) => prepareFeedbackFile(res["headers"]["content-disposition"], res.data.text()))
            .catch((e) => {
                alert(`Error : ${e.response.data}`);
            });
    }

    const prepareFeedbackFile = (feedbackDataName, feedbackData) => {
        let filename = ""
        const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
        const matches = filenameRegex.exec(feedbackDataName);
        if (matches != null && matches[1]) {
            filename = matches[1].replace(/['"]/g, '');
        }
        feedbackData.then((res) => {
            if(filename.endsWith(".pdf")){
                downloadFile(base64StringToBlob(res, 'application/pdf'), filename)
            }else{
                downloadFile(base64StringToBlob(res, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'), filename)
            }
        })
    };

    const DetailsModal = () => {
        const [isModalOpen, setIsModalOpen] = useState(false);
        const handleCloseModal = () => {
            setIsModalOpen(false);
        };

        const onTemplateClick = async (fileName) => {
            if(fileName.endsWith(".pdf")){
                downloadFile(new Blob([Uint8Array.from(currentAssignment.peer_review_template_data.data)], {type: 'application/pdf'}), fileName)
            }else if(fileName.endsWith(".docx")){
                downloadFile(new Blob([Uint8Array.from(currentAssignment.peer_review_template_data.data)], {type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'}), fileName)
            }else{
                downloadFile(new Blob([Uint8Array.from(currentAssignment.peer_review_template_data.data)], {type: 'application/zip'}), fileName)
            }
        };

        const onRubricFileClick = async (fileName) => {
            if(fileName.endsWith(".pdf")){
                downloadFile(new Blob([Uint8Array.from(currentAssignment.rubric_data.data)], {type: 'application/pdf'}), fileName)
            }else if(fileName.endsWith(".docx")){
                downloadFile(new Blob([Uint8Array.from(currentAssignment.rubric_data.data)], {type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'}), fileName)
            }else{
                downloadFile(new Blob([Uint8Array.from(currentAssignment.rubric_data.data)], {type: 'application/zip'}), fileName)
            }
        };

        const onTeamFileClick = async (fileName) => {
            if(fileName.endsWith(".pdf")){
                downloadFile(new Blob([Uint8Array.from(currentAssignment.submission_data.data)], {type: 'application/pdf'}), fileName)
            }else if(fileName.endsWith(".docx")){
                downloadFile(new Blob([Uint8Array.from(currentAssignment.submission_data.data)], {type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'}), fileName)
            }else{
                downloadFile(new Blob([Uint8Array.from(currentAssignment.submission_data.data)], {type: 'application/zip'}), fileName)
            }
        }

        const uploadFeedback = async (teamGraded) => {
            const url = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/${teamName}/${teamGraded}/download`;
            await axios
                .get(url, { responseType: 'blob' })
                .then((res) => prepareFeedbackFile(res["headers"]["content-disposition"], res.data.text()))
                .catch((e) => {
                    alert(`Error : ${e.response.data}`);
                });
        }
        const handleSubmit = async () => {
            const submitAssUrl = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/${currentTeamId}/${teamId}/${grade}/upload`

            await axios
                .post(submitAssUrl, feedbackFileFormData)
                .then((res) => {
                    alert('Successfully uploaded peer review')
                    navigate(`/student/${courseId}`, {
                        state: { initialComponent: 'Submitted' },
                    })
                })
                .catch((e) => {
                    console.error(e.response)
                    alert('Error uploading peer review')
                })
            setGrade(undefined)
        }
        return (
            <>
                {isModalOpen && (
                    <div id="myModal" className="modal">
                        <div className="modal-content">
                            <div className="inter-20-medium-white ass-tile-title">
                                {" "}
                                <span> {"Assignment Details"} </span>
                                <button className="close-modal" onClick={handleCloseModal}>
                                    X
                                </button>
                            </div>
                            <div className="ass-tile-content">
              <span className="inter-24-bold">
                {assignment.assignment_name}
              </span>
                                <span className="inter-20-medium span1-ap">
                Due: {assignment.due_date}
              </span>
                                <br /> <br /> <br />
                                <p className="inter-20-medium">Instructions:</p>
                                <p className="inter-16-medium-black">
                                    {assignment.instructions}
                                </p>
                                <br />
                                <br />
                                <div className="button-group">
                                    <button
                                        className="blue-button-top"
                                        onClick={onRubricFileClick}
                                    >
                                        Rubric Download
                                    </button>
                                    <button
                                        className="blue-button-top"
                                        onClick={onTemplateClick}
                                    >
                                        Template: Download
                                    </button>
                                    <button
                                        className="blue-button-top"
                                        onClick={onTeamFileClick}
                                    >
                                        Team Files : Download
                                    </button>
                                
                                <div className="blue-button-bottom">
                                    <input
                                        type="file"
                                        name="assignment_files"
                                        accept=".pdf,.docx"
                                        onChange={(e) => handleSubmit(e)}
                                        required
                                    />
                                </div>
                                <div className="grade-input-field">
                                    <label> Grade: </label>
                                    <input
                                        type="number"
                                        min="0"
                                        name="peer_review_grade"
                                        value={grade}
                                        required
                                        onChange={(e) => setGrade(e.target.value)}
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                    </div>
                )}
            </>
        );
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
                                {givenPeerReviews.map(
                                    (pr) =>
                                        pr && (
                                            <div className='pr-individual-tile'>
                                                <div className="inter-20-medium-white team-tile-title">
                                                    {' '}
                                                    <span>Team</span>
                                                </div>
                                                <div className="pr-individual-tile-content">
                                                    <div className="pr-individual-tile-info">
                                                        <span className='inter-24-bold'>
                                                            {pr.team_name}
                                                        </span>
                                                        {pr.grade >= 0
                                                            ? (
                                                                <div className="pr-individual-tile-main-info-container">
                                                                    <div className="pr-grade-given-container">
                                                                        <span className="pr-grade-given inter-24-medium">{pr.grade}%</span>
                                                                        <span className="inter-12-light">PR Grade Given</span>
                                                                    </div>
                                                                    <button
                                                                        id="prDownloadButton"
                                                                        key={uuid()}
                                                                        onClick={() => downloadGivenPeerReview(pr.team_name)}
                                                                    >
                                                                        <div id="prTileDownloadIcon"></div>
                                                                        <p>Download PR</p>
                                                                    </button>
                                                                </div>
                                                            ) : (
                                                                <div className="pr-individual-tile-main-info-container">
                                                                    <div className="pr-grade-given-container">
                                                                        <span className="inter-24-medium">-</span>
                                                                    </div>
                                                                    <button
                                                                        id="prDownloadButton"
                                                                        className="pr-download-and-details-button"
                                                                        key={uuid()}
                                                                        onClick={() => setShowDetailsModal(true)}
                                                                    >
                                                                        <div id="prTileDownloadIcon"></div>
                                                                        <p>View Details</p>
                                                                    </button>
                                                                </div>
                                                            )
                                                        }
                                                    </div>
                                                </div>
                                            </div>
                                        )
                                )}
                            </div>
                        ) : (
                            <div className="pr-all-tiles-container">
                                {receivedPeerReviews.map(
                                    (pr) =>
                                        pr && (
                                            <div className='pr-individual-tile'>
                                                <div className="inter-20-medium-white team-tile-title">
                                                    {' '}
                                                    <span>Team</span>
                                                </div>
                                                <div className="pr-individual-tile-content">
                                                    <div className="pr-individual-tile-info">
                                                        <span className='inter-24-bold'>
                                                            {pr.reviewed_by}
                                                        </span>
                                                        {pr.grade >= 0
                                                            ? (
                                                                <div className="pr-individual-tile-main-info-container">
                                                                    <div className="pr-grade-given-container">
                                                                        <span className="pr-grade-given inter-24-medium">{pr.grade}%</span>
                                                                        <span className="inter-12-light">PR Grade Given</span>
                                                                    </div>
                                                                    <button
                                                                        id="prDownloadButton"
                                                                        key={uuid()}
                                                                        onClick={() => downloadReceivedPeerReview(pr.reviewed_by)}
                                                                    >
                                                                        <div id="prTileDownloadIcon"></div>
                                                                        <p>Download PR</p>
                                                                    </button>
                                                                </div>
                                                            ) : (
                                                                <div className="pr-individual-tile-main-info-container">
                                                                    <div className="pr-grade-given-container">
                                                                        <span className="inter-24-medium">-</span>
                                                                    </div>
                                                                    <span className="inter-16-medium-black not-submitted-text">Not Submitted</span>
                                                                </div>
                                                            )
                                                        }
                                                    </div>
                                                </div>
                                            </div>
                                        )
                                )}
                            </div>
                        )
                    }
                </div>
            </div>
            <div>{showDetailsModal ? DetailsModal() : null}</div>
        </div>
    );
}

export default PeerReviewListPage
