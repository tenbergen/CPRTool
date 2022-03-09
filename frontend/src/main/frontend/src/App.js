import TeacherDashboardPage from "./pages/TeacherDashboardPage";
import LoginPage from "./pages/LoginPage";
import {useDispatch, useSelector} from "react-redux";
import "./global_styles/global.css";
import {useEffect} from "react";
import {authenticateUser} from "./redux/features/authSlice";

function App() {
  const dispatch = useDispatch()
  const authentication = useSelector((state) => state);
  const isAuthenticated = authentication.auth.isAuthenticated;

  useEffect(() => {
    const token = localStorage.getItem("jwt_token")
    if (token != null) {
      dispatch(authenticateUser())
    }
  }, [])

  return (
    <div>
        {isAuthenticated? <TeacherDashboardPage/> : <LoginPage />}
    </div>
  );
}

export default App;
