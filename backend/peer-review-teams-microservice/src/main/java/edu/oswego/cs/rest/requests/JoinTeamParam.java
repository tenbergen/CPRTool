package edu.oswego.cs.rest.requests;

import lombok.Data;

@Data
public class JoinTeamParam {
    private String courseID;
    private String studentID;
    private String newTeamID;
}
