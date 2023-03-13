// import React, {useEffect, useState} from 'react';
// import '../../styles/FinalGrade.css';
// import {useDispatch, useSelector} from 'react-redux';
// import {useParams} from 'react-router-dom';
// import {getAssignmentDetailsAsync} from '../../../redux/features/assignmentSlice';
// import axios from 'axios';
// import SidebarComponent from "../../SidebarComponent";
// import GradeAssBarComponent from "../../GradeAssBarComponent";
//
// function TeacherFinalGradeComponent() {
//     const dispatch = useDispatch();
//     const {currentAssignment, currentAssignmentLoaded} = useSelector((state) => state.assignments);
//     const {courseId, assignmentId,teamId} = useParams();
//
//     const [teams, setTeams] = useState(Array())
//     const [isLoaded, setLoaded] = useState(false)
//
//     useEffect(() => {
//         dispatch(getAssignmentDetailsAsync({courseId, assignmentId}));
//         const urlGrade = 'http://moxie.cs.oswego.edu:13125/peer-review/assignments/'
//             + courseId  + '/' + currentAssignment.assignment_id + '/' + teamId + '/getTeamGrades'
//
//         // first axios call, get grades and team name
//         {axios.get(urlGrade).then(gradeResponse => {
//             const teamsUrl = 'http://moxie.cs.oswego.edu:13125/teams/team/' + courseId + '/get/' + teamId
//             let lead
//             let peerReviews = Array()
//
//             // get team lead to call url to get peer review info
//             {axios.get(teamsUrl).then(teamResponse => {
//                 lead = teamResponse.data.team_lead
//
//                 const urlPeerReview = 'http://moxie.cs.oswego.edu:13125/peer-review/assignments/' + courseId + '/'
//                     + currentAssignment.assignment_id + '/reviews-of/' + lead
//
//                 // call to get peer review info from team lead
//                 {axios.get(urlPeerReview).then(peerResponse => {
//                     for (let i = 0; i < peerResponse.data.length; i++) {
//                         peerReviews.push(peerResponse.data[i])
//                     }
//                     for(let j = 0; j < gradeResponse.data.teams.length; j++) {
//                         const data = gradeResponse.data.teams[j]
//                         // put all data into 1 JSON and call to setTeams
//                         for(let k = 0; k < peerReviews.length; k++) {
//                             if(peerReviews[k].reviewed_by === data.team_name) {
//                                 data.submission_name = peerReviews[k].submission_name
//                                 data.reviewed_by = peerReviews[k].reviewed_by
//                             }
//                         }
//                         setTeams((arr) => [...arr, data]);
//                     }
//                 })}
//             })}
//         })}
//         setLoaded(true)
//     }, []);
//
//     const print = () =>{
//         console.log(teams)
//     };
//
//     const downloadFile = (blob, fileName) => {
//         const fileURL = URL.createObjectURL(blob);
//         const href = document.createElement('a');
//         href.href = fileURL;
//         href.download = fileName;
//         href.click();
//     };
//
//     const onFileClick = async (fileName) => {
//         const url = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}/assignments/${assignmentId}/peer-review/download/${fileName}`
//
//         await axios
//             .get(url, {responseType: 'blob'})
//             .then((res) => downloadFile(res.data, fileName));
//     };
//
//     const onFeedBackClick = async (teamName) => {
//         const url = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/${teamName}/${teamId}/download`;
//
//         await axios
//             .get(url, {responseType: 'blob'})
//             .then((res) => downloadFile(res.data, teamName));
//     };
//
//     const onDownloadClick = async (peerReview) => {
//         console.log(peerReview)
//         const srcTeamName = peerReview.reviewed_by
//         const url = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/${srcTeamName}/${teamId}/download`
//
//         await axios.get(url, {responseType: 'blob'})
//             .then(res => downloadFile(res.data, peerReview.submission_name))
//     }
//
//     const finalGrade = () => {
//         let total = 0
//         let size = 0
//         let hasGrade = false
//         for(let i = 0; i < teams.length; i++) {
//             let grade = teams[i].grade_given
//             if(grade !== 'pending' || undefined) {
//                 total += grade
//                 size++
//                 hasGrade = true
//             }
//         }
//         if(!hasGrade){
//             return 'Pending'
//         }
//         return size !== teams.length ? 'Pending' : total/size
//     }
//
//     return (
//         <div>
//             { isLoaded ? (
//                 <div className='scp-parent'>
//                     <SidebarComponent/>
//                     <div className='scp-container'>
//                         <GradeAssBarComponent/>
//                         <div className='scp-component'>
//                             <div>
//                                 {currentAssignmentLoaded ?
//                                     <div className="sac-parent">
//                                         <h2 className="assignment-name">{currentAssignment.assignment_name}</h2>
//                                         <div className="sac-content">
//                                             <div>
//                                                 <span className="sac-title"> Instructions </span>
//                                                 <span className="sac-date sac-title">Due Date: {currentAssignment.due_date}</span>
//                                                 <br/>
//                                                 <p>
//                                                     <span className="sac-text"> {currentAssignment.instructions} </span>
//                                                 </p>
//                                             </div>
//                                             <br/>
//
//                                             <div>
//                                                 <div className="ap-assignment-files">
//                                                     <span className="sac-title"> Rubric: </span>
//                                                     <span className="sac-filename" onClick={() => onFileClick(currentAssignment.peer_review_rubric)}>
//                                     {currentAssignment.peer_review_rubric}
//                                 </span>
//                                                 </div>
//
//                                                 <div className="ap-assignment-files">
//                                                     <span className="sac-title">Template:</span>
//                                                     <span className="sac-filename" onClick={() => onFileClick(currentAssignment.peer_review_template)}>
//                                     {currentAssignment.peer_review_template}
//                                 </span>
//                                                 </div>
//
//                                                 <div className="ap-assignment-files">
//                                                     <span className="sac-title">Team Files:</span>
//                                                     <span className="sac-filename" onClick={() => onFeedBackClick(teamId)}>
//                                     {teamId} File
//                                 </span>
//                                                 </div>
//                                             </div>
//                                             <br/>
//                                             <div>
//                                                 <div>
//                                                     <span className="sac-title"> Peer reviews: </span>
//                                                     <div className='peerReviewList'>
//                                                         {teams.map(team => (
//                                                             <li className='peerReviewListItem'>
//                                                                 <b> {team.team_name}
//                                                                 </b> <span
//                                                                 className="sac-filename"
//                                                                 onClick={() => onDownloadClick(team)}>
//                                                                 {team.grade_given}
//                                                             </span>
//                                                             </li>
//                                                         ))}
//                                                     </div>
//                                                 </div>
//                                             </div>
//                                             <br/><br/>
//                                             <div>
//                                                 <span className="sac-title"> Grade: {finalGrade()}</span>
//                                             </div>
//                                         </div>
//                                     </div> : null
//                                 }
//                             </div>
//                         </div>
//                     </div>
//                 </div>
//             ) : null}
//         </div>
//     );
// }
//
// export default TeacherFinalGradeComponent;
//
//
//
