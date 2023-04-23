import '../../styles/EditCourse.css'
import { useDispatch, useSelector } from 'react-redux'
import { useNavigate, useParams } from 'react-router-dom'
import { getCourseDetailsAsync } from '../../../redux/features/courseSlice'
import * as React from 'react'
import { useEffect, useState } from 'react'
import axios from 'axios'

function ProfessorProfanitySettingsComponent () {
  const dispatch = useDispatch()
  //use this course id value for doing the get and post requests
  let { courseId } = useParams()
  const updateProfanityUrl = `${process.env.REACT_APP_URL}/manage/professor/courses/` + courseId + `/profanity/update`
  const getAdminProfanityUrl = `${process.env.REACT_APP_URL}/manage/admin/views/profanity`
  let navigate = useNavigate()
  let profaninityArray = []
  const { currentCourse } = useSelector((state) => state.courses)

  useEffect(() => {
    //set the profanity array value to whatever the current course has it as so long as it is not null
    profaninityArray = currentCourse == null ? null : currentCourse.blocked_words
    //set the text box input to the currently loaded bad words
    setTextBoxInput(initialValueSet)
  }, [currentCourse])

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
  const [showModal, setShowModal] = useState(false)


  //modal object
  const Modal = () => {
    return (
      <div id="modal">
        <div id="modalContent" style={{
          height: '40%',
          width: '45%'
        }}>
          <svg className="cross" style={{}} onClick={async () => {
            navigate(-1)
            dispatch(getCourseDetailsAsync(courseId))
          }}></svg>
          <span
            style={{
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'center',
              verticalAlign: 'middle',
              position: 'absolute',
              top: '27%'
            }}>
                        <svg className={'check_image'}></svg>
                        <div className="inter-28-bold"
                             style={{
                               marginTop: '3%',
                               marginBottom: '8%'
                             }}>Success!</div>
                        <p style={{ color: 'black' }}>Course profanity list successfully updated!</p>
                    </span>
        </div>
      </div>
    )
  }

  const loadDefaults = async () => {
    //you would run this a lot like how the do in ProfessorEditAssignmentComponent
    await axios
      .get(getAdminProfanityUrl)
      .then((res) => {
        let profanityText = res.data[0]
        let defaultProfanityArray = res.data
        defaultProfanityArray.forEach(element => {
            if (element !== defaultProfanityArray[0]) {
              profanityText += '\n' + element
            }
          }
        )
        setTextBoxInput(() => {return { profanityText } })
      })
      .catch((e) => {
        console.error(e)
        alert('Error updating profanity list')
      })
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
    const blockedWordsArray = textboxInput.profanityText.split('\n').filter((str, index, arr) => str !== '' && arr.indexOf(str) === index)
    await axios
      .post(updateProfanityUrl, blockedWordsArray)
      .then((res) => {
        setShowModal(true)
      })
      .catch((e) => {
        console.error(e)
        alert('Error updating profanity list')
      })
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
              <button className="ecc-button" type="button" onClick={() => handleSubmit()}>Save
                Changes <div>{showModal ? Modal() : null}</div></button>
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