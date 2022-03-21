import React from "react-dom";
import {useState} from "react";
import "./styles/EditCourseStyle.css"
import SidebarComponent from "../../components/SidebarComponent"
import RosterComponent from "../../components/RosterComponent"
import axios from "axios";
import {Link, useLocation, useNavigate} from "react-router-dom";
import EditCourseComponent from "../../components/EditCourseComponent";

function EditCoursePage() {

    const [showEdit, setShowEdit] = useState(true)
    const handleShowEdit = () => {
        setShowEdit(true)
        setShowRoster(false)
    }

    const [showRoster, setShowRoster] = useState(false)
    const handleRoster = () => {
        setShowRoster(true)
        setShowEdit(false)
    }

    return (
        <div className={"parent"}>
            <SidebarComponent/>
            <div className="container">
                <h1> Editing </h1>
                <a onClick={handleShowEdit} className="editCourseA" target="_blank">Manage</a>
                <a onClick={handleRoster} className="editCourseA" target="_blank">Roster</a>
                <a className="editCourseA" target="_blank">Assignments</a>
                <a className="editCourseA" target="_blank">Gradebook</a>
                <div>
                    {showEdit ? <EditCourseComponent/>: null}
                </div>
                <div>
                    {showRoster ? <RosterComponent/> : null}
                </div>
            </div>
        </div>
    )
}

export default EditCoursePage