import {useEffect, useState} from "react";
import axios from "axios";
import {useSelector} from "react-redux";
import "./styles/StudentAss.css"
import {Link} from "react-router-dom";

const ToDoComponent = () => {
    const currentCourse = useSelector((state) => state.courses.currentCourse)
    const role = useSelector((state) => state.auth.role)
    // const assUrl = `http://moxie.cs.oswego.edu:13125/assignments/professor/courses/${currentCourse.course_id}/assignments/`
    const assUrl = `${window.location.protocol}//${window.location.host}/assignments/professor/courses/${currentCourse.course_id}/assignments/`

    const [assignments, setAssignments] = useState()
    const [isLoading, setLoad] = useState(true)

    useEffect(async() => {
        try {
            await axios.get(assUrl).then( r=> {
                setAssignments(Array.from(r.data))
            })
        }
        catch (e) {
            setAssignments(Array())
        }
        setLoad(false)
    },[])

    if(isLoading) {
        return <div><h1>LOADING</h1></div>
    }

    return (
        <h3>
            <div id="assList">
            {assignments.map(assignment =>
                <Link to={`/details/${role}/${currentCourse.course_id}/${assignment.assignment_name}`}>
                    <li id="assListItem">{assignment.assignment_name + "\n\n" + "Due Date: " + assignment.due_date}</li>
                </Link>
            )}
            </div>
        </h3>

    )
}

export default ToDoComponent
