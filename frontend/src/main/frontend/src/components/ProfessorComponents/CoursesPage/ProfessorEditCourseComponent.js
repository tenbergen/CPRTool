import React from "react-dom";
import { useState } from "react";
import "../../styles/EditCourse.css"
import "../../styles/DeleteModal.css"
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import { Form, Field } from 'react-final-form'
import { getCourseDetailsAsync } from "../../../redux/features/courseSlice";

const deleteCourseUrl = `${process.env.REACT_APP_URL}/manage/professor/courses`
const updateUrl = `${process.env.REACT_APP_URL}/manage/professor/courses/course/update`
const uploadCsvUrl = `${process.env.REACT_APP_URL}/manage/professor/courses/course/student/mass-add`
const assignmentUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`

const ProfessorEditCourseComponent = () => {
    let navigate = useNavigate()
    let dispatch = useDispatch()
    const { currentCourse } = useSelector((state) => state.courses)
    const { courseAssignments } = useSelector((state) => state.assignments)
    let { courseId } = useParams()

    const [showModal, setShow] = useState(false)
    const csvFormData = new FormData()

    const fileChangeHandler = (event) => {
        let file = event.target.files[0];
        const renamedFile = new File([file], currentCourse.course_id + ".csv", { type: file.type })
        csvFormData.set("csv_file", renamedFile)
        console.log(csvFormData.get("csv_file"))
    }

    const updateCourse = async (data) => {
        const finalData = {...data, course_id: currentCourse.course_id}

        await axios.put(updateUrl, finalData)
            .then((res) =>{
                console.log(res)
                courseId = res.data
                window.alert("Course successfully updated!")
                if (csvFormData.get("csv_file") != null) {
                    uploadCsv()
                } else {
                    dispatch(getCourseDetailsAsync(res.data))
                    navigate("/details/professor/" + res.data)
                }
            })
            .catch((e) => {
                console.log(e)
                window.alert("Error updating course. Please try again.")
            })
    }

    const uploadCsv = async () => {
        await axios.post(uploadCsvUrl, csvFormData, { headers: { "Content-Type": "multipart/form-data" }})
            .then((res) =>{
                console.log(res)
                window.alert("CSV successfully uploaded!")
            })
            .catch((e) => {
                console.log(e)
                window.alert("Error uploading CSV. Please try again.")
            })
        dispatch(getCourseDetailsAsync(courseId))
        navigate("/details/professor/" + courseId)
    }

    const deleteCourse = async () => {
        const url = `${deleteCourseUrl}/${courseId}/delete`
        await axios.delete(url).then((response) => {
            console.log(response)
        })
        if (courseAssignments.length > 0) await deleteAssignments()
        navigate("/")
    }

    const deleteAssignments = async () => {
        const url = `${assignmentUrl}/${courseId}/remove`
        await axios.delete(url).then((response) => {
            console.log(response)
        })
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

    const initialData = {
        course_name: currentCourse.course_name,
        course_section: currentCourse.course_section,
        semester: currentCourse.semester,
        abbreviation: currentCourse.abbreviation,
        year: currentCourse.year,
        crn: currentCourse.crn
    }

    const handleSubmit = async (formObj) => {
        if (JSON.stringify(initialData) === JSON.stringify(formObj)) {
            if (csvFormData.get("csv_file") != null) {
                await uploadCsv()
            } else {
                alert("Nothing to save!")
            }
        } else {
            await updateCourse(formObj)
        }
    }

    return (
        <div className="ecc-form">
            <Form
                onSubmit={async formObj => {
                    await handleSubmit(formObj)
                }}
                initialValues={initialData}>
                {({ handleSubmit }) => (
                    <form onSubmit={handleSubmit}>
                        <div className="ecc-input-field">
                            <label> <b> Name of course: </b> </label>
                            <Field name="course_name" >
                                {({ input }) => (
                                    <input
                                        type="text"
                                        name="course_name"
                                        {...input}
                                        required
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
                                            required
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
                                            required
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
                                            required
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
                                            required
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
                                accept=".csv"
                                // required
                            />
                        </div>
                        <div className="ecc-button">
                            <button  type="submit"> Save</button>
                        </div>
                    </form>
                )}
            </Form>
            <div className="ecc-delete">
                <a onClick={() => setShow(true)} target="_blank"><b> Delete course </b></a>
                <div>
                    {showModal ? Modal() : null}
                </div>
            </div>
        </div>
    )
}

export default ProfessorEditCourseComponent