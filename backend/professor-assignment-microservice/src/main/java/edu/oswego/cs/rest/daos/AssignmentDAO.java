package edu.oswego.cs.rest.daos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;

@Getter
@AllArgsConstructor
public class AssignmentDAO {
    String assignmentName;
    String instructions;
    String dueDate;
    int points;
    String courseID;
    ArrayList<FileDAO> assignments = new ArrayList<>();

    public void addAssignment(FileDAO newAssignment){
        assignments.add(newAssignment);
    }
}
