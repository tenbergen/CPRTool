package edu.oswego.cs.daos;

import com.mongodb.lang.NonNull;
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
public class TeamDAO {
    @Id @JsonbProperty("team_id") public Integer teamID;
    @JsonbProperty("course_id") public String courseID;
    @JsonbProperty("student_id") public String studentID;
    @JsonbProperty("team_lead") public String teamLead;
    @JsonbProperty("team_members") @ElementCollection public List<String> teamMembers;
    @JsonbProperty("team_name") public String teamName;
    @JsonbProperty("team_size") public Integer teamSize;
    @JsonbProperty("is_finalized") @ElementCollection public List<String> isFinalized;

    @JsonbCreator
    public TeamDAO(
            @NonNull @JsonbProperty("course_id") String courseID,
            @NonNull @JsonbProperty("student_id") String studentID) {
        this.teamID = null;
        this.courseID = courseID;
        this.studentID = studentID;
        this.teamLead = null;
        this.teamMembers = new ArrayList<>();
        this.teamName = null;
        this.teamSize = null;
        this.isFinalized = new ArrayList<>();
    }
}