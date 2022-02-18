package edu.oswego.cs.daos;

import lombok.Getter;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
public class CourseDAO {

    @Id
    private int ID;
    private String courseName;
    private int courseSection;
    private String professor;

    public CourseDAO(int ID, String courseName, int courseSection, String professor) {
        this.ID = ID;
        this.courseName = courseName;
        this.courseSection = courseSection;
        this.professor = professor;
    }

    public CourseDAO() { }
}
