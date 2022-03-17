package edu.oswego.cs.rest.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TeamInitRequest {
    private String courseID;
    private int teamSize;
}
