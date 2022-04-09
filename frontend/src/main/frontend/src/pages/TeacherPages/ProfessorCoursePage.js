import React from "react-dom";
import {useEffect, useState} from "react";
import "./styles/ProfessorCourseStyle.css"
import SidebarComponent from "../../components/SidebarComponent"
import RosterComponent from "../../components/RosterComponent"
import { useParams } from "react-router-dom";
import EditCourseComponent from "../../components/EditCourseComponent";
import TeacherAssComponent from "../../components/TeacherAssComponent";
import { useDispatch } from "react-redux";
import { getCourseDetailsAsync } from "../../redux/features/courseSlice";
import CourseBarComponent from "../../components/CourseBarComponent";
import ProfessorGradebookComponent from "../../components/ProfessorGradebookComponent";

const CourseComponent = ({ active, component, onClick }) => {
    return (
        <p onClick={onClick} className={active ? "pcp-component-link-clicked" : "pcp-component-link"}>
            {component}
        </p>
    );
};

function ProfessorCoursePage() {
    let dispatch = useDispatch()
    let { courseId } = useParams();

    const components = ["Assignments", "Gradebook", "Roster", "Manage"];
    const [chosen, setChosen] = useState("Assignments");

    useEffect( () => {
        dispatch(getCourseDetailsAsync(courseId))
    }, [])

    return (
        <div>
            <div className="pcp-parent">
                <SidebarComponent/>
                <div className="pcp-container">
                    <CourseBarComponent/>
                    <div className="pcp-components">
                        <div className="pcp-component-links">
                            {components.map(t => (
                                <CourseComponent
                                    key={t}
                                    component={t}
                                    active={t === chosen}
                                    onClick={() => setChosen(t)}
                                />
                            ))}
                        </div>
                        <div>
                            {chosen === "Assignments" && <TeacherAssComponent/>}
                            {chosen === "Gradebook" && <ProfessorGradebookComponent/> }
                            {chosen === "Roster" && <RosterComponent/>}
                            {chosen === "Manage" && <EditCourseComponent/>}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default ProfessorCoursePage