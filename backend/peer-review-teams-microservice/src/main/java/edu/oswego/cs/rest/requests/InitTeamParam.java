package edu.oswego.cs.rest.requests;

import lombok.Data;

@Data
public class InitTeamParam {
    private String courseID;
    private int teamSize;
}
