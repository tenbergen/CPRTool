import ProfessorDashboardPage from "./pages/TeacherPages/ProfessorDashboardPage";
import LoginPage from "./pages/AuthPages/LoginPage";
import { useDispatch, useSelector } from "react-redux";
import "./global_styles/global.css";
import React, { useEffect } from "react";
import { setUserInformation } from "./redux/features/authSlice";
import StudentDashboardPage from "./pages/StudentPages/StudentDashboardPage";

function App() {
  const dispatch = useDispatch()
  const authentication = useSelector((state) => state);
  const isAuthenticated = authentication.auth.isAuthenticated;
  const role = authentication.auth.role;

  useEffect(() => {
    dispatch(setUserInformation())
  }, [])

  return (
    <div>
        {isAuthenticated ?
          <div>
            {role === "professor" && <ProfessorDashboardPage/>}
            {role === "student" && <StudentDashboardPage/>}
          </div>
        : <LoginPage/>}
    </div>
  );
}

export default App;
