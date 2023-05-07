import { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import '../../styles/StudentTeamStyle.css'
import { useNavigate, useParams } from 'react-router-dom'
import axios from 'axios'
import { getCurrentCourseTeamAsync } from '../../../redux/features/teamSlice'
import uuid from 'react-uuid'
import * as React from 'react'

const StudentTeamComponent = () => {
  const dispatch = useDispatch()
  const { courseId } = useParams()
  const getTeamsUrl = `${process.env.REACT_APP_URL}/teams/team/get/all/${courseId}`
  const [teams, setTeams] = useState([])
  const { lakerId } = useSelector((state) => state.auth)
  const [showCreateTeamModal, setShowCreateTeamModal] = useState(false)
  const [showCreateTeamConfirmationModal, setShowCreateTeamConfirmationModal] = useState(false)
  const [showCreateTeamSuccessModal, setShowCreateTeamSuccessModal] = useState(false)
  const [showJoinTeamConfirmationModal, setShowJoinTeamConfirmationModal] = useState(false)
  const [showJoinTeamSuccessModal, setShowJoinTeamSuccessModal] = useState(false)
  const [teamName, setTeamName] = useState('')
  let navigate = useNavigate()

  const setBackToTeams = () =>{
      //let path = `${process.env.REACT_APP_URL}/student/${courseId}/teams`
      let path = `/student/${courseId}/teams`
      navigate(path)
    }

  const createTeam = async () => {
    const createUrl = `${process.env.REACT_APP_URL}/teams/team/create`
    const createData = {
      course_id: courseId,
      student_id: lakerId,
      team_name: teamName,
    }

    if (teamName.split(' ').length > 1) {
      alert('Please enter a team name with no spaces!')
      return
    }
    if (teamName === '') {
      alert('Team name cannot be empty!')
      return
    }
    if (teamName.length > 20) {
      alert('Team name is too long!')
      return
    }

    await axios
      .post(createUrl, createData)
      .then((res) => {
        setShowCreateTeamSuccessModal(true)
      })
      .catch((e) => {
        console.error(e)
        alert('Error creating team')
      })
  }

  const CreateTeamModal = () => {

    return (
      <div id="modal">
        <div id="modalContent">
                    <span id="createTeamTitle" className="inter-28-bold">
                        Create Team
                    </span>
          <div id="teamFieldContainer">
            <span className="inter-20-medium">Team Name</span>
            <input
              id="teamInputField"
              className="inter-18-medium"
              type="text"
              name="teamName"
              required
              onChange={() => {setTeamName(document.getElementById('teamInputField').value)}}
            />
          </div>
          <div id="confirmAndCancelButtons">
            <button id="createTeamConfirmButton" className="inter-16-medium-red"
                    onClick={() => {
                      setShowCreateTeamConfirmationModal(true)
                      setShowCreateTeamModal(false)
                    }}>Create
            </button>
            <button id="createTeamCancelButton" className="inter-16-medium-white"
                    onClick={() => setShowCreateTeamModal(false)}>Cancel
            </button>
          </div>
        </div>
      </div>
    )
  }

  const CreateTeamConfirmationModal = () => {
    return (
      <div id="modal">
        <div id="modalContent">
                    <span id="createTeamTitle" className="inter-28-bold">
                        Confirm
                    </span>
          <div id="teamFieldContainer">
            <span className="inter-20-medium">Are you sure you want to create team "{teamName}"?</span>
          </div>
          <div id="createTeamConfirmButtons">
            <button id="createTeamConfirmYesButton" className="inter-16-medium-red"
                    onClick={() => {
                      setShowCreateTeamConfirmationModal(false)
                      createTeam()
                    }}>Yes
            </button>
            <button id="createTeamConfirmNoButton" className="inter-16-medium-white"
                    onClick={() => setShowCreateTeamConfirmationModal(false)}>No
            </button>
          </div>
        </div>
      </div>
    )
  }

  const CreateTeamSuccessModal = () => {
    return (
      <div id="modal">
        <div id="modalContent" style={{
          height: '40%',
          width: '45%'
        }}>
          <svg className="cross" style={{}} onClick={async () => {
            setShowCreateTeamSuccessModal(false)
            //navigate(-1)
            setBackToTeams()
            await dispatch(getCurrentCourseTeamAsync({ courseId, lakerId }))
          }}></svg>
          <span
            style={{
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'center',
              verticalAlign: 'middle',
              position: 'absolute',
              top: '27%'
            }}>
                        <svg className={'check_image'}></svg>
                        <div className="inter-28-bold"
                             style={{
                               marginTop: '3%',
                               marginBottom: '8%'
                             }}>Success!</div>
                        <p>You have successfully created the Team '{teamName}'!</p>
                    </span>
        </div>
      </div>
    )
  }

  useEffect(() => {
    async function fetchData () {
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
      const openTeams = allTeams.filter((team) => !team.team_full)
      setTeams(openTeams)
    }

    fetchData()
  }, [getTeamsUrl])

  const joinTeam = async (teamId) => {
    const joinUrl = `${process.env.REACT_APP_URL}/teams/team/join`
    const data = { team_id: teamId, course_id: courseId, student_id: lakerId }
    await axios
      .put(joinUrl, data)
      .then((res) => {
        setShowJoinTeamSuccessModal(true)
      })
      .catch((e) => {
        console.error(e)
        alert('Error joining team')
      })
  }

  const JoinTeamConfirmationModal = () => {
    return (
      <div id="modal">
        <div id="modalContent">
                    <span id="createTeamTitle" className="inter-28-bold">
                        Confirm
                    </span>
          <div id="teamFieldContainer">
            <span className="inter-20-medium">Are you sure you want to join team "{teamName}"?</span>
          </div>
          <div id="createTeamConfirmButtons">
            <button id="createTeamConfirmYesButton" className="inter-16-medium-red"
                    onClick={async () => {
                      setShowJoinTeamConfirmationModal(false)
                      await joinTeam(teamName)
                    }}>Yes
            </button>
            <button id="createTeamConfirmNoButton" className="inter-16-medium-white"
                    onClick={() => setShowJoinTeamConfirmationModal(false)}>No
            </button>
          </div>
        </div>
      </div>
    )
  }

  const JoinTeamSuccessModal = () => {
    return (
      <div id="modal">
        <div id="modalContent" style={{
          height: '40%',
          width: '45%'
        }}>
          <svg className="cross" style={{}} onClick={async () => {
            setShowJoinTeamSuccessModal(false)
              // console.log("X clicked, leaving to teams page...")
              //setBackToTeams()
              navigate(-1)
            await dispatch(getCurrentCourseTeamAsync({ courseId, lakerId }))
          }}></svg>
          <span
            style={{
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'center',
              verticalAlign: 'middle',
              position: 'absolute',
              top: '27%'
            }}>
                        <svg className={'check_image'}></svg>
                        <div className="inter-28-bold"
                             style={{
                               marginTop: '3%',
                               marginBottom: '8%'
                             }}>Success!</div>
                        <p>You have successfully joined the Team '{teamName}'!</p>
                    </span>
        </div>
      </div>
    )
  }

  return (
    <div id="studentTeamComponentContainer">
      <h2 className="inter-28-medium" id="teamTitle">
        {' '}
        Teams{' '}
      </h2>
      <div id="teamList">
        {teams.map(
          (team) =>
            team && (
              <div className="team-tile">
                <div className="inter-20-medium-white team-tile-title">
                  {' '}
                  <span>Team</span>
                </div>
                <div className="team-tile-content">
                  <div className="team-tile-info">
                                        <span className="inter-24-medium">
                                            {team.team_id}
                                        </span>
                    <div className="members-and-join-button-container">
                      <div className="members-count-container">
                        <span className="members-count inter-24-medium">{team.team_members.length}/{team.team_size}</span>
                        <span className="inter-12-light-italic">Team Members</span>
                      </div>
                      <button
                        id="joinTeamButton"
                        key={uuid()}
                        onClick={() => {
                          setTeamName(team.team_id)
                          setShowJoinTeamConfirmationModal(true)
                        }}
                      >Join
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            )
        )}
      </div>
      <div id="createTeamButton">
        <button className="green-button-large" onClick={() => setShowCreateTeamModal(true)}> Create New Team</button>
        <div>{showCreateTeamModal ? CreateTeamModal() : null}</div>
        <div>{showCreateTeamConfirmationModal ? CreateTeamConfirmationModal() : null}</div>
        <div>{showCreateTeamSuccessModal ? CreateTeamSuccessModal() : null}</div>
        <div>{showJoinTeamConfirmationModal ? JoinTeamConfirmationModal() : null}</div>
        <div>{showJoinTeamSuccessModal ? JoinTeamSuccessModal() : null}</div>
      </div>
    </div>
  )
}

export default StudentTeamComponent
