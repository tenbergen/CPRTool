import React from "react-dom";
import {useEffect, useState} from "react";
import "./styles/CourseDetailsStyle.css"
import SidebarComponent from "../../components/SidebarComponent"
import RosterComponent from "../../components/RosterComponent"
import { useParams } from "react-router-dom";
import EditCourseComponent from "../../components/EditCourseComponent";
import {useDispatch, useSelector} from "react-redux";
import {getCourseDetailsAsync} from "../../redux/features/courseSlice";

function CourseDetailsPage() {
    let dispatch = useDispatch()
    let { courseId } = useParams();
    const isDataLoaded = useSelector((state) => state.courses.currentCourseLoaded)

    useEffect( () => {
        dispatch(getCourseDetailsAsync(courseId))
    }, [])

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
        <div>
        { isDataLoaded ?
            <div className="cdp-parent">
                <SidebarComponent/>
                {/*<CourseBarComponent/>*/}
                <div className="cdp-container">
                    <div className="cdp-component-links">
                        <p className="editCourseA">Assignments</p>
                        <p className="editCourseA">Gradebook</p>
                        <p onClick={handleRoster} className="editCourseA">Roster</p>
                        <p onClick={handleShowEdit} className="editCourseA">Manage</p>
                    </div>
                    <div>
                        {showEdit ? <EditCourseComponent/>: null}
                    </div>
                    <div>
                        {showRoster ? <RosterComponent/> : null}
                    </div>
                </div>
            </div> : null }
        </div>
    )
}

export default CourseDetailsPage