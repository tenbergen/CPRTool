import React, { useState } from 'react';

const Table = () => {

    const [userStatsList, setUserStatsList] = useState([
        { name: 'Danny Dimes', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
        { name: 'Saquads Barkley', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
        { name: 'Gardner Minshew', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
        { name: 'Caleb Williams', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
        { name: 'Patty Mahomes', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
        { name: 'Carl Wheezer', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
        { name: 'Perry P', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
        { name: 'Larry Lobster', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
        { name: 'Big Chungus', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
        { name: 'Dak Prescott', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
        { name: 'John Bones', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
        { name: 'Big Chungus', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
        { name: 'Dak Prescott', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
        { name: 'John Bones', grade1: '95', grade2: '93', grade3: '95', grade4: '92', grade5: '95', grade6: '100' },
    ]);

    const [selectedStudent, setSelectedStudent] = useState("all");

    const filteredUsersStats = userStatsList.filter((user) =>
        (selectedStudent === "all" || user.name === selectedStudent)
    );

    const uniqueStatsNames = userStatsList
        .filter((user, index, arr) => arr.findIndex(u => u.name === user.name) === index)
        .map(user => user.name);

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
            <div className='user-table'>
                <div class="div1">
                    <table>
                        <tr>
                            <th className='name-stats-header'>Name</th>
                            <th className='stats-header'>Assignment 1</th>
                            <th className='stats-header'>Assignment 2</th>
                            <th className='stats-header'>Assignment 3</th>
                            <th className='stats-header'>Assignment 4</th>
                            <th className='stats-header'>Assignment 5</th>
                            <th className='mean-stats-header'>Mean / SD</th>
                        </tr>
                        {
                            filteredUsersStats.map(course => (
                                <>
                                    <tr className='student-div'>
                                        <td>{course.name}</td>
                                        <td>{course.grade1}</td>
                                        <td>{course.grade2}</td>
                                        <td>{course.grade3}</td>
                                        <td>{course.grade4}</td>
                                        <td>{course.grade5}</td>
                                        <td className='mean-column'>{(parseInt(course.grade1) + parseInt(course.grade2)) / 2} / 1.5</td>
                                    </tr>
                                </>
                            ))
                        }
                        <tr>
                            <th className='footer-mean'>Mean / SD</th>
                            <th className='footer'>90 / 1.0</th>
                            <th className='footer'>91 / 1.0</th>
                            <th className='footer'>93 / 1.0</th>
                            <th className='footer'>94 / 1.0</th>
                            <th className='footer'>95 / 1.0</th>
                            <th className='footer-end'></th>
                        </tr>
                    </table>
                </div>
            </div></>
    )
}

export default Table