package edu.oswego.cs.daos;

import lombok.*;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Entity;
import javax.persistence.Id;

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

    @JsonbCreator
    public CourseDAO(@JsonbProperty("CourseName") String courseName,
                     @JsonbProperty("CourseSection") int courseSection,
                     @JsonbProperty("Semester") String semester,
                     @JsonbProperty("Abbreviation") String abbreviation) {
        this.courseName = courseName;
        this.courseSection = courseSection;
        this.semester = semester;
        this.abbreviation = abbreviation;
        this.courseID = this.abbreviation + "-" + this.courseSection + "-" + this.semester;
    }

    public CourseDAO(String abbreviation, int courseSection, String semester) {
        this.abbreviation = abbreviation;
        this.courseSection = courseSection;
        this.semester = semester;
        this.courseName = "";
    }
}
