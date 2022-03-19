package edu.oswego.cs.rest.requests;

import lombok.Data;

@Data
public class TeamParam {
    private int teamSize;
    private String courseID;
    private String studentID;
    private String teamID;
}
