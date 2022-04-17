import React, {useEffect, useState} from 'react'
import {useSelector} from "react-redux";
import "../../styles/StudentTeamStyle.css"
import {useParams} from "react-router-dom";
import axios from "axios";

const TeamComponent = () => {
    const store = useSelector((state) => state)
    const { courseId } = useParams()
    const getTeamsUrl = `${process.env.REACT_APP_URL}/teams/team/get/unlocked-team/all/` + courseId
    const [teams, setTeams] = useState(Array())
    const { lakerId } = useSelector((state) => state.auth)


    useEffect(() => {
        axios.get(getTeamsUrl).then(r => {
            for(let i = 0; i < r.data.length; i++)
            {
                setTeams(arr => [...arr, r.data[i]])
            }
            console.log(r)
        })
    }, [])


    const joinTeam = async (teamId) => {
        const joinUrl = `${process.env.REACT_APP_URL}/teams/team/join`
        console.log(teamId)
        const data = JSON.parse(JSON.stringify({"team_id":teamId,"course_id":courseId,"student_id":lakerId}))
        await axios.put(joinUrl, data)
    }

    const createTeam = async () => {
        const createUrl = `${process.env.REACT_APP_URL}/teams/team/create`
        console.log(lakerId)
        console.log("Need to create teamS")
        const createData = JSON.parse(JSON.stringify({"course_id":courseId,"student_id":lakerId}))
        await axios.post(createUrl, createData)
    }

    return (
        <h3>
            <div id="teamList">
                {teams.map(team =>
                        <li id="teamListItem" onClick={() => joinTeam(team.team_id)}>Team {team.team_id}</li>
                )}
            </div>
            <div id="createTeamButton">
                <button onClick={createTeam}> Create Team </button>
            </div>
        </h3>

    )
}

export default TeamComponent