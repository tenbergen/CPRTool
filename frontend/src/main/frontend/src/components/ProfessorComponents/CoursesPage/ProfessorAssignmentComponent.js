import React, {useEffect} from 'react';
import '../../styles/TeacherAss.css';
import {useDispatch, useSelector} from 'react-redux';
import {Link, useParams} from 'react-router-dom';
import {getCourseAssignmentsAsync} from '../../../redux/features/assignmentSlice';
import AssignmentTile from "../../AssignmentTile";

const ProfessorAssignmentComponent = () => {
    const dispatch = useDispatch();
    const {courseId} = useParams();
    const {courseAssignments} = useSelector((state) => state.assignments);

    useEffect(() => {
        dispatch(getCourseAssignmentsAsync(courseId));
    }, []);

    return (
        <div>
            <div className='TeacherAss'>
                <div id='teacherAssList'>
                    {courseAssignments.map(assignment => (
                        <AssignmentTile
                            assignment={assignment}
                        />
                    ))}
                </div>
                <div id='assAddClass'>
                    <Link to='create/assignment'>
                        <button
                            className='blue-button-filled outfit-20'
                            id='assAddButton'>
                            Create new assignment
                        </button>
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default ProfessorAssignmentComponent;
