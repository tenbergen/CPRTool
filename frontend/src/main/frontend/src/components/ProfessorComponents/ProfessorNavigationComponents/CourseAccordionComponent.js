import './_ProfessorNavigationComponents.css'
import React from 'react';
import {Link} from "react-router-dom";
import {useSelector} from "react-redux";

const CourseAccordionComponent = ({course, onClick}) => {
    const role = useSelector((state) => state.auth.role);

    return(
        <div>
            <div id="course-accordion-title">
                <span className="inter-20-medium">{course.course_name}</span>
            </div>
            <div id="accordionCourseDetails" className="course-accordion-submenu-tile inter-20-medium">Course Details</div>
            <Link to={`/${role}/${course.course_id}`} onClick={onClick}>
                <div id="accordionAssignments" className="course-accordion-submenu-tile inter-20-medium">Assignments</div>
            </Link>
            <div id="accordionPeerReviews" className="course-accordion-submenu-tile inter-20-medium">Peer Reviews</div>
            <div id="accordionTeams" className="course-accordion-submenu-tile inter-20-medium">Teams</div>
            <div id="accordionRoster" className="course-accordion-submenu-tile inter-20-medium">Roster</div>
            <div id="accordionGradesOverview" className="course-accordion-submenu-tile inter-20-medium">Grades Overview</div>
        </div>
    );
};

export default CourseAccordionComponent;