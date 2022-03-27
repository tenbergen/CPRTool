import React from "react-dom";
import {useEffect,useState} from "react";
import "./styles/StudentCourseStyle.css";
import SidebarComponent from "../../components/SidebarComponent";
import ToDoComponent from "../../components/ToDoComponent";
import CourseBarComponent from "../../components/CourseBarComponent";
import SubmittedComponent from "../../components/SubmittedComponent";
import {Link, useLocation, useNavigate, useParams} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {getCourseDetailsAsync} from "../../redux/features/courseSlice";


function StudentCoursePage() {
    let dispatch = useDispatch()
    let { courseId } = useParams();
    const isDataLoaded = useSelector((state) => state.courses.currentCourseLoaded)

    useEffect( () => {
        console.log("Hello")
        dispatch(getCourseDetailsAsync(courseId))
    }, [])

    const [showTodo, setShowTodo] = useState(true);
    const handleShowTodo = () => {
        setShowTodo(true);
        setShowSubmitted(false);
    }

    const [showSubmitted, setShowSubmitted] = useState(false);
    const handleShowSubmitted = () => {
        setShowTodo(false);
        setShowSubmitted(true);
    }

    return (
        <div>
            { isDataLoaded ?
            <div className={"scs-parent"}>
                <SidebarComponent/>
                <div className="scs-container">
                    <h1>Assignments</h1>
                    <a onClick={handleShowTodo} className="assSubTodo" target="_blank">To Do</a>
                    <a onClick={handleShowSubmitted} className="assSubTodo" target="_blank">Submitted</a>
                    <div>
                        {showTodo ? <ToDoComponent/>:null}
                        {showSubmitted ? <SubmittedComponent/>:null}
                    </div>
                </div>
            </div> : null }
        </div>
    );
}

export default StudentCoursePage;