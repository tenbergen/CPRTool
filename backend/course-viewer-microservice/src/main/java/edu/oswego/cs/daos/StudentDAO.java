package edu.oswego.cs.daos;

import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class StudentDAO {
    @JsonbProperty("courses") @ElementCollection public List<String> courses;
    @Id @JsonbProperty("student_id") public String studentID;

    @JsonbCreator
    public StudentDAO(@NonNull @JsonbProperty("student_id") String studentID) {
        this.courses = new ArrayList<>();
        this.studentID = studentID;
    }
}