package edu.oswego.cs.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Course {
    String course;
    String courseNumber;
    String instructorFirstName;
    String instructorLastName;
    int year;
    String semester;
}
