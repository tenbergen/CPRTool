import './_ProfessorNavigationComponents.css'
import React, {useEffect, useState} from 'react';
import YearDropdownComponent from "./YearDropdownComponent";
import { useDispatch , useSelector} from "react-redux";
import CourseAccordionComponent from "./CourseAccordionComponent";
import uuid from "react-uuid";
import {getCourseDetailsAsync, getCurrentCourseStudentsAsync} from "../../../redux/features/courseSlice";
import {getCombinedAssignmentPeerReviews, getCourseAssignmentsAsync} from "../../../redux/features/assignmentSlice";
import {getCurrentCourseTeamAsync} from "../../../redux/features/teamSlice";
import {useParams} from "react-router-dom";

const CoursesTileComponent = ({trigger, deactivateTiles}) => {

    const dispatch = useDispatch();
    const { courses } = useSelector((state) => state.courses);
    const [open, setOPen] = useState(false)
    const [year, setYear] = useState("")
    const [semester, setSemester] = useState("")
    const { role, lakerId, dataLoaded } = useSelector((state) => state.auth);
    const { courseId } = useParams();
    const [chosen, setChosen] = useState(courseId);
    const teamId = '1';

    useEffect((calledFromOutside) => {
        if(calledFromOutside){
            setOPen(false)
        }
        let coursesIcon = document.getElementById("courses-tile-first-line").children[0].children[0];
        let coursesTitle = document.getElementById("courses-tile-first-line").children[0].children[1];
        let dropdownIcon = document.getElementById("courses-tile-first-line").children[1];
        if(trigger){
            coursesIcon.classList.remove("courses-icon-active")
            coursesIcon.classList.add("courses-icon-default");
            coursesTitle.classList.remove("inter-24-bold-blue")
            coursesTitle.classList.add("inter-24-medium")
            dropdownIcon.classList.remove("dropdown-icon-active")
            dropdownIcon.classList.add("dropdown-icon-default")
        }
    })

    const onCourseClick = (course) => {
        const courseId = course.course_id;
        setChosen(courseId);
        dispatch(getCourseDetailsAsync(courseId));
        dispatch(getCurrentCourseStudentsAsync(courseId));
        dispatch(getCourseAssignmentsAsync(courseId));
        dispatch(getCurrentCourseTeamAsync({ courseId, lakerId }));
        role === 'professor'
            ? dispatch(getCourseAssignmentsAsync(courseId))
            : dispatch(
                getCombinedAssignmentPeerReviews({
                    courseId,
                    teamId,
                })
            );
    };

    const toggleActive = () => {
        let coursesTile = document.getElementById("courses-tile")
        if(coursesTile.classList.contains("courses-tile-inactive")){
            coursesTile.classList.remove("courses-tile-inactive")
            coursesTile.classList.add("courses-tile-active")
        } else {
            coursesTile.classList.remove("courses-tile-active")
            coursesTile.classList.add("courses-tile-inactive")
        }
        ToggleSecondLine()
    }

    async function deselectSemesters(){
        document.getElementById("all-button").innerHTML = "All"
        document.getElementById("all-button").classList.remove("inter-20-medium-white")

        document.getElementById("all-button").classList.remove("semester-selection-active")
        document.getElementById("all-button").classList.add("semester-selection-inactive")
        document.getElementById("all-button").classList.add("inter-14-medium-black")

        document.getElementById("spring-button").classList.remove("semester-selection-active")
        document.getElementById("spring-button").classList.add("semester-selection-inactive")
        document.getElementById("spring-button").classList.remove("inter-14-medium-white")
        document.getElementById("spring-button").classList.add("inter-14-medium-black")

        document.getElementById("summer-button").classList.remove("semester-selection-active")
        document.getElementById("summer-button").classList.add("semester-selection-inactive")
        document.getElementById("summer-button").classList.remove("inter-14-medium-white")
        document.getElementById("summer-button").classList.add("inter-14-medium-black")

        document.getElementById("fall-button").classList.remove("semester-selection-active")
        document.getElementById("fall-button").classList.add("semester-selection-inactive")
        document.getElementById("fall-button").classList.remove("inter-14-medium-white")
        document.getElementById("fall-button").classList.add("inter-14-medium-black")

        document.getElementById("winter-button").classList.remove("semester-selection-active")
        document.getElementById("winter-button").classList.add("semester-selection-inactive")
        document.getElementById("winter-button").classList.remove("inter-14-medium-white")
        document.getElementById("winter-button").classList.add("inter-14-medium-black")
    }

    const selectAllSemesters = () => {
        if(document.getElementById("all-button").classList.contains("semester-selection-active")){
            deselectSemesters()
        } else {
            document.getElementById("all-button").classList.remove("semester-selection-inactive")
            document.getElementById("all-button").classList.add("semester-selection-active")
            document.getElementById("all-button").classList.remove("inter-14-medium-black")

            document.getElementById("spring-button").classList.remove("semester-selection-inactive")
            document.getElementById("spring-button").classList.add("semester-selection-active")
            document.getElementById("spring-button").classList.remove("inter-14-medium-black")
            document.getElementById("spring-button").classList.add("inter-14-medium-white")

            document.getElementById("summer-button").classList.remove("semester-selection-inactive")
            document.getElementById("summer-button").classList.add("semester-selection-active")
            document.getElementById("summer-button").classList.remove("inter-14-medium-black")
            document.getElementById("summer-button").classList.add("inter-14-medium-white")

            document.getElementById("fall-button").classList.remove("semester-selection-inactive")
            document.getElementById("fall-button").classList.add("semester-selection-active")
            document.getElementById("fall-button").classList.remove("inter-14-medium-black")
            document.getElementById("fall-button").classList.add("inter-14-medium-white")

            document.getElementById("winter-button").classList.remove("semester-selection-inactive")
            document.getElementById("winter-button").classList.add("semester-selection-active")
            document.getElementById("winter-button").classList.remove("inter-14-medium-black")
            document.getElementById("winter-button").classList.add("inter-14-medium-white")

            document.getElementById("all-button").classList.remove("inter-14-medium-white")
            document.getElementById("all-button").classList.add("inter-20-medium-white")
            document.getElementById("all-button").innerHTML = '&#10003;'
        }
    }

    const ToggleSecondLine = () => {
        setOPen(!open);

        let coursesIcon = document.getElementById("courses-tile-first-line").children[0].children[0];
        let coursesTitle = document.getElementById("courses-tile-first-line").children[0].children[1];
        let dropdownIcon = document.getElementById("courses-tile-first-line").children[1];
        if(coursesIcon.classList.contains("courses-icon-default")){
            deactivateTiles()
        }
        setTimeout(() => {
            if(open){
                coursesIcon.classList.add("courses-icon-default")
                coursesIcon.classList.remove("courses-icon-active");
                coursesTitle.classList.add("inter-24-medium")
                coursesTitle.classList.remove("inter-24-bold-blue")
                dropdownIcon.classList.add("dropdown-icon-default")
                dropdownIcon.classList.remove("dropdown-icon-active")
            } else {
                coursesIcon.classList.remove("courses-icon-default")
                coursesIcon.classList.add("courses-icon-active");
                coursesTitle.classList.remove("inter-24-medium")
                coursesTitle.classList.add("inter-24-bold-blue")
                dropdownIcon.classList.remove("dropdown-icon-default")
                dropdownIcon.classList.add("dropdown-icon-active")
            }
        }, 30)
    };

    return (
        <div>
            <div id="courses-tile" className="courses-tile-inactive">
                <div id="courses-tile-first-line" onClick={() => toggleActive()}>
                    <div className="courses-tile-heading">
                        <div className="courses-icon-default"/>
                        <p className="inter-24-medium">Courses</p>
                    </div>
                    <div className="dropdown-icon-default"/>
                </div>
                {open && (
                    <div id="courses-tile-second-line">
                        <div id="all-button" className="semester-selection-inactive inter-14-medium-black" onClick={() => {
                            selectAllSemesters()
                        }}>All</div>
                        <div className="semester-selection-inactive inter-14-medium-black" id="spring-button" onClick={() => {
                            let button = document.getElementById("spring-button")
                            button.classList.toggle("semester-selection-inactive")
                            button.classList.toggle("inter-14-medium-black")
                            button.classList.toggle("semester-selection-active")
                            button.classList.toggle("inter-14-medium-white")
                        }}>SP</div>
                        <div className="semester-selection-inactive inter-14-medium-black" id="summer-button" onClick={() => {
                            let button = document.getElementById("summer-button")
                            button.classList.toggle("semester-selection-inactive")
                            button.classList.toggle("inter-14-medium-black")
                            button.classList.toggle("semester-selection-active")
                            button.classList.toggle("inter-14-medium-white")
                        }}>SU</div>
                        <div className="semester-selection-inactive inter-14-medium-black" id="fall-button" onClick={() => {
                            let button = document.getElementById("fall-button")
                            button.classList.toggle("semester-selection-inactive")
                            button.classList.toggle("inter-14-medium-black")
                            button.classList.toggle("semester-selection-active")
                            button.classList.toggle("inter-14-medium-white")
                        }}>FA</div>
                        <div className="semester-selection-inactive inter-14-medium-black" id="winter-button" onClick={() => {
                            let button = document.getElementById("winter-button")
                            button.classList.toggle("semester-selection-inactive")
                            button.classList.toggle("inter-14-medium-black")
                            button.classList.toggle("semester-selection-active")
                            button.classList.toggle("inter-14-medium-white")
                        }}>WI</div>
                        <YearDropdownComponent/>
                    </div>
                )}
            </div>
            <div className="course-list">
                {courses.map(
                    (course) =>
                        course && (
                            <CourseAccordionComponent
                                key={uuid()}
                                onClick={() => onCourseClick(course)}
                                active={course.course_id === chosen}
                                course={course}
                            />
                        )
                )}
            </div>
        </div>
    );
};

export default CoursesTileComponent;