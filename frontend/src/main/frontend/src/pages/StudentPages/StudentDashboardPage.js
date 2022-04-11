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

    const courseClickHandler = (course) => {
        dispatch(getCourseDetailsAsync(course.course_id))
    }

    return (
        <div className={"StudentDashboard"}>
            <SidebarComponent />
            <div id="student">
                <h2> Hello {user_given_name}</h2>
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