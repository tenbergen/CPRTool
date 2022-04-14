import ProfessorDashboardPage from "./pages/TeacherPages/ProfessorDashboardPage";
import { useDispatch, useSelector } from "react-redux";
import "./global_styles/global.css";
import React, { useEffect } from "react";
import {refreshTokenAsync, setUserInformation} from "./redux/features/authSlice";
import StudentDashboardPage from "./pages/StudentPages/StudentDashboardPage";

function App() {
  const dispatch = useDispatch()
  const { role } = useSelector((state) => state.auth);

  useEffect(() => {
    dispatch(refreshTokenAsync())
    dispatch(setUserInformation())
  }, [])

  return (
      <div>
        {role === "professor" && <ProfessorDashboardPage/>}
        {role === "student" && <StudentDashboardPage/>}
      </div>
  )
}

export default App;
