import React, {useState} from 'react';
import { Link } from "react-router-dom";

import SidebarComponent from "../components/SidebarComponent";
import "./styles/TeacherDashboardStyle.css"
import axios from "axios";

function TeacherDashboardPage() {
    const getCourseUrl = ""
    const courseList = []
    let listComp = null

    axios.get(getCourseUrl).then( (response) => {
        console.log(response)
        for (let item in response.data) {
            courseList.push(item)
        }

        listComp = courseList.map((string) =>
            <li>{string}</li>
        );
        console.log(courseList)
    })

    return (
        <div className={"TeacherDashboard"}>
                <SidebarComponent />
            <div id="teacher">
                <h1>
                    Teacher Dashboard
                </h1>
                <div id="addClass">
                    <div id="courseList">
                        {courseList.length !== 0 ? {listComp}: null}
                    </div>
                    <Link to="/createCourse">
                        <button id="addButton">
                            Create Course
                        </button>
                    </Link>
                </div>
            </div>
        </div>
    );
}

export default  TeacherDashboardPage;