package edu.oswego.cs.rest.daos;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.BsonBinary;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.conversions.Bson;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Entity;
import javax.persistence.Id;

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
    }
}