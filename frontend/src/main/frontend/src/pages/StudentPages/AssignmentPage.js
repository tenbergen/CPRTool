import React, {useEffect, useState} from 'react';
import {useParams} from "react-router-dom";
import SidebarComponent from "../../components/SidebarComponent";
import AssBarComponent from "../../components/AssBarComponent";
import "./styles/AssignmentPageStyle.css"
import { useDispatch, useSelector } from "react-redux";

import axios from "axios";

function AssignmentPage() {
    const dispatch = useDispatch()
    const currentCourse = useSelector((state) => state.courses.currentCourse)
    const assUrl = `${window.location.protocol}//${window.location.host}/assignments/professor/courses/${currentCourse.course_id}/assignments/`
    //const assUrl = `http://moxie.cs.oswego.edu:13125/assignments/professor/courses/${currentCourse.course_id}/assignments/`
    const [assignments, setAssignments] = useState()
    const [isLoading, setLoad] = useState(true)

    const params = useParams()
    const assignmentId = params.assignmentName
    var index = 0;

    useEffect(async() => {
        try {
            await axios.get(assUrl).then( r=> {
                setAssignments(Array.from(r.data))
            })
        }
        catch (e) {
            setAssignments(Array())
        }
        setLoad(false)
    },[])


    if(isLoading) {
        return <div><h1>LOADING</h1></div>
    }

   for(var i = 0; i < assignments.length; i++){
       if(assignments[i].assignment_name.localeCompare(assignmentId) == 0){
           index = i;
           break;
       }
   }

    return (
        <div>
            <div className="ap-parent">
                <SidebarComponent/>
                <div className="ap-container">
                    <AssBarComponent/>
                    <div className="ap-component">
                        <h2>{params.assignmentName}</h2>
                        <div className="ap-assignmentArea">
                            <div className="ap-component-links">
                                <h3> Instructions: <br/> <br/> <br/>{assignments[index].instructions}</h3>
                                <h3> Due Date: {assignments[index].due_date}</h3>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default AssignmentPage;