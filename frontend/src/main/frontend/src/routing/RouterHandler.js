import { BrowserRouter, Route, Routes } from 'react-router-dom';
import App from '../App';
import CreateCoursePage from '../pages/TeacherPages/CreateCoursePage';
import CourseDetailsPage from '../pages/TeacherPages/CourseDetailsPage';
import StudentCoursePage from "../pages/StudentPages/StudentCoursePage";
import CreateAssignmentPage  from "../pages/TeacherPages/CreateAssignmentPage";
import UnauthedErrorPage from '../pages/AuthPages/UnauthedErrorPage';
import React from 'react';

const RouterHandler = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path='/' element={<App />} />
        <Route path='create/course' element={<CreateCoursePage />} />
        <Route path='details/:courseId' element={<CourseDetailsPage />} />
        <Route path='todoCourse/:courseId' element={<StudentCoursePage />} />
        <Route path='create/assignment' element={<CreateAssignmentPage />} />
        <Route path='unauthenticated' element={<UnauthedErrorPage />} />
        </Routes>
    </BrowserRouter>
  );
};

export default RouterHandler;
