import { useState } from 'react';
import './styles/StudentGradesStyling.css';
import Breadcrumbs from '../../components/Breadcrumbs';
import HeaderBar from '../../components/HeaderBar/HeaderBar';
import NavigationContainerComponent from '../../components/NavigationComponents/NavigationContainerComponent';

function StudentGradesPage() {
    const [assignmentList, setAssignmentList] = useState([
        { assignment_id: '1', assignment_name: 'Assignment 1', grade: '95', pr_given: '75', pr_received: '60', status: 'Submitted', download: 'V' },
        { assignment_id: '1', assignment_name: 'Assignment 1', grade: '95', pr_given: '75', pr_received: '60', status: 'Graded', download: 'V' },
        { assignment_id: '1', assignment_name: 'Assignment 1', grade: '95', pr_given: '75', pr_received: '60', status: 'Graded', download: 'V' },
        { assignment_id: '2', assignment_name: 'Assignment 2', grade: '93', pr_given: '75', pr_received: '60', status: 'Past Due', download: 'V' },
        { assignment_id: '2', assignment_name: 'Assignment 2', grade: '95', pr_given: '75', pr_received: '60', status: 'Submitted', download: 'V' },
        { assignment_id: '2', assignment_name: 'Assignment 2', grade: '60', pr_given: '75', pr_received: '60', status: 'Past Due', download: 'V' },
        { assignment_id: '2', assignment_name: 'Assignment 2', grade: '100', pr_given: '75', pr_received: '60', status: 'Graded', download: 'V' },
        { assignment_id: '2', assignment_name: 'Assignment 2', grade: '100', pr_given: '75', pr_received: '60', status: 'Submitted', download: 'V' },
        { assignment_id: '3', assignment_name: 'Assignment 3', grade: '95', pr_given: '75', pr_received: '60', status: 'Submitted', download: 'V' },
        { assignment_id: '3', assignment_name: 'Assignment 3', grade: '95', pr_given: '75', pr_received: '60', status: 'Submitted', download: 'V' },
        { assignment_id: '3', assignment_name: 'Assignment 3', grade: '95', pr_given: '75', pr_received: '60', status: 'Submitted', download: 'V'},
        { assignment_id: '3', assignment_name: 'Assignment 3', grade: '95', pr_given: '75', pr_received: '60', status: 'Past Due', download: 'V' },
        { assignment_id: '4', assignment_name: 'Assignment 4', grade: '95', pr_given: '75', pr_received: '60', status: 'Graded', download: 'V'},
        { assignment_id: '4', assignment_name: 'Assignment 4', grade: '95', pr_given: '75', pr_received: '60', status: 'Graded', download: 'V'},
        { assignment_id: '4', assignment_name: 'Assignment 4', grade: '95', pr_given: '75', pr_received: '60', status: 'Submitted', download: 'V' },
        { assignment_id: '4', assignment_name: 'Assignment 4', grade: '92', pr_given: '75', pr_received: '60', status: 'Graded', download: 'V'},
        { assignment_id: '5', assignment_name: 'Assignment 5', grade: '95', pr_given: '75', pr_received: '60', status: 'Graded', download: 'V'},
        { assignment_id: '5', assignment_name: 'Assignment 5', grade: '95', pr_given: '75', pr_received: '60', status: 'Past Due', download: 'V'},
        { assignment_id: '5', assignment_name: 'Assignment 5', grade: '95', pr_given: '75', pr_received: '60', status: 'Past Due', download: 'V'},
        { assignment_id: '5', assignment_name: 'Assignment 5', grade: '95', pr_given: '75', pr_received: '60', status: 'Past Due', download: 'V'},
        { assignment_id: '5', assignment_name: 'Assignment 5', grade: '95', pr_given: '75', pr_received: '60', status: 'Past Due', download: 'V'},
        { assignment_id: '5', assignment_name: 'Assignment 5', grade: '95', pr_given: '75', pr_received: '60', status: 'Past Due', download: 'V'},
        { assignment_id: '5', assignment_name: 'Assignment 5', grade: '95', pr_given: '75', pr_received: '60', status: 'Past Due', download: 'V'},
        { assignment_id: '5', assignment_name: 'Assignment 5', grade: '95', pr_given: '75', pr_received: '60', status: 'Past Due', download: 'V'},
        { assignment_id: '5', assignment_name: 'Assignment 5', grade: '95', pr_given: '75', pr_received: '60', status: 'Past Due', download: 'V'},
        { assignment_id: '5', assignment_name: 'Assignment 5', grade: '95', pr_given: '75', pr_received: '60', status: 'Past Due', download: 'V'},
        { assignment_id: '5', assignment_name: 'Assignment 5', grade: '95', pr_given: '75', pr_received: '60', status: 'Past Due', download: 'V'},
        { assignment_id: '5', assignment_name: 'Assignment 5', grade: '95', pr_given: '75', pr_received: '60', status: 'Past Due', download: 'V'},
        { assignment_id: '5', assignment_name: 'Assignment 5', grade: '95', pr_given: '75', pr_received: '60', status: 'Past Due', download: 'V'},
        { assignment_id: '5', assignment_name: 'Assignment 5', grade: '95', pr_given: '75', pr_received: '60', status: 'Past Due', download: 'V'},
        { assignment_id: '5', assignment_name: 'Assignment 5', grade: '95', pr_given: '75', pr_received: '60', status: 'Past Due', download: 'V'},
        { assignment_id: '5', assignment_name: 'Assignment 5', grade: '95', pr_given: '75', pr_received: '60', status: 'Past Due', download: 'V'},
        { assignment_id: '6', assignment_name: 'Assignment 6', grade: '95', pr_given: '75', pr_received: '60', status: 'Past Due', download: 'V' },
    ]);

    return (
        <div className="page-container">
            <HeaderBar />
        <div className='student-container'>
            <NavigationContainerComponent />
            <div className='user-roles'>
                <div className='grades-overview-breadcrumbs'>
                    <Breadcrumbs></Breadcrumbs>
                </div>
                <h2>Grades Overview</h2>
                <div>
                    <div className='user-list'>
                        <div className='user-item header'>
                            <div>Number</div>
                            <div>Assigment</div>
                            <div>Grade</div>
                            <div>PR Given Avg.</div>
                            <div>PR Received Avg.</div>
                            <div>Status</div>
                            <div>Download</div>
                        </div>
                        <div className='all-user-items'>
                            {assignmentList.map((assignment, index) => (
                                <div key={index} className='user-item'>
                                    <div className='number-div'>{assignment.assignment_id}</div>
                                    <div className='name-div'>{assignment.assignment_name}</div>
                                    <div className='grade-div'>{assignment.grade}</div>
                                    <div className='prGiven-div'>{assignment.pr_given}</div>
                                    <div className='prReceived-div'>{assignment.pr_received}</div>
                                    <div className='status-div'> {assignment.status}</div>
                                    <div className='download-div'>{assignment.download}</div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </div>
        </div>
        </div>
    );
}

export default StudentGradesPage;
