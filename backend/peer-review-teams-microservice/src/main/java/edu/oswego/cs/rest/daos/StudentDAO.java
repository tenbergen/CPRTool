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
    public @JsonbProperty("studentID") @NonNull String studentID; // email
    /* for team*/
    public @JsonbProperty("TeamID") String teamID;
    public @JsonbProperty("TeamLead") boolean isLead;
    public @JsonbProperty("Finalized") boolean confirmFinalized;
    /* course */
    public @JsonbProperty("CourseID") @NonNull ArrayList<String> courses;

    public StudentDAO(@JsonbProperty("Email") String studentID) {
        this.studentID = studentID;
        this.teamID = "";
        this.isLead = false;
        this.confirmFinalized = false;
    }
}
