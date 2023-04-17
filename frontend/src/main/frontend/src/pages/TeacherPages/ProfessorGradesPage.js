import { useState } from 'react';
import './styles/ProfessorGradesStyling.css';
import editIcon from '../AdminPages/edit.png';
import { setCurrentCourse } from '../../redux/features/courseSlice';
import { current } from '@reduxjs/toolkit';
import { Line } from 'react-chartjs-2';
import Table from './Table';
import { LinearScale, Chart } from "chart.js";
import { CategoryScale } from "chart.js";
import { LineElement, PointElement } from "chart.js";
import NavigationContainerComponent from "../../components/NavigationComponents/NavigationContainerComponent";
import HeaderBar from "../../components/HeaderBar/HeaderBar";

Chart.register(LineElement);
Chart.register(PointElement);
Chart.register(CategoryScale);
Chart.register(LinearScale);

function ProfessorGradesPage() {
    const [userList, setUserList] = useState([
        { assignment: 'Assignment 1', name: 'Danny Dimes', team: 'New York Giants', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
        { assignment: 'Assignment 1', name: 'Saquads Barkley', team: 'New York Giants', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
        { assignment: 'Assignment 1', name: 'Gardner Minshew', team: 'Indianapolis Colts', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
        { assignment: 'Assignment 2', name: 'Danny Dimes', team: 'New York Giants', grade: '93', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
        { assignment: 'Assignment 2', name: 'Saquads Barkley', team: 'New York Giants', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
        { assignment: 'Assignment 2', name: 'Gardner Minshew', team: 'Indianapolis Colts', grade: '6', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
        { assignment: 'Assignment 2', name: 'Caleb Williams', team: 'Indianapolis Colts', grade: '100', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
        { assignment: 'Assignment 2', name: 'Patty Mahomes', team: 'Kansas City Chiefs', grade: '100', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
        { assignment: 'Assignment 3', name: 'Carl Wheezer', team: 'New York Jets', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
        { assignment: 'Assignment 3', name: 'Perry P', team: 'New York Jets', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
        { assignment: 'Assignment 3', name: 'Larry Lobster', team: 'Baltimore Ravens', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
        { assignment: 'Assignment 3', name: 'Danny Dimes', team: 'New York Giants', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
        { assignment: 'Assignment 4', name: 'Jimmy Neut', team: 'Tennessee Titans', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
        { assignment: 'Assignment 4', name: 'Big Chungus', team: 'Tennessee Titans', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
        { assignment: 'Assignment 4', name: 'Dak Prescott', team: 'Syracuse BenchWarmers', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
        { assignment: 'Assignment 4', name: 'Danny Dimes', team: 'New York Giants', grade: '92', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
        { assignment: 'Assignment 5', name: 'John Bones', team: 'New York Giants', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
        { assignment: 'Assignment 5', name: 'Danny Dimes', team: 'New York Giants', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
        { assignment: 'Assignment 6', name: 'Danny Dimes', team: 'New York Giants', grade: '95', prGiven: '75', prReceived: '60', submitted: '3/25/23 | 8:19am' },
    ]);

    const [userStatsList, setUserStatsList] = useState([
        { name: 'Danny Dimes', grade1: '30', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
        { name: 'Saquads Barkley', grade1: '91', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
        { name: 'Gardner Minshew', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
        { name: 'Caleb Williams', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
        { name: 'Patty Mahomes', grade1: '20', grade2: '93', grade3: '40', grade4: '92', grade5: '95', grade6: '100' },
        { name: 'Carl Wheezer', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
        { name: 'Perry P', grade1: '95', grade2: '93', grade3: '100', grade4: '92', grade5: '95', grade6: '100' },
        { name: 'Larry Lobster', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
        { name: 'Big Chungus', grade1: '95', grade2: '93', grade3: '100', grade4: '92', grade5: '95', grade6: '100' },
        { name: 'Dak Prescott', grade1: '13', grade2: '93', grade3: '95', grade4: '92', grade5: '13', grade6: '100' },
        { name: 'John Bones', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
    ]);

    const [searchTerm, setSearchTerm] = useState('');
    const [selectedAssignment, setSelectedAssignment] = useState("all");
    const [selectedStudent, setSelectedStudent] = useState("all");
    const [selectedVisualStudent, setSelectedVisualStudent] = useState("all");
    const [selectedTeam, setSelectedTeam] = useState("all");

    const filteredUsers = userList.filter((user) =>
        (selectedAssignment === "all" || user.assignment === selectedAssignment) &&
        (selectedStudent === "all" || user.name === selectedStudent) &&
        (selectedTeam === "all" || user.team === selectedTeam)
    );

    const filteredUsersStats = userStatsList.filter((user) =>
        (selectedStudent === "All" || user.name === selectedStudent)
    );

    const filteredUsersVisualStats = userStatsList.filter((user) =>
        (selectedVisualStudent === "Select Student" || user.name === selectedVisualStudent)
    );

    const [page, setPage] = useState("roles")

    const changePage = (page) => {
        setPage(page)
    }

    const uniqueAssignments = userList
        .filter((user, index, arr) => arr.findIndex(u => u.assignment === user.assignment) === index)
        .map(user => user.assignment);

    const uniqueNames = userList
        .filter((user, index, arr) => arr.findIndex(u => u.name === user.name) === index)
        .map(user => user.name);

    const uniqueStatsNames = userStatsList
        .filter((user, index, arr) => arr.findIndex(u => u.name === user.name) === index)
        .map(user => user.name);

    const uniqueTeams = userList
        .filter((user, index, arr) => arr.findIndex(u => u.team === user.team) === index)
        .map(user => user.team);


    const getChartData = () => {
        const data = {
            labels: [], // Labels for the x-axis
            datasets: [
                {
                    label: 'Grades', // Label for the dataset
                    backgroundColor: 'green',
                    borderColor: 'green',
                    borderWidth: 1,
                    data: [], // Array to store the data points
                    pointStyle: [], // Array to store the point styles
                    pointRadius: 5, // Radius of the points
                },
                {
                    label: 'Trend', // Label for the trend line
                    fill: false,
                    borderColor: 'blue', // Color of the trend line
                    borderWidth: 1,
                    data: [], // Array to store the trend line points
                    type: 'line', // Type of chart for the trend line
                    pointRadius: 0, // No points for the trend line
                },
                {
                    label: 'Grades2', // Label for the dataset
                    backgroundColor: 'purple',
                    borderColor: 'purple',
                    borderWidth: 1,
                    data: [], // Array to store the data points
                    pointStyle: [], // Array to store the point styles
                    pointRadius: 5, // Radius of the points
                },
                {
                    label: 'Trend2', // Label for the trend line
                    fill: false,
                    borderColor: 'orange', // Color of the trend line
                    borderWidth: 1,
                    data: [], // Array to store the trend line points
                    type: 'line', // Type of chart for the trend line
                    pointRadius: 0, // No points for the trend line
                },
            ],
        };

        uniqueAssignments.map(item => {
            data.labels.push(item);

            // Calculate trend line dynamically based on mean grades
            let meanSum = 0;
            let count = 0;

            filteredUsersVisualStats.map(user => {
                const grades = [
                    parseInt(user.grade1),
                    parseInt(user.grade2),
                    parseInt(user.grade3),
                    parseInt(user.grade4),
                    parseInt(user.grade5),
                    parseInt(user.grade6),
                ];
                const mean = grades.reduce((a, b) => a + b, 0) / grades.length;
                console.log(grades)
                meanSum += mean;
                count++;
                data.datasets[0].data.push(...grades);
                data.datasets[0].pointStyle.push('circle'); // Use 'circle' as the point style for individual grades
                data.datasets[1].data.push(meanSum / count);
            });
        });
        
        const grades2 = [[], [], [], [], [], []]; // initialize an array to store the grades for each assignment

        userStatsList.forEach((user) => {
            grades2[0].push(parseInt(user.grade1));
            grades2[1].push(parseInt(user.grade2));
            grades2[2].push(parseInt(user.grade3));
            grades2[3].push(parseInt(user.grade4));
            grades2[4].push(parseInt(user.grade5));
            grades2[5].push(parseInt(user.grade6));
        });

        // compute the mean of each assignment grade and push it to the data array
        for (let i = 0; i < grades2.length; i++) {
            const mean2 = grades2[i].reduce((a, b) => a + b, 0) / grades2[i].length;
            data.datasets[2].data.push(...grades2[i]);
            data.datasets[2].pointStyle.push('circle');
            data.datasets[3].data.push(mean2);
        }

        // compute the mean of all grades and push it to the data array
        const allGrades = grades2.flat(); // get all grades in a flat array
        const meanSum2 = allGrades.reduce((a, b) => a + b, 0) / allGrades.length;
        data.datasets[3].data.push(meanSum2);

        return data;
    };

    return (
        <div className="page-container">
            <HeaderBar/>
            <div className='admin-container'>
                <NavigationContainerComponent/>
                <div className='user-roles'>
                    <h2>Grades Overview for {}</h2>
                    <div className='admin-tabs'>
                        <button className='user-roles-tab' onClick={() => changePage('roles')} style={{
                            backgroundColor: page === "roles" ? "#4a7dfc" : "#E6E6E6",
                            color: page === "roles" ? "white" : "black"
                        }}>Grades
                        </button>
                        <button className='courses-tab' onClick={() => changePage('courses')} style={{
                            backgroundColor: page === "courses" ? "#4a7dfc" : "#E6E6E6",
                            color: page === "courses" ? "white" : "black"
                        }}>Numeric Stats
                        </button>
                        <button className='profanity-settings-tab' onClick={() => changePage('profanity')} style={{
                            backgroundColor: page === "profanity" ? "#4a7dfc" : "#E6E6E6",
                            color: page === "profanity" ? "white" : "black"
                        }}>Visual Stats
                        </button>
                    </div>
                    {page === "roles" && (
                        <>
                            <div className='search-filter-add'>
                                <div className='search-bar'>
                                    <label>Assignment</label>
                                    <div className='assignment-dropdown'>
                                        <select name="assignment" id="role"
                                                onChange={(e) => setSelectedAssignment(e.target.value)}>
                                            <option value="all">All</option>
                                            {uniqueAssignments.map(item => {
                                                return (<option key={item} value={item}>{item}</option>);
                                            })}
                                        </select>
                                    </div>
                                </div>
                                <div className='dropdowns-div'>
                                    <div className="student-dropdown">
                                        <label htmlFor="role-filter">Student</label>
                                        <select name="student" id="role"
                                                onChange={(e) => setSelectedStudent(e.target.value)}>
                                            <option value="all">All</option>
                                            {uniqueNames.map(item => {
                                                return (<option key={item} value={item}>{item}</option>);
                                            })}
                                        </select>
                                    </div>
                                    <div className="team-dropdown">
                                        <label htmlFor="role-filter">Team</label>
                                        <select name="team" id="role" onChange={(e) => setSelectedTeam(e.target.value)}>
                                            <option value="all">All</option>
                                            {uniqueTeams.map(item => {
                                                return (<option key={item} value={item}>{item}</option>);
                                            })}
                                        </select>
                                    </div>
                                </div>
                                <div className='csv-download-button-div'>
                                    <button className='csv-download-button'>CSV Download</button>
                                </div>
                            </div>
                            <div>
                                <div className='user-list'>
                                    <div className='user-item header'>
                                        <div>Assignment</div>
                                        <div>Name</div>
                                        <div>Team</div>
                                        <div>Grade</div>
                                        <div>PR Given</div>
                                        <div>PR Received</div>
                                        <div>Submitted</div>
                                        <div>Actions</div>
                                    </div>
                                    <div className='all-user-items'>
                                        {filteredUsers.map((user) => (
                                            <div key={user.id} className='user-item'>
                                                <div>{user.assignment}</div>
                                                <div className='name-div'>{user.name}</div>
                                                <div className='team-div'>{user.team}</div>
                                                <div className='grade-div'>{user.grade}</div>
                                                <div>{user.prGiven}</div>
                                                <div>{user.prReceived}</div>
                                                <div className='submit-div'>{user.submitted}</div>
                                                <div>
                                                    <div className='edit-container'>
                                                        <button className='edit-button'><img className='edit-icon'
                                                                                             src={editIcon}/></button>
                                                    </div>
                                                    <div className='delete-container'>
                                                        <button className='delete-button'>X</button>
                                                    </div>
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            </div>
                        </>
                    )}
                    {page === "courses" && (
                        <Table></Table>
                    )}
                    {page === "profanity" && (
                        <div>
                            <div className="stats-student-dropdown">
                                <label htmlFor="role-filter">Student</label>
                                <select name="student" id="role" defaultValue="Select Student"
                                        onChange={(e) => setSelectedVisualStudent(e.target.value)}>
                                    <option disabled={true} value="Select Student">--Select Student--</option>
                                    {uniqueStatsNames.map(item => {
                                        return (<option key={item} value={item}>{item}</option>);
                                    })}
                                </select>
                            </div>
                            <div style={{height: '350px', width: '800px'}}>
                                <Line className='line-chart' data={getChartData()}/>
                            </div>
                            <div className='key-container'>
                                <label className='key'>Key</label>
                                <div className="box">
                                    <div className="row">
                                        <div className="column">
                                            X-axis : Assignments
                                        </div>
                                        <div className="column">
                                            <span className="square blue"></span>
                                            Students total grade
                                        </div>
                                        <div className="column">
                                            <span className="square green"></span>
                                            Students assignment grades
                                        </div>
                                    </div>
                                    <div className="row">
                                        <div className="column">
                                            Y-axis : Grades
                                        </div>
                                        <div className="column">
                                            <span className="square orange"></span>
                                            Mean of the class total grade
                                        </div>
                                        <div className="column">
                                            <span className="square purple"></span>
                                            Mean of the class assignment grades
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default ProfessorGradesPage;
