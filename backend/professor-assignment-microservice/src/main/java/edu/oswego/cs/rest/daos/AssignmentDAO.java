package edu.oswego.cs.rest.daos;

import lombok.*;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class AssignmentDAO {

    @Id @JsonbProperty("assignment_name") String assignmentName;
    @JsonbProperty("instructions") String instructions;
    @JsonbProperty("due_date") String dueDate;
    @JsonbProperty("course_id") String courseID;
    @JsonbProperty("points") int points;

    @JsonbCreator
    public AssignmentDAO(
            @NonNull @JsonbProperty("assignment_name") String assignmentName,
            @NonNull @JsonbProperty("instructions") String instructions,
            @NonNull @JsonbProperty("due_date") String dueDate,
            @NonNull @JsonbProperty("course_id") String courseID,
            @NonNull @JsonbProperty("points") int points) {
        this.assignmentName = assignmentName;
        this.instructions = instructions;
        this.dueDate = dueDate;
        this.courseID = courseID;
        this.points = points;
    }
}