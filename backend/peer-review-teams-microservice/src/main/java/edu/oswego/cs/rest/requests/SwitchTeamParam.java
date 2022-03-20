package edu.oswego.cs.rest.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SwitchTeamParam {
    private String courseID;
    private String studentID;
    private String oldTeamID;
    private String newTeamID;
    private int newTeamSize;
}
