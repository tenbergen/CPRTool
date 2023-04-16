package edu.oswego.cs.daos;

import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
public class StudentDAO {
    @Id @JsonbProperty("email") public String email;
    @JsonbProperty("abbreviation") public String abbreviation;
    @JsonbProperty("course_name") public String courseName;
    @JsonbProperty("course_section") public String courseSection;
    @JsonbProperty("crn") public String crn;
    @JsonbProperty("semester") public String semester;
    @JsonbProperty("year") public String year;
    public String fullName;
    public String courseID;

    public StudentDAO(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    @JsonbCreator
    public StudentDAO(
            @NonNull @JsonbProperty("email") String email,
            @NonNull @JsonbProperty("abbreviation") String abbreviation,
            @NonNull @JsonbProperty("course_name") String courseName,
            @NonNull @JsonbProperty("course_section") String courseSection,
            @NonNull @JsonbProperty("crn") String crn,
            @NonNull @JsonbProperty("semester") String semester,
            @NonNull @JsonbProperty("year") String year) {
        this.email = email;
        this.abbreviation = abbreviation;
        this.courseName = courseName;
        this.courseSection = courseSection;
        this.crn = crn;
        this.semester = semester;
        this.year = year;
    }

    public StudentDAO(@NonNull String firstName, @NonNull String lastName, @NonNull String email) {
        this.fullName = lastName + ", " + firstName;
        this.email = email;
    }

    public String toString() {
        return this.email + "/" + this.fullName;
    }
}