import '../../styles/EditCourse.css'
import { useSelector } from 'react-redux'
import { useParams } from 'react-router-dom'

function ProfessorProfanitySettingsComponent () {
  const { currentCourse } = useSelector((state) => state.courses)
  let { courseId } = useParams()
  return (
    <div className="ecc-form">
      <h2 className="course-profanity-title">{currentCourse.course_name} Profanity Settings</h2>
      <div>{' '}</div>
      <div className="course-profanity-details">
        <p>Please enter words that you want to include in the profanity checking list for this course.
          You can load a pre-created list of global profanity setting defaults by clicking the "Load Defaults"
          button</p>
        <p><i>(New words to be separated by a line, press "Enter" to add new words and "Delete" to remove)</i></p>

      </div>
    </div>
  )
}

export default ProfessorProfanitySettingsComponent