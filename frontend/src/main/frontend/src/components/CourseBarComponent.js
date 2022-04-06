import React, {useEffect, useState} from "react";
import "./styles/CourseBar.css"
import {useDispatch, useSelector} from "react-redux";
import {getCourseDetailsAsync, getCoursesAsync, getStudentCoursesAsync} from "../redux/features/courseSlice";
import {Link} from "react-router-dom";
import {setUserInformation} from "../redux/features/authSlice";

const CourseBarLink = ({ active, course, onClick }) => {
    const role = useSelector((state) => state.auth.role)
    const normalStyle = { backgroundColor: "rgba(255, 255, 255, 0.25)" }
    const clickedStyle = { backgroundColor: "white"}

    return (
        <Link to={`/details/${role}/${course.course_id}`} onClick={onClick}>
            <tr>
                <td style={active ? clickedStyle : normalStyle} >
                    <div className="colorForTable"/>
                    <div className="course_info">
                        <p className="course_id"> {course.abbreviation}-{course.course_section} </p>
                        <p className="course_text"> <b> {course.course_name} </b> </p>
                    </div>
                </td>
            </tr>
        </Link>
    );
}

const CourseBarComponent = () => {
    const dispatch = useDispatch()
    const { role, lakerId, dataLoaded } = useSelector((state) => state.auth)
    const { courses, currentCourse} = useSelector((state) => state.courses)

    const [chosen, setChosen] = useState(currentCourse.course_id);

    useEffect(() => {
        dispatch(setUserInformation())
        dataLoaded && role === "professor" ? dispatch(getCoursesAsync()) : dispatch(getStudentCoursesAsync(lakerId))
    }, [])

    const onCourseClick = (course) => {
        setChosen(course.course_id)
        dispatch(getCourseDetailsAsync(course.course_id))
    }

    return (
        <div className="cbc-parent">
            <h2> Courses </h2>
            <div className="cbc-courses">
            <div>
                {courses.map(course =>
                    <CourseBarLink
                        onClick={() => onCourseClick(course)}
                        active={course.course_id === chosen}
                        course={course}
                    />
                )}
            </div>
            </div>
        </div>
    );
};

export default CourseBarComponent;