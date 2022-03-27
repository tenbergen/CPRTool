import React, {useEffect} from "react";
import "./styles/CourseBar.css"
import {useDispatch, useSelector} from "react-redux";
import { getCoursesAsync, setCurrentCourse } from "../redux/features/courseSlice";
import {Link} from "react-router-dom";

const CourseBarComponent = () => {
    const dispatch = useDispatch()
    const courses = useSelector((state) => state.courses.courses)

    useEffect(() => {
        dispatch(getCoursesAsync())
    }, [])

    const onCourseClick = (course) => {
        console.log(course)
        dispatch(setCurrentCourse(course))
    }

    return (
        <div className="cbc-parent">
            <h2> Courses </h2>
            <div className="cbc-courses">
                {courses.map(course =>
                    <Link to={"/details/" + course.course_id} onClick={() => onCourseClick(course)}>
                        <tr className="TheTable">
                            <td>
                                <div className="colorForTable"/>
                                <p className="courseText"> {course.course_name} </p>
                            </td>
                        </tr>
                    </Link>
                )}
            </div>
        </div>
    );
};

export default CourseBarComponent;