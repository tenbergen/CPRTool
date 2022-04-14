package edu.oswego.cs.rest.daos;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.HashMap;

@Entity
@NoArgsConstructor
public class AssignmentDAO {

    @Id @JsonbProperty("course_id") public String courseID;
    @JsonbProperty("assignment_name") public String assignmentName;
    @JsonbProperty("assignment_id") public int assignmentID;
    @JsonbProperty("instructions") public String instructions;
    @JsonbProperty("due_date") public String dueDate;
    @JsonbProperty("points") public int points;

    @JsonbProperty("peer_review_instructions") public String peerReviewInstructions;
    @JsonbProperty("peer_review_due_date") public String peerReviewDueDate;
    @JsonbProperty("peer_review_points") public int peerReviewPoints;

    @JsonbProperty("team_submissions") public @ElementCollection HashMap<String,String> teamSubmissions;
    @JsonbProperty("team_review_submissions") public @ElementCollection HashMap<String,String> teamReviewSubmissions;

    @JsonbCreator
    public AssignmentDAO(
            @NonNull @JsonbProperty("course_id") String courseID,
            @NonNull @JsonbProperty("assignment_name") String assignmentName,
            @NonNull @JsonbProperty("instructions") String instructions,
            @NonNull @JsonbProperty("due_date") String dueDate,
            @NonNull @JsonbProperty("points") int points,
            @NonNull @JsonbProperty("peer_review_instructions") String peerReviewInstructions,
            @NonNull @JsonbProperty("peer_review_due_date") String peerReviewDueDate,
            @NonNull @JsonbProperty("peer_review_points") int peerReviewPoints
            )
    {
        this.assignmentName = assignmentName;
        this.courseID = courseID;
        this.dueDate = dueDate;
        this.instructions = instructions;
        this.peerReviewInstructions = peerReviewInstructions;
        this.points = points;
        this.peerReviewDueDate = peerReviewDueDate;
        this.peerReviewPoints = peerReviewPoints;
        teamSubmissions = new HashMap<>();
        teamReviewSubmissions = new HashMap<>();
    }
}