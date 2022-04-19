import React, {useEffect, useState} from 'react';
import '../../styles/TeacherAss.css';
import {useDispatch, useSelector} from 'react-redux';
import {Link, useParams} from 'react-router-dom';
import {getCourseAssignmentsAsync} from '../../../redux/features/assignmentSlice';
import axios from 'axios';
import Loader from '../../LoaderComponenets/Loader';

const assignmentUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`;

const ProfessorAssignmentComponent = () => {
    const [isLoading, setIsLoading] = useState(false);
    const dispatch = useDispatch();
    const {courseId} = useParams();
    const {courseAssignments, currentAssignmentLoaded} = useSelector(
        (state) => state.assignments
    );

    useEffect(() => {
        setIsLoading(true);
        dispatch(getCourseAssignmentsAsync(courseId));
        setIsLoading(false);
    }, []);
    const confirmDelete = (assignment) => {
        let confirmAction = window.confirm(
            'Are you sure to delete this assignment?'
        );
        if (confirmAction) {
            deleteAssignment(assignment);
        }
    };

    console.log(courseAssignments);

    const deleteAssignment = async (assignment) => {
        setIsLoading(true);
        const url = `${assignmentUrl}/${courseId}/assignments/${assignment.assignment_id}/remove`;
        await axios.delete(url).then((res) => {
            console.log(res);
        });
        dispatch(getCourseAssignmentsAsync(courseId));
        if (currentAssignmentLoaded) alert('Assignment successfully deleted.');
        setIsLoading(false);
    };

    return (
        <div>
            {isLoading ? (
                <div className='loader-wrapper'>
                    <Loader/>
                </div>
            ) : (
                <div className={'TeacherAss'}>
                    <div id='ass'>
                        <div id='assList'>
                            {courseAssignments.map((assignment) => (
                                <div className='assListItem'>
                                    <Link
                                        to={`/details/professor/${courseId}/${assignment.assignment_id}/grade`}
                                    >
                                        <li>
                                            <div className='ass-title'>
                                                {assignment.assignment_name}
                                                <span1>Due Date: {assignment.due_date}</span1>
                                                <br></br>
                                            </div>
                                        </li>
                                    </Link>
                                    <div>
                                        <p>
                                            {/*{assignment}*/}
                                            <br></br>
                                            <div className='delete-btn-wrapper'>
                                                <div
                                                    className='delete-ass-btn'
                                                    onClick={() => confirmDelete(assignment)}
                                                >
                                                    Delete Assignment
                                                </div>
                                            </div>
                                        </p>
                                    </div>
                                </div>
                            ))}
                        </div>
                        <div id='assAddClass'>
                            <Link to='create/assignment'>
                                <button id='assAddButton'>Create new assignment</button>
                            </Link>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ProfessorAssignmentComponent;
