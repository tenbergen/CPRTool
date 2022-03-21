import { BrowserRouter, Route, Routes } from 'react-router-dom';
import App from '../App';
import CreateCoursePage from '../pages/TeacherPages/CreateCoursePage';
import TeacherDashboardPage from '../pages/TeacherPages/TeacherDashboardPage';
import StudentDashboardPage from '../pages/StudentPages/StudentDashboardPage'
import EditCoursePage from '../pages/TeacherPages/EditCoursePage';
import TodoPage from "../pages/StudentPages/TodoPage";
import CreateAssignmentPage  from "../pages/TeacherPages/CreateAssignmentPage";
import UnauthedErrorPage from '../pages/AuthPages/UnauthedErrorPage';
import React from 'react';


const RouterHandler = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path='/' element={<App />} />
        <Route path='createCourse' element={<CreateCoursePage />} />
        <Route path='teacherDashboard' element={<TeacherDashboardPage />} />
        <Route path='studentDashboard' element={<StudentDashboardPage />} />
        <Route path='todoCourse' element={<TodoPage />} />
        <Route path='editCourse' element={<EditCoursePage />} />
        <Route path='createAssignment' element={<CreateAssignmentPage />} />
        <Route path='unauthenticated' element={<UnauthedErrorPage />} />
        </Routes>
    </BrowserRouter>
  );
};

export default RouterHandler;
