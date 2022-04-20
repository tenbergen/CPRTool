import React, {useEffect, useState} from 'react'
import {useSelector} from "react-redux";
import "../../styles/StudentTeamStyle.css"
import {useNavigate, useParams} from "react-router-dom";
import axios from "axios";

const TeamComponent = () => {
    const navigate = useNavigate()
    const {courseId} = useParams()
    const getTeamsUrl = `${process.env.REACT_APP_URL}/teams/team/get/unlocked-team/all/` + courseId
    const [teams, setTeams] = useState(Array())
    const {lakerId} = useSelector((state) => state.auth)

    useEffect(async () => {
        await axios.get(getTeamsUrl).then(r => {
            for (let i = 0; i < r.data.length; i++) {
                setTeams(arr => [...arr, r.data[i]])
            }
            console.log(r)
        })
    }, [])


    const joinTeam = async (teamId) => {
        const joinUrl = `${process.env.REACT_APP_URL}/teams/team/join`
        const data = JSON.parse(JSON.stringify({"team_id": teamId, "course_id": courseId, "student_id": lakerId}))
        await axios.put(joinUrl, data)
        navigate(`/`)
        navigate(`/details/student/` + courseId)
    }

    const createTeam = async () => {
        const createUrl = `${process.env.REACT_APP_URL}/teams/team/create`
        const createData = JSON.parse(JSON.stringify({"course_id": courseId, "student_id": lakerId}))
        await axios.post(createUrl, createData)
        navigate(`/`)
        navigate(`/details/student/` + courseId)
    }

    return (
        <h3>
            <h2 id="teamTitle"> Join a team </h2>
            <div id="teamList">
                {teams.map(team =>
                    <li id="teamListItem" onClick={() => joinTeam(team.team_id)}>Team {team.team_id}</li>
                )}
            </div>
            <div id="createTeamButton">
                <button onClick={createTeam}> Create Team</button>
            </div>
        </h3>

    )
}

export default TeamComponent