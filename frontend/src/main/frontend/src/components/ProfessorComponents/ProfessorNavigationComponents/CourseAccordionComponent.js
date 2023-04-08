import './_ProfessorNavigationComponents.css'
import React, {useEffect, useState} from 'react';
import {Link} from "react-router-dom";
import {useSelector} from "react-redux";

const CourseAccordionComponent = ({course, onClick, trigger, deselectCourses}) => {
    const role = useSelector((state) => state.auth.role);
    const [open, setOpen] = useState(false)


    const toggleActive = () => {
        setOpen(!open)
        let title = document.getElementById(course.course_id.toString())
        title.classList.toggle("course-accordion-title-active")
        title.classList.toggle("course-accordion-title-default")
    }

    return(
        <div>
            <div id={course.course_id.toString()} className="course-accordion-title-default" onClick={() => toggleActive()}>
                <span className="inter-20-medium">{course.course_name}</span>
            </div>
            {open && (
                <div id="courseOptions">
                    <div id="accordionCourseDetails" className="course-accordion-submenu-tile inter-20-medium">Course Details</div>
                    <Link to={`/${role}/${course.course_id}`} onClick={onClick}>
                        <div id="accordionAssignments" className="course-accordion-submenu-tile inter-20-medium">Assignments</div>
                    </Link>
                    <div id="accordionPeerReviews" className="course-accordion-submenu-tile inter-20-medium">Peer Reviews</div>
                    <div id="accordionTeams" className="course-accordion-submenu-tile inter-20-medium">Teams</div>
                    <div id="accordionRoster" className="course-accordion-submenu-tile inter-20-medium">Roster</div>
                    <div id="accordionGradesOverview" className="course-accordion-submenu-tile inter-20-medium">Grades Overview</div>
                </div>
            )}
        </div>
    );
};

export default CourseAccordionComponent;