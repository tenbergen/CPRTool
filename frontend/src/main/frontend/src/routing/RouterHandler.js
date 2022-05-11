import {Route, Routes} from 'react-router-dom';
import App from '../App';
import CreateCoursePage from '../pages/TeacherPages/CreateCoursePage';
import ProfessorCoursePage from '../pages/TeacherPages/ProfessorCoursePage';
import StudentCoursePage from '../pages/StudentPages/StudentCoursePage';
import CreateAssignmentPage from '../pages/TeacherPages/CreateAssignmentPage';
import StudentAssignmentPage from '../pages/StudentPages/StudentAssignmentPage';
import UnauthedErrorPage from '../pages/AuthPages/UnauthedErrorPage';
import React from 'react';
import RoleRouteHandler from './RoleRouteHandler';
import AuthRouteHandler from './AuthRouteHandler';
import ProfessorAssignmentPage from '../pages/TeacherPages/ProfessorAssignmentPage';
import StudentSubmittedAssignmentPage from "../pages/StudentPages/StudentSubmittedAssignmentPage";
import ProfessorSubmittedAssignmentPage from "../pages/TeacherPages/ProfessorSubmittedAssignmentPage";

const RouterHandler = () => {
    return (
        <Routes>
            <Route>
                {/*public routes*/}
                <Route path='unauthenticated' element={<UnauthedErrorPage/>}/>
                <Route path='*' element={<p> Page doesn't exist </p>}/>

                <Route element={<AuthRouteHandler/>}>
                    <Route path='/' element={<App/>}/>

                    {/*professor-only routes*/}
                    <Route element={<RoleRouteHandler allowedRoles={['professor']}/>}>
                        <Route path='create/course' element={<CreateCoursePage/>}/>
                        <Route path='details/professor/:courseId' element={<ProfessorCoursePage/>}/>
                        <Route path='details/professor/:courseId/create/assignment' element={<CreateAssignmentPage/>}/>
                        <Route path='details/professor/:courseId/:assignmentId' element={<ProfessorAssignmentPage/>}/>
                        <Route path='details/professor/:courseId/:assignmentId/:teamId/submitted' element={<ProfessorSubmittedAssignmentPage/>}/>
                    </Route>

                    {/*student routes*/}
                    <Route element={<RoleRouteHandler allowedRoles={['student', 'professor']}/>}>
                        <Route path='details/student/:courseId' element={<StudentCoursePage/>}/>
                        <Route path='details/student/:courseId/:assignmentId/:assignmentType' element={<StudentAssignmentPage/>}/>
                        <Route path='details/student/:courseId/:assignmentId/:assignmentType/:teamId' element={<StudentAssignmentPage/>}/>
                        <Route path='details/student/:courseId/:assignmentId/:teamId/submitted' element={<StudentSubmittedAssignmentPage />}/>
                    </Route>
                </Route>
            </Route>
        </Routes>
    );
};

export default RouterHandler;
