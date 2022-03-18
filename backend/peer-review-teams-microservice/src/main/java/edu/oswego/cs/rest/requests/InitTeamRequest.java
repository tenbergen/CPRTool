package edu.oswego.cs.rest.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InitTeamRequest {
    private String courseID;
    private int teamSize;
}
