import {useState} from "react";
import axios from "axios";
import "./styles/TeacherAss.css"
import {useSelector} from "react-redux";
import {Link} from "react-router-dom";

const TeacherAssComponent = () => {
    const currentCourse = useSelector((state) => state.courses.currentCourse)
    console.log(currentCourse);
    const assUrl = `${window.location.protocol}//${window.location.host}/assignments/professor/courses/${currentCourse.course_id}/assignments/`
    const assignments = ["Assignment1", "Assignment2"] // change this to hit endpoint



    return (
        <div className={"TeacherAss"}>
            <div id="ass">
                <div id="assList">
                    {assignments.map(assignment =>
                    // <Link to={}> add this for functionality
                        <li id="assListItem">{assignment}</li>
                    // </Link>
                      )}
                </div>
                <div id="assAddClass">
                    <Link to="create/assignment">
                        <button id="assAddButton">
                            Create new assignment
                        </button>
                    </Link>
                </div>
            </div>
        </div>
    )
}

export default TeacherAssComponent;