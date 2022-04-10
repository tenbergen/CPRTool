package edu.oswego.cs.requests;

import javax.json.bind.annotation.JsonbProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SwitchTeamParam {
    @JsonbProperty("course_id") private String courseID;
    @JsonbProperty("student_id") private String studentID;
    @JsonbProperty("current_team_id") private String currentTeamID;
    @JsonbProperty("target_team_id") private String targetTeamID;
}
