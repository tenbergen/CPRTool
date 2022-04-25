import React, {useEffect, useState} from "react";
import "./styles/CourseBar.css"
import {useDispatch, useSelector} from "react-redux";
import {
    getCourseDetailsAsync,
    getCoursesAsync,
    getCurrentCourseStudentsAsync,
    getStudentCoursesAsync
} from "../redux/features/courseSlice";
import {Link, useParams} from "react-router-dom";
import {setUserInformation} from "../redux/features/authSlice";
import {getCombinedAssignmentPeerReviews, getCourseAssignmentsAsync} from "../redux/features/assignmentSlice";
import {getCurrentCourseTeamAsync} from "../redux/features/teamSlice";

const CourseBarLink = ({active, course, onClick}) => {
    const role = useSelector((state) => state.auth.role)
    const normalStyle = {backgroundColor: "rgba(255, 255, 255, 0.25)"}
    const clickedStyle = {backgroundColor: "white"}

    return (
        <Link to={`/details/${role}/${course.course_id}`} onClick={onClick}>
            <tr>
                <td style={active ? clickedStyle : normalStyle}>
                    <div className="colorForTable"/>
                    <div className="course_info">
                        <p className="outfit-16 course_id"> {course.abbreviation}-{course.course_section} </p>
                        <p className="kumba-25 course_text"> {course.course_name} </p>
                    </div>
                </td>
            </tr>
        </Link>
    );
}

const CourseBarComponent = ({title}) => {
    const dispatch = useDispatch()
    const {role, lakerId, dataLoaded} = useSelector((state) => state.auth)
    const {courses} = useSelector((state) => state.courses)
    const {courseId} = useParams()
    const [chosen, setChosen] = useState(courseId);
    const teamId = "1"

    useEffect(() => {
        dispatch(setUserInformation())
        dataLoaded && role === "professor" ? dispatch(getCoursesAsync()) : dispatch(getStudentCoursesAsync(lakerId))
    }, [])

    const onCourseClick = (course) => {
        const courseId = course.course_id
        setChosen(courseId)
        dispatch(getCourseDetailsAsync(courseId))
        dispatch(getCurrentCourseStudentsAsync(courseId))
        dispatch(getCourseAssignmentsAsync(courseId))
        dispatch(getCurrentCourseTeamAsync({courseId, lakerId}))
        role === "professor" ? dispatch(getCourseAssignmentsAsync(courseId)) : dispatch(getCombinedAssignmentPeerReviews({
            courseId,
            teamId
        }))
    }

    return (
        <div className="cbc-parent">
            <h2 className="kumba-30"> {title} </h2>
            {courses.length < 1 ? <p className="outfit-16" style={{marginLeft: "10%"}}> Create your first course</p> : null}
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