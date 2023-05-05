import { useState, useEffect } from 'react'
import './styles/CreateAssignmentStyle.css'
import SidebarComponent from '../../components/SidebarComponent'
import { useNavigate, useParams } from 'react-router-dom'
import axios from 'axios'
import { Field, Form } from 'react-final-form'
import CourseBarComponent from '../../components/CourseBarComponent'
import Loader from '../../components/LoaderComponenets/Loader'
import * as React from 'react'
import HeaderBar from '../../components/HeaderBar/HeaderBar'
import {getCoursesAsync} from "../../redux/features/courseSlice";
import {useDispatch} from "react-redux";
import NavigationContainerComponent from "../../components/NavigationComponents/NavigationContainerComponent";
import Breadcrumbs from "../../components/Breadcrumbs";

const profAssignmentUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`
const assignmentFileFormData = new FormData()
let assignmentFileName = ''
const rubricFileFormData = new FormData()
let rubricFileName = ''
const templateFileFormData = new FormData()
let templateFileName = ''

const CreateAssignmentPage = () => {
  let navigate = useNavigate()
  let { courseId } = useParams()
  const dispatch = useDispatch();

  const submitCourseUrl = `${profAssignmentUrl}/create-assignment`
  const submitCourseNoPeerReviewURL = `${profAssignmentUrl}/create-assignment-no-peer-review`
  const getAssUrl = `${profAssignmentUrl}/${courseId}/assignments`
  const [loading, setLoading] = useState(false)
  const [checked, setChecked] = React.useState(false)
  const handleChangeInCheckBox = () => {
    setChecked(!checked)
  }
  const fileChangeHandler = (event, fileType) => {
    let file = event.target.files[0]
    var reader = new FileReader()
    reader.onloadend = () => {
      // Use a regex to remove data url part
      const base64String = reader.result
        .replace('data:', '')
        .replace(/^.+,/, '')
      if (fileType === 'assignment') {
        for (var key of assignmentFileFormData.keys()) {
          // here you can add filtering conditions
          assignmentFileFormData.delete(key)
        }
        assignmentFileName = file.name
        assignmentFileFormData.set(file.name, base64String)
        //console.log(assignmentFileFormData.keys().next())
      } else if (fileType === 'rubric') {
        for (var key of rubricFileFormData.keys()) {
          // here you can add filtering conditions
          rubricFileFormData.delete(key)
        }
        rubricFileName = file.name
        rubricFileFormData.set(file.name, base64String)
      } else {
        for (var key of rubricFileFormData.keys()) {
          // here you can add filtering conditions
          templateFileFormData.delete(key)
        }
        templateFileName = file.name
        templateFileFormData.set(file.name, base64String)
      }
    }
    reader.readAsDataURL(file)
  }
  useEffect(() => {
    dispatch(getCoursesAsync());
  }, [dispatch]);

  const uploadFiles = async (assignmentId) => {
    const assignmentFileUrl = `${getAssUrl}/${assignmentId}/upload`
    const rubricUrl = `${getAssUrl}/${assignmentId}/peer-review/rubric/upload`
    const templateUrl = `${getAssUrl}/${assignmentId}/peer-review/template/upload`

    await axios
      .post(assignmentFileUrl, assignmentFileFormData)
      .then((res) => {})
      .catch((e) => {
        console.error(e)
        alert('Error uploading assignment file.')
      })

    await axios
      .post(rubricUrl, rubricFileFormData)
      .then((res) => {})
      .catch((e) => {
        console.error(e)
        alert('Error uploading peer review rubric.')
      })

    await axios
      .post(templateUrl, templateFileFormData)
      .then((res) => {})
      .catch((e) => {
        console.error(e)
        alert('Error uploading peer review template.')
      })
  }

  const handleSubmit = async (data) => {
    if (checked) {
      let due_date = new Date(data['due_date']).getTime()
      let peer_review_due_date = new Date(data['peer_review_due_date']).getTime()
      if (due_date >= peer_review_due_date) {
        alert('Peer Review Due Date CANNOT be due before the due date of the Assignment!')
        return
      } else {

        let { points } = data
        points = parseInt(points)
        const course_id = courseId

        setLoading(true)

        const sentData = { ...data, points, course_id }

        await axios
          .post(submitCourseUrl, sentData)
          .then((res) => {
          })
          .catch((e) => {
            console.error(e.response.data)
          })
        const assignment_id = await axios
          .get(getAssUrl)
          .then((res) => {
            return res.data.pop().assignment_id
          })
          .catch((e) => {
            console.error(e.response.data)
          })
        await uploadFiles(assignment_id)
      }
    } else {
      //remove keys related to peer review data if it is unchecked and prepare to create assignment with no peer review
      delete data['peer_review_instructions']
      delete data['peer_review_due_date']
      delete data['peer_review_points']

      //now that any previously existing peer review data is gone, upload the data as normal
      let { points } = data
      points = parseInt(points)
      const course_id = courseId

      setLoading(true)

      const sentData = { ...data, points, course_id }

      await axios
        .post(submitCourseNoPeerReviewURL, sentData)
        .then((res) => {
        })
        .catch((e) => {
          console.error(e.response.data)
        })
      const assignment_id = await axios
        .get(getAssUrl)
        .then((res) => {
          return res.data.pop().assignment_id
        })
        .catch((e) => {
          console.error(e.response.data)
        })

      const assignmentFileUrl = `${getAssUrl}/${assignment_id}/upload`

      await axios
        .post(assignmentFileUrl, assignmentFileFormData)
        .then((res) => {})
        .catch((e) => {
          console.error(e)
          alert('Error uploading assignment file.')
        })

    }

    setLoading(false)
    navigate('/professor/' + courseId)
  }

  function PeerReviewComponent () {
    if (checked) {
      return (
        <div className="field-container-alt">
          <div className="field-title" style={{ padding: '15px 0', marginLeft: '3%' }}>
            {' '}
            <span className="inter-20-medium-white"> Peer Review </span>{' '}
          </div>
          <div className="field-content">
            <div className="input-field cap-instructions">
              <label className="inter-20-medium">
                {' '}
                <span className="required">
                                Instructions:
                                </span>{' '}
              </label>
              <Field name="peer_review_instructions">
                {({ input }) => (
                  <textarea
                    name="peer_review_instructions"
                    {...input}
                    required
                  />
                )}
              </Field>
            </div>

            <div className="cap-assignment-info-group">
              <div className="cap-assignment-info">
                <label className="inter-20-medium">
                                  <span className="required">
                                    Due Date:
                                  </span>
                </label>
                <Field name="peer_review_due_date">
                  {({ input }) => (
                    <input
                      type="date"
                      name="peer_review_due_date"
                      {...input}
                      required
                      min={new Date().toISOString().split('T')[0]}
                      style={{ width: '100%' }}
                    />
                  )}
                </Field>
              </div>

              <div className="cap-assignment-info">
                <label className="inter-20-medium">
                                  <span className="required">
                                    Points:
                                  </span>
                </label>
                <Field name="peer_review_points">
                  {({ input }) => (
                    <input
                      type="number"
                      min="0"
                      name="peer_review_points"
                      {...input}
                      required
                      onWheel={(e) => e.target.blur()}
                      style={{ width: '100%' }}
                    />
                  )}
                </Field>
              </div>
            </div>

            <div className="cap-assignment-files">
              <label className="inter-20-medium">
                                <span className="required">
                                  Rubric:
                                </span>
              </label>
              <input
                type="file"
                name="peer_review_rubric"
                accept=".pdf,.zip,.docx"
                required
                onChange={(e) => fileChangeHandler(e, 'rubric')}
              />

              <label className="inter-20-medium">
                                <span className="required">
                                  Template:
                                </span>
              </label>
              <input
                type="file"
                name="peer_review_template"
                accept=".pdf,.zip,.docx"
                required
                onChange={(e) => fileChangeHandler(e, 'template')}
              />
            </div>
          </div>
        </div>
      )
    } else {
      return <div></div>
    }
  }

  return (
    <div>
      {loading ? (
        <Loader/>
      ) : (
        <Form
          onSubmit={async (formObj) => {
            await handleSubmit(formObj)
          }}
        >
          {({ handleSubmit }) => (
            <div className="page-container">
              <HeaderBar/>
              <div className="pcp-parent">
                <div className="ccp-container">
                  <NavigationContainerComponent />
                  <div className="pcp-components">
                    <Breadcrumbs />
                    <h2 className="inter-28-bold"> New Assignment </h2>
                    <div className="cap-form">
                      <form onSubmit={handleSubmit}>
                        {/*assignment field*/}
                        <div className="field-container">
                          <div className="field-title" style={{ padding: '15px 0', marginLeft: '3%' }}>
                            {' '}
                            <span className="inter-20-medium-white"> Assignment </span>{' '}
                          </div>
                          <div className="field-content">
                            <div className="input-field cap-input-field">
                              <label className="inter-20-medium">
                                <span className="required">
                                  Name of Assignment:
                                </span>
                              </label>
                              <Field name="assignment_name">
                                {({ input }) => (
                                  <input
                                    type="text"
                                    name="assignment_name"
                                    {...input}
                                    required
                                    style={{ width: '75%', height: '43px' }}
                                  />
                                )}
                              </Field>
                            </div>

                            <div className="input-field cap-instructions">
                              <label className="inter-20-medium">
                                <span className="required">
                                  Instructions:
                                </span>
                              </label>
                              <Field name="instructions">
                                {({ input }) => (
                                  <textarea
                                    name="instructions"
                                    {...input}
                                    required
                                  />
                                )}
                              </Field>
                            </div>

                            <div className="cap-assignment-info-group">
                              <div className="cap-assignment-info">
                                <label className="inter-20-medium">
                                  <span className="required">
                                    Due Date:
                                  </span>
                                </label>
                                <Field name="due_date">
                                  {({ input }) => (
                                    <input
                                      type="date"
                                      name="due_date"
                                      {...input}
                                      required
                                      min={new Date().toISOString().split('T')[0]}
                                      style={{ width: '100%' }}
                                    />
                                  )}
                                </Field>
                              </div>

                              <div className="cap-assignment-info">
                                <label className="inter-20-medium">
                                  <span className="required">
                                    Points:
                                  </span>
                                </label>
                                <Field name="points">
                                  {({ input }) => (
                                    <input
                                      defaultValue={0}
                                      min={0}
                                      type="number"
                                      name="points"
                                      {...input}
                                      required
                                      onWheel={(e) => e.target.blur()}
                                      style={{ width: '100%' }}
                                    />
                                  )}
                                </Field>
                              </div>
                            </div>

                            <div className="cap-assignment-files">
                              <label className="inter-20-medium">
                                <span className="required">
                                  Files:
                                </span>
                              </label>
                              <input
                                type="file"
                                name="assignment_files"
                                accept=".pdf,.zip,.docx"
                                required
                                onChange={(e) =>
                                  fileChangeHandler(e, 'assignment')
                                }
                              />
                            </div>
                          </div>
                        </div>

                        <div style={{ paddingBottom: 50 }}>
                          <label className="inter-20-medium">
                            <input
                              type="checkbox"
                              checked={checked}
                              onChange={handleChangeInCheckBox}
                            />
                            Include Peer Review Data
                          </label>
                        </div>

                        {/*peer review fields*/}
                        <PeerReviewComponent></PeerReviewComponent>

                        <div>
                          <label className="inter-20-medium">
                      <span className="required-alt">
                        Indicates Required Field
                      </span>
                          </label>
                        </div>

                        <div className="cap-button">
                          <button className="green-button-large" type="submit" style={{marginRight:'3vw'}}>
                            {' '}
                            Create Assignment{' '}
                          </button>
                          <button className='cancel-button' onClick={() => {navigate(`/professor/${courseId}`)}}>
                            Cancel
                          </button>
                        </div>
                      </form>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}
        </Form>
      )}
    </div>
  )
}

export default CreateAssignmentPage
