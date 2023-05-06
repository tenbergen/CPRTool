import React, { useState, useEffect } from 'react';
import axios from 'axios';

const Table = () => {

    // const [userStatsList, setUserStatsList] = useState([
    //     { name: 'Danny Dimes', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
    //     { name: 'Saquads Barkley', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
    //     { name: 'Gardner Minshew', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
    //     { name: 'Caleb Williams', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
    //     { name: 'Patty Mahomes', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
    //     { name: 'Carl Wheezer', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
    //     { name: 'Perry P', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
    //     { name: 'Larry Lobster', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
    //     { name: 'Big Chungus', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
    //     { name: 'Dak Prescott', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
    //     { name: 'John Bones', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
    //     { name: 'Big Chungus', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
    //     { name: 'Dak Prescott', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
    //     { name: 'John Bones', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
    // ]);

    const [selectedStudent, setSelectedStudent] = useState("all");
    const courseId = window.location.pathname;
    const course = courseId.split("/")[2];
    const getAllStudentsUrl = `${process.env.REACT_APP_URL}/view/professor/courses/${course}/students`
    const getAllAssignmentsUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses/${course}/assignments`
    const getAllGradesUrl = `${process.env.REACT_APP_URL}/assignments/student/${course}/all-grades`;

    let [userStatsList, setUserStatsList] = useState([]);

    useEffect(() => {
        axios.get(getAllStudentsUrl)
            .then(response => {
                console.log(response.data);
                console.log(getAllStudentsUrl);
                setUserStatsList(response.data);
            })
            .catch(error => console.error(error.message));
    }, []);

    let [assignmentList, setAssignmentList] = useState([]);

    useEffect(() => {
        axios.get(getAllAssignmentsUrl)
            .then(response => {
                console.log(response.data);
                console.log(getAllAssignmentsUrl);
                setAssignmentList(response.data);
            })
            .catch(error => console.error(error.message));
    }, []);

    let [gradesList, setGradesList] = useState([]);

    useEffect(() => {
        axios.get(getAllGradesUrl)
            .then(response => {
                console.log(response.data);
                console.log(getAllGradesUrl);
                setGradesList(response.data);
            })
            .catch(error => console.error(error.message));
    }, []);

    const filteredUsersStats = userStatsList.filter((user) =>
        (selectedStudent === "all" || user.student_id === selectedStudent)
    );

    const uniqueStatsNames = userStatsList
        .filter((user, index, arr) => arr.findIndex(u => u.student_id === user.student_id) === index)
        .map(user => user.student_id);

    // function getGrade(course){
    //     for (let i = 0; i < course.team_submissions.length; i++){
    //         return(
    //             {course.team_submissions[i].grade}
    //         )
    //     }
    // }

    let numMean = 0;
    let index = 0;
    let [meanArray, setMeanArray] = useState([0]);

    function calculateMean(num){
        numMean += num;
        meanArray[index] += num;
        console.log(meanArray);
        index += 1;
        return num;
    }

    function getMean(){
        let realMean = numMean / index;
        numMean = 0;
        index = 0;
        return realMean;
    }

    return (
        <><div className='stats-dropdown-title'>
            <div className="stats-student-dropdown">
                <label for="role-filter">Student</label>
                <select name="student" id="role" onChange={(e) => setSelectedStudent(e.target.value)}>
                    <option value="all">All</option>
                    {uniqueStatsNames.map(item => {
                        return (<option key={item} value={item}>{item}</option>);
                    })}
                </select>
            </div>
            <div className='numeric-stats-title'>
                <label className='stats-title'>Numeric Stats for All Students</label>
            </div>
        </div>
            <div className='professor-user-table'>
                <div class="professor-div1">
                    <table>
                        <tr>
                            <th className='name-stats-header'>Name</th>
                            {assignmentList.map((assignment) => (
                                <th className='name-stats-header'>{assignment.assignment_name}</th>))}
                            <th className='mean-stats-header'>Mean</th>
                        </tr>
                        {
                            filteredUsersStats.map(course => (
                                <>
                                    <tr className='student-div'>
                                        <td>{course.student_id}</td>
                                        {course.team_submissions.slice(0, course.team_submissions.length / 2).map(grade => (
                                            <td>{calculateMean(grade.grade)}</td>
                                        ))}
                                        <td className='mean-column'>{getMean()}</td>
                                    </tr>
                                </>
                            ))
                        }
                        <tr>
                            <th className='footer-mean'>Mean</th>
                            {meanArray.map(mean => (
                                <th className='footer'>{mean / 2}</th>
                            ))}
                            {/* <td className='mean-column'>{(parseInt(course.grade1) + parseInt(course.grade2)) / 2} / 1.5</td> */}
                            <th className='footer-end'></th>
                        </tr>
                    </table>
                </div>
            </div></>
    )
}

export default Table