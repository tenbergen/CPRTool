import {useState} from "react";
import axios from "axios";
import "./styles/Roster.css"
import {useSelector} from "react-redux";


const RosterComponent = () => {
    const addStudentUrl = `${window.location.protocol}//${window.location.host}/manage/professor/courses/course/student/add`
    // const addStudentUrl = `http://moxie.cs.oswego.edu:13125/manage/professor/courses/course/student/add`
    const deleteStudentUrl = `${window.location.protocol}//${window.location.host}/manage/professor/courses/course/student/delete`
    // const deleteStudentUrl = `http://moxie.cs.oswego.edu:13125/manage/professor/courses/course/student/delete`
    const currentCourse = useSelector((state) => state.courses.currentCourse)
    const [studentArray, updateArray] = useState(Array.from(currentCourse.students))

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
            const data = {
                email: Email,
                abbreviation: currentCourse.abbreviation,
                course_id: currentCourse.course_id,
                course_name: currentCourse.course_name,
                course_section: currentCourse.course_section,
                semester: currentCourse.semester,
                year: currentCourse.year
            };
            updateArray((arr) => [...arr, Email])
            await axios.post(addStudentUrl, data)
            setFalse()
        }
    }

    const deleteStudent = async (Email) => {
        const data = {
            email: Email,
            abbreviation: currentCourse.abbreviation,
            course_id: currentCourse.course_id,
            course_name: currentCourse.course_name,
            course_section: currentCourse.course_section,
            semester: currentCourse.semester,
            year: currentCourse.year
        };

        let index = studentArray.indexOf(Email)
        studentArray.splice(index, 1)
        updateArray((arr) => [...arr])
        await axios.post(deleteStudentUrl, data)
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
                        <th className="rosterHeader">Status</th>
                    </tr>
                    {studentArray.map(d =>
                        <tr>
                            <th>{d}</th>
                            <th>{d.Email}</th>
                            <th>{d.Team}</th>
                            <th>{d.Status}</th>
                            <span onClick={() => deleteStudent(d)} className="crossMark">&#10060;</span>
                        </tr>
                    )}
                </table>
            </div>
            {show ? addsStudent(): <button className="button_plus" onClick={setTrue}>
                <img className="button_plus" src={require("./styles/plus-purple.png")}/></button>}
        </div>
    )
}

export default RosterComponent