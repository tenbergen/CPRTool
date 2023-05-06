import './_ProfessorNavigationComponents.css'
import React, {useEffect, useState} from 'react';
import { useDispatch , useSelector} from "react-redux";
import CourseAccordionComponent from "./CourseAccordionComponent";
import uuid from "react-uuid";
import {getCourseDetailsAsync, getCurrentCourseStudentsAsync} from "../../redux/features/courseSlice";
import {getCombinedAssignmentPeerReviews, getCourseAssignmentsAsync} from "../../redux/features/assignmentSlice";
import {getCurrentCourseTeamAsync} from "../../redux/features/teamSlice";
import {useParams} from "react-router-dom";

const CoursesTileComponent = ({trigger, homeActive, deactivateHome}) => {

    const dispatch = useDispatch();
    const { courses } = useSelector((state) => state.courses);
    const [open, setOPen] = useState(false)
    const [year, setYear] = useState((new Date()).getFullYear().toString() + " \u2304")
    const [semester, setSemester] = useState((new Date).getMonth() < 2 ? "Winter" : (new Date).getMonth() < 5 ? "Spring" : (new Date).getMonth() < 8 ? "Summer" : "Fall")
    const { role, lakerId, dataLoaded } = useSelector((state) => state.auth);
    const { courseId } = useParams();
    const [chosen, setChosen] = useState(courseId);
    const teamId = '1';

    useEffect(() => {
        if(homeActive){
            setOPen(false)
            let coursesIcon = document.getElementById("courses-tile-first-line").children[0].children[0]
            let coursesTitle = document.getElementById("courses-tile-first-line").children[0].children[1]
            let dropdownIcon = document.getElementById("courses-tile-first-line").children[1]
            let coursesTile = document.getElementById("courses-tile")
            coursesIcon.classList.remove("courses-icon-active")
            coursesIcon.classList.add("courses-icon-default")
            coursesTitle.classList.remove("inter-24-bold-blue")
            coursesTitle.classList.add("inter-24-medium")
            dropdownIcon.classList.remove("dropdown-icon-active")
            dropdownIcon.classList.add("dropdown-icon-default")
            coursesTile.classList.remove("courses-tile-active")
            coursesTile.classList.add("courses-tile-inactive")
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
            setSemester((new Date).getMonth() < 2 ? "Winter" : (new Date).getMonth() < 5 ? "Spring" : (new Date).getMonth() < 8 ? "Summer" : "Fall")
            setYear((new Date()).getFullYear().toString() + " \u2304")
        } else {
            coursesTile.classList.remove("courses-tile-active")
            coursesTile.classList.add("courses-tile-inactive")
        }
        ToggleSecondLine()
    }

    const ToggleSecondLine = () => {
        setOPen(!open);

        let coursesIcon = document.getElementById("courses-tile-first-line").children[0].children[0]
        let coursesTitle = document.getElementById("courses-tile-first-line").children[0].children[1]
        let dropdownIcon = document.getElementById("courses-tile-first-line").children[1]
        setTimeout(() => {
            if(open){
                coursesIcon.classList.add("courses-icon-default")
                coursesIcon.classList.remove("courses-icon-active")
                coursesTitle.classList.add("inter-24-medium")
                coursesTitle.classList.remove("inter-24-bold-blue")
                dropdownIcon.classList.add("dropdown-icon-default")
                dropdownIcon.classList.remove("dropdown-icon-active")
            } else {
                coursesIcon.classList.remove("courses-icon-default")
                coursesIcon.classList.add("courses-icon-active")
                coursesTitle.classList.remove("inter-24-medium")
                coursesTitle.classList.add("inter-24-bold-blue")
                dropdownIcon.classList.remove("dropdown-icon-default")
                dropdownIcon.classList.add("dropdown-icon-active")
            }
        }, 30)
    };

    const selectSemester = (activeButton, inactiveButtons) => {
        if(activeButton.classList.contains("semester-selection-inactive")){
            activeButton.classList.remove("semester-selection-inactive")
            activeButton.classList.remove("inter-14-medium-black")
            activeButton.classList.add("semester-selection-active")
            activeButton.classList.add("inter-14-medium-white")
        }

        inactiveButtons.forEach(button => {
            button.classList.remove("semester-selection-active")
            button.classList.remove("inter-14-medium-white")
            button.classList.add("semester-selection-inactive")
            button.classList.add("inter-14-medium-black")
        })
    }

    const selectYear = () => {
        let popup = document.getElementById("myPopup")
        popup.classList.toggle("show")
    }

    return (
        <div>
            <div id="courses-tile" className="courses-tile-inactive">
                <div>
                    <div id="courses-tile-first-line" onClick={() => {
                        deactivateHome()
                        toggleActive()
                    }}>
                        <div className="courses-tile-heading">
                            <div className="courses-icon-default"/>
                            <p className="inter-24-medium">Courses</p>
                        </div>
                        <div className="dropdown-icon-default"/>
                    </div>
                </div>
                {open && (
                    <div id="courses-tile-second-line">
                        {/*<div id="all-button" className="semester-selection-inactive inter-14-medium-black" onClick={() => {*/}
                        {/*    selectAllSemesters()*/}
                        {/*}}>All</div>*/}
                        <div className={(new Date()).getMonth() > 1 && (new Date()).getMonth() < 5
                            ? "semester-selection-active inter-14-medium-white"
                            : "semester-selection-inactive inter-14-medium-black"}
                             id="spring-button"
                             onClick={() => {
                            let button = document.getElementById("spring-button")
                            let buttonsToDeactivate = [
                                document.getElementById("summer-button"),
                                document.getElementById("fall-button"),
                                document.getElementById("winter-button")
                            ]
                            selectSemester(button, buttonsToDeactivate)
                            setSemester("Spring")
                        }}>SP</div>
                        <div className={(new Date()).getMonth() > 4 && (new Date()).getMonth() < 8 ? "semester-selection-active inter-14-medium-white" : "semester-selection-inactive inter-14-medium-black"} id="summer-button" onClick={() => {
                            let button = document.getElementById("summer-button")
                            let buttonsToDeactivate = [
                                document.getElementById("spring-button"),
                                document.getElementById("fall-button"),
                                document.getElementById("winter-button")
                            ]
                            selectSemester(button, buttonsToDeactivate)
                            setSemester("Summer")
                        }}>SU</div>
                        <div className={(new Date()).getMonth() > 8 ? "semester-selection-active inter-14-medium-white" : "semester-selection-inactive inter-14-medium-black"} id="fall-button" onClick={() => {
                            let button = document.getElementById("fall-button")
                            let buttonsToDeactivate = [
                                document.getElementById("spring-button"),
                                document.getElementById("summer-button"),
                                document.getElementById("winter-button")
                            ]
                            selectSemester(button, buttonsToDeactivate)
                            setSemester("Fall")
                        }}>FA</div>
                        <div className={(new Date()).getMonth() < 2 ? "semester-selection-active inter-14-medium-white" : "semester-selection-inactive inter-14-medium-black"} id="winter-button" onClick={() => {
                            let button = document.getElementById("winter-button")
                            let buttonsToDeactivate = [
                                document.getElementById("spring-button"),
                                document.getElementById("summer-button"),
                                document.getElementById("fall-button")
                            ]
                            selectSemester(button, buttonsToDeactivate)
                            setSemester("Winter")
                        }}>WI</div>
                        <div id="year-popup" className="inter-16-medium-black" onClick={() => selectYear()}>{year}
                            <div className="popup-text" id="myPopup">
                                <div className="year-individual-selection" onClick={() => setYear((new Date().getFullYear() + 1).toString() + " \u2304")}>
                                    {/*<input type="checkbox"/>*/}
                                    <span className="inter-14-medium-black">{new Date().getFullYear() + 1}</span>
                                </div>
                                <div className="year-individual-selection" onClick={() => setYear((new Date().getFullYear()).toString() + " \u2304")}>
                                    {/*<input type="checkbox"/>*/}
                                    <span className="inter-14-medium-black">{new Date().getFullYear()}</span>
                                </div>
                                <div className="year-individual-selection" onClick={() => setYear((new Date().getFullYear() - 1).toString() + " \u2304")}>
                                    {/*<input type="checkbox"/>*/}
                                    <span className="inter-14-medium-black">{new Date().getFullYear() - 1}</span>
                                </div>
                                <div className="year-individual-selection" onClick={() => setYear((new Date().getFullYear() - 2).toString() + " \u2304")}>
                                    {/*<input type="checkbox"/>*/}
                                    <span className="inter-14-medium-black">{new Date().getFullYear() - 2}</span>
                                </div>
                                <div className="year-individual-selection" onClick={() => setYear((new Date().getFullYear() - 3).toString() + " \u2304")}>
                                    {/*<input type="checkbox"/>*/}
                                    <span className="inter-14-medium-black">{new Date().getFullYear() - 3}</span>
                                </div>
                                <div className="year-individual-selection" onClick={() => setYear((new Date().getFullYear() - 4).toString() + " \u2304")}>
                                    {/*<input type="checkbox"/>*/}
                                    <span className="inter-14-medium-black">{new Date().getFullYear() - 4}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                )}
            </div>
            {open && (
                <div className="course-list">
                    {courses.map(
                        (course) =>
                            // course && (
                            course.year === year.substring(0,4) && course.semester === semester && (
                                <CourseAccordionComponent
                                    key={uuid()}
                                    onClick={() => onCourseClick(course)}
                                    active={course.course_id === chosen}
                                    course={course}
                                />
                            )
                    )}
                </div>
            )}
        </div>
    );
};

export default CoursesTileComponent;
