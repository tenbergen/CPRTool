package edu.oswego.cs.requests;
import javax.json.bind.annotation.JsonbProperty;

import lombok.Data;

@Data
public class TeamParam {
    @JsonbProperty("course_id") private String courseID;
    @JsonbProperty("team_id") private String teamID;
    @JsonbProperty("student_id") private String studentID;
    @JsonbProperty("team_size") private int teamSize;
}
