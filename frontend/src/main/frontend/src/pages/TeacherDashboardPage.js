import React, {useEffect, useState} from 'react';
import { Link } from "react-router-dom";

import SidebarComponent from "../components/SidebarComponent";
import "./styles/TeacherDashboardStyle.css"
import axios from "axios";

function TeacherDashboardPage() {
    // put the url of the endpoint here!
    const [isLoading, setLoading] = useState(true)
    const getCourseUrl = "http://moxie.cs.oswego.edu:13128/view/professor/courses"
    const courseList = []
    const [listComp, setListComp] = useState()


    useEffect(() => {
        axios.get(getCourseUrl).then( (response) => {
            //parse JSON into a array of strings here!
            console.log(response)
            console.log("DATA: " + response.data)
            for(let i = 0; i < response.data.length; i++)
            {
                courseList.push(response.data[i])
            }


            setListComp( courseList.map((object) =>
                <Link to="/editCourse" state={{from: object}}>
                    <li className="courseListItem" value={object}>{object.CourseName}</li>
                </Link>
            ));
            setLoading(false)
        })
    }, []);


    // this allows the courses to be received and rendered, hence this and useEffect
    if(isLoading) {
        return <div><h1>LOADING</h1></div>
    }

    return (
        <div className={"TeacherDashboard"}>
                <SidebarComponent />
            <div id="teacher">
                <h1>Hello Teacher</h1>
                <div id="courseList">
                    {/*{courseList.length !== 0 ? {listComp}: null}*/}
                    <ul>{listComp}</ul>
                </div>
                <div id="addClass">
                    <Link to="/createCourse">
                        <button id="addButton">
                            Create new course
                        </button>
                    </Link>
                </div>
            </div>
        </div>
    );
}

export default  TeacherDashboardPage;