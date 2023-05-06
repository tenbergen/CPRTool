import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useSelector } from 'react-redux';
import '../../styles/MyTeam.css';
import axios from 'axios';
import uuid from 'react-uuid';
import * as React from "react";

const MyTeamComponent = () => {
  const { courseId } = useParams();
  const { currentTeamId, teamLoaded } = useSelector((state) => state.teams);
  const membersUrl = `${process.env.REACT_APP_URL}/teams/team/${courseId}/get/${currentTeamId}`;
  const [members, setMembers] = useState([]);

  const getTeamsUrl = `${process.env.REACT_APP_URL}/teams/team/get/all/${courseId}`
  const [teamSize, setTeamSize] = useState(0)

  useEffect(async () => {
    axios.get(membersUrl).then((r) => {
      for (let i = 0; i < r.data.team_members.length; i++) {
        // setMembers((arr) => [...arr, r.data.team_members[i]]);

        const studentUrl = `${process.env.REACT_APP_URL}/view/professor/students/${r.data.team_members[i]}`;
        axios.get(studentUrl).then((r) => {
          setMembers((arr) => [
            ...arr,
            [r.data.first_name + ' ' + r.data.last_name, r.data.student_id],
          ]);
        });
      }
    });

    const allTeams = await axios
        .get(getTeamsUrl)
        .then((res) => {
          if (res.data.length > 0) return res.data
          return []
        })
        .catch((e) => {
          console.error(e.response.data)
          return []
        })
    const openTeams = allTeams.filter((team) => team.team_id === currentTeamId)
    setTeamSize(openTeams[0].team_size)
  }, [membersUrl, getTeamsUrl]);

  return (
      <div>
        {' '}
        {teamLoaded ? (
            <div className='my-team-container'>
              <div className='team-header inter-24-bold'>Teams</div>
              <div className='my-team-tile'>
                <div className="inter-20-medium-white my-team-tile-title">
                  {' '}
                  <span>Team</span>
                </div>
                <div id="myTeamTileContentAndTableContainer">
                  <div className="my-team-tile-content">
                    <div className="my-team-tile-info">
                    <span className='inter-28-bold'>
                      {currentTeamId}
                    </span>
                      <div className="my-members-count-container">
                        <span className="my-members-count inter-24-medium-green">{members.length}/{teamSize}</span>
                        <span className="inter-12-light-italic">Team Members</span>
                      </div>
                    </div>
                  </div>
                  <span className="inter-20-medium">Team Members</span>
                  <table id="myMembersTable" cellSpacing="0">
                    <thead>
                      <tr>
                        <th align="left" className="inter-20-bold">Name</th>
                        <th align="left" className="inter-20-bold">Email ID</th>
                      </tr>
                    </thead>
                    <tbody className="inter-20-medium">
                    {members.map((member) => (
                        <tr>
                          <td>{member[0]}</td>
                          <td>{member[1]}</td>
                        </tr>
                    ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
        ) : null}
      </div>
  );
};

export default MyTeamComponent;
