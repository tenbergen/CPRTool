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

    return (
        <div className="courseBar">
            Courses <br/>
            <tr className="TheTable">
                <th>
                    <div className="colorForTable">
                        {dummyCourses.map(course => {
                            return <div className="courseText" onClick={() => onCourseClick(course)}> {course} </div>
                        })}
                    </div>
                </th>
            </tr>
        </div>
    );
};

export default CourseBarComponent;