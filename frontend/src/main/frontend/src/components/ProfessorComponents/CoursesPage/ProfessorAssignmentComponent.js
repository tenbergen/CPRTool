import { useEffect } from "react";
import "../../styles/TeacherAss.css";
import { useDispatch, useSelector } from "react-redux";
import { Link, useParams } from "react-router-dom";
import { getCourseAssignmentsAsync } from "../../../redux/features/assignmentSlice";
import AssignmentTile from "../../AssignmentTile";
import noAssignment from "../../../assets/no-course.png";

import uuid from "react-uuid";

const ProfessorAssignmentComponent = () => {
  const dispatch = useDispatch();
  const { courseId } = useParams();
  const { courseAssignments } = useSelector((state) => state.assignments);

  useEffect(() => {
    dispatch(getCourseAssignmentsAsync(courseId));
  }, [dispatch, courseId]);

  return (
    <div className="assignment-container">
      <div className="assignment-list-wrapper">
        {courseAssignments.length === 0 ? (
          <div className="no-assigment-wrapper">
            <img
              className="no-assignment-img"
              src={noAssignment}
              alt="No assignment"
            />
          </div>
        ) : (
          <div id="assignment-list">
            {courseAssignments.map(
              (assignment) =>
                assignment && (
                  <AssignmentTile key={uuid()} assignment={assignment} />
                )
            )}
          </div>
        )}
      </div>

      <div className="create-class-container">
        <Link to="create/assignment">
          <button className="create-assignment-btn">
            Create Assignment
          </button>
        </Link>
      </div>
    </div>
  );
};

export default ProfessorAssignmentComponent;
