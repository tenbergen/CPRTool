package edu.oswego.cs.daos;

import lombok.*;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;

@ToString
@Entity
@NoArgsConstructor
@Getter
public class CourseDAO {

    @Id
    public String courseID;
    public @JsonbProperty("CourseName") @NonNull String courseName;
    public @JsonbProperty("CourseSection") @NonNull int courseSection;
    public @JsonbProperty("Semester") @NonNull String semester;
    public @JsonbProperty("Abbreviation") @NonNull String abbreviation;
    public @JsonbProperty ("Students") ArrayList students;
    public @JsonbProperty("Teams") ArrayList teams;
    public @JsonbProperty("Tas") ArrayList tas;


    @JsonbCreator
    public CourseDAO(
            @JsonbProperty("CourseName") String courseName,
            @JsonbProperty("CourseSection") int courseSection,
            @JsonbProperty("Semester") String semester,
            @JsonbProperty("Abbreviation") String abbreviation) {
        this.courseName = courseName;
        this.courseSection = courseSection;
        this.semester = semester;
        this.abbreviation = abbreviation;
        this.courseID = this.abbreviation + "-" + this.courseSection + "-" + this.semester;
        this.students = new ArrayList();
        this.teams = new ArrayList();
        this.tas = new ArrayList();
    }


}
