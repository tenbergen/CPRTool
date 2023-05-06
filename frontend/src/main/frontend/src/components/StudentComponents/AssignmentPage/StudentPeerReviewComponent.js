import { useDispatch, useSelector } from 'react-redux'
import axios from 'axios'
import '../../../pages/StudentPages/styles/AssignmentPageStyle.css'
import { useNavigate, useParams } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { getAssignmentDetailsAsync } from '../../../redux/features/assignmentSlice'
import { base64StringToBlob } from 'blob-util'

const StudentPeerReviewComponent = () => {
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const { courseId, assignmentId, teamId } = useParams()

  const { currentAssignment, currentAssignmentLoaded } = useSelector(
    (state) => state.assignments
  )
  const { currentTeamId, teamLoaded } = useSelector((state) => state.teams)

  const [grade, setGrade] = useState(undefined)
  const [feedbackFileFormData, setFeedbackFileFormData] = useState(new FormData())

  const onFeedbackFileHandler = (event) => {
    let file = event.target.files[0];
    const reader = new FileReader();
    reader.onloadend = () => {
      // Use a regex to remove data url part
      const base64String = reader.result
        .replace('data:', '')
        .replace(/^.+,/, '');
      for(var pair of feedbackFileFormData.entries()){
        feedbackFileFormData.delete(pair[0])
      }
      feedbackFileFormData.append(file.name, base64String);
    };
    reader.readAsDataURL(file);
  }

    useEffect(() => {
      dispatch(getAssignmentDetailsAsync({ courseId, assignmentId }))
    }, [courseId, assignmentId, dispatch])

    const prepareTeamFile = (teamDataName, teamData) => {
      var filename = ''
      var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/
      var matches = filenameRegex.exec(teamDataName)
      if (matches != null && matches[1]) {
        filename = matches[1].replace(/['"]/g, '')
      }
      teamData.then((res) => {
        if (filename.endsWith('.pdf')) {
          downloadFile(base64StringToBlob(res, 'application/pdf'), filename)
        } else {
          downloadFile(base64StringToBlob(res, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'), filename)
        }
      })
    }

    const downloadFile = (blob, fileName) => {
      const fileURL = URL.createObjectURL(blob)
      const href = document.createElement('a')
      href.href = fileURL
      href.download = fileName
      href.click()
    }

    const onTemplateClick = async (fileName) => {
      if (fileName.endsWith('.pdf')) {
        downloadFile(new Blob([Uint8Array.from(currentAssignment.peer_review_template_data.data)], { type: 'application/pdf' }), fileName)
      } else if (fileName.endsWith('.docx')) {
        downloadFile(new Blob([Uint8Array.from(currentAssignment.peer_review_template_data.data)], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' }), fileName)
      } else {
        downloadFile(new Blob([Uint8Array.from(currentAssignment.peer_review_template_data.data)], { type: 'application/zip' }), fileName)
      }
    }

    const onRubricFileClick = async (fileName) => {
      if (fileName.endsWith('.pdf')) {
        downloadFile(new Blob([Uint8Array.from(currentAssignment.rubric_data.data)], { type: 'application/pdf' }), fileName)
      } else if (fileName.endsWith('.docx')) {
        downloadFile(new Blob([Uint8Array.from(currentAssignment.rubric_data.data)], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' }), fileName)
      } else {
        downloadFile(new Blob([Uint8Array.from(currentAssignment.rubric_data.data)], { type: 'application/zip' }), fileName)
      }
    }

    const onTeamFileClick = async () => {
      const url = `${process.env.REACT_APP_URL}/assignments/student/courses/${courseId}/assignments/${assignmentId}/${teamId}/download`

      await axios
        .get(url, { responseType: 'blob' })
        .then((res) => prepareTeamFile(res['headers']['content-disposition'], res.data.text()))
    }

    const handleSubmit = async () => {
      const submitAssUrl = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/${currentTeamId}/${teamId}/${grade}/upload`
      await axios
        .post(submitAssUrl, feedbackFileFormData)
        .then((res) => {
          alert('Successfully uploaded peer review')
          navigate(`/student/${courseId}`, {
            state: { initialComponent: 'Submitted' },
          })
        })
        .catch((e) => {
          console.error(e.response)
          alert('Error uploading peer review')
        })
      setGrade(undefined)
    }

    return (
      <div>
        {currentAssignmentLoaded && teamLoaded ? (
          <div>
            <h2 className="inter-28-bold">Team {teamId} Peer Review</h2>
            <div className="ap-assignmentArea">
              <h3>
                <span className="outfit-25"> Instructions: </span>
                <span className="outfit-25 span1-ap">
                Due: {currentAssignment.peer_review_due_date}
              </span>
                <br/>
                <p className="inter-20-bold">
                  {currentAssignment.peer_review_instructions}
                </p>
                <br/>

                <span className="inter-20-bold"> Rubric: </span>
                <span
                  className="inter-18-medium"
                  onClick={() =>
                    onRubricFileClick(currentAssignment.rubric_name)
                  }
                >
                {currentAssignment.rubric_name}
              </span>
                <br/>
                <br/>

                <span className="inter-20-bold"> Template: </span>
                <span
                  className="inter-18-medium"
                  onClick={() =>
                    onTemplateClick(currentAssignment.peer_review_template_name)
                  }
                >
                {currentAssignment.peer_review_template_name}
              </span>
                <br/>
                <br/>

                <span className="inter-20-bold"> Team Files: </span>
                <span className="inter-18-medium-blue" onClick={onTeamFileClick}>
                {teamId} Files
              </span>
                <br/>
                <br/>
                <br/>

                <div className="input-field">
                  <label className="inter-20-bold"> Grade: </label>
                  <input
                    type="number"
                    min="0"
                    name="peer_review_grade"
                    value={grade}
                    required
                    onChange={(e) => setGrade(e.target.value)}
                  />
                </div>
                <br/>

                <div className="ap-assignment-files" style={{display: 'flex', flexDirection: 'row', alignItems: 'center'}}>
                  <label className="inter-20-bold" style={{marginRight: '12px', marginBottom: '0'}}> Feedback: </label>
                  <input
                    type="file"
                    name="assignment_files"
                    accept=".pdf,.docx"
                    onChange={onFeedbackFileHandler}
                    required
                  />
                </div>
                <div className="ap-button">
                  <div className="ap-button">
                    <button className="green-button-large" onClick={handleSubmit}>
                      {' '}
                      Submit
                    </button>
                  </div>
                </div>
              </h3>
            </div>
          </div>
        ) : null}
      </div>
    )
  }
export default StudentPeerReviewComponent
