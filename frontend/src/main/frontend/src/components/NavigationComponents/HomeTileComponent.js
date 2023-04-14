import './_ProfessorNavigationComponents.css'
import React, {forwardRef, useEffect, useImperativeHandle} from 'react';
import { Link } from 'react-router-dom';
import {useSelector} from "react-redux";

const HomeTileComponent = ({trigger, deactivateCourses}) => {
    const role = useSelector((state) => state.auth.role);
    useEffect(() => {
        if(trigger){
            document.getElementById("home-tile").classList.remove("inter-24-bold-blue")
            document.getElementById("home-tile").classList.add("inter-24-medium")
            document.getElementById("home-tile").children[0].classList.remove("home-icon-active")
            document.getElementById("home-tile").children[0].classList.add("home-icon-default")
        }
    })

    const onClick = () => {
        deactivateCourses()
        setTimeout(() => {
            document.getElementById("home-tile").classList.remove("inter-24-medium")
            document.getElementById("home-tile").classList.add("inter-24-bold-blue")
            document.getElementById("home-tile").children[0].classList.remove("home-icon-default")
            document.getElementById("home-tile").children[0].classList.add("home-icon-active")
        }, 30)
    }

    return (
        <Link to={`/`} onClick={onClick}>
            <div id="home-tile" className="inter-24-medium">
                <div className="home-icon-default"></div>
                <p>Home</p>
            </div>
        </Link>
    );
};

export default HomeTileComponent;