import {useEffect, useState} from 'react';
import axios from 'axios';
import '../../styles/Roster.css';
import {useDispatch, useSelector} from 'react-redux';
import {getCourseDetailsAsync, getCurrentCourseStudentsAsync} from '../../../redux/features/courseSlice';
import {useParams} from 'react-router-dom';
import React from "react-dom";

const ProfessorRosterComponent = () => {
    const dispatch = useDispatch();
    const {courseId} = useParams();
    const url = `${process.env.REACT_APP_URL}/manage/professor/courses`;
    const {currentCourseStudents, currentCourseStudentsLoaded} = useSelector((state) => state.courses);

    useEffect(() => {
        dispatch(getCurrentCourseStudentsAsync(courseId))
    }, []);

    const [formData, setFormData] = useState({
        Name: '',
        Email: '',
    });

    const {Name, Email} = formData;
    const OnChange = (e) => setFormData({...formData, [e.target.name]: e.target.value});

    const handleSubmit = async (e) => {
        const nameArray = Name.split(' ');
        const first = nameArray[0];
        const last = nameArray[1];
        if (Name === '' || Email === '') {
            alert('Please enter both name and email for the student!');
        } else if (nameArray.length < 2) {
            alert('Please enter first and last name!');
        } else {
            e.preventDefault();
            const firstLastEmail = first + '-' + last + '-' + Email;
            const addStudentUrl = `${url}/${courseId}/students/${firstLastEmail}/add`;
            await axios
                .post(addStudentUrl)
                .then((res) => {
                    console.log(res.data);
                    alert('Successfully added student.');
                    dispatch(getCourseDetailsAsync(courseId));
                })
                .catch((e) => {
                    console.log(e);
                    alert('Error adding student.');
                });
            setFalse();
            setFormData({...formData, Name: '', Email: ''});
        }
    };

    const deleteStudent = async (Email) => {
        const deleteStudentUrl = `${url}/${courseId}/students/${Email.student_id}/delete`;
        await axios.delete(deleteStudentUrl)
            .then((res) => {
                console.log(res);
                alert('Successfully deleted student.');
            })
            .catch((e) => {
                console.log(e);
                alert('Error deleting student.');
            });
        dispatch(getCourseDetailsAsync(courseId));
    };

    const addsStudent = () => {
        return (
            <div id='addStudentDiv'>
                <label>Name:</label>
                <input
                    type='text'
                    className='rosterInput'
                    name='Name'
                    value={Name}
                    required
                    onChange={(e) => OnChange(e)}
                />
                <label>Email:</label>
                <input
                    type='text'
                    className='rosterInput'
                    name='Email'
                    value={Email}
                    required
                    onChange={(e) => OnChange(e)}
                />
                <button id='addStudentButton' onClick={handleSubmit}>
                    Add Student
                </button>
            </div>
        );
    };

    const [show, setShow] = useState(false);
    const setTrue = () => setShow(true);
    const setFalse = () => setShow(false);

    return (
        <div className='RosterPage'>
            <div id='roster'>
                {!currentCourseStudentsLoaded ? (
                    <h1> Loading </h1>
                ) : (
                    <table className='rosterTable'>
                        <tr>
                            <th className='rosterHeader'>Name</th>
                            <th className='rosterHeader'>Email</th>
                            <th className='rosterHeader'>Team</th>
                            <th className='rosterHeader'></th>
                        </tr>
                        {currentCourseStudents.map(student => (
                        <tr>
                            <th className='rosterComp'>
                                {student.first_name ? student.first_name + ' ' + student.last_name : ''}
                            </th>
                            <th className='rosterComp'>{student.student_id}</th>
                            <th className='rosterComp'>
                                {student.team !== null ? student.team : ''}
                            </th>
                            <th className='rosterComp'>
                                <div className='crossMark-wrapper'>
                                    <div onClick={() => deleteStudent(student)} className='crossMark'>
                                        X
                                    </div>
                                </div>
                            </th>
                        </tr>
                        ))}
                    </table>
                )}
            </div>
            {show ? (
                addsStudent()
            ) : (
                <button className='button_plus' onClick={setTrue}>
                    <img
                        className='button_plus'
                        src={require('../../styles/plus-purple.png')}
                        alt='plus_button'
                    />
                </button>
            )}
        </div>
    );
};

export default ProfessorRosterComponent;
