import { Field, Form } from 'react-final-form'
import { useEffect } from 'react'
import * as React from 'react'
import { useDispatch, useSelector, useState } from 'react-redux'
import {
  getAssignmentDetailsAsync, getCourseAssignmentsAsync,
} from '../../../redux/features/assignmentSlice'
import { redirect, useNavigate, useParams } from 'react-router-dom'
import '../../styles/EditAssignmentStyle.css'
import '../../styles/AssignmentTile.css'
import axios from 'axios'
import '../../../global_styles/RequiredField.css'

const profAssignmentUrl = `${process.env.REACT_APP_URL}/assignments/professor/courses`
const assignmentFileFormData = new FormData()
let assignmentFileName = ''
const rubricFileFormData = new FormData()
let rubricFileName = ''
const templateFileFormData = new FormData()
let templateFileName = ''

const ProfessorEditAssignmentComponent = () => {
  let navigate = useNavigate()
  const [checked, setChecked] = React.useState(false)
  const initialCheckBoxState = () => {
    if (alreadySetInitialCheckState) {
      return checked
    }
    if (currentAssignmentLoaded) {
      // console.log(currentAssignment)
      setChecked(currentAssignment.has_peer_review)
      if (!alreadySetInitialCheckState) {
        setAlreadySetInitialCheckState(true)
      }
      return checked
    }
    setChecked(false)
    return checked
  }

  const handleChangeInCheckBox = () => {
    if (currentAssignment.has_peer_review) {
      alert('Cannot remove peer review information once submitted!')
      return
    }
    setChecked(!checked)
  }

  const dispatch = useDispatch()
  const { courseId, assignmentId } = useParams()
  const { currentAssignment, currentAssignmentLoaded } = useSelector((state) => state.assignments)

  const [alreadySetInitialCheckState, setAlreadySetInitialCheckState] = React.useState(false)
  const getAssUrl = `${profAssignmentUrl}/${courseId}/assignments`

  useEffect(() => {
    dispatch(getAssignmentDetailsAsync({ courseId, assignmentId }))
    // console.log(checked)
  }, [courseId, assignmentId, dispatch])

  const confirmDelete = async () => {
    let confirmAction = window.confirm(
      'Are you sure to delete this assignment?'
    )
    if (confirmAction) {
      await deleteAssignment()
    }
  }

  const deleteAssignment = async () => {
    const url = `${profAssignmentUrl}/${courseId}/assignments/${currentAssignment.assignment_id}/remove`
    await axios.delete(url)
    //reroute to the assignments homepage
    navigate(-1)

    alert('Assignment successfully deleted.')
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

  const handleSubmit = async (formObj) => {
    //do the update as normal if the assignment alread had peer review data
    const assignmentFileUrl = `${getAssUrl}/${assignmentId}/upload`
    const rubricUrl = `${getAssUrl}/${assignmentId}/peer-review/rubric/upload`
    const templateUrl = `${getAssUrl}/${assignmentId}/peer-review/template/upload`
    if (currentAssignment.has_peer_review) {
      const editUrl = `${getAssUrl}/${assignmentId}/edit`

      if (JSON.stringify(() => initialValue()) === JSON.stringify(formObj)) {
        alert('Nothing to save!')
        return
      }

      let due_date = new Date(formObj['due_date']).getTime()
      let peer_review_due_date = new Date(formObj['peer_review_due_date']).getTime()
      if (due_date >= peer_review_due_date) {
        alert('Peer Review Due Date CANNOT be due before the due date of the Assignment!')
        return
      }

      await axios.put(editUrl, { ...formObj, course_id: courseId }).catch((e) => {
        console.error(e.response)
      })

      await submitNewFiles()
      dispatch(getCourseAssignmentsAsync(courseId))
      dispatch(getAssignmentDetailsAsync({ courseId, assignmentId }))
      alert('Successfully updated assignment!')
    } else if (!checked) {
      //if the box is not checked, we will update the assignment without peer review data
      const editUrl = `${getAssUrl}/${assignmentId}/editNoPeerReview`
      delete formObj['peer_review_instructions']
      delete formObj['peer_review_due_date']
      delete formObj['peer_review_points']
      // console.log(formObj)
      //update the assignment data
      await axios.put(editUrl, { ...formObj, course_id: courseId }).catch((e) => {
        console.error(e.response)
      })
      //only submit the assignment instruction file (if any changes were even made)
      if (assignmentFileFormData.get(assignmentFileName)) {
        // console.log(assignmentFileFormData.get('file'))
        await axios.post(assignmentFileUrl, assignmentFileFormData).catch((e) => {
          console.error(e)
          alert('Error uploading assignment file.')
        })
      }
      dispatch(getCourseAssignmentsAsync(courseId))
      dispatch(getAssignmentDetailsAsync({ courseId, assignmentId }))
      alert('Successfully updated assignment!')
    } else {
      const editUrl = `${getAssUrl}/${assignmentId}/editNoPeerReview`
      const addUrl = `${getAssUrl}/${assignmentId}/addPeerReviewData`
      //the box is checked and there was no peer review data previously,
      //save all the peer review data that exists to a separate json object
      let due_date = new Date(formObj['due_date']).getTime()
      let peer_review_due_date = new Date(formObj['peer_review_due_date']).getTime()
      if (due_date >= peer_review_due_date) {
        alert('Peer Review Due Date CANNOT be due before the due date of the Assignment!')
        return
      }

      var peerReviewData = {}
      peerReviewData['peer_review_instructions'] = formObj['peer_review_instructions']
      peerReviewData['peer_review_due_date'] = formObj['peer_review_due_date']
      peerReviewData['peer_review_points'] = formObj['peer_review_points']

      //now delete it from the previous formObj and update the regular assignment info if needed
      delete formObj['peer_review_instructions']
      delete formObj['peer_review_due_date']
      delete formObj['peer_review_points']
      await axios.put(editUrl, { ...formObj, course_id: courseId }).catch((e) => {
        console.error(e.response)
      })
      //only submit the assignment instruction file (if any changes were even made)
      if (assignmentFileFormData.get(assignmentFileName)) {
        // console.log(assignmentFileFormData.get('file'))
        await axios.post(assignmentFileUrl, assignmentFileFormData).catch((e) => {
          console.error(e)
          alert('Error uploading assignment file.')
        })
      }
      //now update with the peer review info
      await axios.put(addUrl, { ...peerReviewData }).catch((e) => {
        console.error(e.response)
      })
      //add the files
      if (rubricFileFormData.get(rubricFileName)) {
        await axios.post(rubricUrl, rubricFileFormData).catch((e) => {
          console.error(e)
          alert('Error uploading peer review rubric.')
        })
      }

      if (templateFileFormData.get(templateFileName)) {
        await axios.post(templateUrl, templateFileFormData).catch((e) => {
          console.error(e)
          alert('Error uploading peer review template.')
        })
      }
      dispatch(getCourseAssignmentsAsync(courseId))
      dispatch(getAssignmentDetailsAsync({ courseId, assignmentId }))
      alert('Successfully updated assignment!')
    }
  }

  const submitNewFiles = async () => {
    const assignmentFileUrl = `${getAssUrl}/${assignmentId}/upload`
    const rubricUrl = `${getAssUrl}/${assignmentId}/peer-review/rubric/upload`
    const templateUrl = `${getAssUrl}/${assignmentId}/peer-review/template/upload`

    if (assignmentFileFormData.get(assignmentFileName)) {
      // console.log(assignmentFileFormData.get('file'))
      await axios.post(assignmentFileUrl, assignmentFileFormData).catch((e) => {
        console.error(e)
        alert('Error uploading assignment file.')
      })
    }

    if (rubricFileFormData.get(rubricFileName)) {
      await axios.post(rubricUrl, rubricFileFormData).catch((e) => {
        console.error(e)
        alert('Error uploading peer review rubric.')
      })
    }

    if (templateFileFormData.get(templateFileName)) {
      await axios.post(templateUrl, templateFileFormData).catch((e) => {
        console.error(e)
        alert('Error uploading peer review template.')
      })
    }
  }

  const deleteFile = async (fileName, isPeerReviewRubric, isPeerReviewTemplate) => {
    const url = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}/assignments/${assignmentId}`
    let deleteUrl = url
    if (isPeerReviewRubric) {
      deleteUrl = `${url}/peer-review/rubric/remove-file`
    } else if (isPeerReviewTemplate) {
      deleteUrl = `${url}/peer-review/template/remove-file`
    } else {
      deleteUrl = `${url}/remove-file`
    }

    await axios.delete(deleteUrl).catch((e) => {
      console.error(e)
    })

    dispatch(getAssignmentDetailsAsync({ courseId, assignmentId }))
  }

  const downloadFile = (blob, fileName) => {
    const fileURL = URL.createObjectURL(blob)
    const href = document.createElement('a')
    href.href = fileURL
    href.download = fileName
    href.click()
  }

  const onFileClick = async (fileName, isPeerReviewTemplate, isPeerReviewRubric) => {
    if (isPeerReviewTemplate) {
      if (fileName.endsWith('.pdf')) {
        downloadFile(new Blob([Uint8Array.from(currentAssignment.peer_review_template_data.data)], { type: 'application/pdf' }), fileName)
      } else if (fileName.endsWith('.docx')) {
        downloadFile(new Blob([Uint8Array.from(currentAssignment.peer_review_template_data.data)], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' }), fileName)
      } else {
        downloadFile(new Blob([Uint8Array.from(currentAssignment.peer_review_template_data.data)], { type: 'application/zip' }), fileName)
      }
    } else if (isPeerReviewRubric) {
      if (fileName.endsWith('.pdf')) {
        downloadFile(new Blob([Uint8Array.from(currentAssignment.rubric_data.data)], { type: 'application/pdf' }), fileName)
      } else if (fileName.endsWith('.docx')) {
        downloadFile(new Blob([Uint8Array.from(currentAssignment.rubric_data.data)], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' }), fileName)
      } else {
        downloadFile(new Blob([Uint8Array.from(currentAssignment.rubric_data.data)], { type: 'application/zip' }), fileName)
      }
    } else {
      if (fileName.endsWith('.pdf')) {
        downloadFile(new Blob([Uint8Array.from(currentAssignment.assignment_instructions_data.data)], { type: 'application/pdf' }), fileName)
      } else if (fileName.endsWith('.docx')) {
        downloadFile(new Blob([Uint8Array.from(currentAssignment.assignment_instructions_data.data)], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' }), fileName)
      } else {
        downloadFile(new Blob([Uint8Array.from(currentAssignment.assignment_instructions_data.data)], { type: 'application/zip' }), fileName)
      }
    }
  }

  function AssignmentFilesComponent () {
    if (currentAssignment.has_peer_review) {
      return (<div className="inter-20-medium-black eac-assignment-files-multiple">
          <div style={{ width: '100%' }}>
            <div
              className="eac-assignment-files"
              style={{ marginBottom: '8%' }}
            >
              Current rubric:
              <span
                className="eac-file-name"
                onClick={() => onFileClick(currentAssignment.rubric_name, false, true)}
              >
                        {currentAssignmentLoaded ? currentAssignment.rubric_name : null}
                        </span>
              <span
                onClick={() => deleteFile(currentAssignment.peer_review_rubric, true, false)}
                className={currentAssignmentLoaded && currentAssignment.peer_review_rubric !== '' ? 'eac-crossmark' : 'eac-crossmark-gone'}
              >
                        &#10060;
                        </span>
            </div>
            <div
              className="eac-assignment-files"
              style={{ marginBottom: '0' }}
            >
              <div style={{ display: 'table' }}>
                <label> New rubric: </label>
                <input
                  type="file"
                  name="peer_review_rubric"
                  accept=".pdf,.zip,.docx"
                  onChange={(e) => fileChangeHandler(e, 'rubric')}
                />
              </div>
            </div>
          </div>
          <div style={{ width: '100%' }}>
            <div
              className="eac-assignment-files"
              style={{ marginBottom: '8%' }}
            >
              Current template:
              <span
                className="eac-file-name"
                onClick={() => onFileClick(currentAssignment.peer_review_template_name, true, false)}
              >
                  {currentAssignmentLoaded ? currentAssignment.peer_review_template_name : null}
                  </span>
              <span
                onClick={() => deleteFile(currentAssignment.peer_review_template, false, true)}
                className={currentAssignmentLoaded && currentAssignment.peer_review_template !== '' ? 'eac-crossmark' : 'eac-crossmark-gone'}
              >
                  &#10060;
                  </span>
            </div>
            <div
              className="eac-assignment-files"
              style={{ marginBottom: '0' }}
            >
              <div style={{ display: 'table' }}>
                <label> New template: </label>
                <input
                  type="file"
                  name="peer_review_template"
                  accept=".pdf,.zip..docx"
                  onChange={(e) => fileChangeHandler(e, 'template')}
                />
              </div>
            </div>
          </div>
        </div>
      )
    } else {
      return (
        <div className="inter-20-medium-black eac-assignment-files-multiple">
          <div style={{ width: '100%' }}>
            <div
              className="eac-assignment-files"
              style={{ marginBottom: '0' }}
            >
              <div style={{ display: 'table' }}>
                <label> New rubric: </label>
                <input
                  type="file"
                  name="peer_review_rubric"
                  accept=".pdf,.zip,.docx"
                  required
                  onChange={(e) => fileChangeHandler(e, 'rubric')}
                />
              </div>
            </div>
          </div>
          <div style={{ width: '100%' }}>
            <div
              className="eac-assignment-files"
              style={{ marginBottom: '0' }}
            >
              <div style={{ display: 'table' }}>
                <label> New template: </label>
                <input
                  type="file"
                  name="peer_review_template"
                  accept=".pdf,.zip..docx"
                  required
                  onChange={(e) => fileChangeHandler(e, 'template')}
                />
              </div>
            </div>
          </div>
        </div>
      )

    }
  }

  function AssignmentData () {
    return (<div
      className={'ass-tile'}
      style={{ marginLeft: 0, marginRight: 0 }}
    >
      <div className="inter-20-medium-white ass-tile-title">
        {' '}
        <span>Assignment</span>
      </div>
      <div className="ass-tile-content" style={{ padding: '30px', boxSizing: 'border-box', cursor: 'default' }}>
        <div className="ass-tile-info" style={{ display: 'block', boxSizing: 'border-box' }}>
          <div className="inter-20-medium-black eac-input-field">
            <label className="inter-20-medium">
              <span className="required">
                Name of Assignment:
              </span>
            </label>
            <Field name="assignment_name">
              {({ input }) => (<input
                className="inter-16-medium-black"
                type="text"
                name="assignment_name"
                {...input}
                required
                style={{ width: '75%', height: '43px', fontSize: 16, paddingLeft: 16 }}
              />)}
            </Field>
          </div>
          <span className="inter-20-medium" style={{ boxSizing: 'border-box' }}>
               <div>
                  <div className="inter-20-medium-black eac-instructions">
                    <label className="required"> Instructions: </label>
                    <Field name="instructions" >
                    {({ input }) => (<textarea name="instructions" className="inter-16-medium-black" {...input} required/>)}
                     </Field>
                  </div>
                 <div className="eac-assignment-info-group">
                  <div className="inter-20-medium-black eac-assignment-info">
                     <label className="required">Due Date:</label>
                    <Field name="due_date">
                      {({ input }) => (<input
                        className="inter-16-medium-black"
                        type="date"
                        name="due_date"
                        {...input}
                        required
                        min={new Date().toISOString().split('T')[0]}
                        style={{ width: '100%', paddingLeft: 16 }}
                      />)}
                    </Field>
                  </div>

                  <div className="inter-20-medium-black eac-assignment-info">
                    <label className="required"> Points: </label>
                    <Field name="points">
                    {({ input }) => (<input
                      className="inter-16-medium-black"
                      type="number"
                      name="points"
                      {...input}
                      required
                      onWheel={(e) => e.target.blur()}
                      style={{ width: '100%', paddingLeft: 16 }}
                    />)}
                    </Field>
                  </div>
                 </div>
               <div style={{ width: '100%' }}>
                  <div
                    className="eac-assignment-files"
                    style={{ marginBottom: '8%' }}
                  >
                        Current files:
                      <span
                        className="eac-file-name"
                        onClick={() => onFileClick(currentAssignment.assignment_instructions_name, false, false)}
                      >
                            {currentAssignmentLoaded ? currentAssignment.assignment_instructions_name : null}
                          </span>
                      <span
                        onClick={() => deleteFile(currentAssignment.assignment_instructions, false, false)}
                        className={currentAssignmentLoaded && currentAssignment.assignment_instructions !== '' ? 'eac-crossmark' : 'eac-crossmark-gone'}
                      >
                            &#10060;
                          </span>
                     </div>
                     <div className="inter-20-medium-black eac-assignment-files">
                       <div style={{ display: 'table' }}>
                        <label> New files: </label>
                        <input
                          type="file"
                          name="assignment_files"
                          accept=".pdf,.zip,.docx"
                          onChange={(e) => fileChangeHandler(e, 'assignment')}
                        />
                     </div>
                    </div>
               </div>
</div>
</span>
        </div>
      </div>
    </div>)
  }

  function PeerReviewData () {
    if (checked) {
      return (<div
        className={'ass-tile-alt'}
        style={{ marginLeft: 0, marginRight: 0 }}
      >
        <div className="inter-20-medium-white ass-tile-alt-title">
          {' '}
          <span> Peer Review</span>
        </div>
        <div className="ass-tile-content" style={{ padding: '30px', cursor: 'default' }}>
          <div className="ass-tile-info" style={{ display: 'block' }}>
            <span className="inter-20-medium">
               <div>
                  <div className="inter-20-medium-black eac-instructions">
                     <label className="required"> Peer Review Instructions: </label>
                     <Field name="peer_review_instructions">
                        {({ input }) => (<textarea
                          name="peer_review_instructions"
                          {...input}
                          required
                        />)}
                     </Field>
                  </div>
                 <div className="eac-assignment-info-group">
                  <div className="inter-20-medium-black eac-assignment-info">
                     <label className="required"> Due Date: </label>
                     <Field name="peer_review_due_date">
                        {({ input }) => (<input
                          type="date"
                          name="peer_review_due_date"
                          {...input}
                          required
                          min={new Date().toISOString().split('T')[0]}
                          style={{ width: '100%' }}
                        />)}
                     </Field>
                  </div>
                 <div className="inter-20-medium-black eac-assignment-info">
                     <label className="required"> Points: </label>
                     <Field name="peer_review_points">
                        {({ input }) => (<input
                          type="number"
                          min="0"
                          name="peer_review_points"
                          {...input}
                          required
                          onWheel={(e) => e.target.blur()}
                          style={{ width: '100%' }}
                        />)}
                     </Field>
                  </div>
                 </div>
                 <AssignmentFilesComponent></AssignmentFilesComponent>
</div>
</span>
          </div>
        </div>
      </div>)
    } else {
      return <div></div>
    }
  }

  const initialValue = () => {
    if (currentAssignmentLoaded) {
      if (checked) {
        return {
          assignment_name: currentAssignment.assignment_name,
          instructions: currentAssignment.instructions,
          due_date: currentAssignment.due_date,
          points: currentAssignment.points,
          peer_review_instructions: currentAssignment.peer_review_instructions,
          peer_review_due_date: currentAssignment.peer_review_due_date,
          peer_review_points: currentAssignment.peer_review_points,
        }
      } else {
        return {
          assignment_name: currentAssignment.assignment_name,
          instructions: currentAssignment.instructions,
          due_date: currentAssignment.due_date,
          points: currentAssignment.points,
        }
      }
    }
  }

  return (<div className="eac-form">
    <Form
      onSubmit={async (formObj) => {
        await handleSubmit(formObj)
      }}
      initialValues={() => initialValue()}
    >
      {({ handleSubmit }) => (<form onSubmit={handleSubmit}>
        <AssignmentData></AssignmentData>
        <div style={{ paddingBottom: 50 }}>
          <label className="inter-20-medium">
            <input
              type="checkbox"
              // defaultChecked={currentAssignmentLoaded ? initialCheckBoxState() : checked}
              checked={currentAssignmentLoaded ? initialCheckBoxState() : checked}
              onChange={handleChangeInCheckBox}
            />
            Include Peer Review Data
          </label>
        </div>

        <PeerReviewData></PeerReviewData>
        <div>
          <label className="inter-20-medium">
                      <span className="required-alt">
                        Indicates Required Field
                      </span>
          </label>
        </div>

        <div className="button-group">
          <div className="cap-button">
            <button className="green-button-large" type="submit"> Save Changes</button>
          </div>

          <button className="red-button-large" type="button" onClick={confirmDelete}>Delete Assignment</button>
        </div>
      </form>)}
    </Form>
  </div>)
}

export default ProfessorEditAssignmentComponent