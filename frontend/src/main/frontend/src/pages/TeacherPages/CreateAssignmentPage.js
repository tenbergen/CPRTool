import React, { useState } from "react";
import "./styles/CreateAssignmentStyle.css"
import SidebarComponent from "../../components/SidebarComponent";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const CreateCoursePage = () => {
    const submitCourseUrl = `${process.env.REACT_APP_URL}/manage/professor/courses/course/create`
    let navigate = useNavigate()

    const [formData, setFormData] = useState({
        CourseName: '',
        CourseSection: undefined,
        Semester: '',
        Abbreviation: ''
    });

    const { AssignmentName, AssignmentInstructions } = formData;

    const OnChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

    const handleSubmit = async (e) => {
        if (AssignmentName === '' || AssignmentInstructions === '' ) alert("Fields can't be empty!")
        else {
            e.preventDefault()
            const data = {
                AssignmentName: AssignmentName,
                AssignmentInstructions: AssignmentInstructions,
            
            };
            await axios.post(submitCourseUrl, data);
            navigate("/teacherDashboard")
        }
    }

    return (
        <div className="cap-parent">
            <SidebarComponent />
            <div className="cap-container">
                <h2> Add new assignment </h2>
                <form className="cap-form">

                    <div className="cap-input-field">
                        <label> <b> Name of assignment: </b> </label>
                        <input
                            type="text"
                            name="AssignmentName"
                            value={AssignmentName}
                            required
                            onChange={(e) => OnChange(e)}
                        />
                    </div>

                    <div className="cap-instructions">
                        <label> <b> Instructions:</b> </label>
                        <input
                            type="text"
                            name="AssignmentInstructions"
                            value={AssignmentInstructions}
                            required
                        />
                    </div>

                    <div className="cap-assignment-files">
                        <label> <b> Files: </b> </label>
                        <input
                            //onChange={changeHandler}
                            type="file"
                            name="AssignmentFiles"
                            required
                        />
                    </div>

                    <div className="cap-assignment-info">
                        <label> <b> Due Date: </b> </label>
                        <input
                            type="date"
                            name="AssignmentDueDate"
                            required
                        />
                        <label> <b>Points: </b> </label>
                        <input
                            type="number"
                            min="0"
                            name="AssignmentPoints"
                            required
                        />

                    </div>

                    <div className="cap-button">
                        <button /*onClick={handleSubmit}*/> Create Peer Review </button>
                    </div>

                    <div class="cap-altbutton">
                        <a href="">Create Assignment Only </a>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default CreateCoursePage;
