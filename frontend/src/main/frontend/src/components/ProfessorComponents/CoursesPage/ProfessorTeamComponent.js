import { useEffect, useState } from 'react';
import '../../styles/TeamManager.css';
import axios from 'axios';
import { useSelector } from 'react-redux';

const ProfessorTeamComponent = () => {
  const { currentCourse } = useSelector((state) => state.courses);
  const getTeamsUrl = `${process.env.REACT_APP_URL}/teams/team/get/all/${currentCourse.course_id}`;
  const [teams, setTeams] = useState([]);
  const [isActive, setActive] = useState(Array());
  const [isAdd, setAdd] = useState(Array());
  const [studentId, setStudentId] = useState({ id: '' });
  const { id } = studentId;

  useEffect(async () => {
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
  }, []);

  const ProfessorTeamAccordion = (team) => {
    const members = Array();
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

    const handleStudentId = (e) =>
      setStudentId({ ...studentId, [e.target.name]: e.target.value });

    const handleAddStudent = async () => {
      const data = {
        team_id: team.team_id,
        course_id: currentCourse.course_id,
        student_id: id,
      };
      console.log(data);

      await axios
        .put(
          `${process.env.REACT_APP_URL}/teams/professor/team/add-student`,
          data
        )
        .then((r) => {
          console.log(r);
          alert('Successfully added student.');
          window.location.reload();
        })
        .catch((e) => {
          console.log(e.response.data);
          alert(`Error adding student: ${e.response.data}`);
        });
      setStudentId({ ...studentId, id: '' });
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
          console.log(r);
          alert('Successfully removed student.');
          window.location.reload();
        })
        .catch((e) => {
          console.log(e);
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
      console.log(data);

      await axios
        .delete(`${process.env.REACT_APP_URL}/teams/professor/team/delete`, {
          data,
        })
        .then((r) => {
          console.log(r);
          alert('Successfully removed team.');
          setTeams(teams.filter((t) => t !== team));
        })
        .catch((e) => {
          console.log(e);
          alert('Error removing team.');
        });
    };

    return (
      <div className='accordionItem'>
        <div className='accordionTitle' onClick={handleSetActive}>
          <div>
            {isActive.includes(team) ? '-' : '+'}
            <text className='accordionTeamTitle'>{team.team_id}</text>
            <span
              onClick={() => handleDeleteTeam(team.team_id)}
              className='crossMarkTeam'
            >
              &#10060;
            </span>
          </div>
          {/*<div className='plusMinusTeam'>*/}
          {/*    {isActive.includes(team) ? '-' : '+'}*/}
          {/*</div>*/}
        </div>
        {isActive.includes(team) && (
          <div className='accordionContent'>
            {members.map((name) => (
              <div className='memberItem'>
                <text className='teamMember'>{name}</text>
                <span
                  onClick={() => handleRemoveStudent(name)}
                  className='crossMarkTeam'
                >
                  &#10060;
                </span>
              </div>
            ))}
            {isAdd.includes(team) ? (
              <div className='teamMember'>
                <label>Student ID:</label>
                <input
                  name='id'
                  value={id}
                  required
                  onChange={(e) => handleStudentId(e)}
                  className='emailInput'
                  type='text'
                />
                <a onClick={handleAddStudent} className='teamA'>
                  Add
                </a>
              </div>
            ) : (
              <input
                onClick={handleSetAdd}
                className='buttonPlusTeam'
                type='image'
                alt='plus-button'
                src={require('../../styles/plus-purple.png')}
              />
            )}
          </div>
        )}
      </div>
    );
  };

  return (
    <div>
      <div className='accordion'>
        {teams.map((team) => (
          <div>{ProfessorTeamAccordion(team)}</div>
        ))}
      </div>
    </div>
  );
};

export default ProfessorTeamComponent;
