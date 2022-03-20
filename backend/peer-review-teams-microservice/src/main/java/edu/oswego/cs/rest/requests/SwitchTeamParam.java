package edu.oswego.cs.rest.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SwitchTeamParam {
    private String course_id;
    private String student_id;
    private String old_team_id;
    private String new_team_id;
    private int new_team_size;
}
