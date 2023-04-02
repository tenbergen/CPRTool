package edu.oswego.cs.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Course {
    String course;
    String crn;
    String professor;
    int year;
    String semester;
    

    public Course(String course, String crn, String professor, int year, String semester) {
        this.course = course;
        this.crn = crn;
        this.professor = professor;
        this.year = year;
        this.semester = semester;
    }
}
