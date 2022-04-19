import React from 'react';
import './styles/CreateAssignmentStyle.css';
import SidebarComponent from '../../components/SidebarComponent';
import {useNavigate, useParams} from 'react-router-dom';
import axios from 'axios';
import {Field, Form} from 'react-final-form';

const profAssignmentUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`;

const CreateAssignmentPage = () => {
    let navigate = useNavigate();
    let {courseId} = useParams();

    const submitCourseUrl = `${profAssignmentUrl}/create-assignment`;
    const getAssUrl = `${profAssignmentUrl}/${courseId}/assignments`;

    const assignmentFileFormData = new FormData();
    const rubricFileFormData = new FormData();
    const templateFileFormData = new FormData();

    const assignmentFileHandler = (event) => {
        let file = event.target.files[0];
        assignmentFileFormData.set('file', file);
    };

    const peerReviewRubricHandler = (event) => {
        let file = event.target.files[0];
        rubricFileFormData.set('file', file);
    };

    const peerReviewTemplateHandler = (event) => {
        let file = event.target.files[0];
        templateFileFormData.set('file', file);
    };

    const uploadFiles = async (assignmentId) => {
        console.log(assignmentFileFormData);
        console.log(assignmentFileFormData.get('file'));

        const assignmentFileUrl = `${getAssUrl}/${assignmentId}/upload`;
        const rubricUrl = `${getAssUrl}/${assignmentId}/peer-review/rubric/upload`;
        const templateUrl = `${getAssUrl}/${assignmentId}/peer-review/template/upload`;

        await axios
            .post(assignmentFileUrl, assignmentFileFormData)
            .then((res) => {
                console.log(res);
            })
            .catch((e) => {
                console.log(e);
                alert('Error uploading assignment file.');
            });

        await axios
            .post(rubricUrl, rubricFileFormData)
            .then((res) => {
                console.log(res);
            })
            .catch((e) => {
                console.log(e);
                alert('Error uploading peer review rubric.');
            });

        await axios
            .post(templateUrl, templateFileFormData)
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

        const sentData = {...data, points, course_id};
        console.log(sentData);

        await axios.post(submitCourseUrl, sentData).then((res) => {
            console.log(res);
        });

        const assignment_id = await axios.get(getAssUrl).then((res) => {
            console.log(res);
            return res.data.pop().assignment_id;
        });

        await uploadFiles(assignment_id);

        navigate('/details/professor/' + courseId);
    };

    return (
        <div>
            <Form
                onSubmit={async (formObj) => {
                    await handleSubmit(formObj);
                }}
            >
                {({handleSubmit}) => (
                    <div className='cap-parent'>
                        <SidebarComponent/>
                        <div className='cap-container'>
                            <h2> Add new assignment </h2>
                            <div className='cap-form'>
                                <form onSubmit={handleSubmit}>
                                    <div className='cap-input-field'>
                                        <label>
                                            {' '}
                                            <b> Name of assignment: </b>{' '}
                                        </label>
                                        <Field name='assignment_name'>
                                            {({input}) => (
                                                <input
                                                    type='text'
                                                    name='assignment_name'
                                                    {...input}
                                                    required
                                                />
                                            )}
                                        </Field>
                                    </div>

                                    <div className='cap-instructions'>
                                        <label>
                                            {' '}
                                            <b> Instructions:</b>{' '}
                                        </label>
                                        <Field name='instructions'>
                                            {({input}) => (
                                                <input
                                                    type='text'
                                                    name='instructions'
                                                    {...input}
                                                    required
                                                />
                                            )}
                                        </Field>
                                    </div>

                                    <div className='cap-assignment-files'>
                                        <label>
                                            {' '}
                                            <b> Files: </b>{' '}
                                        </label>
                                        <input
                                            type='file'
                                            name='assignment_files'
                                            accept='.pdf,.zip'
                                            onChange={(e) => assignmentFileHandler(e)}
                                        />
                                    </div>

                                    <div className='cap-assignment-info'>
                                        <label>
                                            {' '}
                                            <b> Due Date: </b>{' '}
                                        </label>
                                        <Field name='due_date'>
                                            {({input}) => (
                                                <input
                                                    type='date'
                                                    name='due_date'
                                                    {...input}
                                                    required
                                                />
                                            )}
                                        </Field>

                                        <label>
                                            {' '}
                                            <b> Points: </b>{' '}
                                        </label>
                                        <Field name='points'>
                                            {({input}) => (
                                                <input
                                                    type='number'
                                                    name='points'
                                                    {...input}
                                                    required
                                                />
                                            )}
                                        </Field>
                                    </div>

                                    <div className='cap-instructions'>
                                        <label>
                                            {' '}
                                            <b> Peer Review Instructions:</b>{' '}
                                        </label>
                                        <Field name='peer_review_instructions'>
                                            {({input}) => (
                                                <input
                                                    type='text'
                                                    name='peer_review_instructions'
                                                    {...input}
                                                    required
                                                />
                                            )}
                                        </Field>
                                    </div>

                                    <div className='cap-assignment-files'>
                                        <label>
                                            {' '}
                                            <b> Rubric: </b>{' '}
                                        </label>
                                        <input
                                            type='file'
                                            name='peer_review_rubric'
                                            accept='.pdf,.zip'
                                            required
                                            onChange={(e) => peerReviewRubricHandler(e)}
                                        />

                                        <label>
                                            {' '}
                                            <b> Template: </b>{' '}
                                        </label>
                                        <input
                                            type='file'
                                            name='peer_review_template'
                                            accept='.pdf,.zip'
                                            required
                                            onChange={(e) => peerReviewTemplateHandler(e)}
                                        />
                                    </div>

                                    <div className='cap-assignment-info'>
                                        <label>
                                            {' '}
                                            <b> Due Date: </b>{' '}
                                        </label>
                                        <Field name='peer_review_due_date'>
                                            {({input}) => (
                                                <input
                                                    type='date'
                                                    name='peer_review_due_date'
                                                    {...input}
                                                    required
                                                />
                                            )}
                                        </Field>

                                        <label>
                                            {' '}
                                            <b>Points: </b>{' '}
                                        </label>
                                        <Field name='peer_review_points'>
                                            {({input}) => (
                                                <input
                                                    type='number'
                                                    min='0'
                                                    name='peer_review_points'
                                                    {...input}
                                                    required
                                                />
                                            )}
                                        </Field>
                                    </div>
                                    <div className='cap-button'>
                                        <button type='submit'> Save</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                )}
            </Form>
        </div>
    );
};

export default CreateAssignmentPage;
