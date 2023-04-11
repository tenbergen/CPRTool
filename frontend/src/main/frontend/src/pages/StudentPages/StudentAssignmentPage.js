import { useEffect } from 'react';
import './styles/AssignmentPageStyle.css';
import { useDispatch } from 'react-redux';
import { useParams } from 'react-router-dom';
import { getAssignmentDetailsAsync } from '../../redux/features/assignmentSlice';
import RegularAssignmentComponent from '../../components/StudentComponents/AssignmentPage/RegularAssignmentComponent';
import StudentPeerReviewComponent from '../../components/StudentComponents/AssignmentPage/StudentPeerReviewComponent';
import NavigationContainerComponent from "../../components/NavigationComponents/NavigationContainerComponent";
import HeaderBar from "../../components/HeaderBar/HeaderBar";

function StudentAssignmentPage() {
  const dispatch = useDispatch();
  const { courseId, assignmentId, assignmentType } = useParams();

  
  useEffect(() => {
    dispatch(getAssignmentDetailsAsync({ courseId, assignmentId }));
  }, [courseId, assignmentId, dispatch]);

  return (
    <div className="page-container">
      <HeaderBar/>
      <div className='ap-container'>
        <NavigationContainerComponent/>
        <div className='ap-component'>
          {assignmentType === 'peer-review' ? (
              <StudentPeerReviewComponent />
          ) : (
              <RegularAssignmentComponent />
          )}
        </div>
      </div>
    </div>
  );
}

export default StudentAssignmentPage;
