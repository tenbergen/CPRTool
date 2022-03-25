package edu.oswego.cs.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TeamParam {
    private int team_size;
    private String course_id;
    private String student_id;
    private String team_id;
}
