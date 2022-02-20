import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { BrowserRouter, Routes, Route } from "react-router-dom";
import TeacherDashboardPage from "./pages/TeacherDashboardPage";
import CreateCoursePage from "./pages/CreateCoursePage";
import EditCoursePage from "./pages/EditCoursePage";
import LoginPage from "./pages/LoginPage";

ReactDOM.render(
    <BrowserRouter>
        <Routes>
            <Route path="/" element={<LoginPage />} />
            {/*<Route path="login" element={<LoginPage />} />*/}
            <Route path="createCourse" element={<CreateCoursePage />} />
            <Route path="teacherDashboard" element={<TeacherDashboardPage />} />
            <Route path="editCourse" element={<EditCoursePage />} />
        </Routes>
    </BrowserRouter>,
  document.getElementById('root')
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
