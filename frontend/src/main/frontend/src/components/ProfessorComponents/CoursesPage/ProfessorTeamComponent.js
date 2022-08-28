import { useEffect, useState } from 'react';
import '../../styles/TeamManager.css';
import axios from 'axios';
import { useSelector } from 'react-redux';
import uuid from 'react-uuid';
import noTeam from '../../../assets/no-team-no-bg.png';
import ProfessorTeamAccordion from './ProfessorTeamAccordion.jsx';

const ProfessorTeamComponent = () => {
  const { currentCourse } = useSelector((state) => state.courses);
  const getTeamsUrl = `${process.env.REACT_APP_URL}/teams/team/get/all/${currentCourse.course_id}`;
  const [teams, setTeams] = useState([]);

  useEffect(() => {
    async function fetchData() {
      const allTeams = await axios
        .get(getTeamsUrl)
        .then((res) => {
          if (res.data.length > 0) return res.data;
          return [];
        })
        .catch((e) => {
          alert(e.response.data);
          return [];
        });
      setTeams(allTeams);
    }
    fetchData();
  }, [getTeamsUrl]);

  return (
    <div className='team-container'>
      {teams.length > 0 ? (
        <div className='acordion-wrapper'>
          <div className='accordion'>
            {teams.map(
              (team) =>
                team && (
                  <div key={uuid()}>
                    <ProfessorTeamAccordion
                      team={team}
                      teams={teams}
                      setTeams={setTeams}
                    />
                  </div>
                )
            )}
          </div>
        </div>
      ) : (
        <div className='no-team-wrapper'>
          <div className='no-team-img-wrapper'>
            <img className='no-team-img' src={noTeam} alt='no team created' />

            <div className='no-team-description'>No Team Created</div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProfessorTeamComponent;
