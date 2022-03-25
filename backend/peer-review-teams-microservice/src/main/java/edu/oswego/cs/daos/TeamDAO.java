package edu.oswego.cs.daos;

import java.util.HashMap;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.mongodb.lang.NonNull;

import lombok.Data;

@Entity
@Data
public class TeamDAO {
    @Id public @JsonbProperty("team_id") @NonNull String teamID; // teamID will be the pointer of the TeamDAO since teamName is not generated until .full = true
    public @JsonbProperty("team_name") String teamName;
    public @JsonbProperty("team_members") @NonNull HashMap<String, Boolean> teamMembers; // teamMembers = {"studID", isStudentFinalized:boolean}
    public @JsonbProperty("is_full") @NonNull boolean isFull;
    public @JsonbProperty("is_finalized") @NonNull boolean isFinalized; // make sure if everyone in the team hit the "finalize" button
    public @JsonbProperty("team_size") int teamSize;

    @JsonbCreator public TeamDAO(@JsonbProperty("team_id") String teamID) {
        this.teamID = teamID; // teamID can be the position of the team (index 0 index 1 ...) sent from FE
        this.teamName = "";   // teamName is not generated until the .full is true
        this.teamMembers = new HashMap<String, Boolean>(); // create the teamMembers hashMap in resources
        this.isFull = false;
        this.isFinalized = false;
        this.teamSize = 0;
    }
}
