import React from "react-dom";
import {useState} from "react";
import "./styles/EditCourseStyle.css"
import SidebarComponent from "../components/SidebarComponent";
import axios from "axios";
import {Link, useLocation, useNavigate} from "react-router-dom";

function EditCoursePage() {
    // put the url of the endpoint here! but delete/edit is will not work as we need a way
    // to pass around courseIDs (probably with redux)
    const deleteUrl = "http://moxie.cs.oswego.edu:13127/manage/professor/courses/course/delete"
    const {from} = useLocation().state
    let currentCourse = Object()
    currentCourse.CourseName = from.CourseName
    currentCourse.CourseSection = from.CourseSection
    currentCourse.Semester = from.Semester
    currentCourse.Abbreviation = from.Abbreviation

    let navigate = useNavigate()
    console.log(currentCourse)

    const [courseDescription, setCourseDescription] = useState()
    const [courseName, setCourseName] = useState()

    let [selectedFile, setSelectedFile] = useState()
    let [isFilePicked, setIsFilePicked] = useState(false)

    const changeHandler = (event) => {
        setSelectedFile(event.target.files[0])
        setIsFilePicked(true)
    }

    const deleteCourse = async () => {
        await axios.post(deleteUrl, currentCourse).then((response) => {
            console.log(response)
        })
        navigate("/teacherDashboard")
    }

    return (
        <div className={"parent"}>
            <SidebarComponent/>
            <div className="container">
                <h1> Editing Course </h1>
                <form>
                    <div className="course-name">
                        <label> <b> Name of course: </b> </label>
                        <input
                            type="text"
                            name="courseName"
                            value={courseName}
                            required
                            //onChange={(e) => OnChange(e)}
                        />
                    </div>

                    <div className="course-csv">
                        <label> <b> Add course CSV: </b> </label>
                        <input
                            type="file"
                            name="courseCSV"
                            required
                        />
                    </div>

                    <div className="team-size">
                        <label> <b> Select peer review team size: </b> </label>
                        <input
                            type="number"
                            min="1"
                            value="3"
                            name="teamSize"
                            required 
                        />
                    </div>

                    <div className="editCourseButton">
                        <button> Submit </button>
                        <a onClick={deleteCourse} target="_blank"> Delete course </a>
                    </div>

                </form>
            </div>
        </div>
    )
}

export default EditCoursePage