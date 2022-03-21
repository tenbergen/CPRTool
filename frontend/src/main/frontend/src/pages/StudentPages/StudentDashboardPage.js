import React, { useEffect } from 'react';
import { Link } from "react-router-dom";
import SidebarComponent from "../../components/SidebarComponent";
import "./styles/StudentDashboardStyle.css"
import { useDispatch, useSelector } from "react-redux";
import {getCoursesAsync, setCurrentCourse} from "../../redux/features/courseSlice";

function StudentDashboardPage() {
    const dispatch = useDispatch()
    const courses = useSelector((state) => state.courses.courses)
    const user = useSelector((state) => state.auth.user)

    useEffect( ()=> {
       dispatch(getCoursesAsync())
    },[]);

    if (!courses) {
        return <div><h1>LOADING</h1></div>
    }

    const courseClickHandler = (course) => {
        dispatch(setCurrentCourse(course))
    }

    return (
        <div className={"StudentDashboard"}>
            <SidebarComponent />
            <div id="student">
                <h1> Hello {user}</h1>
                <div id="courseList">
                    {courses.map(course =>
                        <Link to="/toDoCourse" onClick={() => courseClickHandler(course)} state={{from: course}}>
                            <li className="courseListItem">{course.CourseName}</li>
                        </Link>)}
                </div>
            </div>
        </div>
    );


}

export default StudentDashboardPage;