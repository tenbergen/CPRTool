import { BrowserRouter, Route, Routes } from 'react-router-dom';
import App from '../App';
import CreateCoursePage from '../pages/CreateCoursePage';
import TeacherDashboardPage from '../pages/TeacherDashboardPage';
import StudentDashboardPage from '../pages/StudentDashboardPage'
import EditCoursePage from '../pages/EditCoursePage';
import TodoPage from "../pages/TodoPage";
import CreateAssignmentPage  from "../pages/CreateAssignmentPage";
import UnauthedErrorPage from '../pages/UnauthedErrorPage';
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
