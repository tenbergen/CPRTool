import { useEffect, useState } from 'react'
import '../../styles/TeamManager.css'
import axios from 'axios'
import { useSelector, useDispatch } from 'react-redux'
import uuid from 'react-uuid'
import noTeam from '../../../assets/no-team-no-bg.png'
import loading from '../../../assets/loading.gif'
import ProfessorTeamAccordion from './ProfessorTeamAccordion.jsx'
import { getCurrentCourseTeamAsync } from '../../../redux/features/teamSlice'
import { useParams } from 'react-router-dom'

const ProfessorTeamComponent = () => {
  const dispatch = useDispatch()
  const { courseId } = useParams()
  const getTeamsUrl = `${process.env.REACT_APP_URL}/teams/team/get/all/${courseId}`
  const [teams, setTeams] = useState([])
  const [isLoading, setIsLoading] = useState(false)

  useEffect(() => {
    async function fetchData () {
      const allTeams = await axios
        .get(getTeamsUrl)
        .then((res) => {
          if (res.data.length > 0) return res.data
          return []
        })
        .catch((e) => {
          alert(e.response.data)
          return []
        })
      setTeams(allTeams)
      setIsLoading(true)
    }

    fetchData()
  }, [getTeamsUrl])

  const createTeam = async () => {
    const team_name = prompt('Enter team name: ')
    const lakerId = prompt('Enter student leader email minus the @oswego.edu')
    const createUrl = `${process.env.REACT_APP_URL}/teams/team/create`
    const createData = {
      course_id: courseId,
      student_id: lakerId,
      team_name: team_name,
    }

    if (team_name.split(' ').length > 1) {
      alert('Please enter a team name with no spaces!')
      return
    }
    if (team_name === '') {
      alert('Team name cannot be empty!')
      return
    }
    if (team_name.length > 20) {
      alert('Team name is too long!')
      return
    }

    await axios
      .post(createUrl, createData)
      .then((res) => {
        alert('Successfully created team')
        dispatch(getCurrentCourseTeamAsync({ courseId, lakerId }))
      })
      .catch((e) => {
        console.error(e)
        alert('Error creating team')
      })
  }

  return (
    <div className="team-container">

      {isLoading
        ? <div> {teams.length > 0 ? (
          <div className="acordion-wrapper">
            <div className="accordion">
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
          <>
            <div className="no-team-wrapper">
              <div className="no-team-img-wrapper">
                <img className="no-team-img" src={noTeam} alt="no team created"/>
                <div className="no-team-description">No Team Created</div>
              </div>
            </div>
          </>
        )}</div>
        : <>
          <div className="loading-wrapper">
            <div className="loading-img-wrapper">
              <img className="loading-gif" id="loadImage" src={loading} alt="Loading Screen"></img>
            </div>
          </div>
        </>
      }
      <div id="createTeamButton">
        <button className="green-button-large" onClick={createTeam} style={{ marginBottom: '3%' }}> Create Team
        </button>
      </div>
    </div>
  )
}

export default ProfessorTeamComponent
