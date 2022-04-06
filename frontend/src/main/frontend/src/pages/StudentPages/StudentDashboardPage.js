import React, { useEffect } from 'react';
import { Link } from "react-router-dom";
import SidebarComponent from "../../components/SidebarComponent";
import "./styles/StudentDashboardStyle.css"
import { useDispatch, useSelector } from "react-redux";
import {getCourseDetailsAsync, getStudentCoursesAsync} from "../../redux/features/courseSlice";

function StudentDashboardPage() {
    const dispatch = useDispatch()
    const courses = useSelector((state) => state.courses.courses)
    const { user_given_name, lakerId } = useSelector((state) => state.auth)

    useEffect( ()=> {
       dispatch(getStudentCoursesAsync(lakerId))
    },[]);

    if (!courses) {
        return <div><h1>LOADING</h1></div>
    }

    const professorView = () => {
        localStorage.setItem("alt_role", "professor")
        window.location.reload(false);
    }

    const originalView = () => {
        localStorage.removeItem("alt_role")
        window.location.reload(false);
    }

    const courseClickHandler = () => {
        dispatch(getCourseDetailsAsync)
    }

    return (
        <div className={"StudentDashboard"}>
            <SidebarComponent />
            <div id="student">
                <h2> Hello {user_given_name}</h2>
                    {/*<div>*/}
                    {/*    <button onClick={professorView} style={{marginRight: "10px"}}> Professor View </button>*/}
                    {/*    <button onClick={originalView}> Original View </button>*/}
                    {/*</div>*/}
                <div id="courseList">
                    {courses.map(course =>
                        <Link to={"/details/student/" + course.course_id} onClick={() => courseClickHandler(course)} state={{from: course}}>
                            <li className="courseListItem">{course.course_id + "\n\n"  + course.course_name}</li>
                        </Link>)}
                </div>
            </div>
        </div>
    );


}

export default StudentDashboardPage;