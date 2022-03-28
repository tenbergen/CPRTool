import React, {useEffect, useState} from "react";
import "./styles/CourseBar.css"
import {useDispatch, useSelector} from "react-redux";
import { getCoursesAsync, setCurrentCourse } from "../redux/features/courseSlice";
import {Link} from "react-router-dom";

const CourseBarLink = ({ active, course, onClick }) => {
    const role = useSelector((state) => state.auth.role)
    const normalStyle = { backgroundColor: "rgba(255, 255, 255, 0.25)" }
    const clickedStyle = { backgroundColor: "white" }

    return (
        <Link to={`/details/${role}/${course.course_id}`} onClick={onClick}>
            <tr>
                <td style={active ? clickedStyle : normalStyle} >
                    <div className="colorForTable"/>
                    <p className="courseText"> {course.course_name} </p>
                </td>
            </tr>
        </Link>
    );
}

const CourseBarComponent = () => {
    const dispatch = useDispatch()
    const courseState = useSelector((state) => state.courses)
    const courses = courseState.courses
    const currentCourse = courseState.currentCourse

    const [chosen, setChosen] = useState(currentCourse.course_id);

    useEffect(() => {
        dispatch(getCoursesAsync())
    }, [])

    const onCourseClick = (course) => {
        setChosen(course.course_id)
        dispatch(setCurrentCourse(course))
    }

    return (
        <div className="cbc-parent">
            <h2> Courses </h2>
            <div className="cbc-courses">
                {courses.map(course =>
                    <CourseBarLink
                        onClick={() => onCourseClick(course)}
                        active={course.course_id === chosen}
                        course={course}
                    />
                )}
            </div>
        </div>
    );
};

export default CourseBarComponent;