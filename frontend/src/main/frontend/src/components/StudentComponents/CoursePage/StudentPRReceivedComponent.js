import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import '../../styles/StudentAss.css';
import { useParams } from 'react-router-dom';
import { getCombinedAssignmentPeerReviews } from '../../../redux/features/assignmentSlice';
import AssignmentTile from '../../AssignmentTile';
import uuid from 'react-uuid';
import noData from '../../../assets/no-data.png';
import AssBarComponent from "../../AssBarComponent";
import PRReceivedTile from "../../PRReceivedTile";

const StudentToDoComponent = () => {
  const dispatch = useDispatch();
  const store = useSelector((state) => state);
  const { combinedAssignmentPeerReviews, assignmentsLoaded } =
    store.assignments;
  const { currentTeamId, teamLoaded } = store.teams;
  const { courseId } = useParams();
  const { assignmentId } = useParams();
  const { lakerId } = store.auth;

  useEffect(() => {
    dispatch(
      getCombinedAssignmentPeerReviews({ courseId, currentTeamId, lakerId })
    );
  }, [courseId, currentTeamId, lakerId, dispatch]);

  return (
    <h3>
      {assignmentsLoaded && teamLoaded ? (
          {assignmentId}
      ) : null}
    </h3>
  );
};

export default StudentToDoComponent;
