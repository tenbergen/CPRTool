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
        <div className={"EditCourse"}>
            <SidebarComponent/>
            <h1>CSC 480</h1>
            <div id={"editCourseDiv"}>
                <label>Course description:</label>
                <br/>
                <label>lorem ipsum or hwatevefsfd thi sis some tedt oh sdifnsojf sojsdoj;fsdf</label>
                <br/>
                <label>Add course CSV</label>
            </div>
        </div>
    )
}

export default EditCoursePage