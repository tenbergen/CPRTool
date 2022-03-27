package edu.oswego.cs.daos;

import lombok.*;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class CourseDAO {
    @JsonbProperty("abbreviation") public String abbreviation;
    @Id @JsonbProperty("course_name") public String courseName;
    @JsonbProperty("course_section") public String courseSection;
    @JsonbProperty("semester") public String semester;
    @JsonbProperty("students") @ElementCollection public List<String> students;
    @JsonbProperty("year") public String year;
    @JsonbProperty("course_id") public String courseID;
    @JsonbProperty("crn") int crn;

    @JsonbCreator
    public CourseDAO(
            @NonNull @JsonbProperty("abbreviation") String abbreviation,
            @NonNull @JsonbProperty("course_name")  String courseName,
            @NonNull @JsonbProperty("course_section") String courseSection,
            @NonNull @JsonbProperty("semester") String semester,
            @NonNull @JsonbProperty("year") String year,
            @NonNull @JsonbProperty("crn") int crn) {
        this.abbreviation = abbreviation;
        this.courseName = courseName;
        this.courseSection = courseSection;
        this.semester = semester;
        this.students = new ArrayList<>();
        this.year = year;
        this.crn = crn;
        this.courseID = this.abbreviation + "-" + this.courseSection + "-" + this.semester + "-" + this.year;
    }
}