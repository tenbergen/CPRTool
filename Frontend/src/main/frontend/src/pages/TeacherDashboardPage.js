import React, {useState} from 'react';
import { Link } from "react-router-dom";

import SidebarComponent from "../components/SidebarComponent";
import "./styles/TeacherDashboardStyle.css"
import axios from "axios";

function TeacherDashboardPage() {
    // put the url of the endpoint here!
    const getCourseUrl = ""
    const courseList = []
    let listComp = null

    axios.get(getCourseUrl).then( (response) => {
        console.log(response)
        //parse JSON into a array of strings here!
        for (let item in response.data) {
            courseList.push(item)
        }

        listComp = courseList.map((string) =>
            <Link to="/editCourse">
            <li>{string}</li>
            </Link>
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
                <div id="courseList">
                    {/*{courseList.length !== 0 ? {listComp}: null}*/}
                    {listComp}
                </div>
                <div id="addClass">
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