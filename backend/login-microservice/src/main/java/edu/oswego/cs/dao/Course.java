package edu.oswego.cs.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Course {
    String course;
    String courseNumber;
    String instructorFirstName;
    String instructorLastName;
    int year;
    String semester;
}
