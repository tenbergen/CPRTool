import React, { useState } from "react";
import "./styles/CreateCourseStyle.css"
import SidebarComponent from "../../components/SidebarComponent";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const CreateCoursePage = () => {
    const submitCourseUrl = `${process.env.REACT_APP_URL}/manage/professor/courses/course/create`
    let navigate = useNavigate()

    const [formData, setFormData] = useState({
        course_name: '',
        course_section: '',
        semester: '',
        abbreviation: '',
        year: '',
        crn: ''
    });

    const { course_name, course_section, semester, abbreviation, year, crn } = formData;

    const OnChange = (e) => setFormData(
        { ...formData, [e.target.name]: e.target.value }
    );

    const handleSubmit = async (e) => {
        if (abbreviation === '' || course_name === '' || course_section === '' || semester === '' || year === '' || crn === '') {
            alert("Fields can't be empty!")
        }
        else {
            e.preventDefault()
            const data = {
                course_name: course_name,
                course_section: course_section,
                semester: semester,
                abbreviation: abbreviation,
                year: year,
                crn: crn
            };
            await axios.post(submitCourseUrl, data);
            navigate("/")
        }
    }

    return (
        <div className="cpp-parent">
            <SidebarComponent />
            <div className="cpp-container">
                <h2> Add new course </h2>
                <form className="ccp-form">
                    <div className="ccp-input-field">
                        <label> <b> Course name: </b> </label>
                        <input
                            type="text"
                            name="course_name"
                            value={course_name}
                            required
                            onChange={(e) => OnChange(e)}
                        />
                    </div>

                    <div className="cpp-row-multiple">
                        <div className="ccp-input-field">
                            <label> <b> Course abbreviation: </b> </label>
                            <input
                                type="text"
                                name="abbreviation"
                                value={abbreviation}
                                required
                                onChange={(e) => OnChange(e)}
                            />
                        </div>

                        <div className="ccp-input-field">
                            <label> <b> Course section: </b> </label>
                            <input
                                type="text"
                                name="course_section"
                                value={course_section}
                                required
                                onChange={(e) => OnChange(e)}
                            />
                        </div>
                    </div>

                    <div className="cpp-row-multiple">
                        <div className="ccp-input-field">
                            <label> <b> Semester: </b> </label>
                            <input
                                type="text"
                                name="semester"
                                value={semester}
                                required
                                onChange={(e) => OnChange(e)}
                            />
                        </div>

                        <div className="ccp-input-field">
                            <label> <b> Year: </b> </label>
                            <input
                                name="year"
                                type="text"
                                value={year}
                                required
                                onChange={(e) => OnChange(e)}
                            />
                        </div>
                    </div>

                    <div className="ccp-input-field">
                        <label> <b> CRN: </b> </label>
                        <input
                            type="text"
                            name="crn"
                            value={crn}
                            required
                            onChange={(e) => OnChange(e)}
                        />
                    </div>

                    <div className="ccp-button">
                        <button onClick={handleSubmit}> Create </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default CreateCoursePage;