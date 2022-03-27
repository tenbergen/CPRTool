import {useState} from "react";
import axios from "axios";
import {useSelector} from "react-redux";
import "./styles/StudentAss.css"

const ToDoComponent = () => {

    // Need assingment endpoints

    const currentCourse = useSelector((state) => state.courses.currentCourse)
    console.log(currentCourse);
    const assUrl = `${window.location.protocol}//${window.location.host}/assignments/professor/courses/${currentCourse.course_id}/assignments/`
    const assignments = ["Homework #1\n\n Due on 4/1/22 at 11:59:59 PM", "Homework #2\n\n Due on 4/5/22 at 11:59:59 PM", "Homework #3\n\n Due on 4/9/22 at 11:59:59 PM",]
    const duedates = ["4/1/22, 11:59:59 PM", "4/5/22, 11:59:59 PM"]

    return (

        <h3>
            <div id="assList">
            {assignments.map(assignment =>
                    // <Link to={}> add this for functionality
                    <li id="assListItem">{assignment}</li>
                // </Link>
            )}
            </div>
        </h3>

    )
}

export default ToDoComponent
