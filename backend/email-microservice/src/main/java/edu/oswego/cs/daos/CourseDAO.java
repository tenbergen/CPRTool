package edu.oswego.cs.daos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.ArrayList;
import java.util.List;

/**
 * This class only exists in the email microservice for testing purposes
 */
@Entity
@NoArgsConstructor
@ToString
@Data
public class CourseDAO {
    @Id @JsonbProperty("course_id") public String courseID;
    @JsonbProperty("abbreviation") public String abbreviation;
    @JsonbProperty("course_name") public String courseName;
    @JsonbProperty("course_section") public String courseSection;
    @JsonbProperty("crn") public String crn;
    @JsonbProperty("professor_id") public String professorID;
    @JsonbProperty("semester") public String semester;
    @JsonbProperty("students") @ElementCollection public List<String> students;
    @JsonbProperty("year") public String year;
    @JsonbProperty("team_size") public int teamSize;

    @JsonbCreator
    public CourseDAO(
            @NonNull @JsonbProperty("abbreviation") String abbreviation,
            @NonNull @JsonbProperty("course_name")  String courseName,
            @NonNull @JsonbProperty("course_section") String courseSection,
            @NonNull @JsonbProperty("crn") String crn,
            @NonNull @JsonbProperty("semester") String semester,
            @NonNull @JsonbProperty("year") String year)
    {
        this.abbreviation = abbreviation;
        this.courseName = courseName;
        this.courseSection = courseSection;
        this.crn = crn;
        this.professorID = "";
        this.semester = semester;
        this.students = new ArrayList<>();
        this.year = year;
        this.teamSize = 1;
        this.courseID = this.abbreviation + "-" + this.courseSection + "-" + this.crn + "-" + this.semester + "-" + this.year;
    }
}