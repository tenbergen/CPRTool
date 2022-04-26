import React, {useEffect, useState} from 'react';
import '../../styles/FinalGrade.css';
import {useDispatch, useSelector} from 'react-redux';
import {useParams} from 'react-router-dom';
import {getAssignmentDetailsAsync} from '../../../redux/features/assignmentSlice';
import axios from 'axios';
import AssBarComponent from "../../AssBarComponent";
import SidebarComponent from "../../SidebarComponent";
import GradeAssBarComponent from "../../GradeAssBarComponent";
import ProfessorSubmissionsComponent from "./ProfessorSubmissionsComponent";

function TeacherFinalGradeComponent() {
    const dispatch = useDispatch();
    const {currentAssignment, currentAssignmentLoaded} = useSelector((state) => state.assignments);
    const {courseId, assignmentId,teamId} = useParams();


    const [teams, setTeams] = useState(Array());
    const [isLoaded, setLoaded] = useState(false)

    useEffect(() => {
        dispatch(getAssignmentDetailsAsync({courseId, assignmentId}));
        const urlGrade = 'http://moxie.cs.oswego.edu:13125/peer-review/assignments/'
            + courseId  + '/' + currentAssignment.assignment_id + '/' + teamId + '/getTeamGrades'

        {axios.get(urlGrade).then(r => {
            console.log(r)
            for (let i = 0; i < r.data.teams.length; i++) {
                setTeams((arr) => [...arr, r.data.teams[i]]);
            }
        })}

        setLoaded(true)
    }, []);

    const print = () =>{
        console.log(teams)
    };

    const downloadFile = (blob, fileName) => {
        const fileURL = URL.createObjectURL(blob);
        const href = document.createElement('a');
        href.href = fileURL;
        href.download = fileName;
        href.click();
    };

    const onFileClick = async (fileName) => {
        const url = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}/assignments/${assignmentId}/peer-review/download/${fileName}`

        await axios
            .get(url, {responseType: 'blob'})
            .then((res) => downloadFile(res.data, fileName));
    };

    const onFeedBackClick = async (teamName) => {
        const url = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/${teamName}/${teamId}/download`;

        await axios
            .get(url, {responseType: 'blob'})
            .then((res) => downloadFile(res.data, teamName));
    };

    /*
    *
    * moxie.cs.oswego.edu:13125/peer-review/assignments/{course_id}/{assignment_id}/{team_name}/getTeamGrades
    *
    * In Agreed Upon Endpoints
    *
    * {courseID}/{assignmentID}/{srcTeamName}/{destTeamName}/download

Description: Endpoint for a team to get a peer review given by another team
in Calib
    *
    *
    * */

    return (
        <div>
            { isLoaded ? (
                <div className='scp-parent'>
                    <SidebarComponent/>
                    <div className='scp-container'>
                        <GradeAssBarComponent/>
                        <div className='scp-component'>
                            <div>
                                {currentAssignmentLoaded ?
                                    <div className="sac-parent">
                                        <h2 className="assignment-name">{currentAssignment.assignment_name}</h2>
                                        <div className="sac-content">
                                            <div>
                                                <span className="sac-title"> Instructions </span>
                                                <span className="sac-date sac-title">Due Date: {currentAssignment.due_date}</span>
                                                <br/>
                                                <p>
                                                    <span className="sac-text"> {currentAssignment.instructions} </span>
                                                </p>
                                            </div>
                                            <br/>

                                            <div>
                                                <div className="ap-assignment-files">
                                                    <span className="sac-title"> Rubric: </span>
                                                    <span className="sac-filename" onClick={() => onFileClick(currentAssignment.peer_review_rubric)}>
                                    {currentAssignment.peer_review_rubric}
                                </span>
                                                </div>

                                                <div className="ap-assignment-files">
                                                    <span className="sac-title">Template:</span>
                                                    <span className="sac-filename" onClick={() => onFileClick(currentAssignment.peer_review_template)}>
                                    {currentAssignment.peer_review_template}
                                </span>
                                                </div>

                                                <div className="ap-assignment-files">
                                                    <span className="sac-title">Team Files:</span>
                                                    <span className="sac-filename" onClick={() => currentAssignment.team_file}>
                                    {currentAssignment.team_file}
                                </span>
                                                </div>
                                            </div>
                                            <br/>
                                            <div>
                                                <div>
                                                    <span className="sac-title"> Peer reviews: </span>
                                                    <div className='peerReviewList'>
                                                        {teams.map(team => (
                                                            <li className='peerReviewListItem'>
                                                                <b> {team.team_name}
                                                                </b> <span className="sac-filename"> {team.grade_given} </span>
                                                            </li>
                                                        ))}
                                                    </div>
                                                </div>
                                            </div>
                                            <br/><br/>
                                            <div>
                                                <span className="sac-title"> Grade: {teams.grade}</span>
                                            </div>
                                        </div>
                                    </div> : null
                                }
                            </div>
                        </div>
                    </div>
                </div>
            ) : null}
        </div>
    );
}

export default TeacherFinalGradeComponent;



