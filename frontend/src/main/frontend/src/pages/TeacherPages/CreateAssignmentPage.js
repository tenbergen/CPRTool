import React, {useState} from 'react';
import './styles/CreateAssignmentStyle.css';
import SidebarComponent from '../../components/SidebarComponent';
import {useNavigate, useParams} from 'react-router-dom';
import axios from 'axios';
import {Field, Form} from 'react-final-form';
import CourseBarComponent from "../../components/CourseBarComponent";
import Loader from "../../components/LoaderComponenets/Loader";

const profAssignmentUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`;

const CreateAssignmentPage = () => {
    let navigate = useNavigate();
    let {courseId} = useParams();

    const submitCourseUrl = `${profAssignmentUrl}/create-assignment`;
    const getAssUrl = `${profAssignmentUrl}/${courseId}/assignments`;
    const [loading, setLoading] = useState(false)

    const [assignmentFocus, setAssignmentFocus] = useState(true)
    const [peerReviewFocus, setPeerReviewFocus] = useState(false)

    const assignmentFileFormData = new FormData();
    const rubricFileFormData = new FormData();
    const templateFileFormData = new FormData();

    const fileChangeHandler = (event, fileType) => {
        let file = event.target.files[0];
        if (fileType === "assignment") {
            assignmentFileFormData.set('file', file);
        } else if (fileType === "rubric"){
            rubricFileFormData.set('file', file);
        } else {
            templateFileFormData.set('file', file);
        }
    };

    const uploadFiles = async (assignmentId) => {
        console.log(assignmentFileFormData);
        console.log(assignmentFileFormData.get('file'));

        const assignmentFileUrl = `${getAssUrl}/${assignmentId}/upload`;
        const rubricUrl = `${getAssUrl}/${assignmentId}/peer-review/rubric/upload`;
        const templateUrl = `${getAssUrl}/${assignmentId}/peer-review/template/upload`;

        await axios.post(assignmentFileUrl, assignmentFileFormData)
            .then((res) => {
                console.log(res);
            })
            .catch((e) => {
                console.log(e);
                alert('Error uploading assignment file.');
            });

        await axios.post(rubricUrl, rubricFileFormData)
            .then((res) => {
                console.log(res);
            })
            .catch((e) => {
                console.log(e);
                alert('Error uploading peer review rubric.');
            });

        await axios.post(templateUrl, templateFileFormData)
            .then((res) => {
                console.log(res);
            })
            .catch((e) => {
                console.log(e);
                alert('Error uploading peer review template.');
            });
    };

    const handleSubmit = async (data) => {
        let {points} = data;
        points = parseInt(points);
        const course_id = courseId;

        setLoading(true)

        const sentData = {...data, points, course_id};

        await axios.post(submitCourseUrl, sentData)
            .then((res) => {
                console.log(res);
            })
            .catch(e => {
                console.log(e.response.data)
            })

        const assignment_id = await axios.get(getAssUrl)
            .then((res) => {
                console.log(res);
                return res.data.pop().assignment_id;
            })
            .catch(e => {
                console.log(e.response.data)
            })

        await uploadFiles(assignment_id);

        setLoading(false)
        navigate('/details/professor/' + courseId);
    };

    const toggleAssignmentFocus = () => {
        setPeerReviewFocus(false)
        setAssignmentFocus(true)
    }

    const togglePeerReviewFocus = () => {
        setAssignmentFocus(false)
        setPeerReviewFocus(true)
    }

    return (
        <div>
            { loading ? <Loader /> :
                <Form
                    onSubmit={async (formObj) => {
                        await handleSubmit(formObj);
                    }}>
                    {({handleSubmit}) => (
                        <div className='pcp-parent'>
                            <SidebarComponent/>
                            <div className='ccp-container'>
                                <CourseBarComponent title={"Courses"} />
                                <div className='pcp-components'>
                                    <h2 className="kumba-30"> Add new assignment </h2>
                                    <div className='cap-form'>
                                        <form onSubmit={handleSubmit}>
                                            {/*assignment field*/}
                                            <div className={assignmentFocus ? "field-container" : ""}>
                                                <div className="field-title"> {assignmentFocus && <span> Homework</span>} </div>
                                                <div className="field-content">
                                                    <div className='input-field cap-input-field'>
                                                        <label> Name of assignment: </label>
                                                        <Field name='assignment_name'>
                                                            {({input}) => (
                                                                <input
                                                                    type='text'
                                                                    name='assignment_name'
                                                                    {...input}
                                                                    required
                                                                    onFocus={toggleAssignmentFocus}
                                                                />
                                                            )}
                                                        </Field>
                                                    </div>

                                                    <div className="input-field cap-instructions">
                                                        <label> Instructions: </label>

                                                        <Field name='instructions'>
                                                            {({input}) => (
                                                                <textarea
                                                                    name='instructions'
                                                                    {...input}
                                                                    required
                                                                    onFocus={toggleAssignmentFocus}
                                                                />
                                                            )}
                                                        </Field>
                                                    </div>

                                                    <div className='cap-assignment-files'>
                                                        <label className="outfit-25">Files:</label>
                                                        <input
                                                            type='file'
                                                            name='assignment_files'
                                                            accept='.pdf,.zip,.docx'
                                                            required
                                                            onChange={(e) => fileChangeHandler(e, "assignment")}
                                                            onFocus={toggleAssignmentFocus}
                                                        />
                                                    </div>

                                                    <div className='input-field cap-assignment-info'>
                                                        <label> Due Date: </label>
                                                        <Field name='due_date'>
                                                            {({input}) => (
                                                                <input
                                                                    type='date'
                                                                    name='due_date'
                                                                    {...input}
                                                                    required
                                                                    min={new Date().toISOString().split('T')[0]}
                                                                    onFocus={toggleAssignmentFocus}
                                                                />
                                                            )}
                                                        </Field>

                                                        <label> Points: </label>
                                                        <Field name='points'>
                                                            {({input}) => (
                                                                <input
                                                                    type='number'
                                                                    name='points'
                                                                    {...input}
                                                                    required
                                                                    onWheel={(e) => e.target.blur()}
                                                                    onFocus={toggleAssignmentFocus}
                                                                />
                                                            )}
                                                        </Field>
                                                    </div>
                                                </div>
                                            </div>

                                            {/*peer review fields*/}
                                            <div className={peerReviewFocus ? "field-container" : ""}>
                                                <div className="field-title"> { peerReviewFocus && <span> Peer Review </span>} </div>
                                                <div className="field-content">
                                                    <div className='input-field cap-instructions'>
                                                        <label className="outfit-25"> Peer Review Instructions: </label>
                                                        <Field name='peer_review_instructions'>
                                                            {({input}) => (
                                                                <textarea
                                                                    name='peer_review_instructions'
                                                                    {...input}
                                                                    required
                                                                    onFocus={togglePeerReviewFocus}
                                                                />
                                                            )}
                                                        </Field>
                                                    </div>

                                                    <div className='cap-assignment-files'>
                                                        <label className="outfit-25"> Rubric: </label>
                                                        <input
                                                            type='file'
                                                            name='peer_review_rubric'
                                                            accept='.pdf,.zip,.docx'
                                                            required
                                                            onChange={(e) => fileChangeHandler(e, "rubric")}
                                                            onFocus={togglePeerReviewFocus}
                                                        />

                                                        <label className="outfit-25"> Template: </label>
                                                        <input
                                                            type='file'
                                                            name='peer_review_template'
                                                            accept='.pdf,.zip,.docx'
                                                            required
                                                            onChange={(e) => fileChangeHandler(e, "template")}
                                                            onFocus={togglePeerReviewFocus}
                                                        />
                                                    </div>

                                                    <div className='input-field cap-assignment-info'>
                                                        <label> Due Date: </label>
                                                        <Field name='peer_review_due_date'>
                                                            {({input}) => (
                                                                <input
                                                                    type='date'
                                                                    name='peer_review_due_date'
                                                                    {...input}
                                                                    required
                                                                    min={new Date().toISOString().split('T')[0]}
                                                                    onFocus={togglePeerReviewFocus}
                                                                />
                                                            )}
                                                        </Field>

                                                        <label> Points: </label>
                                                        <Field name='peer_review_points'>
                                                            {({input}) => (
                                                                <input
                                                                    type='number'
                                                                    min='0'
                                                                    name='peer_review_points'
                                                                    {...input}
                                                                    required
                                                                    onWheel={(e) => e.target.blur()}
                                                                    onFocus={togglePeerReviewFocus}
                                                                />
                                                            )}
                                                        </Field>
                                                    </div>
                                                </div>
                                            </div>

                                            <div className='cap-button'>
                                                <button className="green-button" type='submit'> Create </button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>

                    )}
                </Form>
            }
        </div>
    );
};

export default CreateAssignmentPage;
