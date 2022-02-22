import React from "react-dom";
import {useState} from "react";
import "./styles/EditCourseStyle.css"
import SidebarComponent from "../components/SidebarComponent";

function EditCoursePage() {
    const [courseDescription, setCourseDescription] = useState()
    const [courseName, setCourseName] = useState()

    let [selectedFile, setSelectedFile] = useState()
    let [isFilePicked, setIsFilePicked] = useState(false)

    const changeHandler = (event) => {
        setSelectedFile(event.target.files[0])
        setIsFilePicked(true)
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

                    <div className="button">
                        <button> Submit </button>
                        <a href="default.asp" target="_blank"> Delete course </a>
                    </div>

                </form>
            </div>
        </div>
    )
}

export default EditCoursePage