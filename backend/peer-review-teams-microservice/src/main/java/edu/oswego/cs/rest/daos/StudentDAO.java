package edu.oswego.cs.rest.daos;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.json.bind.annotation.JsonbProperty;


@Entity
@Data
@AllArgsConstructor
public class StudentDAO {
    @Id public @JsonbProperty("Email") @NonNull String email;
    /* team-MS */
    public @JsonbProperty("TeamID") String teamID;
    public @JsonbProperty("Team Lead") boolean isLead;
    public @JsonbProperty("Finalized") boolean confirmFinalized;
    /* end team-MS */

    public @JsonbProperty("Abbreviation") @NonNull String abbreviation;
    public @JsonbProperty("CourseSection") @NonNull int courseSection;
    public @JsonbProperty("Semester") @NonNull String semester;
    public @JsonbProperty("CourseName") @NonNull String courseName;

}
