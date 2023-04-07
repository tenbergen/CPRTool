import { useEffect } from 'react';
import './styles/AssignmentPageStyle.css';
import { useDispatch, useSelector } from 'react-redux';
import { useParams } from 'react-router-dom';
import { getSubmittedAssignmentDetailsAsync } from '../../redux/features/submittedAssignmentSlice';
import SubmittedAssignmentComponent from '../../components/StudentComponents/AssignmentPage/SubmittedAssignmentComponent';
import SubmittedAssBarComponent from '../../components/SubmittedAssBarComponent';
import StudentHeaderBar from "../../components/StudentComponents/StudentHeaderBar";

function StudentSubmittedAssignmentPage() {
  const dispatch = useDispatch();
  const { currentSubmittedAssignment, currentSubmittedAssignmentLoaded } =
    useSelector((state) => state.submittedAssignments);
  const { courseId, assignmentId, teamId } = useParams();

  useEffect(() => {
    dispatch(
      getSubmittedAssignmentDetailsAsync({ courseId, assignmentId, teamId })
    );
  }, [courseId, assignmentId, teamId, dispatch]);

  return (
    <div className="page-container">
      <StudentHeaderBar/>
      <div className='ap-parent'>
        <div className='ap-container'>
          <SubmittedAssBarComponent />
          <div className='ap-component'>
            {currentSubmittedAssignmentLoaded ? (
              <SubmittedAssignmentComponent
                currentSubmittedAssignment={currentSubmittedAssignment}
              />
            ) : null}
          </div>
        </div>
      </div>
    </div>
  );
}

export default StudentSubmittedAssignmentPage;
