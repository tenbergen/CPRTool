import React, {useEffect} from 'react';
import { Link } from "react-router-dom";
import SidebarComponent from "../../components/SidebarComponent";
import "./styles/ProfessorDashboardStyle.css"
import { useDispatch, useSelector } from "react-redux";
import {getCoursesAsync} from "../../redux/features/courseSlice";

function ProfessorDashboardPage() {
    const dispatch = useDispatch()
    const courses = useSelector((state) => state.courses.courses)
    const user = useSelector((state) => state.auth.user_given_name)
    const hideButtons =  localStorage.getItem("hideButtons")

    useEffect(() => {
        dispatch(getCoursesAsync())
    }, []);

    const studentView = () => {
        localStorage.setItem("alt_role", "student")
        window.location.reload(false);
    }

    const originalView = () => {
        localStorage.removeItem("alt_role")
        window.location.reload(false);
    }

    // this allows the courses to be received and rendered, hence this and useEffect
    if (!courses) {
        return <div><h1>LOADING</h1></div>
    }

    return (
        <div className={"TeacherDashboard"}>
            <SidebarComponent />
            <div id="teacher">
                <h1>Hello {user}</h1>
                {
                    hideButtons
                        ? null
                        :
                        <div>
                            <button onClick={studentView} style={{marginRight: "10px"}}> Student View</button>
                            <button onClick={originalView}> Original View</button>
                        </div>
                }
                <div id="proCourseList">
                    {courses.map(course =>
                    <Link to={"/details/professor/" + course.course_id}>
                        <li className="courseListItem">{course.course_id + "\n\n"  + course.course_name}</li>
                    </Link>)}
                </div>
                <div id="addClass">
                    <Link to="/create/course">
                        <button id="addButton">
                            Create new course
                        </button>
                    </Link>
                </div>
            </div>
        </div>
    );
}

export default  ProfessorDashboardPage;