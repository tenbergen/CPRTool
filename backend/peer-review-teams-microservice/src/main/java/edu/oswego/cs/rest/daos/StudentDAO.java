package edu.oswego.cs.rest.daos;

import lombok.Data;
import lombok.NonNull;

import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;

@Entity
@Data
public class StudentDAO {
    @Id
    public @JsonbProperty("course_id") @NonNull String studentID; // email
    /* for team*/
    public @JsonbProperty("team_id") String teamID;
    public @JsonbProperty("team_lead") boolean isLead;
    public @JsonbProperty("finalized") boolean confirmFinalized;
    /* course */
    public @JsonbProperty("course_id") @NonNull ArrayList<String> courses;

    public StudentDAO(@JsonbProperty("email") String studentID) {
        this.studentID = studentID;
        this.teamID = "";
        this.isLead = false;
        this.confirmFinalized = false;
    }
}
