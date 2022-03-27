import React from "react-dom";
import { useState} from "react";
import "./styles/EditCourse.css"
import "./styles/DeleteModal.css"
import {useSelector} from "react-redux";
import {useNavigate} from "react-router-dom";
import axios from "axios";
import { Form, Field } from 'react-final-form'

const deleteUrl = `${process.env.REACT_APP_URL}/manage/professor/courses/course/delete`
const csvUrl = ""

const EditCourseComponent = () => {
    let navigate = useNavigate()
    const currentCourse = useSelector((state) => state.courses.currentCourse)
    console.log(currentCourse)

    const [showModal, setShow] = useState(false)
    const [file, setFile] = useState(null)

    const fileChangeHandler = (event) => {
        let file = event.target.files[0];
        const renamedFile = new File([file], currentCourse.course_id, { type: file.type })
        const fileFormData = new FormData()
        fileFormData.append("File", renamedFile)
        setFile(fileFormData)
    }

    const editCourse = (data) => {
        console.log(data)
        // await axios.post(editUrl, currentCourse).then((response) =>{
        //     console.log(response)
        // })
    }

    const deleteCourse = async () => {
        await axios.post(deleteUrl, currentCourse).then((response) => {
            console.log(response)
        })
        navigate("/")
    }

    const Modal = () => {
        return (
            <div id="deleteModal">
                <div id="modalContent">
                    <span id="deleteSpan">Are you sure you want to delete this course?</span>
                    <div id="deleteButtons">
                        <button onClick={deleteCourse}>Yes</button>
                        <button onClick={() => setShow(false)}>No</button>
                    </div>
                </div>
            </div>
        )
    }

    return (
        <div>
            <Form
                onSubmit={formObj => {
                    editCourse(formObj)
                }}
                initialValues={{
                    course_name: currentCourse.course_name,
                    course_section: currentCourse.course_section,
                    semester: currentCourse.semester,
                    abbreviation: currentCourse.abbreviation,
                    year: currentCourse.year,
                }}>
                {({ handleSubmit }) => (
                    <form onSubmit={handleSubmit} className="ecc-form">
                        <div className="ecc-input-field">
                            <label> <b> Name of course: </b> </label>
                            <Field name="course_name" >
                                {({ input }) => (
                                    <input
                                        type="text"
                                        name="course_name"
                                        {...input}
                                    />
                                )}
                            </Field>
                        </div>

                        <div className="ecc-row-multiple">
                            <div className="ecc-input-field">
                                <label> <b> Course abbreviation: </b> </label>
                                <Field name="abbreviation">
                                    {({ input }) => (
                                        <input
                                            type="text"
                                            name="abbreviation"
                                            {...input}
                                        />
                                    )}
                                </Field>
                            </div>

                            <div className="ecc-input-field">
                                <label> <b> Course section: </b> </label>
                                <Field name="course_section">
                                    {({ input }) => (
                                        <input
                                            type="text"
                                            name="course_section"
                                            {...input}
                                        />
                                    )}
                                </Field>
                            </div>
                        </div>

                        <div className="ecc-row-multiple">
                            <div className="ecc-input-field">
                                <label> <b> Semester: </b> </label>
                                <Field name="semester">
                                    {({ input }) => (
                                        <input
                                            type="text"
                                            name="semester"
                                            {...input}
                                        />
                                    )}
                                </Field>
                            </div>
                            <div className="ecc-input-field">
                                <label> <b> Year: </b> </label>
                                <Field name="year">
                                    {({ input }) => (
                                        <input
                                            type="text"
                                            name="year"
                                            {...input}
                                        />
                                    )}
                                </Field>
                            </div>
                        </div>

                        <div className="ecc-row-multiple">
                            <div className="ecc-input-field">
                                <label> <b> CRN: </b> </label>
                                <Field name="crn" >
                                    {({ input }) => (
                                        <input
                                            type="text"
                                            name="crn"
                                            {...input}
                                        />
                                    )}
                                </Field>

                            </div>

                            <div className="ecc-input-field">
                                <label> <b> Team size: </b> </label>
                                <Field name="team_size" >
                                    {({ input }) => (
                                        <input
                                            type="number"
                                            min="1"
                                            name="team_size"
                                            {...input}
                                        />
                                    )}
                                </Field>

                            </div>
                        </div>

                        <div className="ecc-file-upload">
                            <label> <b> Course CSV: </b> </label>
                            <input
                                onChange={fileChangeHandler}
                                type="file"
                                name="course_csv"
                                // required
                            />
                        </div>

                        <div className="ecc-button">
                            <button type="submit"> Save</button>
                            <a onClick={() => setShow(true)} target="_blank"> Delete course </a>
                            <div>
                                {showModal ? Modal() : null}
                            </div>
                        </div>
                    </form>
                )}
            </Form>
        </div>
    )
}

export default EditCourseComponent