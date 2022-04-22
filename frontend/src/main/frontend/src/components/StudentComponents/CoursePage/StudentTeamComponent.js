import React, {useEffect, useState} from 'react'
import {useSelector} from "react-redux";
import "../../styles/StudentTeamStyle.css"
import {useNavigate, useParams} from "react-router-dom";
import axios from "axios";

const TeamComponent = () => {
    const navigate = useNavigate()
    const {courseId} = useParams()
    const getTeamsUrl = `${process.env.REACT_APP_URL}/teams/team/get/all/` + courseId
    const [teams, setTeams] = useState(Array())
    const {lakerId} = useSelector((state) => state.auth)
    
    useEffect(async () => {
        await axios.get(getTeamsUrl).then(r => {
            for (let i = 0; i < r.data.length; i++) {
                if(r.data[i].team_full === false) setTeams(arr => [...arr, r.data[i]])
            }
            console.log(r)
        })
    }, [])

    const joinTeam = async (teamId) => {
        const joinUrl = `${process.env.REACT_APP_URL}/teams/team/join`
        const data = JSON.parse(JSON.stringify({"team_id": teamId, "course_id": courseId, "student_id": lakerId}))
        await axios.put(joinUrl, data)
        .then(res => {
            console.log(res)
            navigate(`/`)
            navigate(`/details/student/` + courseId)
        }).catch((e) => {
            console.log(e)
            alert("Error joining team")
        })
    }

    const createTeam = async () => {
        const team_name = prompt("Enter team name: ")
        const createUrl = `${process.env.REACT_APP_URL}/teams/team/create`
        const createData = JSON.parse(JSON.stringify({"course_id": courseId, "student_id": lakerId, "team_name": team_name}))
        if(team_name.split(" ").length > 1) {
            alert("Please enter a team name with no spaces!")
        }
        else {
            await axios.post(createUrl, createData)
                .then(res => {
                    console.log(res)
                    navigate(`/`)
                    navigate(`/details/student/` + courseId)
                }).catch((e) => {
                    console.log(e)
                    alert("Error creating team")
                })
        }
        // await axios.post(createUrl, createData)
        // .then(res => {
        //     console.log(res)
        //     navigate(`/`)
        //     navigate(`/details/student/` + courseId)
        // }).catch((e) => {
        //     console.log(e)
        //     alert("Error creating team")
        // })
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
                <button onClick={createTeam}> Create New Team</button>
            </div>
        </h3>

    )
}

export default TeamComponent
