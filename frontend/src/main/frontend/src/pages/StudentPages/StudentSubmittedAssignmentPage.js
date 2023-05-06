import { useEffect } from 'react';
import './styles/AssignmentPageStyle.css';
import { useDispatch, useSelector } from 'react-redux';
import { useParams } from 'react-router-dom';
import { getSubmittedAssignmentDetailsAsync } from '../../redux/features/submittedAssignmentSlice';
import SubmittedAssignmentComponent from '../../components/StudentComponents/AssignmentPage/SubmittedAssignmentComponent';
import NavigationContainerComponent from "../../components/NavigationComponents/NavigationContainerComponent";
import HeaderBar from "../../components/HeaderBar/HeaderBar";
import {getStudentCoursesAsync} from "../../redux/features/courseSlice";

function StudentSubmittedAssignmentPage() {
  const dispatch = useDispatch();
  const { currentSubmittedAssignment, currentSubmittedAssignmentLoaded } =
    useSelector((state) => state.submittedAssignments);
  const { courseId, assignmentId, teamId } = useParams();
  const {lakerId } = useSelector((state) => state.auth)

  useEffect(() => {
    dispatch(getStudentCoursesAsync(lakerId))
    dispatch(
      getSubmittedAssignmentDetailsAsync({ courseId, assignmentId, teamId })
    );
  }, [lakerId,courseId, assignmentId, teamId, dispatch]);


  return (
    <div className="page-container">
      <HeaderBar/>
      <div className='ap-container'>
        <NavigationContainerComponent/>
        <div className='ap-component'>
          {currentSubmittedAssignmentLoaded ? (
              <SubmittedAssignmentComponent
                  currentSubmittedAssignment={currentSubmittedAssignment}
              />
          ) : null}
        </div>
      </div>
    </div>
  );
}

export default StudentSubmittedAssignmentPage;
