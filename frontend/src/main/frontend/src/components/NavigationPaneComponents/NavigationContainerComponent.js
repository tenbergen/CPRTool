import uuid from "react-uuid";
import './_NavigationComponentStyle.css'
import useCollapse from "react-collapsed";
import HomeTileComponent from "./HomeTileComponent";
import CoursesTileComponent from "./CoursesTileComponent";
import {useRef, useState} from "react";
import LogoutButton from "../GlobalComponents/LogoutButton";

const NavigationContainerComponent = () => {

    const [trigger, setTrigger] = useState(0)

    const deactivateTiles = () => {
        setTrigger((trigger) => trigger + 1)
    }

    return (
        <div className="parent-container">
            <div className="navigation-container">
                <HomeTileComponent trigger={trigger} deactivateTiles={deactivateTiles}/>
                <CoursesTileComponent trigger={trigger} deactivateTiles={deactivateTiles}/>
            </div>
            <LogoutButton/>
        </div>
    );
};

export default NavigationContainerComponent;