import React, { useState } from "react";
import "./styles/CreateCourseStyle.css"
import SidebarComponent from "../components/SidebarComponent";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const CreateCoursePage = () => {
    const submitCourseUrl = "http://moxie.cs.oswego.edu:13127/manage/professor/courses/course/create"
    let navigate = useNavigate()

    const [formData, setFormData] = useState({
        CourseName: '',
        CourseSection: undefined,
        Semester: '',
        Abbreviation: ''
    });

    const { CourseName, CourseSection, Semester, Abbreviation } = formData;

    const OnChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

    const handleSubmit = async (e) => {
        if (Abbreviation === '' || CourseName === '' || CourseSection === undefined || Semester === '') alert("Fields can't be empty!")
        else {
            e.preventDefault()
            const data = {
                CourseName: CourseName,
                CourseSection: CourseSection,
                Semester: Semester,
                Abbreviation: Abbreviation
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
                            name="CourseName"
                            value={CourseName}
                            required
                            onChange={(e) => OnChange(e)}
                        />
                    </div>

                    <div className="course-name">
                        <label> <b> Course section: </b> </label>
                        <input
                            type="number"
                            name="CourseSection"
                            value={CourseSection}
                            required
                            onChange={(e) => OnChange(e)}
                        />
                    </div>

                    <div className="course-name">
                        <label> <b> Semester: </b> </label>
                        <input
                            type="text"
                            name="Semester"
                            value={Semester}
                            required
                            onChange={(e) => OnChange(e)}
                        />
                    </div>

                    <div className="course-name">
                        <label> <b> Course abbreviation: </b> </label>
                        <input
                            type="text"
                            name="Abbreviation"
                            value={Abbreviation}
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