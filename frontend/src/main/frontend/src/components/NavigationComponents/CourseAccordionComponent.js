import './_ProfessorNavigationComponents.css'
import React, {useState} from 'react';
import { Link, useNavigate } from 'react-router-dom'
import {useSelector} from "react-redux";
import professorAssignmentPage from "../../pages/TeacherPages/ProfessorAssignmentPage";
import { Navigate } from "react-router-dom";
const CourseAccordionComponent = ({course, onClick}) => {
    const role = useSelector((state) => state.auth.role);
    const [open, setOpen] = useState(false)
    const navigate = useNavigate()

    const toggleActive = () => {
        setOpen(!open)
        let title = document.getElementById(course.course_id.toString())
        title.classList.toggle("course-accordion-title-active")
        title.classList.toggle("course-accordion-title-default")
        title.children[0].classList.toggle("inter-20-medium")
        title.children[0].classList.toggle("inter-20-bold-blue")
        title.children[1].classList.toggle("dropdown-icon-default")
        title.children[1].classList.toggle("dropdown-icon-active")
    }

    return(
        <div>
            <div id={course.course_id.toString()} className="course-accordion-title-default" onClick={() => toggleActive()}>
                <span className="inter-20-medium">{course.course_name}</span>
                <div className="dropdown-icon-default"/>
            </div>
            {open && (
                <div id="courseOptions">
                    {role === 'professor'
                        ? (
                            <Link to={`/${role}/${course.course_id}/details`} onClick={onClick}>
                                <div id="accordionCourseDetails" className="course-accordion-submenu-tile inter-20-medium">Course Details</div>
                            </Link>
                        ) : (
                            <div className="invisible-div"/>
                        )
                    }
                    <Link to={`/${role}/${course.course_id}/assignments`} onClick={onClick}>
                        <div id="accordionAssignments" className="course-accordion-submenu-tile inter-20-medium">Assignments</div>
                    </Link>

                    {role === 'professor'
                        ? (
                          //Note from Dom here, the way the display matrix page is set up causes the matrix to not update properly
                            //so I had to make the page renavigate to the peer review page and then completely reload.
                            <Link to={`/${role}/${course.course_id}/Peer Review`} onClick={() => {navigate(`/${role}/${course.course_id}/Peer Review`)
                                                                                                            window.location.reload(false)}}>
                            <div id="accordionPeerReviews" className="course-accordion-submenu-tile inter-20-medium">Peer Reviews</div>
                            </Link>
                        ) : (
                            <Link to={`/${role}/${course.course_id}/peer-review`} onClick={onClick}>
                                <div id="accordionPeerReviews" className="course-accordion-submenu-tile inter-20-medium">Peer Reviews</div>
                            </Link>
                        )
                    }

                    <Link to={`/${role}/${course.course_id}/teams`} onClick={onClick}>
                        <div id="accordionTeams" className="course-accordion-submenu-tile inter-20-medium">Teams</div>
                    </Link>
                    {role === 'professor'
                        ? (
                            <Link to={`/${role}/${course.course_id}/roster`} onClick={onClick}>
                                <div id="accordionRoster" className="course-accordion-submenu-tile inter-20-medium">Roster</div>
                            </Link>
                        ) : (
                            <div className="invisible-div"/>
                        )
                    }
                    <Link to={`/${role}/${course.course_id}/grades`} onClick={onClick}>
                        <div id="accordionGradesOverview" className="course-accordion-submenu-tile inter-20-medium">Grades Overview</div>
                    </Link>
                </div>
            )}
        </div>
    );
};

export default CourseAccordionComponent;