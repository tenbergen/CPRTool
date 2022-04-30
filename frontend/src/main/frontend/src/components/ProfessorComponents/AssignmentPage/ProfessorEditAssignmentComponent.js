import {Field, Form} from 'react-final-form';
import React, {useEffect} from 'react';
import {useDispatch, useSelector} from 'react-redux';
import {getAssignmentDetailsAsync, getCourseAssignmentsAsync} from '../../../redux/features/assignmentSlice';
import {useParams} from 'react-router-dom';
import '../../styles/EditAssignmentStyle.css';
import axios from 'axios';

const profAssignmentUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`;

const ProfessorEditAssignmentComponent = () => {
    const dispatch = useDispatch();
    const {courseId, assignmentId} = useParams();
    const {currentAssignment, currentAssignmentLoaded} = useSelector((state) => state.assignments);

    const assignmentFileFormData = new FormData();
    const rubricFileFormData = new FormData();
    const templateFileFormData = new FormData();

    const getAssUrl = `${profAssignmentUrl}/${courseId}/assignments`;

    useEffect(() => {
        dispatch(getAssignmentDetailsAsync({courseId, assignmentId}));
    }, []);

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

    const handleSubmit = async (formObj) => {
        const editUrl = `${getAssUrl}/${assignmentId}/edit`;
        console.log(editUrl)
        console.log({...formObj, course_id: courseId})

        if (JSON.stringify(() => initialValue()) === JSON.stringify(formObj)) {
            alert('Nothing to save!');
            return;
        }

        await axios.put(editUrl, {...formObj, course_id: courseId})
            .then((res) => {
                console.log(res.data);
            })
            .catch((e) => {
                console.log(e.response);
            });

        await submitNewFiles()
        dispatch(getCourseAssignmentsAsync(courseId))
        dispatch(getAssignmentDetailsAsync({courseId, assignmentId}))
        alert("Successfully updated assignment!")
    };

    const submitNewFiles = async () => {
        const assignmentFileUrl = `${getAssUrl}/${assignmentId}/upload`;
        const rubricUrl = `${getAssUrl}/${assignmentId}/peer-review/rubric/upload`;
        const templateUrl = `${getAssUrl}/${assignmentId}/peer-review/template/upload`;

        if (assignmentFileFormData.get('file')) {
            await axios.post(assignmentFileUrl, assignmentFileFormData)
                .then((res) => {
                    console.log(res);
                })
                .catch((e) => {
                    console.log(e);
                    alert('Error uploading assignment file.');
                });
        }

        if (assignmentFileFormData.get('file')) {
            await axios.post(rubricUrl, rubricFileFormData)
                .then((res) => {
                    console.log(res);
                })
                .catch((e) => {
                    console.log(e);
                    alert('Error uploading peer review rubric.');
                });
        }

        if (templateFileFormData.get('file')) {
            await axios.post(templateUrl, templateFileFormData)
                .then((res) => {
                    console.log(res);
                })
                .catch((e) => {
                    console.log(e);
                    alert('Error uploading peer review template.');
                });
        }
    }

    const deleteFile = async (fileName, isPeerReview) => {
        const url = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}/assignments/${assignmentId}`;

        const deleteUrl = isPeerReview
            ? `${url}/peer-review/remove-file/${fileName}`
            : `${url}/remove-file/${fileName}`;

        await axios.delete(deleteUrl)
            .then((res) => {
                console.log(res);
            })
            .catch((e) => {
                console.log(e);
            });

        dispatch(getAssignmentDetailsAsync({courseId, assignmentId}));
    };

    const downloadFile = (blob, fileName) => {
        const fileURL = URL.createObjectURL(blob);
        const href = document.createElement('a');
        href.href = fileURL;
        href.download = fileName;
        href.click();
    };

    const onFileClick = async (fileName, isPeerReview) => {
        const url = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}/assignments/${assignmentId}`;
        const downloadUrl = isPeerReview
            ? `${url}/peer-review/download/${fileName}`
            : `${url}/download/${fileName}`;

        await axios.get(downloadUrl, {responseType: 'blob'})
            .then((res) => downloadFile(res.data, fileName));
    };

    const initialValue = () => {
        if (currentAssignmentLoaded) {
            return {
                assignment_name: currentAssignment.assignment_name,
                instructions: currentAssignment.instructions,
                due_date: currentAssignment.due_date,
                points: currentAssignment.points,
                peer_review_instructions: currentAssignment.peer_review_instructions,
                peer_review_due_date: currentAssignment.peer_review_due_date,
                peer_review_points: currentAssignment.peer_review_points,
            };
        }
    };

    return (
        <div className='eac-form'>
            <Form
                onSubmit={async (formObj) => {
                    await handleSubmit(formObj);
                }}
                initialValues={() => initialValue()}>
                {({handleSubmit}) => (
                    <form onSubmit={handleSubmit}>
                        <div className='eac-input-field'>
                            <label> Name of assignment: </label>
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

                        <div className='eac-instructions'>
                            <label> Instructions: </label>
                            <Field name='instructions'>
                                {({input}) => (
                                    <textarea
                                        name='instructions'
                                        {...input}
                                        required
                                    />
                                )}
                            </Field>
                        </div>

                        <div className='eac-assignment-files'>
                            Current files:
                            <span
                                className="eac-file-name"
                                onClick={() => onFileClick(currentAssignment.assignment_instructions, false)}>
                                  {currentAssignmentLoaded
                                      ? currentAssignment.assignment_instructions
                                      : null}
                            </span>
                            <span
                                onClick={() => deleteFile(currentAssignment.assignment_instructions, false)}
                                className={currentAssignmentLoaded && currentAssignment.assignment_instructions !== "" ? 'eac-crossmark' : 'eac-crossmark-gone'}>
                                    &#10060;
                                </span>
                        </div>

                        <div className='eac-assignment-files'>
                            <label> New files: </label>
                            <input
                                type='file'
                                name='assignment_files'
                                accept='.pdf,.zip'
                                onChange={(e) => fileChangeHandler(e, "assignment")}
                            />
                        </div>

                        <div className='eac-assignment-info'>
                            <label> Due Date: </label>
                            <Field name='due_date'>
                                {({input}) => (
                                    <input
                                        type='date'
                                        name='due_date'
                                        {...input}
                                        required/>
                                )}
                            </Field>

                            <label> Points: </label>
                            <Field name='points'>
                                {({input}) => (
                                    <input
                                        type='number'
                                        name='points'
                                        {...input}
                                        required/>
                                )}
                            </Field>
                        </div>

                        <div className='eac-instructions'>
                            <label> Peer Review Instructions: </label>
                            <Field name='peer_review_instructions'>
                                {({input}) => (
                                    <textarea
                                        name='peer_review_instructions'
                                        {...input}
                                        required
                                    />
                                )}
                            </Field>
                        </div>

                        <div className='eac-assignment-files-multiple'>
                            <div>
                                <div className='eac-assignment-files' style={{marginBottom: "8%"}}>
                                    Current files:
                                    <span
                                        className="eac-file-name"
                                        onClick={() => onFileClick(currentAssignment.peer_review_rubric, true)}>
                                              {currentAssignmentLoaded
                                                  ? currentAssignment.peer_review_rubric
                                                  : null}
                                    </span>
                                    <span onClick={() => deleteFile(currentAssignment.peer_review_rubric, true)}
                                          className={currentAssignmentLoaded && currentAssignment.peer_review_rubric !== "" ? 'eac-crossmark' : 'eac-crossmark-gone'}>
                                        &#10060;
                                    </span>
                                </div>

                                <div className='eac-assignment-files' style={{marginBottom: "0"}}>
                                    <label> New rubric: </label>
                                    <input
                                        type='file'
                                        name='peer_review_rubric'
                                        accept='.pdf,.zip'
                                        onChange={(e) => fileChangeHandler(e, "rubric")}
                                    />
                                </div>
                            </div>

                            <div>
                                <div className='eac-assignment-files' style={{marginBottom: "8%"}}>
                                    Current files:
                                    <span onClick={() => onFileClick(currentAssignment.peer_review_template, true)}>
                                      {currentAssignmentLoaded
                                          ? currentAssignment.peer_review_template
                                          : null}
                                    </span>
                                    <span
                                        onClick={() => deleteFile(currentAssignment.peer_review_template, true)}
                                        className={currentAssignmentLoaded && currentAssignment.peer_review_template !== "" ? 'eac-crossmark' : 'eac-crossmark-gone'}>
                                        &#10060;
                                    </span>
                                </div>

                                <div className='eac-assignment-files' style={{marginBottom: "0"}}>
                                    <label> New template: </label>
                                    <input
                                        type='file'
                                        name='peer_review_template'
                                        accept='.pdf,.zip'
                                        onChange={(e) => fileChangeHandler(e, "template")}
                                    />
                                </div>
                            </div>
                        </div>

                        <div className='eac-assignment-info'>
                            <label> Due Date: </label>
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

                            <label> Points: </label>
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
                )}
            </Form>
        </div>
    );
};

export default ProfessorEditAssignmentComponent;
