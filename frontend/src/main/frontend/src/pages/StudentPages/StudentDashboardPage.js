import React, {useEffect, useState} from 'react';
import {Link} from 'react-router-dom';
import SidebarComponent from '../../components/SidebarComponent';
import './styles/StudentDashboardStyle.css';
import {useDispatch, useSelector} from 'react-redux';
import Loader from '../../components/LoaderComponenets/Loader';
import {getCourseDetailsAsync, getStudentCoursesAsync,} from '../../redux/features/courseSlice';

function StudentDashboardPage() {
    const [isLoading, setIsLoading] = useState(false);
    const dispatch = useDispatch();
    const courses = useSelector((state) => state.courses.courses);
    const {user_given_name, lakerId} = useSelector((state) => state.auth);
    const alt_role = localStorage.getItem('alt_role');

    useEffect(() => {
        setIsLoading(true);
        dispatch(getStudentCoursesAsync(lakerId));
        setTimeout(() => setIsLoading(false), 750);
    }, []);

    if (!courses) {
        return (
            <div>
                <Loader/>
            </div>
        );
    }

    const courseClickHandler = (course) => {
        dispatch(getCourseDetailsAsync(course.course_id));
    };

    const changeView = () => {
        localStorage.removeItem('alt_role');
        window.location.reload(false);
    };

    return (
        <div>
            {isLoading ? (
                <Loader/>
            ) : (
                <div className={'StudentDashboard'}>
                    <SidebarComponent/>
                    <div id='student'>
                        <div className='welcome-banner-st'>
                            <div className='student-welcome'> Hello, {user_given_name}!</div>
                            {alt_role && alt_role === 'student' ? (
                                <div className='btn-wrapper'>
                                    <button className='prof-view-btn' onClick={changeView}>
                                        {' '}
                                        Back to Professor View
                                    </button>
                                </div>
                            ) : null}
                        </div>

                        <div id='courseList'>
                            {courses.map((course) => (
                                <Link
                                    to={'/details/student/' + course.course_id}
                                    onClick={() => courseClickHandler(course)}
                                    state={{from: course}}
                                >
                                    <li className='courseListItem'>
                                        {course.course_id + '\n\n' + course.course_name}
                                    </li>
                                </Link>
                            ))}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default StudentDashboardPage;
