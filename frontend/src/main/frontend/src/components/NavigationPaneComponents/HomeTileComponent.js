import './_NavigationComponentStyle.css'
import React, {forwardRef, useEffect, useImperativeHandle} from 'react';
import CoursesTileComponent from "./CoursesTileComponent";

const HomeTileComponent = ({trigger, deactivateTiles}) => {

    useEffect(() => {
        if(trigger){
            document.getElementById("home-tile").classList.remove("inter-24-bold-blue")
            document.getElementById("home-tile").classList.add("inter-24-medium")
            document.getElementById("home-tile").children[0].classList.remove("home-icon-active")
            document.getElementById("home-tile").children[0].classList.add("home-icon-default")
        }
    })

    const onClick = () => {
        deactivateTiles()
        setTimeout(() => {
            document.getElementById("home-tile").classList.remove("inter-24-medium")
            document.getElementById("home-tile").classList.add("inter-24-bold-blue")
            document.getElementById("home-tile").children[0].classList.remove("home-icon-default")
            document.getElementById("home-tile").children[0].classList.add("home-icon-active")
        }, 30)
    }

    return (
        <div id="home-tile" className="inter-24-medium" onClick={() => onClick()}>
            <div className="home-icon-default"></div>
            <p>Home</p>
        </div>
    );
};

export default HomeTileComponent;