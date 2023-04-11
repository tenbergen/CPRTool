import ProfessorDashboardPage from './pages/TeacherPages/ProfessorDashboardPage';
import { useDispatch, useSelector } from 'react-redux';
import './global_styles/fonts.css';
import React, { useEffect } from 'react';
import {
  refreshTokenAsync,
  setUserInformation,
} from './redux/features/authSlice';
import StudentDashboardPage from './pages/StudentPages/StudentDashboardPage';
import HeaderBar from "./components/HeaderBar/HeaderBar";
import NavigationContainerComponent from "./components/NavigationComponents/NavigationContainerComponent";

function App() {
  const dispatch = useDispatch();
  const { role } = useSelector((state) => state.auth);

  useEffect(() => {
    dispatch(refreshTokenAsync());
    dispatch(setUserInformation());
  }, [dispatch]);

  return (
    <div>
      {role === 'professor' && <ProfessorDashboardPage />}
      {role === 'student' && <StudentDashboardPage />}
    </div>
  );
}

export default App;
