import '../../components/styles/EditCourse.css'
import { useDispatch, useSelector } from 'react-redux'
import { useNavigate } from 'react-router-dom'
import * as React from 'react'
import { useEffect, useState } from 'react'
import axios from 'axios'

function AdminProfanityComponent () {
  const updateProfanityUrl = `${process.env.REACT_APP_URL}/manage/admin/profanity/update`
  let navigate = useNavigate()

  const getAdminProfanityUrl = `${process.env.REACT_APP_URL}/manage/admin/views/profanity`
  let profaninityArray = []
  const [showModal, setShowModal] = useState(false)
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

  async function FetchDefaultProfanityList () {
    let listVal = []
    await axios
      .get(getAdminProfanityUrl)
      .then((res) => {
        listVal = res.data
      })
      .catch((e) => {
        console.error(e)
        alert('Error getting global profanity list')
      })
    return listVal
  }

  useEffect(() => {
    FetchDefaultProfanityList().then((result) => {
        profaninityArray = result
        setTextBoxInput(initialValueSet)
      }
    )
  }, [])

  //modal object
  const Modal = () => {
    return (
      <div id="modal" style={{ position: 'fixed' }}>
        <div id="modalContent" style={{
          height: '40%',
          width: '45%'
        }}>
          <svg className="cross" style={{}} onClick={async () => {
            window.location.reload()
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
    <div className="ecc-form" style={{ padding: '0%', borderRadius: '0px' }}>
      <div className="course-profanity-details">
        <p style={{ paddingTop: '0%' }}>Please enter words that you want to include in the global profanity checking
          list.</p>
        <p><i>(New words to be separated by a line, press "Enter" to add new words and "Delete" to remove)</i></p>
        <div className="ecc-input-field">
          <form onSubmit={handleSubmit}>
                      <textarea value={textboxInput.profanityText} onChange={handleTextBoxChange} name="blockedWords"
                                required/>
          </form>
          <div id="ecc-button-container">
            <form onSubmit={handleSubmit} style={{ marginTop: '2%' }}>
              <button className="ecc-button" type="button"
                      onClick={() => handleSubmit()}>Save
                Changes
              </button>
            </form>
          </div>
        </div>

      </div>
      <div>{showModal ? Modal() : null}</div>
    </div>
  )
}

export default AdminProfanityComponent