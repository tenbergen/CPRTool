import React from 'react-dom';
import {useEffect, useState} from 'react';
import './styles/ProfessorCourseStyle.css';
import SidebarComponent from '../../components/SidebarComponent';
import ProfessorRosterComponent from '../../components/ProfessorComponents/CoursesPage/ProfessorRosterComponent';
import {useParams} from 'react-router-dom';
import ProfessorEditCourseComponent from '../../components/ProfessorComponents/CoursesPage/ProfessorEditCourseComponent';
import ProfessorAssignmentComponent from '../../components/ProfessorComponents/CoursesPage/ProfessorAssignmentComponent';
import {useDispatch} from 'react-redux';
import {getCourseDetailsAsync} from '../../redux/features/courseSlice';
import CourseBarComponent from '../../components/CourseBarComponent';
import ProfessorGradebookComponent from '../../components/ProfessorComponents/CoursesPage/ProfessorGradebookComponent';
import ProfessorTeamComponent from '../../components/ProfessorComponents/CoursesPage/ProfessorTeamComponent';
import Loader from '../../components/LoaderComponenets/Loader';

const CourseComponent = ({active, component, onClick}) => {
    return (
        <p
            onClick={onClick}
            className={active ? 'pcp-component-link-clicked' : 'pcp-component-link'}
        >
            {component}
        </p>
    );
};

function ProfessorCoursePage() {
    const [isLoading, setIsLoading] = useState(false);
    let dispatch = useDispatch();
    let {courseId} = useParams();

    const components = ['Assignments', 'Gradebook', 'Roster', 'Teams', 'Manage'];
    const [chosen, setChosen] = useState('Assignments');

    useEffect(() => {
        setIsLoading(true);
        dispatch(getCourseDetailsAsync(courseId));
        setTimeout(() => setIsLoading(false), 200);
    }, []);

    return (
        <div>
            {isLoading ? (
                <Loader/>
            ) : (
                <div className='pcp-parent'>
                    <SidebarComponent/>
                    <div className='pcp-container'>
                        <CourseBarComponent/>
                        <div className='pcp-components'>
                            <div className='pcp-component-links'>
                                {components.map((t) => (
                                    <CourseComponent
                                        key={t}
                                        component={t}
                                        active={t === chosen}
                                        onClick={() => setChosen(t)}
                                    />
                                ))}
                            </div>
                            <div>
                                {chosen === 'Assignments' && <ProfessorAssignmentComponent/>}
                                {chosen === 'Gradebook' && <ProfessorGradebookComponent/>}
                                {chosen === 'Roster' && <ProfessorRosterComponent/>}
                                {chosen === 'Teams' && <ProfessorTeamComponent/>}
                                {chosen === 'Manage' && <ProfessorEditCourseComponent/>}
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default ProfessorCoursePage;
