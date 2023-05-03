import { useEffect } from 'react';
import './styles/AssignmentPageStyle.css';
import {useDispatch, useSelector} from 'react-redux';
import { useParams } from 'react-router-dom';
import { getAssignmentDetailsAsync } from '../../redux/features/assignmentSlice';
import RegularAssignmentComponent from '../../components/StudentComponents/AssignmentPage/RegularAssignmentComponent';
import StudentPeerReviewComponent from '../../components/StudentComponents/AssignmentPage/StudentPeerReviewComponent';
import NavigationContainerComponent from "../../components/NavigationComponents/NavigationContainerComponent";
import HeaderBar from "../../components/HeaderBar/HeaderBar";
import {getStudentCoursesAsync} from "../../redux/features/courseSlice";

function StudentAssignmentPage() {
  const dispatch = useDispatch();
  const { courseId, assignmentId, assignmentType } = useParams();
  const {lakerId } = useSelector((state) => state.auth)
  
  useEffect(() => {
    dispatch(getAssignmentDetailsAsync({ courseId, assignmentId }));
    dispatch(getStudentCoursesAsync(lakerId))
  }, [courseId, assignmentId, dispatch,lakerId]);


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
