import React, {useEffect, useState} from 'react';
import { Link } from "react-router-dom";

import SidebarComponent from "../components/SidebarComponent";
import "./styles/TeacherDashboardStyle.css"
import axios from "axios";

function TeacherDashboardPage() {
    // put the url of the endpoint here!
    const [isLoading, setLoading] = useState(true)
    const getCourseUrl = "http://moxie.cs.oswego.edu:13128/professor/courses"
    const courseList = []
    const [listComp, setListComp] = useState()


    useEffect(() => {
        axios.get(getCourseUrl).then( (response) => {
            //parse JSON into a array of strings here!
            console.log(response)
            for(let i = 0; i < response.data.length; i++)
            {
                courseList.push(response.data[i].courseName)

            }

            setListComp( courseList.map((string) =>
                <Link to="/editCourse">
                    <li className="courseListItem">{string}</li>
                </Link>
            ));
            setLoading(false)
        })
    }, []);


    if(isLoading) {
        console.log(listComp)
        return <div><h1>LOADING</h1></div>
    }
    else {
        console.log(listComp)
    }

    return (
        <div className={"TeacherDashboard"}>
                <SidebarComponent />
            <div id="teacher">
                <h1>
                    Teacher Dashboard
                </h1>
                <div id="courseList">
                    {/*{courseList.length !== 0 ? {listComp}: null}*/}
                    <ul>{listComp}</ul>
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