import React from "react-dom";
import {useState} from "react";
import "./styles/TodoPageStyle.css"
import SidebarComponent from "../../components/SidebarComponent"
import EditCourseComponent from "../../components/EditCourseComponent";
import RosterComponent from "../../components/RosterComponent";

function TodoPage() {
    return (
        <div className={"parent"}>
            <SidebarComponent/>
            <div className="container">
                <h1> To Do </h1>
            </div>
        {/*        TO DO FOR ASSIGNMENTS */}
        </div>
    );
}

export default TodoPage;