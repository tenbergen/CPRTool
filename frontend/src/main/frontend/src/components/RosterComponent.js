import {useLocation} from "react-router-dom";
import {useState} from "react";
import axios from "axios";
import "./styles/Roster.css"


const RosterComponent = () => {
    const addStudentUrl = `${window.location.protocol}//${window.location.host}/`
    // const {from} = useLocation().state
    // console.log(from)

    const [formData, setFormData] = useState({
        Name: '',
        Email: ''
    });

    const { Name, Email } = formData
    const OnChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

    const handleSubmit = async (e) => {
        if(Name === '' || Email === '') alert("Please enter both name and email for the student!")
        else {
            e.preventDefault()
            const data = {
                Name: Name,
                Email: Email
            };
            await axios.post(addStudentUrl, data)
        }
    }
    return (
        <div className="RosterPage">
            <div id="roster">
                <h1>Roster</h1>
                <table id="rosterTable">
                    <tr>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Team</th>
                        <th>Status</th>
                    </tr>
                    <tr>
                        <th>Joshua Smith</th>
                        <th>jsmith60@oswego.edu</th>
                        <th>loremIpsumTeam01</th>
                        <th>Student</th>
                    </tr>
                </table>
                <div id="addStudentDiv">
                    <label >Name:</label>
                    <input className="rosterInput" type="text" value={Name} onChange={(e) => OnChange(e)}/>
                    <label>Email:</label>
                    <input className="rosterInput" type="text" value={Email} onChange={(e) => OnChange(e)}/>
                    <button id="addStudentButton" onClick={handleSubmit}>Add Student</button>
                </div>
            </div>
        </div>
    )
}

export default RosterComponent