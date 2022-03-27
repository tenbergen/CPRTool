package edu.oswego.cs.rest.daos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class AssignmentDAO {
    String assignmentName;
    String instructions;
    String dueDate;
    String courseID;
    int points;

    public AssignmentDAO(){}
}
