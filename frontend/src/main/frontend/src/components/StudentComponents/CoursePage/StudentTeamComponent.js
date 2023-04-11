import { useEffect, useState } from "react";
import "../../styles/StudentAss.css";
import '../../styles/DeleteModal.css';
import { useDispatch, useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import '../../styles/StudentTeamStyle.css';
import { getCurrentCourseTeamAsync } from '../../../redux/features/teamSlice';
import '../../styles/AssignmentTile.css'
import axios from 'axios';

import uuid from "react-uuid";

const StudentTeamComponent = () => {

const dispatch = useDispatch();
const { courseId } = useParams();
const [teams, setTeams] = useState([]);
const { lakerId } = useSelector((state) => state.auth);
const { courseAssignments } = useSelector((state) => state.assignments);
const getTeamsUrl = `${process.env.REACT_APP_URL}/teams/team/get/all/${courseId}`;
const [showModal, setShow] = useState(false);


useEffect(() => {
    async function fetchData() {
        const allTeams = await axios
            .get(getTeamsUrl)
            .then((res) => {
                if (res.data.length > 0) return res.data;
                return [];
            })
            .catch((e) => {
                console.error(e.response.data);
                return [];
            });
        const openTeams = allTeams.filter((team) => !team.team_full);
        setTeams(openTeams)
      console.log(teams)
    }
    fetchData();
}, [getTeamsUrl]);

const Modal = (teamId) => {
  return (
    <div id='modal'>
      <div id='modalContent'>
        <span id='deleteSpan'>
          Are you sure you want to delete this course?
        </span>

        <div id='deleteButtons'>
          <button id='ecc-delete-button-delete' class='inter-16-medium-red' onClick={joinTeam(teamId)}>Delete</button>
          <button id='ecc-delete-button-cancel' class='inter-16-medium-white' onClick={() => setShow(false)}>Cancel</button>
        </div>
      </div>
    </div>
  );
};

const joinTeam = async (teamId) => {
  const joinUrl = `${process.env.REACT_APP_URL}/teams/team/join`;
  const data = { team_id: teamId, course_id: courseId, student_id: lakerId };
  await axios
      .put(joinUrl, data)
      .then((res) => {
          alert(`Successfully joined team ${teamId}`);
          dispatch(getCurrentCourseTeamAsync({ courseId, lakerId }));
      })
      .catch((e) => {
          console.error(e);
          alert('Error joining team');
      });
};




const createTeam = async () => {
  // const team_name = prompt('Enter team name: ');
  // const createUrl = `${process.env.REACT_APP_URL}/teams/team/create`;
  // const createData = {
  //     course_id: courseId,
  //     student_id: lakerId,
  //     team_name: team_name,
  // };

  // if (team_name.split(' ').length > 1) {
  //     alert('Please enter a team name with no spaces!');
  //     return;
  // }
  // if (team_name === '') {
  //     alert('Team name cannot be empty!');
  //     return;
  // }
  // if (team_name.length > 20){
  //     alert('Team name is too long!');
  //     return;
  // }

  // await axios
  //     .post(createUrl, createData)
  //     .then((res) => {
  //         alert('Successfully created team');
  //         dispatch(getCurrentCourseTeamAsync({ courseId, lakerId }));
  //     })
  //     .catch((e) => {
  //         console.error(e);
  //         alert('Error creating team');
  //     });
};

return (
    <div id = "teams">
        <div className="teams-list">
        <h1 className="inter-36-bold" id="welcome-message">
            Create or Join a Team
          </h1>
        </div>
  <div className="team-container">
    <div className="team-list-wrapper">
      <div id="team-list">
          {teams.map(
            (team) =>
              team && (
                <div className={'ass-tile'} style={{ marginLeft: 0, marginRight: 0 }}>
                  <div className="inter-20-medium-white ass-tile-title">
                    {' '}
                    <span>Team</span>
                  </div>
                  <div className="ass-tile-content" style={{ padding: '15px', boxSizing: 'border-box', cursor: 'default' }}>
                    <div className="ass-tile-info" style={{ display: 'block', boxSizing: 'border-box' }}>
                      <h2 className='inter-30' id='teamTitle'>
                        {team.team_id}
                      </h2>

                    </div>
                  </div>
                </div>
              )
          )}
        </div>
    </div>
</div>
            <div id='createTeamButton'>
                <button class='green-button-large' onClick={createTeam}> Create New Team</button>
            </div>
  </div>
);};
export default StudentTeamComponent;
