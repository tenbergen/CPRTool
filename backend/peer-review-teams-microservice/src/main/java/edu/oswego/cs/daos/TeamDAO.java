package edu.oswego.cs.daos;

import com.mongodb.lang.NonNull;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
public class TeamDAO {
    @Id @JsonbProperty("team_id") private Integer teamID;
    @JsonbProperty("course_id") private String courseID;
    @JsonbProperty("team_lead") private String teamLead;
    @JsonbProperty("team_members") @ElementCollection private List<String> teamMembers;
    @JsonbProperty("team_name") private String teamName;
    @JsonbProperty("team_size") private Integer teamSize;
    @JsonbProperty("is_finalized") @ElementCollection private List<String> isFinalized;

    @JsonbCreator
    public TeamDAO( 
        @NonNull @JsonbProperty("team_id") Integer teamID,
        @NonNull @JsonbProperty("course_id") String courseID,
        @NonNull @JsonbProperty("team_size") Integer teamSize
        
        ) {
        this.teamID = teamID;
        this.courseID = courseID;
        this.teamLead = null;
        this.teamMembers = new ArrayList<>();
        this.teamName = null;
        this.teamSize = teamSize;
        this.isFinalized = new ArrayList<>();
    }
}