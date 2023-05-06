import { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import '../../styles/TeamSubmission.css'
import { useParams } from 'react-router-dom'
import axios from 'axios'
import { getSubmittedAssignmentsAsync } from '../../../redux/features/submittedAssignmentSlice'
import AssignmentTile from '../../AssignmentTile'
import uuid from 'react-uuid'
import '../../ProfessorComponents/AssignmentPage/styles/ProfessorAllSubmissionsStyle.css'
import { base64StringToBlob } from 'blob-util'
import { getAssignmentDetailsAsync, getCourseAssignmentsAsync } from '../../../redux/features/assignmentSlice'

const ProfessorAllSubmissionsComponent = () => {
  const dispatch = useDispatch()
  const { courseId, assignmentId } = useParams()
  const { courseSubmittedAssignments, assignmentsLoaded } = useSelector(
    (state) => state.submittedAssignments
  )
  const { currentAssignment, currentAssignmentLoaded } = useSelector((state) => state.assignments)
  const [assignedTeamCount, setAssignedTeamCount] = useState()

  useEffect(() => {
    async function fetchData () {
      dispatch(getSubmittedAssignmentsAsync({ courseId, assignmentId }))
      dispatch(getAssignmentDetailsAsync({ courseId, assignmentId }))
    }

    fetchData()
  }, [dispatch, courseId, assignmentId])

  const distribute = async () => {
    if (!currentAssignment.has_peer_review) {
      alert('No peer review instructions have been given yet!')
      return
    }
    const url = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/assign/${assignedTeamCount}`
    await axios
      .get(url)
      .then((res) => {
        alert('Assignments successfully distributed for peer review!')
      })
      .catch((e) => {
        console.error(e.response.data)
        alert(
          `Error: Teams for this assignment cannot review more than ${
            courseSubmittedAssignments.length - 1
          } team(s).`
        )
      })
    setAssignedTeamCount(0)
  }

  const onSubmitClick = async () => {
    const url = `${process.env.REACT_APP_URL}/assignments/student/${assignmentId}/${courseId}/course-assignment-files`

    await axios
      .get(url, { responseType: 'blob' })
      .then((res) => prepareSubmissionFile(res['headers']['content-disposition'], res.data.text()))
  }

  const prepareSubmissionFile = (teamDataName, teamData) => {
    var filename = ''
    var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/
    var matches = filenameRegex.exec(teamDataName)
    if (matches != null && matches[1]) {
      filename = matches[1].replace(/['"]/g, '')
    }
    teamData.then((res) => {
      downloadFile(base64StringToBlob(res, 'application/zip'), filename)
    })
  }

  const downloadFile = (blob, fileName) => {
    const fileURL = URL.createObjectURL(blob)
    const href = document.createElement('a')
    href.href = fileURL
    href.download = fileName
    href.click()
  }

  return (
    <div>
      {assignmentsLoaded ? (
        <div className="psc-container">
          <div id="assList">
            {courseSubmittedAssignments.map(
              (assignment) =>
                assignment && (
                  <AssignmentTile
                    key={uuid()}
                    assignment={assignment}
                    submitted={true}
                  />
                )
            )}
          </div>
          <div className="row-multiple" style={{ justifyContent: 'space-between', padding: '2% 4%' }}>
            <div
              className="input-field"
              style={{
                marginBottom: '5%',
                justifyContent: 'center',
                alignItems: 'center',
                textAlign: 'center',
              }}
            >
              <label className="inter-20-medium" style={{ display: 'block', marginBottom: '3%' }}> Number of
                peer reviews to distribute to each team: </label>
              <input
                type="number"
                value={assignedTeamCount}
                onChange={(e) => setAssignedTeamCount(e.target.value)}
                style={{
                  width: '76.91px',
                  height: '50px', display: 'inline-block',
                  marginRight: '4%'
                }}
              />
              <button
                id="distribute-button"
                style={{ padding: '2%', fontSize: '20px', width: '181.89px', display: 'inline-block' }}
                onClick={distribute}
              >
                Distribute{' '}
              </button>
            </div>
            <div className="inter-20-medium" style={{
              justifyContent: 'center',
              alignItems: 'center',
              textAlign: 'center',
              marginBottom: '5%',
            }}>
              <label style={{ display: 'block', marginBottom: '5%' }}> Bulk download
                for this
                assignment: </label>
              <span className="inter-16-bold-blue p2">
              <button className="blue-button-large" onClick={onSubmitClick}>
                <div style={{ display: 'inline-block', verticalAlign: 'middle', margin: '0 auto' }}>
                  <svg className={'bulk-download-white-icon-default'}
                       alt={'Bulk Download For Student'} style={{ display: 'inline-block', margin: '0 8px 0 0' }}></svg>
                  <span style={{ display: 'inline-block' }}>Download</span>
                </div>
              </button>
              </span>
            </div>
          </div>
        </div>
      ) : null}
    </div>
  )
}

export default ProfessorAllSubmissionsComponent
