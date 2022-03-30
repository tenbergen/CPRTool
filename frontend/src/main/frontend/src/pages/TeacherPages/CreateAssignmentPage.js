import React, { useState } from "react";
import "./styles/CreateAssignmentStyle.css"
import SidebarComponent from "../../components/SidebarComponent";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import {useSelector} from "react-redux";

const CreateAssignmentPage = () => {
    const currentCourse = useSelector((state) => state.courses.currentCourse)
    const courseId = currentCourse.course_id
    //const submitCourseUrl = `${process.env.REACT_APP_URL}/manage/professor/courses/course/create`
    const submitCourseUrl = 'http://moxie.cs.oswego.edu:13125/assignments/professor/courses/create-assignment'
    // const uploadFileUrl = 'http://moxie.cs.oswego.edu:13125/professor/courses/' + courseId +
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

    const handleSubmit = async (e) => {
        if (AssignmentName === '' || AssignmentInstructions === '' || files === '' || AssignmentDueDate === '' || AssignmentPoints === '' 
        || ReviewInstructions === '' || ReviewRubric === '' || ReviewDueDate === '' || ReviewPoints === '') alert("Fields can't be empty!")
        else {
            e.preventDefault()
            const data = {
                assignment_name: AssignmentName,
                instructions: AssignmentInstructions,
                due_date: AssignmentDueDate,
                points: AssignmentPoints,
                course_id: courseId
            };
            await axios.post(submitCourseUrl, data).then(r => console.log(r));
            let assignmentId
            await axios.get(getAssUrl).then(r => {
                console.log(r)
                assignmentId = r.data[r.data.length - 1].assignment_id
            });
            const fileUrl = 'http://moxie.cs.oswego.edu:13125/assignments/professor/courses/' + courseId + '/assignments/'
            + assignmentId + '/upload'
            let form = new FormData()
            form.set(AssignmentName + ".pdf", files)
            await axios.post(fileUrl, form)
            // const u = 'http://moxie.cs.oswego.edu:13125/assignments/professor/courses/' + courseId + '/assignments/'
            //     + assignmentId + '/view-files'
            // axios.get(u).then(r => console.log(r))

            navigate("/details/professor/" + courseId)
        }
    }

    return (
        <div className="cap-parent">
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
                            type="file"
                            name="AssignmentFiles"
                            value={files}
                            required
                            onChange={(e) => OnChange(e)}
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