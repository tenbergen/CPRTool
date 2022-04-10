import { useState } from "react";
import axios from "axios";
import "../../styles/Roster.css"
import { useDispatch, useSelector } from "react-redux";
import { getCourseDetailsAsync } from "../../../redux/features/courseSlice";
import { useParams } from "react-router-dom";


const ProfessorRosterComponent = () => {
    const dispatch = useDispatch()
    const { courseId } = useParams()
    const addStudentUrl = `${process.env.REACT_APP_URL}/manage/professor/courses/course/student/add`
    const deleteStudentUrl = `${process.env.REACT_APP_URL}/manage/professor/courses/course/student/delete`
    const { currentCourse } = useSelector((state) => state.courses)

    const [formData, setFormData] = useState({
        Name: '',
        Email: ''
    });

    const { Name, Email } = formData
    const OnChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

    const handleSubmit = async (e) => {
        if(Name === '' || Email === '') {
            alert("Please enter both name and email for the student!")
        }
        else {
            e.preventDefault()
            const data = { email: Email, ...currentCourse };
            await axios.post(addStudentUrl, data)
                .then(res => {
                    console.log(res.data)
                    alert("Successfully added student.")
                    dispatch(getCourseDetailsAsync(courseId))
                })
                .catch(e => {
                    console.log(e)
                    alert("Error adding student.")
                })
            setFalse()
            setFormData({...formData, Name: '', Email: ''})
        }
    }

    const deleteStudent = async (Email) => {
        console.log(Email)
        const data = { email: Email, ...currentCourse};
        await axios.post(deleteStudentUrl, data)
            .then(res => {
                console.log(res)
                alert("Successfully deleted student.")
                dispatch(getCourseDetailsAsync(courseId))
            })
            .catch(e => {
                console.log(e)
                alert("Error deleting student.")
            })
    }

    const addsStudent = () => {
        return (
            <div id="addStudentDiv">
                <label>Name:</label>
                <input type="text" className="rosterInput" name="Name" value={Name}
                       required onChange={(e) => OnChange(e)}/>
                <label>Email:</label>
                <input type="text" className="rosterInput" name="Email" value={Email}
                       required onChange={(e) => OnChange(e)}/>
                <button id="addStudentButton" onClick={handleSubmit}>Add Student</button>
            </div>
        )
    }

    const [show, setShow] = useState(false)
    const setTrue = () => setShow(true)
    const setFalse = () => setShow(false)

    return (
        <div className="RosterPage">
            <div id="roster">
                <table className="rosterTable">
                    <tr>
                        <th className="rosterHeader">Name</th>
                        <th className="rosterHeader">Email</th>
                        <th className="rosterHeader">Team</th>
                        <th className="rosterHeader">Remove</th>
                    </tr>
                    {currentCourse.students.map(d =>
                        <tr>
                            <th className="rosterComp">{d}</th>
                            <th className="rosterComp">{d.Email}</th>
                            <th className="rosterComp">{d.Team}</th>
                            <th className="rosterComp"> <span onClick={() => deleteStudent(d)} className="crossMark">&#10060;</span></th>
                        </tr>
                    )}
                </table>
            </div>
            {show ? addsStudent(): <button className="button_plus" onClick={setTrue}>
                <img className="button_plus" src={require("../../styles/plus-purple.png")}/></button>}
        </div>
    )
}

export default ProfessorRosterComponent