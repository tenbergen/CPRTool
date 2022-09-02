import { useState } from 'react';
import axios from 'axios';
import { useSelector } from 'react-redux';
import '../../styles/TeamManager.css';
import uuid from 'react-uuid';

const ProfessorTeamAccordion = ({ team, teams, setTeams }) => {
  const { currentCourse } = useSelector((state) => state.courses);
  const [isActive, setActive] = useState([]);
  const [isAdd, setAdd] = useState([]);
  const [studentId, setStudentId] = useState('');
  const members = [];
  const handleSetActive = () => {
    if (isActive.includes(team)) {
      setActive(isActive.filter((t) => t !== team));
    } else {
      setActive((arr) => [...arr, team]);
    }
  };
  const handleSetAdd = () => {
    setAdd(isAdd.filter((t) => t === team));
    setAdd((arr) => [...arr, team]);
  };

  for (let i = 0; i < team.team_members.length; i++) {
    members.push(team.team_members[i]);
  }

  const handleAddStudent = async () => {
    const data = {
      team_id: team.team_id,
      course_id: currentCourse.course_id,
      student_id: studentId,
    };

    await axios
      .put(
        `${process.env.REACT_APP_URL}/teams/professor/team/add-student`,
        data
      )
      .then((r) => {
        alert('Successfully added student.');
        window.location.reload();
      })
      .catch((e) => {
        console.error(e.response.data);
        alert(`Error adding student: ${e.response.data}`);
      });
    setStudentId('');
  };

  const handleRemoveStudent = async (id) => {
    let confirmAction = window.confirm(
      `Are you sure you want to delete this student from team ${team.team_id}?`
    );
    if (!confirmAction) return;

    const data = {
      team_id: team.team_id,
      course_id: currentCourse.course_id,
      student_id: id,
    };

    await axios
      .put(
        `${process.env.REACT_APP_URL}/teams/professor/team/remove-student`,
        data
      )
      .then((r) => {
        alert('Successfully removed student.');
        window.location.reload();
      })
      .catch((e) => {
        console.error(e);
        alert('Error removing student.');
      });
  };

  const handleDeleteTeam = async (name) => {
    let confirmAction = window.confirm(
      `Are you sure you want to delete team ${team.team_id}?`
    );
    if (!confirmAction) return;

    const data = {
      team_id: team.team_id,
      course_id: currentCourse.course_id,
    };

    await axios
      .delete(`${process.env.REACT_APP_URL}/teams/professor/team/delete`, {
        data,
      })
      .then((r) => {
        alert('Successfully removed team.');
        setTeams(teams.filter((t) => t !== team));
      })
      .catch((e) => {
        console.error(e);
        alert('Error removing team.');
      });
  };

  return (
    <div className='accordionItem'>
      <div className='accordionTitle' onClick={handleSetActive}>
        <div className='accordionWrapper'>
          <div className='accordionHead'>
            {isActive.includes(team) ? '-' : '+'}
            <div className='accordionTeamTitle'>{team.team_id}</div>
          </div>
          <span
            onClick={() => handleDeleteTeam(team.team_id)}
            className='crossMarkTeam'
          >
            &#10060;
          </span>
        </div>
      </div>
      {isActive.includes(team) && (
        <div className='accordionContent'>
          {members.map(
            (name) =>
              name && (
                <div key={uuid()} className='memberItem'>
                  <div className='memberWrapper'>
                    <div className='teamMember'>{name}</div>
                    <span
                      onClick={() => handleRemoveStudent(name)}
                      className='crossMarkTeam'
                    >
                      &#10060;
                    </span>
                  </div>
                </div>
              )
          )}
          {isAdd.includes(team) ? (
            <div className='teamMember'>
              <label>Student ID:</label>
              <input
                type='text'
                value={studentId}
                required
                onChange={(e) => setStudentId(e.target.value)}
                className='emailInput'
              />

              <button onClick={handleAddStudent} className='teamA'>
                Add
              </button>
            </div>
          ) : (
            <img
              onClick={handleSetAdd}
              className='buttonPlusTeam'
              // type='image'
              alt='plus-button'
              src={require('../../styles/plus-purple.png')}
            />
          )}
        </div>
      )}
    </div>
  );
};

export default ProfessorTeamAccordion;
