import uuid from "react-uuid";
import './_NavigationComponentStyle.css'
import useCollapse from "react-collapsed";
import HomeTileComponent from "./HomeTileComponent";
import CoursesTileComponent from "./CoursesTileComponent";
import {useRef, useState} from "react";

const NavigationContainerComponent = () => {

    const [trigger, setTrigger] = useState(0)

    const deactivateTiles = () => {
        setTrigger((trigger) => trigger + 1)
    }

    return (
        <div className="parent-container">
            <HomeTileComponent trigger={trigger} deactivateTiles={deactivateTiles}/>
            <CoursesTileComponent trigger={trigger} deactivateTiles={deactivateTiles}/>
        </div>
    );
};

export default NavigationContainerComponent;