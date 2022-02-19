import React from 'react';

import Sidebar from "./Sidebar";
import "./TeacherDashboard.css"

function TeacherDashboard(){
    return (
        <div className={"TeacherDashboard"}>
                <Sidebar />
            <div id="teacher">
                <h1>
                    Teacher Dashboard
                </h1>
                <div id="addClass">
                    <button id="addButton">
                        Add Class
                    </button>
                </div>
            </div>

        </div>

    );
}

export default  TeacherDashboard;