import { useEffect, useState } from 'react';
import '../../../pages/StudentPages/styles/AssignmentPageStyle.css';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate, useParams, useLocation } from 'react-router-dom';
import axios from 'axios';
import { getAssignmentDetailsAsync } from '../../../redux/features/assignmentSlice';
import { getCurrentCourseTeamAsync } from '../../../redux/features/teamSlice';
import '../../styles/StudentAss.css';
import './RegularAssignmentComponent.css'

import SidebarComponent from '../../../components/SidebarComponent';
import StudentTeamComponent from '../../../components/StudentComponents/CoursePage/StudentTeamComponent';
import StudentToDoComponent from '../../../components/StudentComponents/CoursePage/StudentToDoComponent';
import CourseBarComponent from '../../../components/CourseBarComponent';
import StudentSubmittedComponent from '../../../components/StudentComponents/CoursePage/StudentSubmittedComponent';
import { getCourseDetailsAsync } from '../../../redux/features/courseSlice';
import MyTeamComponent from '../../../components/StudentComponents/CoursePage/MyTeamComponent';
import uuid from 'react-uuid';

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

  const routeChange = () => {
    let path = `/student/${courseId}/assignments`
    navigate(path)
  }

  const handleSubmit = async () => {
    const submitAssUrl = `${process.env.REACT_APP_URL}/assignments/student/courses/${courseId}/assignments/${assignmentId}/${currentTeamId}/upload`;

    await axios
      .post(submitAssUrl, assignmentFileFormData)
      .then((res) => {
        alert('Successfully uploaded assignment');
        navigate(`/student/${courseId}`, {
          state: { initialComponent: 'Submitted' },
        });
      })
      .catch((e) => {
        console.error(e.response);
        alert(`Error: ${e.response.data}`);
      });
  };

  return (
    <div>
      {currentAssignmentLoaded && (
          <div
              className={
                currentAssignment.assignment_type === 'peer-review'
                    ? 'ass-tile ass-tile-yellow'
                    : 'ass-tile'
              }
          >
        <div className='inter-20-medium-white ass-tile-title'> {' '}
          <span> {'Assignment Details'} </span>
        </div>
          <div className='ass-tile-content' >
              <span className='inter-24-bold'> {currentAssignment.assignment_name} </span>
              <span className='inter-20-medium span1-ap'>
                Due: {currentAssignment.due_date}
              </span>
              <br /> <br /> <br />
              <p className='inter-20-medium' >Instructions:</p>
              <p className='inter-16-medium-black'>{currentAssignment.instructions}</p>
              <br />
              <br />
              <span className='inter-20-bold'> Rubric: </span>
              <span className='inter-16-bold-blue p2' >
                <button className='blue-button-small' onClick={onAssignmentClick} >
                  {' '}
                  Download{' '}
              </button>
              </span>
              <div className='ap-assignment-files rubric-button'>
                <input
                  type='file'
                  name='assignment_files'
                  accept='.pdf,.docx'
                  onChange={(e) => assignmentFileHandler(e)}
                  required
                />
            </div>
          </div>
    </div>)}
      <div className='ap-button'>
        <button className='green-button-large submit-button' onClick={handleSubmit} >
          {' '}
          Submit{' '}
        </button>
        <button className='cancel-button' onClick={routeChange}>
          Cancel
        </button>
      </div>
    </div>
  );
};

export default RegularAssignmentComponent;
