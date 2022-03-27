import React from "react-dom";
import {useEffect, useState} from "react";
import "./styles/CourseDetailsStyle.css"
import SidebarComponent from "../../components/SidebarComponent"
import RosterComponent from "../../components/RosterComponent"
import { useParams } from "react-router-dom";
import EditCourseComponent from "../../components/EditCourseComponent";
import TeacherAssComponent from "../../components/TeacherAssComponent";
import {useDispatch, useSelector} from "react-redux";
import {getCourseDetailsAsync} from "../../redux/features/courseSlice";
import CourseBarComponent from "../../components/CourseBarComponent";

const DetailComponent = ({ active, component, onClick }) => {
    return (
        <p onClick={onClick} className={active ? "cdp-component-link-clicked" : "cdp-component-link"}>
            {component}
        </p>
    );
};

function CourseDetailsPage() {
    let dispatch = useDispatch()
    let { courseId } = useParams();
    const isDataLoaded = useSelector((state) => state.courses.currentCourseLoaded)

    const components = ["Assignments", "Gradebook", "Roster", "Manage"];
    const [chosen, setChosen] = useState("Assignments");

    useEffect( () => {
        dispatch(getCourseDetailsAsync(courseId))
    }, [])

    return (
        <div>
        { isDataLoaded ?
            <div className="cdp-parent">
                <SidebarComponent/>
                <div className="cdp-container">
                    <CourseBarComponent/>
                    <div className="cdp-components">
                        <div className="cdp-component-links">
                            {components.map(t => (
                                <DetailComponent
                                    key={t}
                                    component={t}
                                    active={t === chosen}
                                    onClick={() => setChosen(t)}
                                />
                            ))}
                        </div>
                        <div>
                            {chosen === "Assignments" && <TeacherAssComponent/>}
                            {chosen === "Assignments" && null}
                            {chosen === "Roster" && <RosterComponent/>}
                            {chosen === "Manage" && <EditCourseComponent/>}
                        </div>
                    </div>
                </div>
            </div> : null }
        </div>
    )
}

export default CourseDetailsPage