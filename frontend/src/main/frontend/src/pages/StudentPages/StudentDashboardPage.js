import React, { useEffect } from 'react';
import { Link } from "react-router-dom";
import SidebarComponent from "../../components/SidebarComponent";
import "./styles/StudentDashboardStyle.css"
import { useDispatch, useSelector } from "react-redux";
import {getCourseDetailsAsync, getCoursesAsync} from "../../redux/features/courseSlice";

function StudentDashboardPage() {
    const dispatch = useDispatch()
    const courses = useSelector((state) => state.courses.courses)
    const user = useSelector((state) => state.auth.user_given_name)

    useEffect( ()=> {
       dispatch(getCoursesAsync())
    },[]);

    if (!courses) {
        return <div><h1>LOADING</h1></div>
    }

    const courseClickHandler = () => {
        dispatch(getCourseDetailsAsync)
    }

    return (
        <div className={"StudentDashboard"}>
            <SidebarComponent />
            <div id="student">
                <h2> Hello {user}</h2>
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