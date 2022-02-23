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
    @Id
    public @JsonbProperty("Email") @NonNull String email;
    public @JsonbProperty("Abbreviation") @NonNull String abbreviation;
    public @JsonbProperty("CourseSection") @NonNull int courseSection;
    public @JsonbProperty("Semester") @NonNull String semester;

    @JsonbCreator
    public StudentDAO(@JsonbProperty("Email") String email,
                      @JsonbProperty("Abbreviation") String abbreviation,
                      @JsonbProperty("CourseSection") int courseSection,
                      @JsonbProperty("Semester") String semester) {
        this.email = email;
        this.courseSection = courseSection;
        this.semester = semester;
    }
}
