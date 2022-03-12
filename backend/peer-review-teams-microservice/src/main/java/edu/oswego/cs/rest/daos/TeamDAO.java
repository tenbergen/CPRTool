package edu.oswego.cs.rest.daos;

import java.util.ArrayList;
import java.util.HashMap;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.mongodb.lang.NonNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class TeamDAO {
    
    @Id public @JsonbProperty("TeamID") @NonNull String teamID; // teamID will be the pointer of the TeamDAO since teamName is not generated until .isFull = true
    
    public @JsonbProperty("TeamName") String teamName;
    public @JsonbProperty("Team Members") @NonNull HashMap<String, Boolean> teamMembers; // teamMembers = {"studID", isStudentFinalized:boolean}
    public @JsonbProperty("Member Requests") ArrayList<String> memRequests;
    public @JsonbProperty("Is Full") @NonNull boolean isFull;
    public @JsonbProperty("Is Finalized") @NonNull boolean isFinalized; // make sure if everyone in the team hit the "finalize" button

    @JsonbCreator public TeamDAO( 
        @JsonbProperty("TeamID") String teamID,
        @JsonbProperty("Team Members") HashMap<String, Boolean> teamMembers
    ) {

        this.teamID = teamID; // teamID can be the position of the team (index 0 index 1 ...)
                              // this can be sent from FE or retrieved from DB

        this.teamName = "";   // teamName is not generated until the .isFull is true
        this.teamMembers = teamMembers; // create the teamMembers hashMap in resources
        this.memRequests = new ArrayList<String>();
        this.isFull = false;
        this.isFinalized = false;

    }
    
}
