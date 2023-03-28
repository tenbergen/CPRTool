import './_NavigationComponentStyle.css'
import React from 'react';

const CourseAccordionComponent = ({courseName}) => {
    return(
        <div id="course-accordion-title">
            <span className="inter-20-medium">{courseName}</span>

        </div>
    );
};

export default CourseAccordionComponent;