package edu.oswego.cs.rest.daos;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
public class AssignmentDAO {

    @Id @JsonbProperty("assignment_name") public String assignmentName;
    @JsonbProperty("assignment_id") public int assignmentID;
    @JsonbProperty("course_id") public String courseID;
    @JsonbProperty("due_date") public String dueDate;
    @JsonbProperty("instructions") public String instructions;
    @JsonbProperty("peer_review_instructions") public String peerReviewInstructions;
    @JsonbProperty("points") public int points;

    @JsonbCreator
    public AssignmentDAO(
            @NonNull @JsonbProperty("assignment_name") String assignmentName,
            @NonNull @JsonbProperty("course_id") String courseID,
            @NonNull @JsonbProperty("due_date") String dueDate,
            @NonNull @JsonbProperty("instructions") String instructions,
            @NonNull @JsonbProperty("peer_review_instructions") String peerReviewInstructions,
            @NonNull @JsonbProperty("points") int points)
    {
        this.assignmentName = assignmentName;
        this.courseID = courseID;
        this.dueDate = dueDate;
        this.instructions = instructions;
        this.peerReviewInstructions = peerReviewInstructions;
        this.points = points;
    }
}