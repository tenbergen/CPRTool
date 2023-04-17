import '../../styles/EditCourse.css'
import { useSelector } from 'react-redux'
import { useNavigate, useParams } from 'react-router-dom'
import { Field, Form } from 'react-final-form'
import { getCoursesAsync } from '../../../redux/features/courseSlice'
import * as React from 'react'
import { useEffect, useState } from 'react'
import { getAssignmentDetailsAsync } from '../../../redux/features/assignmentSlice'

function ProfessorProfanitySettingsComponent () {
  //yse this course id value for doing the get and post requests :)
  let { courseId } = useParams()
  let navigate = useNavigate()
  //DUMMY VALUES
  //this list would be what the instructor has set to their custom blocked word list
  const profaninityArray = JSON.parse('["Heck", "Phoebe Bridgers", "Conor Oberst", "Another"]')
  //this list would be the defualt from the admin
  const defaultProfanityArray = JSON.parse('["Dank", "Plonk", "Bad", "Words"]')
  const { currentCourse } = useSelector((state) => state.courses)

  useEffect(() => {
    //leaving the courseId dependency since you'll need it to query backend to get the list of blocked words
    //keeping this blank for now but this is where we would talk to the frontend to get the initial blocked words array
  }, [courseId])

  const initialValueSet = () => {
    //you would run this a lot like how the do in ProfessorEditAssignmentComponent
    let profanityText = profaninityArray[0]
    profaninityArray.forEach(element => {
        if (element !== profaninityArray[0]) {
          profanityText += '\n' + element
        }
      }
    )
    return {
      profanityText
    }
  }
  const [textboxInput, setTextBoxInput] = useState(initialValueSet)

  const loadDefaults = () => {
    //you would run this a lot like how the do in ProfessorEditAssignmentComponent
    let profanityText = defaultProfanityArray[0]
    defaultProfanityArray.forEach(element => {
        if (element !== defaultProfanityArray[0]) {
          profanityText += '\n' + element
        }
      }
    )
    setTextBoxInput(() => {return { profanityText } })
    console.log(textboxInput)
  }

  const handleTextBoxChange = event => {
    setTextBoxInput(() => {
      return {
        profanityText: event.target.value
      }
    })
  }

  const handleSubmit = async () => {
    //remove any extraneous new line characters while turning the input back into an array
    const blockedWordsArray = textboxInput.profanityText.split('\n').filter((str) => str !== '')
    //printing them out to the console just so you can see the array that gets made
    console.log(blockedWordsArray)
  }

  return (
    <div className="ecc-form">
      <h2
        className="course-profanity-title">{currentCourse == null ? null : currentCourse.course_name} Profanity
        Settings</h2>
      <div>{' '}</div>
      <div className="course-profanity-details">
        <p>Please enter words that you want to include in the profanity checking list for this course.
          You can load a pre-created list of global profanity setting defaults by clicking the "Load Defaults"
          button</p>
        <p><i>(New words to be separated by a line, press "Enter" to add new words and "Delete" to remove)</i></p>
        <button className="ecc-button-defaults" onClick={() => loadDefaults()}>
          Load Defaults
        </button>
        <div className="ecc-input-field">
          <form onSubmit={handleSubmit}>
                      <textarea value={textboxInput.profanityText} onChange={handleTextBoxChange} name="blockedWords"
                                required/>
          </form>
          <div id="ecc-button-container">
            <form onSubmit={handleSubmit} style={{ marginTop: '5%' }}>
              <button className="ecc-button" type="button" onClick={() => handleSubmit()}>Save Changes</button>
            </form>
            <button className="ecc-anchor-cancel" onClick={() => navigate(-1)}>
              Go Back
            </button>
          </div>
        </div>

      </div>
    </div>
  )
}

export default ProfessorProfanitySettingsComponent