import React from "react-dom";
import {useNavigate} from "react-router-dom";
import {useState} from "react";
import "./styles/EditCourse.css"
import "./styles/DeleteModal.css"
import {useSelector} from "react-redux";

const EditCourseComponent = () => {
    const deleteUrl = `${process.env.REACT_APP_URL}/manage/professor/courses/course/delete`
    const editUrl = ""
    const csvUrl = ""
    // const {from} = useLocation().state
    // let currentCourse = Object()

    const currentCourse = useSelector((state) => state.courses.currentCourse)


    // currentCourse.CourseName = from.CourseName
    // currentCourse.CourseSection = from.CourseSection
    // currentCourse.Semester = from.Semester
    // currentCourse.Abbreviation = from.Abbreviation

    let navigate = useNavigate()
    console.log(currentCourse)

    const [courseDescription, setCourseDescription] = useState()
    const [courseName, setCourseName] = useState(currentCourse.CourseName)

    let [selectedFile, setSelectedFile] = useState()
    let [isFilePicked, setIsFilePicked] = useState(false)

    // TODO move post request to submit button handler
    // const changeHandler = async (event) => {
    //     setSelectedFile(event.target.files[0])
    //     setIsFilePicked(true)
    //     const formData = new FormData()
    //     formData.append("File", event.target.files[0])
    //
    //     await axios.post(csvUrl, formData).then( (response) => {
    //         console.log(response)
    //     })
    // }

    // const deleteCourse = async () => {
    //     await axios.post(deleteUrl, currentCourse).then((response) => {
    //         console.log(response)
    //     })
    //     navigate("/teacherDashboard")
    // }

    //
    // const editCourse = async () => {
    //     // add
    //     await axios.post(editUrl, currentCourse).then((response) =>{
    //         console.log(response)
    //     })
    // }

    const modal = () => {
        return (
            <div id="deleteModal">
                <div id="modalContent">
                    <span id="deleteSpan">Are you sure you want to delete this course?</span>
                    <div id="deleteButtons">
                        <button /*onClick={} */ >Yes</button>
                        <button onClick={handleClose}>No</button>
                    </div>
                </div>
            </div>

        )
    }
    
    const [showModal, setShow] = useState(false)
    const handleShow = () => setShow(true)
    const handleClose = () => setShow(false)

    return (
        <form id="editCourseForm">
            <div className="course-name">
                <label> <b> Name of course: </b> </label>
                <input
                    type="text"
                    name="courseName"
                    value={currentCourse}
                    required
                    //onChange={(e) => OnChange(e)}
                />
            </div>

            <div className="course-csv">
                <label> <b> Add course CSV: </b> </label>
                <input
                    //onChange={changeHandler}
                    type="file"
                    name="courseCSV"
                    required
                />
            </div>

            <div className="team-size">
                <label> <b> Select peer review team size: </b> </label>
                <input
                    type="number"
                    min="1"
                    name="teamSize"
                    required
                />
            </div>

            <div className="editCourseButton">
                <button> Submit</button>
                <a onClick={handleShow} target="_blank"> Delete course </a>
                <div>
                    {showModal ? modal() : null}
                </div>
            </div>
        </form>
    )
}

export default EditCourseComponent