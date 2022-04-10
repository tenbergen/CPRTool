import {useSelector} from "react-redux";
import "../../styles/StudentAss.css"

const StudentSubmittedComponent = () => {
    // Need assingment endpoints
    const currentCourse = useSelector((state) => state.courses.currentCourse)
    console.log(currentCourse);
    const assUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses/${currentCourse.course_id}/assignments/`
    const assignments = ["Thesis First Draft", "Thesis Submission"]

    return (

        <h3>
            <div id="assList">
                {assignments.map(assignment =>
                        // <Link to={}> add this for functionality
                        <li id="assListItem">{assignment}</li>
                    // </Link>
                )}
            </div>
        </h3>

    )
}

export default StudentSubmittedComponent