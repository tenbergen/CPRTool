import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import '../../styles/TeamSubmission.css';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { getSubmittedAssignmentsAsync } from '../../../redux/features/submittedAssignmentSlice';
import AssignmentTile from '../../AssignmentTile';
import uuid from 'react-uuid';
import '../../ProfessorComponents/AssignmentPage/styles/ProfessorAllSubmissionsStyle.css'
import {base64StringToBlob} from "blob-util";

const ProfessorAllSubmissionsComponent = () => {
  const dispatch = useDispatch();
  const { courseId, assignmentId } = useParams();
  const { courseSubmittedAssignments, assignmentsLoaded } = useSelector(
    (state) => state.submittedAssignments
  );
  const [assignedTeamCount, setAssignedTeamCount] = useState();

  useEffect(() => {
    async function fetchData() {
      dispatch(getSubmittedAssignmentsAsync({ courseId, assignmentId }));
    }
    fetchData();
  }, [dispatch, courseId, assignmentId]);

  const distribute = async () => {
    const url = `${process.env.REACT_APP_URL}/peer-review/assignments/${courseId}/${assignmentId}/assign/${assignedTeamCount}`;
    await axios
      .get(url)
      .then((res) => {
        alert('Assignments successfully distributed for peer review!');
      })
      .catch((e) => {
        console.error(e.response.data);
        alert(
          `Error: Teams for this assignment cannot review more than ${
            courseSubmittedAssignments.length - 1
          } team(s).`
        );
      });
    setAssignedTeamCount(0);
  };

  const onSubmitClick = async () => {
    const url = `${process.env.REACT_APP_URL}/assignments/student/${assignmentId}/${courseId}/course-assignment-files`;

    await axios
        .get(url, { responseType: 'blob' })
        .then((res) => prepareSubmissionFile(res["headers"]["content-disposition"], res.data.text()));
  }

  const prepareSubmissionFile = (teamDataName, teamData) => {
    var filename = ""
    var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
    var matches = filenameRegex.exec(teamDataName);
    if (matches != null && matches[1]) {
      filename = matches[1].replace(/['"]/g, '');
    }
    teamData.then((res) => {
      downloadFile(base64StringToBlob(res, 'application/zip'), filename)
    })
  };

  const downloadFile = (blob, fileName) => {
    const fileURL = URL.createObjectURL(blob);
    const href = document.createElement('a');
    href.href = fileURL;
    href.download = fileName;
    href.click();
  };

  return (
    <div>
      {assignmentsLoaded ? (
        <div className='psc-container'>
          <div id='assList'>
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
          <div className='row-multiple'>
          <div
            className='input-field'
            style={{ marginLeft: '5%', marginBottom: '5%' }}
          >
            <span className='inter-16-medium-black'> Number of teams to review: </span>
            <input
              type='number'
              value={assignedTeamCount}
              onChange={(e) => setAssignedTeamCount(e.target.value)}
              style={{ marginRight: '2%' }}
            />
            <button
              id='distribute-button'
              style={{ padding: '2%', fontSize: '14px' }}
              onClick={distribute}
            >
              {' '}
              Distribute Peer Reviews{' '}
            </button>
          </div>
            <div className='inter-20-medium'> Bulk Download for this Assignment:
            <span className='inter-16-bold-blue p2' >
              <button className='blue-button-small' onClick={onSubmitClick}>
                  {' '}
                  Download{' '}
              </button>
              </span>
            </div>
        </div>
        </div>
      ) : null}
    </div>
  );
};

export default ProfessorAllSubmissionsComponent;
