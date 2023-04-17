import { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import '../../styles/StudentTeamStyle.css'
import { useParams } from 'react-router-dom'
import axios from 'axios'
import { getCurrentCourseTeamAsync } from '../../../redux/features/teamSlice'
import uuid from 'react-uuid'

const StudentTeamComponent = () => {
    const dispatch = useDispatch()
    const { courseId } = useParams()
    const getTeamsUrl = `${process.env.REACT_APP_URL}/teams/team/get/all/${courseId}`
    const [teams, setTeams] = useState([])
    const { lakerId } = useSelector((state) => state.auth)
    const [showModal, setShow] = useState(false);
    let team_name = ''

    const createTeam = async () => {
        const createUrl = `${process.env.REACT_APP_URL}/teams/team/create`;
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

    const getNumberOfMembers = (members) => {
        return members.length;
    }

    const Modal = (/*teamId*/) => {

        return (
            <div id='modal'>
                <div id='modalContent'>
                    <span id="createTeamTitle" className='inter-28-bold'>
                        Create Team
                    </span>
                    <div id="teamFieldContainer">
                        <span className='inter-20-medium'>Team Name</span>
                        <input
                            id="teamInputField"
                            className="inter-18-medium"
                            type="text"
                            name="team_name"
                            required
                            onChange={() => {team_name = document.getElementById("teamInputField").value}}
                        />
                    </div>
                    <div id='confirmAndCancelButtons'>
                        <button id='createTeamConfirmButton' className='inter-16-medium-red'
                                onClick={() => createTeam()}>Create
                        </button>
                        <button id='createTeamCancelButton' className='inter-16-medium-white'
                                onClick={() => setShow(false)}>Cancel
                        </button>
                    </div>
                </div>
            </div>
        );
    };

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
                alert(`Successfully joined team ${teamId}`)
                dispatch(getCurrentCourseTeamAsync({ courseId, lakerId }))
            })
            .catch((e) => {
                console.error(e)
                alert('Error joining team')
            })
    }

    const confirmJoin = async (teamId) => {
        let confirmAction = window.confirm(
            'Are you sure you want to join this team?'
        )
        if (confirmAction) {
            await joinTeam(teamId)
        }
    }

    return (
        <div>
            <h2 className="inter-28-medium" id="teamTitle">
                {' '}
                Teams{' '}
            </h2>
            <div id="teamList">
                {teams.map(
                    (team) =>
                        team && (
                            <div className={'team-tile'}>
                                <div className="inter-20-medium-white team-tile-title">
                                    {' '}
                                    <span>Team</span>
                                </div>
                                <div className="team-tile-content">
                                    <div className="team-tile-info">
                                        <span className='inter-24-medium'>
                                            {team.team_id}
                                        </span>
                                        <div className="members-and-join-button-container">
                                            <div className="members-count-container">
                                                <span className="members-count inter-24-medium">{/*team.team_members.length*/}/{team.team_size}</span>
                                                <span className="inter-12-light-italic">Team Members</span>
                                            </div>
                                            <button
                                                id="joinTeamButton"
                                                key={uuid()}
                                                onClick={() => confirmJoin(team.team_id)}
                                            >Join</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        )
                )}
            </div>
            <div id="createTeamButton">
                <button className="green-button-large" onClick={() => setShow(true)}> Create New Team</button>
                <div>{showModal ? Modal() : null}</div>
            </div>
        </div>
    )
}

export default StudentTeamComponent
