import React, { useState } from "react";
import "./styles/CreateCourseStyle.css"
import SidebarComponent from "../components/SidebarComponent";

const CreateCoursePage = () => {
    const [formData, setFormData] = useState({
        courseName: '',
        courseDescription: ''
    });

    const { courseName, courseDescription } = formData;

    const OnChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

    const handleSubmit = () => {
        if (courseName === '' || courseDescription === '') alert("Fields can't be empty!")
        else {
            alert("Success!")
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