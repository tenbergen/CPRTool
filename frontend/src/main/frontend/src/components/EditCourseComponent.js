import React from "react-dom";
import {useNavigate} from "react-router-dom";
import {useState} from "react";
import "./styles/EditCourse.css"

const EditCourseComponent = () => {
    const deleteUrl = `${process.env.REACT_APP_URL}/manage/professor/courses/course/delete`
    const editUrl = ""
    const csvUrl = ""
    // const {from} = useLocation().state
    let currentCourse = Object()

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
    
    const [showModal, setShow] = useState(false)
    const handleShow = () => setShow(true)

    return (
        <form id="editCourseForm">
            <div className="course-name">
                <label> <b> Name of course: </b> </label>
                <input
                    type="text"
                    name="courseName"
                    value={courseName}
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
                    {}
                </div>
            </div>
        </form>
    )
}

export default EditCourseComponent