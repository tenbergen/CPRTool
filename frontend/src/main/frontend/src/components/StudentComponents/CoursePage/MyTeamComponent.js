import React, {useEffect, useState} from 'react';
import {useParams} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import '../../styles/MyTeam.css';
import axios from "axios";

const MyTeamComponent = () => {
    const dispatch = useDispatch()
    const {courseId} = useParams()
    const {lakerId} = useSelector((state) => state.auth)
    const {currentTeamId, teamLoaded} = useSelector((state) => state.teams)
    const membersUrl = `${process.env.REACT_APP_URL}/teams/team/${courseId}/get/${currentTeamId}`
    const studentUrl = `${process.env.REACT_APP_URL}/view/professor/students/{studentID}`
    const [members, setMembers] = useState(Array());

    useEffect(() => {
        axios.get(membersUrl).then((r => {
            for(let i = 0; i < r.data.team_members.length; i++) {
                // setMembers((arr) => [...arr, r.data.team_members[i]]);

                const studentUrl = `${process.env.REACT_APP_URL}/view/professor/students/${r.data.team_members[i]}`
                axios.get(studentUrl).then((r => {
                    setMembers((arr) => [...arr, r.data.first_name + " " + r.data.last_name])
                }));
            }
        }));
    },[])

    return (
        <div> {
            teamLoaded ?
            <div className='my-team-container'>
                <div className='team-header'>Team:</div>
                    <div className='team-name-container'>
                        <div className='team-name'>
                            <p>{currentTeamId}</p>
                        </div>
                    </div>
                <div className='team-header'>Members:</div>
                {members.map((member) =>(
                    <div className='members-name-container'>
                        <div className='my-team-members'>
                            <p>{member}</p>
                        </div>
                    </div>
                ))}
            </div> : null
        }
        </div>

    )
}

export default MyTeamComponent