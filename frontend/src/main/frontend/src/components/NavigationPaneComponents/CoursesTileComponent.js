import './_NavigationComponentStyle.css'
import React, {useEffect} from 'react';

const CoursesTileComponent = ({trigger, deactivateTiles}) => {

    useEffect(() => {
        let coursesIcon = document.getElementById("courses-tile-first-line").children[0].children[0];
        let coursesTitle = document.getElementById("courses-tile-first-line").children[0].children[1];
        let dropdownIcon = document.getElementById("courses-tile-first-line").children[1];
        if(trigger){
            coursesIcon.classList.remove("courses-icon-active")
            coursesIcon.classList.add("courses-icon-default");
            coursesTitle.classList.remove("inter-24-bold-blue")
            coursesTitle.classList.add("inter-24-medium")
            dropdownIcon.classList.remove("dropdown-icon-active")
            dropdownIcon.classList.add("dropdown-icon-default")
        }
    })

    const toggleActive = () => {
        let coursesIcon = document.getElementById("courses-tile-first-line").children[0].children[0];
        let coursesTitle = document.getElementById("courses-tile-first-line").children[0].children[1];
        let dropdownIcon = document.getElementById("courses-tile-first-line").children[1];
        if(coursesIcon.classList.contains("courses-icon-default")){
            deactivateTiles()
        }
        setTimeout(() => {
            coursesIcon.classList.toggle("courses-icon-default")
            coursesIcon.classList.toggle("courses-icon-active");
            coursesTitle.classList.toggle("inter-24-medium")
            coursesTitle.classList.toggle("inter-24-bold-blue")
            dropdownIcon.classList.toggle("dropdown-icon-default")
            dropdownIcon.classList.toggle("dropdown-icon-active")
        }, 30)
    }

    const deselectSemesters = () => {
        let semesterSelectionButtons = document.getElementsByClassName("semester-selection-active")
        for(const button of semesterSelectionButtons){
            button.classList.remove("semester-selection-active")
            button.classList.add("semester-selection-inactive")
            button.classList.remove("inter-14-medium-white")
            button.classList.add("inter-14-medium-black")
        }
    }

    return (
        <div className="courses-tile">
            <div id="courses-tile-first-line" onClick={() => toggleActive()}>
                <div className="courses-tile-heading">
                    <div className="courses-icon-default"/>
                    <p className="inter-24-medium">Courses</p>
                </div>
                <div className="dropdown-icon-default"/>
            </div>
            <div className="courses-tile-second-line">
                <div id="all-button" className="semester-selection-inactive inter-14-medium-black" onClick={() => {
                    deselectSemesters()
                    let button = document.getElementById("all-button")
                    button.classList.remove("semester-selection-inactive")
                    button.classList.remove("inter-14-medium-black")
                    button.classList.add("semester-selection-active")
                    button.classList.add("inter-14-medium-white")
                }}>All</div>
                <div className="semester-selection-inactive inter-14-medium-black" id="spring-button" onClick={() => {
                    deselectSemesters()
                    let button = document.getElementById("spring-button")
                    button.classList.remove("semester-selection-inactive")
                    button.classList.remove("inter-14-medium-black")
                    button.classList.add("semester-selection-active")
                    button.classList.add("inter-14-medium-white")
                }}>SP</div>
                <div className="semester-selection-inactive inter-14-medium-black" id="summer-button" onClick={() => {
                    deselectSemesters()
                    let button = document.getElementById("summer-button")
                    button.classList.remove("semester-selection-inactive")
                    button.classList.remove("inter-14-medium-black")
                    button.classList.add("semester-selection-active")
                    button.classList.add("inter-14-medium-white")
                }}>SU</div>
                <div className="semester-selection-inactive inter-14-medium-black" id="fall-button" onClick={() => {
                    deselectSemesters()
                    let button = document.getElementById("fall-button")
                    button.classList.remove("semester-selection-inactive")
                    button.classList.remove("inter-14-medium-black")
                    button.classList.add("semester-selection-active")
                    button.classList.add("inter-14-medium-white")
                }}>FA</div>
                <div className="semester-selection-inactive inter-14-medium-black" id="winter-button" onClick={() => {
                    deselectSemesters()
                    let button = document.getElementById("winter-button")
                    button.classList.remove("semester-selection-inactive")
                    button.classList.remove("inter-14-medium-black")
                    button.classList.add("semester-selection-active")
                    button.classList.add("inter-14-medium-white")
                }}>WI</div>
                <select id="year-selection" className="inter-14-medium-black">
                    <option value={new Date().getFullYear()}>{new Date().getFullYear()}</option>
                    <option value={new Date().getFullYear() + 1}>{new Date().getFullYear() + 1}</option>
                </select>
            </div>
        </div>
    );
};

export default CoursesTileComponent;