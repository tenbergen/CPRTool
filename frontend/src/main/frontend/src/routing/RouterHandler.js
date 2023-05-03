import { Route, Routes } from 'react-router-dom'
import App from '../App'
import CreateCoursePage from '../pages/TeacherPages/CreateCoursePage'
import ProfessorCoursePage from '../pages/TeacherPages/ProfessorCoursePage'
import StudentCoursePage from '../pages/StudentPages/StudentCoursePage'
import CreateAssignmentPage from '../pages/TeacherPages/CreateAssignmentPage'
import StudentAssignmentPage from '../pages/StudentPages/StudentAssignmentPage'
import UnauthedErrorPage from '../pages/AuthPages/UnauthedErrorPage'
import React from 'react'
import RoleRouteHandler from './RoleRouteHandler'
import AuthRouteHandler from './AuthRouteHandler'
import ProfessorAssignmentPage from '../pages/TeacherPages/ProfessorAssignmentPage'
import StudentSubmittedAssignmentPage from '../pages/StudentPages/StudentSubmittedAssignmentPage'
import ProfessorSubmittedAssignmentPage from '../pages/TeacherPages/ProfessorSubmittedAssignmentPage'
import AdminInterface from '../pages/AdminPages/AdminInterface'
import ProfessorGradesPage from '../pages/TeacherPages/ProfessorGradesPage'
import StudentGradesPage from '../pages/StudentPages/StudentGradesPage'
import PeerReviewPage from "../pages/StudentPages/PeerReviewPage";
import PeerReviewListPage from "../components/StudentComponents/PeerReviewPage/PeerReviewListPage";
import DisplayMatrix from '../pages/TeacherPages/DisplayMatrix'
import AboutPage from "../pages/AboutPage/AboutPage";

const RouterHandler = () => {
  return (
    <Routes>
      <Route>
        {/*public routes*/}
        <Route path="unauthenticated" element={<UnauthedErrorPage/>}/>
        <Route path="*" element={<p> Page doesn't exist </p>}/>

        <Route element={<AuthRouteHandler/>}>
          <Route path="/" element={<App/>}/>

          <Route
              path="/about"
              element={<AboutPage />}
          />

          {/*professor-only routes*/}
          <Route element={<RoleRouteHandler allowedRoles={['professor']}/>}>
            <Route path="create" element={<CreateCoursePage/>}/>
            <Route
              path="professor/:courseId"
              element={<ProfessorCoursePage chosen="Assignments"/>}
            />
            <Route
              path="professor/:courseId/grades"
              element={<ProfessorGradesPage/>}
            />
            <Route
              path="professor/:courseId/Peer Review"
              element={<DisplayMatrix/>}
            />
            <Route
              path="professor/:courseId/create/assignment"
              element={<CreateAssignmentPage/>}
            />
            <Route
              path="professor/:courseId/:assignmentId"
              element={<ProfessorAssignmentPage/>}
            />
            <Route
              path={"professor/:courseId/assignments/create/assignment"}
              element={<CreateAssignmentPage/>}
            />
            <Route
              path="professor/:courseId/:assignmentId/:teamId/submitted"
              element={<ProfessorSubmittedAssignmentPage/>}
            />
            <Route
              path="professor/admin"
              element={<AdminInterface/>}
            />
            <Route
              path="professor"
              element={<App/>}
            />
            <Route
              path="/professor/:courseId/teams"
              element={<ProfessorCoursePage chosen="Teams"/>}
            />
            <Route
              path="/professor/:courseId/roster"
              element={<ProfessorCoursePage chosen="Roster"/>}
            />
            <Route
              path="/professor/:courseId/assignments"
              element={<ProfessorCoursePage chosen="Assignments"/>}
            />
            <Route
              path="/professor/:courseId/details"
              element={<ProfessorCoursePage chosen="Manage"/>}
            />
            <Route
              path="professor/:courseId/details/Profanity Settings"
              element={<ProfessorCoursePage chosen="Profanity"/>}
            />
            <Route
               path='professor/display-matrix/:courseId'
               element={<DisplayMatrix />}
            />
          </Route>

          {/*student routes*/}
          <Route
            element={
              <RoleRouteHandler allowedRoles={['student', 'professor']}/>
            }
          >
            <Route
              path="student/:courseId"
              element={<StudentCoursePage chosen="To Do"/>}
            />
            <Route
              path="student/:courseId/:assignmentId/:assignmentType"
              element={<StudentAssignmentPage/>}
            />
            <Route
              path="student/:courseId/:assignmentId/:assignmentType/:teamId"
              element={<StudentAssignmentPage/>}
            />
            <Route
              path="student/:courseId/:assignmentId/:teamId/submitted"
              element={<StudentSubmittedAssignmentPage/>}
            />
            <Route
              path="student/:courseId/grades"
              element={<StudentGradesPage/>}
            />

            <Route
              path="student"
              element={<App/>}
            />
            <Route
              path="/student/:courseId/teams"
              element={<StudentCoursePage chosen="My Team"/>}
            />
            <Route
              path="/student/:courseId/assignments"
              element={<StudentCoursePage chosen="To Do"/>}
            />
            <Route
                path="student/:courseId/peer-review"
                element={<PeerReviewPage />}
            />
            <Route
                path="student/:courseId/peer-review/:assignmentId/:teamId/pr-list"
                element={<PeerReviewListPage />}
            />
          </Route>
        </Route>
      </Route>
    </Routes>
  )
}

export default RouterHandler
