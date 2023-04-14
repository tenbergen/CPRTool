import { useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import SidebarComponent from "../../components/SidebarComponent";
import "./styles/ProfessorDashboardStyle.css";
import Loader from "../../components/LoaderComponenets/Loader";
import { useDispatch, useSelector } from "react-redux";
import {
  getCourseDetailsAsync,
  getCoursesAsync,
} from "../../redux/features/courseSlice";
import uuid from "react-uuid";
import HeaderBar from "../../components/HeaderBar/HeaderBar";
import LogoutButton from "../../components/GlobalComponents/LogoutButton";
import NavigationContainerComponent from "../../components/NavigationComponents/NavigationContainerComponent";

function ProfessorDashboardPage() {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { courses, coursesLoaded } = useSelector((state) => state.courses);
  const user = useSelector((state) => state.auth.user_given_name);

  useEffect(() => {
    dispatch(getCoursesAsync());
  }, [dispatch]);

  const studentView = () => {
    localStorage.setItem("alt_role", "student");
    window.location.reload(false);
  };

  const onCourseClick = (course) => {
    dispatch(getCourseDetailsAsync(course.course_id));
  };

  return (
      <div className="page-container">
        <HeaderBar/>
        <div className="pdp-container">
          <NavigationContainerComponent/>
          <div id="teacher">
            <div className="welcome-banner">
              <h1 className="inter-36-bold" id="welcome-message">
                Hello, {user}!
              </h1>
              <div className="views">
                <button
                    className="blue-button-large student-view"
                    onClick={studentView}
                >
                  Student View
                </button>
              </div>
            </div>
            <div id="proCourseList">
              {courses.map(
                  (course) =>
                      course && (
                          <Link
                              key={uuid()}
                              to={"/professor/" + course.course_id}
                              onClick={() => onCourseClick(course)}
                          >
                            <li className="courseListItem">
                        <span className="inter-20-extralight pdp-coursename">
                          {course.course_id}
                        </span>
                              <span className="inter-24-bold">{course.course_name}</span>
                            </li>
                          </Link>
                      )
              )}
            </div>
            <div className="create-course">
              <div id="addClass">
                <Link to="/create">
                  <button className="green-button-large">
                    Create new course
                  </button>
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
  );
}

export default ProfessorDashboardPage;
