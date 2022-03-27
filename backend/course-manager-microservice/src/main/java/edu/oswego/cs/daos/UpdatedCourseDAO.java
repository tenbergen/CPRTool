package edu.oswego.cs.daos;

import lombok.NonNull;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class UpdatedCourseDAO {

    @JsonbProperty("original_course_id") public String originalCourseId;
    @JsonbProperty("abbreviation") public String abbreviation;
    @JsonbProperty("course_name") public String courseName;
    @JsonbProperty("course_section") public String courseSection;
    @JsonbProperty("semester") public String semester;
    @JsonbProperty("year") public String year;
    @JsonbProperty("course_id") public String courseID;
    @JsonbProperty("crn") int crn;

    @JsonbCreator
    public UpdatedCourseDAO(
            @NonNull @JsonbProperty("original_course_id") String originalCourseId,
            @NonNull @JsonbProperty("abbreviation") String abbreviation,
            @NonNull @JsonbProperty("course_name")  String courseName,
            @NonNull @JsonbProperty("course_section") String courseSection,
            @NonNull @JsonbProperty("semester") String semester,
            @NonNull @JsonbProperty("year") String year) {
        this.originalCourseId = originalCourseId;
        this.abbreviation = abbreviation;
        this.courseName = courseName;
        this.courseSection = courseSection;
        this.semester = semester;
        this.year = year;
        this.courseID = this.abbreviation + "-" + this.courseSection + "-" + this.semester + "-" + this.year;
    }

}
