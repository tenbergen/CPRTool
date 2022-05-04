import React, {useEffect, useState} from 'react'
import {useDispatch, useSelector} from "react-redux";
import "../../styles/StudentTeamStyle.css"
import {useParams} from "react-router-dom";
import axios from "axios";
import {getCurrentCourseTeamAsync} from "../../../redux/features/teamSlice";

const TeamComponent = () => {
    const dispatch = useDispatch()
    const {courseId} = useParams()
    const getTeamsUrl = `${process.env.REACT_APP_URL}/teams/team/get/all/${courseId}`
    const [teams, setTeams] = useState([])
    const {lakerId} = useSelector((state) => state.auth)
    
    useEffect(async () => {
        const allTeams = await axios.get(getTeamsUrl)
            .then(res => {
                console.log(res)
                if (res.data.length > 0) return res.data
                return []
            })
            .catch(e => {
                console.log(e.response.data)
                return []
            })
        const openTeams = allTeams.filter(team => !team.team_full)
        setTeams(openTeams)
    }, [])

    const joinTeam = async (teamId) => {
        const joinUrl = `${process.env.REACT_APP_URL}/teams/team/join`
        const data = {"team_id": teamId, "course_id": courseId, "student_id": lakerId}
        await axios.put(joinUrl, data)
            .then(res => {
                console.log(res)
                alert(`Successfully joined team ${teamId}`)
                dispatch(getCurrentCourseTeamAsync({courseId, lakerId}))
            }).catch((e) => {
                console.log(e)
                alert("Error joining team")
            })
    }

    const createTeam = async () => {
        const team_name = prompt("Enter team name: ")
        const createUrl = `${process.env.REACT_APP_URL}/teams/team/create`
        const createData = {"course_id": courseId, "student_id": lakerId, "team_name": team_name}

        if (team_name.split(" ").length > 1) {
            alert("Please enter a team name with no spaces!")
            return
        }
        if (team_name === "") {
            alert("Team name cannot be empty!")
            return
        }

        await axios.post(createUrl, createData)
            .then(res => {
                console.log(res)
                alert("Successfully created team")
                dispatch(getCurrentCourseTeamAsync({courseId, lakerId}))
            }).catch((e) => {
                console.log(e)
                alert("Error creating team")
            })
    }

    const confirmJoin = async (teamId) => {
        let confirmAction = window.confirm('Are you sure you want to join this team?');
        if (confirmAction) {
            await joinTeam(teamId);
        }
    };

    return (
        <h3>
            <h2 className="kumba-30" id="teamTitle"> Join a team </h2>
            <div id="teamList">
                {teams.map(team =>
                    <li id="teamListItem" onClick={() => confirmJoin(team.team_id)}>Team {team.team_id}</li>
                )}
            </div>
            <div id="createTeamButton">
                <button onClick={createTeam}> Create New Team</button>
            </div>
        </h3>

    )
}

export default TeamComponent
