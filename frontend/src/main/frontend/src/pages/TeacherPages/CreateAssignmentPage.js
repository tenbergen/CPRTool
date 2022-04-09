import React, { useState } from "react";
import "./styles/CreateAssignmentStyle.css"
import SidebarComponent from "../../components/SidebarComponent";
import {useNavigate, useParams} from "react-router-dom";
import axios from "axios";
import {useSelector} from "react-redux";

const CreateAssignmentPage = () => {
    const currentCourse = useSelector((state) => state.courses.currentCourse)
    const { courseId } = useParams()
    //const submitCourseUrl = `${process.env.REACT_APP_URL}/manage/professor/courses/course/create`
    const submitCourseUrl = 'http://moxie.cs.oswego.edu:13125/assignments/professor/courses/create-assignment'
    const getAssUrl = 'http://moxie.cs.oswego.edu:13125/assignments/professor/courses/' + courseId + '/assignments/'

    console.log(courseId)
    let navigate = useNavigate()

    const [formData, setFormData] = useState({
        CourseName: '',
        CourseSection: undefined,
        Semester: '',
        Abbreviation: ''
    });

    const { AssignmentName, AssignmentInstructions, files, AssignmentDueDate, AssignmentPoints, ReviewInstructions, ReviewRubric, ReviewDueDate, ReviewPoints} = formData;

    const OnChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

    const fileFormData = new FormData()

    const fileChangeHandler = (event) => {
        let file = event.target.files[0];
        const renamedFile = new File([file], "something.pdf", { type: file.type })
        fileFormData.append("file", renamedFile)
    }

    const uploadFiles = async (assignmentId) => {
        const fileUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}/assignments/${assignmentId}/upload`
        await axios.post(fileUrl, fileFormData)
            .then(res => {
                console.log(res)
            })
            .catch(e => {
                console.log(e)
            })
    }

    const handleSubmit = async (e) => {
        if (AssignmentName === '' || AssignmentInstructions === '' || files === '' || AssignmentDueDate === '' || AssignmentPoints === '' 
        || ReviewInstructions === '' || ReviewRubric === '' || ReviewDueDate === '' || ReviewPoints === '') alert("Fields can't be empty!")
        else {
            e.preventDefault()
            const data = {
                assignment_name: AssignmentName,
                instructions: AssignmentInstructions,
                peer_review_instructions: ReviewInstructions,
                due_date: AssignmentDueDate,
                points: AssignmentPoints,
                course_id: courseId
            };

            await axios.post(submitCourseUrl, data).then(res => {
                console.log(res)
            });

            const assignmentId = await axios.get(getAssUrl).then(res => {
                console.log(res)
                return res.data[res.data.length - 1].assignment_id
            });

            setTimeout(() => {
                uploadFiles(assignmentId)
            }, 5000);

            navigate("/details/professor/" + courseId)
        }
    }

    return (
        <div className="cap-parent">
            {/*<FileUploadComponent />*/}
            <SidebarComponent />
            <div className="cap-container">
                <h2> Add new assignment </h2>
                <form className="cap-form">

                    <div className="cap-input-field">
                        <label> <b> Name of assignment: </b> </label>
                        <input
                            type="text"
                            name="AssignmentName"
                            value={AssignmentName}
                            required
                            onChange={(e) => OnChange(e)}
                        />
                    </div>

                    <div className="cap-instructions">
                        <label> <b> Instructions:</b> </label>
                        <input
                            type="text"
                            name="AssignmentInstructions"
                            value={AssignmentInstructions}
                            required
                            onChange={(e) => OnChange(e)}
                        />
                    </div>

                    <div className="cap-assignment-files">
                        <label> <b> Files: </b> </label>
                        <input
                            onChange={fileChangeHandler}
                            type="file"
                            name="assignment_file"
                            accept=".pdf"
                        />
                    </div>

                    <div className="cap-assignment-info">
                        <label> <b> Due Date: </b> </label>
                        <input
                            type="date"
                            name="AssignmentDueDate"
                            value={AssignmentDueDate}
                            required
                            onChange={(e) => OnChange(e)}
                        />
                        <label> <b> Points: </b> </label>
                        <input
                            type="number"
                            min="0"
                            name="AssignmentPoints"
                            value={AssignmentPoints}
                            required
                            onChange={(e) => OnChange(e)}
                        />
                    </div>

                    <div className="cap-instructions">
                        <label> <b> Peer Review Instructions:</b> </label>
                        <input
                            type="text"
                            name="ReviewInstructions"
                            value={ReviewInstructions}
                            required
                            onChange={(e) => OnChange(e)}
                        />
                    </div>

                    <div className="cap-assignment-files">
                        <label> <b> Rubric: </b> </label>
                        <input
                            type="file"
                            name="ReviewRubric"
                            value={ReviewRubric}
                            required
                            onChange={(e) => OnChange(e)}
                        />
                    </div>

                    <div className="cap-assignment-info">
                        <label> <b> Due Date: </b> </label>
                        <input
                            type="date"
                            name="ReviewDueDate"
                            value={ReviewDueDate}
                            required
                            onChange={(e) => OnChange(e)}
                        />
                        <label> <b>Points: </b> </label>
                        <input
                            type="number"
                            min="0"
                            name="ReviewPoints"
                            value={ReviewPoints}
                            required
                            onChange={(e) => OnChange(e)}
                        />
                    </div>

                    <div className="cap-button">
                        <button onClick={handleSubmit}> Create Peer Review </button>
                    </div>

                    {/* <div class="cap-altbutton">
                        <a href="">Create Assignment Only </a>
                    </div> */}
                </form>
            </div>
        </div>
    );
}

export default CreateAssignmentPage;