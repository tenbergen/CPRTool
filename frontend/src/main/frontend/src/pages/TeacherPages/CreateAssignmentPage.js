import React  from "react";
import "./styles/CreateAssignmentStyle.css"
import SidebarComponent from "../../components/SidebarComponent";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import { Form, Field } from 'react-final-form'

const profAssignmentUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`

const CreateAssignmentPage = () => {
    let navigate = useNavigate()
    let { courseId } = useParams()

    const submitCourseUrl = `${profAssignmentUrl}/create-assignment`
    const getAssUrl = `${profAssignmentUrl}/${courseId}/assignments`

    const assignmentFileFormData = new FormData()
    const rubricFileFormData = new FormData()

    const assignmentFileHandler = (event) => {
        let file = event.target.files[0];
        assignmentFileFormData.set("file", file)
    }

    const peerReviewRubricHandler = (event) => {
        let file = event.target.files[0];
        rubricFileFormData.set("file", file)
    }

    const uploadFiles = async (assignmentId) => {
        console.log(assignmentFileFormData)
        console.log(assignmentFileFormData.get("file"))

        const fileUrl = `${getAssUrl}/${assignmentId}/upload`

        await axios.post(fileUrl, assignmentFileFormData)
            .then(res => {
                console.log(res)
            })
            .catch(e => {
                console.log(e)
            })
    }

    const handleSubmit = async (data) => {
        let { assignment_name, instructions, due_date, peer_review_instructions, points } = data;
        points = parseInt(points)
        const course_id = courseId

        const sentData = { assignment_name, instructions, peer_review_instructions, due_date, points, course_id };
        console.log(sentData)

        await axios.post(submitCourseUrl, sentData).then(res => {
            console.log(res)
        });

        await axios.get(getAssUrl).then(res => {
            console.log(res)
            uploadFiles(res.data.pop().assignment_id)
        });

        navigate("/details/professor/" + courseId)
    }

    return (
        <div>
            <Form
                onSubmit={formObj => {
                    handleSubmit(formObj)
                }}>
                {({ handleSubmit }) => (
                    <div className="cap-parent">
                    <SidebarComponent />
                        <div className="cap-container">
                            <h2> Add new assignment </h2>
                            <div className="cap-form">
                                <form onSubmit={handleSubmit}>
                                    <div className="cap-input-field">
                                        <label> <b> Name of assignment: </b> </label>
                                        <Field name="assignment_name" >
                                            {({ input }) => (
                                                <input
                                                    type="text"
                                                    name="assignment_name"
                                                    {...input}
                                                    required
                                                />
                                            )}
                                        </Field>
                                    </div>

                                    <div className="cap-instructions">
                                        <label> <b> Instructions:</b> </label>
                                        <Field name="instructions" >
                                            {({ input }) => (
                                                <input
                                                    type="text"
                                                    name="instructions"
                                                    {...input}
                                                    required
                                                />
                                            )}
                                        </Field>
                                    </div>

                                    <div className="cap-assignment-files">
                                        <label> <b> Files: </b> </label>
                                        <input
                                            type="file"
                                            name="AssignmentFiles"
                                            accept=".pdf"
                                            onChange={(e) => assignmentFileHandler(e)}
                                        />
                                    </div>

                                    <div className="cap-assignment-info">
                                        <label> <b> Due Date: </b> </label>
                                        <Field name="due_date" >
                                            {({ input }) => (
                                                <input
                                                    type="date"
                                                    name="due_date"
                                                    {...input}
                                                    required
                                                />
                                            )}
                                        </Field>

                                        <label> <b> Points: </b> </label>
                                        <Field name="points" >
                                            {({ input }) => (
                                                <input
                                                    type="number"
                                                    name="points"
                                                    {...input}
                                                    required
                                                />
                                            )}
                                        </Field>
                                    </div>

                                    <div className="cap-instructions">
                                        <label> <b> Peer Review Instructions:</b> </label>
                                        <Field name="peer_review_instructions" >
                                            {({ input }) => (
                                                <input
                                                    type="text"
                                                    name="peer_review_instructions"
                                                    {...input}
                                                    required
                                                />
                                            )}
                                        </Field>
                                    </div>

                                    <div className="cap-assignment-files">
                                        <label> <b> Rubric: </b> </label>
                                        <input
                                            type="file"
                                            name="ReviewRubric"
                                            accept=".pdf"
                                            required
                                            onChange={(e) => peerReviewRubricHandler(e)}
                                        />
                                    </div>

                                    <div className="cap-assignment-info">
                                        <label> <b> Due Date: </b> </label>
                                        <Field name="ReviewDueDate" >
                                            {({ input }) => (
                                                <input
                                                    type="date"
                                                    name="ReviewDueDate"
                                                    {...input}
                                                    required
                                                />
                                            )}
                                        </Field>

                                        <label> <b>Points: </b> </label>
                                        <Field name="ReviewPoints" >
                                            {({ input }) => (
                                                <input
                                                    type="number"
                                                    min="0"
                                                    name="ReviewPoints"
                                                    {...input}
                                                    required
                                                />
                                            )}
                                        </Field>
                                    </div>
                                    <div className="cap-button">
                                        <button type="submit"> Save</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                )}
            </Form>
        </div>
    );
}

export default CreateAssignmentPage;