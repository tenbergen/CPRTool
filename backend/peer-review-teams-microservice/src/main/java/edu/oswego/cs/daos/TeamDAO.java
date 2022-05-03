package edu.oswego.cs.daos;

import com.mongodb.lang.NonNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
public class TeamDAO {
    @JsonbProperty("team_id") private String teamID;
    @JsonbProperty("course_id") private String courseID;
    @JsonbProperty("team_full") private Boolean teamFull;
    @JsonbProperty("team_lead") private String teamLead;
    @JsonbProperty("team_members") @ElementCollection private List<String> teamMembers;
    @JsonbProperty("team_size") private Integer teamSize;

    @JsonbCreator
    public TeamDAO(
            @NonNull @JsonbProperty("team_id") String teamID,
            @NonNull @JsonbProperty("course_id") String courseID,
            @NonNull @JsonbProperty("team_size") Integer teamSize,
            @NonNull @JsonbProperty("team_lead") String teamLead) {
        this.teamID = teamID;
        this.courseID = courseID;
        this.teamFull = false;
        this.teamLead = teamLead;
        this.teamMembers = new ArrayList<>();
        this.teamSize = teamSize;
    }
}