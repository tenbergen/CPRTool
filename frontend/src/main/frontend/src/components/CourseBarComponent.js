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
        <div className="courseBar">
            <h5> Courses </h5>
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
    );
};

export default CourseBarComponent;