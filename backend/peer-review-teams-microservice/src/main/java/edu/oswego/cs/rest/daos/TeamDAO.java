package edu.oswego.cs.rest.daos;

import java.util.HashMap;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.mongodb.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class TeamDAO {
    
    @Id public @JsonbProperty("TeamID") @NonNull String teamID; // teamID will be the pointer of the TeamDAO since teamName is not generated until .isFull = true
    
    public @JsonbProperty("Team Name") String teamName;
    public @JsonbProperty("Team Members") @NonNull HashMap<String, Boolean> teamMembers; // teamMembers = {"studID", isStudentFinalized:boolean}
    public @JsonbProperty("Is Full") @NonNull boolean isFull;
    public @JsonbProperty("Is Finalized") @NonNull boolean isFinalized; // make sure if everyone in the team hit the "finalize" button

    @JsonbCreator public TeamDAO( 
        @JsonbProperty("TeamID") String teamID
    ) {

        this.teamID = teamID; // teamID can be the position of the team (index 0 index 1 ...) sent from FE

        this.teamName = "";   // teamName is not generated until the .isFull is true
        this.teamMembers = new HashMap<String, Boolean>(); // create the teamMembers hashMap in resources
        this.isFull = false;
        this.isFinalized = false;

    }
    
}
