import React, { useEffect } from 'react';
import { Link } from "react-router-dom";
import SidebarComponent from "../components/SidebarComponent";
import "./styles/TeacherDashboardStyle.css"
import { useDispatch, useSelector } from "react-redux";
import { getCoursesAsync } from "../redux/features/courseSlice";

function TeacherDashboardPage() {
    const dispatch = useDispatch()
    const courses = useSelector((state) => state.courses)
    const user = useSelector((state) => state.auth.user)

    useEffect(() => {
        dispatch(getCoursesAsync())
    }, []);

    // this allows the courses to be received and rendered, hence this and useEffect
    if (!courses) {
        return <div><h1>LOADING</h1></div>
    }

    return (
        <div className={"TeacherDashboard"}>
                <SidebarComponent />
            <div id="teacher">
                <h1>Hello {user}</h1>
                <div id="courseList">
                    {courses.map(course =>
                    <Link to="/editCourse" state={{from: course}}>
                        <li className="courseListItem" value={course}>{course.CourseName}</li>
                    </Link>)}
                </div>
                <div id="addClass">
                    <Link to="/createCourse">
                        <button id="addButton">
                            Create new course
                        </button>
                    </Link>
                </div>
            </div>
        </div>
    );
}

export default  TeacherDashboardPage;