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
    public @JsonbProperty("email") @NonNull String email;
    public @JsonbProperty("courseName") @NonNull String courseName;
    public @JsonbProperty("courseSection") @NonNull int courseSection;

    @JsonbCreator
    public StudentDAO(@JsonbProperty("email") String email,
                      @JsonbProperty("courseName") String courseName,
                      @JsonbProperty("courseSection") int courseSection) {
        this.email = email;
        this.courseName = courseName;
        this.courseSection = courseSection;
    }
}
