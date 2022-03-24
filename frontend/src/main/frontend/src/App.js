import TeacherDashboardPage from "./pages/TeacherPages/TeacherDashboardPage";
import LoginPage from "./pages/AuthPages/LoginPage";
import {useDispatch, useSelector} from "react-redux";
import "./global_styles/global.css";
import {useEffect} from "react";
import {authenticateUser} from "./redux/features/authSlice";
import StudentDashboardPage from "./pages/StudentPages/StudentDashboardPage";

function App() {
  const dispatch = useDispatch()
  const authentication = useSelector((state) => state);
  const isAuthenticated = authentication.auth.isAuthenticated;
  const role = authentication.auth.role;

  useEffect(() => {
    const token = localStorage.getItem("jwt_token")
    if (token != null) {
      dispatch(authenticateUser())
    }
  }, [])

  return (
    <div>
        {isAuthenticated ?
          <div>
            {role === "teacher" && <TeacherDashboardPage/>}
            {role === "student" && <StudentDashboardPage/>}
          </div>
        : <LoginPage />}
    </div>
  );
}

export default App;
