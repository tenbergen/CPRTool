import { useEffect } from 'react';
import '../../../pages/StudentPages/styles/AssignmentPageStyle.css';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import { getAssignmentDetailsAsync } from '../../../redux/features/assignmentSlice';
import { getCurrentCourseTeamAsync } from '../../../redux/features/teamSlice';

const RegularAssignmentComponent = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { currentAssignment, currentAssignmentLoaded } = useSelector(
    (state) => state.assignments
  );
  const { lakerId } = useSelector((state) => state.auth);
  const { courseId, assignmentId } = useParams();
  const { currentTeamId } = useSelector((state) => state.teams);
  const assignmentFileFormData = new FormData();

  useEffect(() => {
    dispatch(getCurrentCourseTeamAsync({ courseId, lakerId }));
    dispatch(getAssignmentDetailsAsync({ courseId, assignmentId }));
  }, [courseId, lakerId, assignmentId, dispatch]);

  const assignmentFileHandler = (event) => {
    let file = event.target.files[0];
    const reader = new FileReader();
    reader.onloadend = () => {
      // Use a regex to remove data url part
      const base64String = reader.result
          .replace('data:', '')
          .replace(/^.+,/, '');
      for(var pair of assignmentFileFormData.entries()){
        assignmentFileFormData.delete(pair[0])
      }
      assignmentFileFormData.set(file.name, base64String);
    };
    reader.readAsDataURL(file);
  };

  const onAssignmentClick = async () => {
    if(currentAssignment.assignment_instructions_name.endsWith(".pdf")){
      downloadFile(new Blob([Uint8Array.from(currentAssignment.assignment_instructions_data.data)], {type: 'application/pdf'}), currentAssignment.assignment_instructions_name)
    }else if(currentAssignment.assignment_instructions_name.endsWith(".docx")){
      downloadFile(new Blob([Uint8Array.from(currentAssignment.assignment_instructions_data.data)], {type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'}), currentAssignment.assignment_instructions_name)
    }else{
      downloadFile(new Blob([Uint8Array.from(currentAssignment.assignment_instructions_data.data)], {type: 'application/zip'}), currentAssignment.assignment_instructions_name)
    }
  };

  const downloadFile = (blob, fileName) => {
    const fileURL = URL.createObjectURL(blob);
    const href = document.createElement('a');
    href.href = fileURL;
    href.download = fileName;
    href.click();
  };

  const handleSubmit = async () => {
    const submitAssUrl = `${process.env.REACT_APP_URL}/assignments/student/courses/${courseId}/assignments/${assignmentId}/${currentTeamId}/upload`;
    const submitAssEmailUrl = `${process.env.REACT_APP_URL}/email/send/${courseId}/${currentTeamId}/${assignmentId}/assignment-submitted`

    await axios
      .post(submitAssUrl, assignmentFileFormData)
      .then((res) => {
        alert('Successfully uploaded assignment');
        navigate(`/details/student/${courseId}`, {
          state: { initialComponent: 'Submitted' },
        });
      })
      .catch((e) => {
        console.error(e.response);
        alert(`Error: ${e.response.data}`);
      });
    await axios
        .post(submitAssEmailUrl)
        .then((res) => {
        })
        .catch((e) => {
          console.error(e.response);
          alert(`Error: ${e.response.data}`);
        });
  };

  return (
    <div>
      {currentAssignmentLoaded && (
        <div>
          <h2 className='inter-28-bold'>{currentAssignment.assignment_name}</h2>
          <div className='ap-assignmentArea'>
            <h3>
              <span className='inter-20-bold'> Instructions: </span>
              <span className='inter-20-bold span1-ap'>
                Due: {currentAssignment.due_date}
              </span>
              <br />
              <p className='inter-16-medium-black'>{currentAssignment.instructions}</p>
              <br />
              <br />
              <span className='inter-20-bold'> Files: </span>
              <span className='inter-16-bold-blue p2' onClick={onAssignmentClick}>
                {currentAssignment.assignment_instructions_name}
              </span>
              <br />
              <br />
              <div className='ap-assignment-files'>
                <input
                  type='file'
                  name='assignment_files'
                  accept='.pdf,.docx'
                  onChange={(e) => assignmentFileHandler(e)}
                  required
                />
              </div>
              <div className='ap-button'>
                <button className='green-button-medium' onClick={handleSubmit}>
                  {' '}
                  Submit{' '}
                </button>
              </div>
            </h3>
          </div>
        </div>
      )}
    </div>
  );
};

export default RegularAssignmentComponent;
