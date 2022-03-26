import React from "react";
import "./styles/CourseBar.css"
import {useDispatch, useSelector} from "react-redux";
import {setCurrentCourse} from "../redux/features/courseSlice";

const CourseBarComponent = () => {
    const dispatch = useDispatch()
    const courses = useSelector((state) => state.courses.courses)

    // to use when endpoint is not working
    const dummyCourses = ["Intro to Programming", "Programming Languages", "Systems Programming"]

    const onCourseClick = (course) => {
        dispatch(setCurrentCourse(course))
    }

    const trList = []

    dummyCourses.forEach(course => {
        trList.push(<tr className="TheTable">
            <td>
                <div className="colorForTable"/>
                <div className="courseText" onClick={() => onCourseClick(course)}> {course} </div>
            </td>
        </tr>)
    })

    return (
        <div className="courseBar">
            Courses <br/>
            {trList}
        </div>
    );
};

export default CourseBarComponent;