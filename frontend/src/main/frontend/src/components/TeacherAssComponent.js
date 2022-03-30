import React, {useEffect, useState} from "react";
import axios from "axios";
import "./styles/TeacherAss.css"
import {useSelector} from "react-redux";
import {Link} from "react-router-dom";

const TeacherAssComponent = () => {
    const currentCourse = useSelector((state) => state.courses.currentCourse)
    const assUrl = `${window.location.protocol}//${window.location.host}/assignments/professor/courses/${currentCourse.course_id}/assignments/`
    // const assUrl = `http://moxie.cs.oswego.edu:13125/assignments/professor/courses/${currentCourse.course_id}/assignments/`
    const [assignments, setAssignments] = useState()
    const [isLoading, setLoad] = useState(true)

    useEffect(async () => {
        try {
            await axios.get(assUrl).then(r => {
                if(r.length !== 0) {
                    setAssignments(Array.from(r.data))
                }
                else {
                    setAssignments(Array())
                }
            })
        }
        catch (e) {
            setAssignments(Array())
        }
        setLoad(false)
    })

    if (isLoading) {
        return <div><h1>LOADING</h1></div>
    }

    return  (
        <div className={"TeacherAss"}>
            <div id="ass">
                <div id="assList">
                    {assignments.map(assignment =>
                            // <Link to={}> add this for functionality
                            <li className="assListItem">{assignment.assignment_name}</li>
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