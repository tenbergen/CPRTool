package edu.oswego.cs.rest.daos;

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

    @Id public @JsonbProperty("TeamID") @NonNull String teamID; // teamID will be the pointer of the TeamDAO since teamName is not generated until .full = true

    public @JsonbProperty("TeamName") String teamName;
    public @JsonbProperty("TeamMembers") @NonNull HashMap<String, Boolean> teamMembers; // teamMembers = {"studID", isStudentFinalized:boolean}
    public @JsonbProperty("IsFull") @NonNull boolean isfull;
    public @JsonbProperty("IsFinalized") @NonNull boolean isfinalized; // make sure if everyone in the team hit the "finalize" button
    public @JsonbProperty("TeamSize") int teamSize;

    @JsonbCreator public TeamDAO(@JsonbProperty("TeamID") String teamID) {
        this.teamID = teamID; // teamID can be the position of the team (index 0 index 1 ...) sent from FE
        this.teamName = "";   // teamName is not generated until the .full is true
        this.teamMembers = new HashMap<String, Boolean>(); // create the teamMembers hashMap in resources
        this.isfull = false;
        this.isfinalized = false;
        this.teamSize = 0;

    }

}
