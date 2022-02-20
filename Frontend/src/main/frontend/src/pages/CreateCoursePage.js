import React, { useState } from "react";
import "./styles/CreateCourseStyle.css"
import SidebarComponent from "../components/SidebarComponent";
import {useNavigate} from "react-router-dom";
import axios from "axios";

const CreateCoursePage = () => {
    const submitCourseUrl = ""
    let navigate = useNavigate()

    const [formData, setFormData] = useState({
        courseName: '',
        courseDescription: ''
    });

    const { courseName, courseDescription } = formData;

    const OnChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

    const handleSubmit = async (e) => {
        if (courseName === '' || courseDescription === '') alert("Fields can't be empty!")
        else {
            e.preventDefault()

            const data = {
                "Bastian Tenbergen":[
                    {"courseName" : courseName},
                    {"courseDescription" : courseDescription}
                ]
            }
            console.log(data)

            navigate("/teacherDashboard")
            await axios.post(submitCourseUrl, data);
        }
    }

    return (
        <div className="parent">
            <SidebarComponent />
            <div className="container">
                <h1> New course </h1>
                <form>
                    <div className="course-name">
                        <label> <b> Name of course: </b> </label>
                        <input
                            type="text"
                            name="courseName"
                            value={courseName}
                            required
                            onChange={(e) => OnChange(e)}
                        />
                    </div>

                    <div className="course-description">
                        <label> <b> Course description: </b> </label>
                        <input
                            type="text"
                            name="courseDescription"
                            value={courseDescription}
                            required
                            onChange={(e) => OnChange(e)}
                        />
                    </div>

                    <div className="button">
                        <button onClick={handleSubmit}> Create </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default CreateCoursePage;