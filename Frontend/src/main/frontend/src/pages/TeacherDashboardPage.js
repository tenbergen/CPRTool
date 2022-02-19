import React from 'react';

import SidebarComponent from "../components/SidebarComponent";
import "./styles/TeacherDashboardStyle.css"

function TeacherDashboardPage(){
    return (
        <div className={"TeacherDashboard"}>
                <SidebarComponent />
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

export default  TeacherDashboardPage;