package edu.oswego.cs.rest.daos;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class AssignmentDAO {

    public @JsonbProperty("course_id") String courseID;
    public @JsonbProperty("assignment_id") String assignmentID;
    public @JsonbProperty("assignment_name") String assignmentName;
    public @JsonbProperty("assignment_instructions") String assignmentInstructions;
    public @JsonbProperty("peer_review_instructions") String peerReviewInstructions;
    public @JsonbProperty("assignment_due_date") String assignmentDueDate;
    public @JsonbProperty("peer_review_due_date") String peerReviewDueDate;
    public @JsonbProperty("assignment_points") int assignmentPoints;
    public @JsonbProperty("peer_review_points") int peerReviewPoints;

    @JsonbCreator
    public AssignmentDAO(
            @JsonbProperty("course_id") String courseID,
            @JsonbProperty("assignment_id") String assignmentID,
            @JsonbProperty("assignment_name") String assignmentName,
            @JsonbProperty("assignment_instructions") String assignmentInstructions,
            @JsonbProperty("peer_review_instructions") String peerReviewInstructions,
            @JsonbProperty("assignment_due_date") String assignmentDueDate,
            @JsonbProperty("peer_review_due_date") String peerReviewDueDate,
            @JsonbProperty("assignment_points") int assignmentPoints,
            @JsonbProperty("peer_review_points") int peerReviewPoints
    ) {
        this.courseID = courseID;
        this.assignmentID = assignmentID;
        this.assignmentName = assignmentName;
        this.assignmentInstructions = assignmentInstructions;
        this.peerReviewInstructions = peerReviewInstructions;
        this.assignmentDueDate = assignmentDueDate;
        this.peerReviewDueDate = peerReviewDueDate;
        this.assignmentPoints = assignmentPoints;
        this.peerReviewPoints = peerReviewPoints;
    }


}
