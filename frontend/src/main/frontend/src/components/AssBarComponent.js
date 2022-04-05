import React, {useEffect, useState} from "react";
import "./styles/AssBar.css"
import {useSelector} from "react-redux";
import {Link} from "react-router-dom";
import axios from "axios";

const AssBarLink = ({active, assignment, onClick}) => {
    const currentCourse = useSelector((state) => state.courses.currentCourse)
    const role = useSelector((state) => state.auth.role)
    const normalStyle = { backgroundColor: "rgba(255, 255, 255, 0.25)" }
    const clickedStyle = { backgroundColor: "white" }

    return (
        <Link to={`/details/${role}/${currentCourse.course_id}/${assignment.assignment_name}`} params={{ assignmentName:  assignment.assignment_name }} onClick={onClick}>
            <tr>
                <td style={active ? clickedStyle : normalStyle} >
                    <div className="colorForTable"/>
                    <p className="courseText"> {assignment.assignment_name} </p>
                </td>
            </tr>
        </Link>
    );
}

const AssBarComponent = () => {
    const currentCourse = useSelector((state) => state.courses.currentCourse)
    const assUrl = `${window.location.protocol}//${window.location.host}/assignments/professor/courses/${currentCourse.course_id}/assignments/`
    // const assUrl = `http://moxie.cs.oswego.edu:13125/assignments/professor/courses/${currentCourse.course_id}/assignments/`
    const [assignments, setAssignments] = useState()
    const [isLoading, setLoad] = useState(true)
    const [chosen, setChosen] = useState(currentCourse.course_id);

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
    //console.log(assignments)

    const onAssClick = (assignment) =>{
        setChosen(assignment.assignment_name)
    }


    if(isLoading) {
        return <div></div>
    }

    return (
        <div className="abc-parent">
            <h2> Assignments </h2>
            <div className="abc-assignments">
                {assignments.map(assignment =>
                <AssBarLink
                    onClick={()=> onAssClick(assignment)}
                    active={assignment.assignment_name === chosen}
                    assignment={assignment}
                    />
                )}
            </div>
        </div>
    );
}

export default AssBarComponent;