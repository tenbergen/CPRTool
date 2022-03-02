import TeacherDashboardPage from './pages/TeacherDashboardPage';
import LoginPage from './pages/LoginPage';
import { useSelector } from 'react-redux';

function App() {
  const authentication = useSelector((state) => state);
  const isAuthenticated = authentication.auth.isAuthenticated;

  return (
    // <div>
    //     {isAuthenticated? <TeacherDashboardPage/> : <LoginPage />}
    // </div>

    <div>
      {/token/.test(window.location.href) ? (
        <TeacherDashboardPage />
      ) : (
        <LoginPage />
      )}
    </div>
  );
}

export default App;
