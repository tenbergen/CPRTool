package edu.oswego.cs.rest.daos;

import lombok.NonNull;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Id;

public class AssignmentNoPeerReviewDAO {
    @Id
    @JsonbProperty("course_id") public String courseID;
    @JsonbProperty("assignment_name") public String assignmentName;
    @JsonbProperty("assignment_id") public int assignmentID;
    @JsonbProperty("instructions") public String instructions;
    @JsonbProperty("due_date") public String dueDate;
    @JsonbProperty("points") public int points;
    @JsonbCreator
    public AssignmentNoPeerReviewDAO(
            @NonNull @JsonbProperty("course_id") String courseID,
            @NonNull @JsonbProperty("assignment_name") String assignmentName,
            @NonNull @JsonbProperty("instructions") String instructions,
            @NonNull @JsonbProperty("due_date") String dueDate,
            @NonNull @JsonbProperty("points") Integer points
    )
    {
        this.assignmentName = assignmentName;
        this.courseID = courseID;
        this.dueDate = dueDate;
        this.instructions = instructions;
        this.points = points;
    }
}
