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
@ToString
@Data
public class CourseDAO {
    @JsonbProperty("abbreviation") public String abbreviation;
    @JsonbProperty("course_name") public String courseName;
    @JsonbProperty("course_section") public String courseSection;
    @JsonbProperty("semester") public String semester;
    @JsonbProperty("students") @ElementCollection public List<String> students;
    @JsonbProperty("year") public String year;
    @Id @JsonbProperty("course_id") public String courseID;
    @JsonbProperty("crn") public String crn;

    @JsonbCreator
    public CourseDAO(
            @NonNull @JsonbProperty("abbreviation") String abbreviation,
            @NonNull @JsonbProperty("course_name")  String courseName,
            @NonNull @JsonbProperty("course_section") String courseSection,
            @NonNull @JsonbProperty("crn") String crn,
            @NonNull @JsonbProperty("semester") String semester,
            @NonNull @JsonbProperty("year") String year) {
        this.abbreviation = abbreviation;
        this.courseName = courseName;
        this.courseSection = courseSection;
        this.crn = crn;
        this.semester = semester;
        this.students = new ArrayList<>();
        this.year = year;
        this.courseID = this.abbreviation + "-" + this.courseSection + "-" + this.semester + "-" + this.year;
    }
}