import uuid from "react-uuid";
import './_ProfessorNavigationComponents.css'
import useCollapse from "react-collapsed";
import HomeTileComponent from "./HomeTileComponent";
import CoursesTileComponent from "./CoursesTileComponent";
import {useRef, useState} from "react";
import LogoutButton from "../GlobalComponents/LogoutButton";
import BulkDownloadTileComponent from "./BulkDownloadTileComponent";

const NavigationContainerComponent = () => {

    const [coursesTrigger, setCoursesTrigger] = useState(0)
    const [homeTrigger, setHomeTrigger] = useState(0)
    const [homeActive, setHomeActive] = useState(false)

    const deactivateCourses = () => {
        setHomeActive(true)
        setCoursesTrigger((trigger) => trigger + 1)
    }

    const deactivateHome = () => {
        setHomeActive(false)
        setHomeTrigger((trigger) => trigger + 1)
    }

    return (
        <div className="parent-container">
            <div className="navigation-container">
                <HomeTileComponent trigger={homeTrigger} deactivateCourses={deactivateCourses}/>
                <CoursesTileComponent trigger={coursesTrigger} homeActive={homeActive} deactivateHome={deactivateHome}/>
            </div>
            <LogoutButton/>
        </div>
    );
};

export default NavigationContainerComponent;