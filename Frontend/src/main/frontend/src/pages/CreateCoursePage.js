import React, { useState } from "react";
import "./styles/CreateCourseStyle.css"
import SidebarComponent from "../components/SidebarComponent";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const CreateCoursePage = () => {
    const submitCourseUrl = "http://moxie.cs.oswego.edu:13127/professor/courses/course/create/"
    let navigate = useNavigate()

    const [formData, setFormData] = useState({
        courseId: '',
        courseName: '',
        courseSection: undefined,
        courseAbbreviation: '',
        semester: ''
    });

    const { courseName, courseSection,  courseAbbreviation, semester } = formData;

    const OnChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

    const handleSubmit = async (e) => {
        if (courseName === '' || courseSection === undefined) alert("Fields can't be empty!")
        else {
            e.preventDefault()
            const data = {
                courseName: courseName,
                courseSection: courseSection,
                // courseAbbreviation: courseAbbreviation,
                // semester: semester,
            };
            await axios.post(submitCourseUrl, data);
            navigate("/teacherDashboard")
        }
    }

    return (
        <div className="parent">
            <SidebarComponent />
            <div className="container">
                <h1> New course </h1>
                <form>
                    <div className="course-name">
                        <label> <b> Course name: </b> </label>
                        <input
                            type="text"
                            name="courseName"
                            value={courseName}
                            required
                            onChange={(e) => OnChange(e)}
                        />
                    </div>

                    <div className="course-name">
                        <label> <b> Course section: </b> </label>
                        <input
                            type="number"
                            name="courseSection"
                            value={courseSection}
                            required
                            onChange={(e) => OnChange(e)}
                        />
                    </div>

                    <div className="course-name">
                        <label> <b> Course abbreviation: </b> </label>
                        <input
                            type="text"
                            name="courseAbbreviation"
                            value={courseAbbreviation}
                            required
                            onChange={(e) => OnChange(e)}
                        />
                    </div>

                    <div className="course-name">
                        <label> <b> Semester: </b> </label>
                        <input
                            type="text"
                            name="semester"
                            value={semester}
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