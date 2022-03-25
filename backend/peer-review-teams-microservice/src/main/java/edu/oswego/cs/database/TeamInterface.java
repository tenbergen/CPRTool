package edu.oswego.cs.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import edu.oswego.cs.daos.TeamDAO;
import edu.oswego.cs.requests.SwitchTeamParam;
import edu.oswego.cs.requests.TeamParam;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeamInterface {
    private MongoDatabase courseDB;
    private MongoDatabase studentDB;
    private MongoCollection<Document> courseCollection;
    private MongoCollection<Document> studentCollection;
    private final String courseID = "course_id";
    private final String studentID = "student_id";

    public TeamInterface() {
        DatabaseManager databaseManager = new DatabaseManager();
        try {
            courseDB = databaseManager.getCourseDB();
            studentDB = databaseManager.getStudentDB();
            courseCollection = courseDB.getCollection("courses");
            studentCollection = studentDB.getCollection("students");
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    /**
     * Calculates the maximum number of teams in a course by dividing the total number of students by the determined
     * team size. If there are left out students, create a new team and take one student from each of other full teams
     * and add to the new team.
     * Returns an array of integers representing the team size for each team.
     */
    public ArrayList<Integer> initTeamHandler(TeamParam request) {
        try {
            Document courseDoc = courseDB.getCollection("courses").find(new Document(courseID, request.getCourse_id())).first();
            @SuppressWarnings("unchecked")
            ArrayList<String> students = (ArrayList<String>) courseDoc.get("students");

            int totalStudent = students.size();
            int teamSize = request.getTeam_size();
            int maxTeams = totalStudent / teamSize; // maximum number of teams in a course
            int remainingStudents = totalStudent % teamSize;

            if (remainingStudents > 0) maxTeams += 1; // The remaining students can add up to one more team

            ArrayList<Integer> teamsMemArray = new ArrayList<>(Collections.nCopies(maxTeams, teamSize)); // fill the ArrayList with the value of teamSize

            for (int i = 0; i < (maxTeams * teamSize - totalStudent); i++) {
                teamsMemArray.set(i, teamsMemArray.get(i) - 1); //
            }

            teamsMemArray.sort(Collections.reverseOrder());
            return teamsMemArray;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Integer joinTeamHandler(TeamParam request) {
        /*
         - desc: allow students to join teams
         - Notes:
            + fix isStudentIDvalid => wrong studentID still works (just for safety purpose. FE should make sure the studentID is correct before send it to BE)
        */

        try {
            // >>> Initialization <<<
            Document courseDoc = courseCollection.find(new Document(courseID, request.getCourse_id())).first();
            Document studentDoc = studentCollection.find(new Document(studentID, request.getStudent_id())).first();
            List<Document> teams = courseDoc.getList("teams", Document.class);
            Document targetTeamDoc = new Document();
            Document teamMember = new Document();
            Integer teamSize = 0;
            boolean isTeamAdded = false;
            Boolean isFull = false;

            for (Document team : teams) {
                if (team.get("team_id").equals(request.getTeam_id())) {
                    targetTeamDoc = team;
                    isTeamAdded = true;
                    teamSize = (Integer) targetTeamDoc.get("team_size");
                    isFull = (Boolean) targetTeamDoc.get("is_full");
                }
            }
            if (!isFull) {
                if (isTeamAdded) {
                    /*
                     - the team with newTeamID is already added
                     - the student does not have a chance to be a team lead -- OR student can still be a team lead if the team is created
                     - just add the student to the teams.teamMember and set value of isStudentFinalized to false
                     - update isFull
                    */

                    if (teamSize == request.getTeam_size()) { // safety purpose
                        // >>> Team Document <<<
                        teamMember = (Document) targetTeamDoc.get("team_members");
                        /* if the team is created then left empty -> first one join is the team lead*/
                        if (teamMember.size() == 0) {
                            Bson leadUpdates = Updates.set("team_lead", true);
                            UpdateOptions leadOptions = new UpdateOptions().upsert(true);
                            studentCollection.updateOne(studentDoc, leadUpdates, leadOptions);
                        }
                        teamMember.append(request.getStudent_id(), false); // automatically update targetTeamDoc Document
                        if (teamMember.size() == teamSize) {
                            targetTeamDoc.replace("is_full", false, true);
                        }

                        // >>> Student Document <<<
                        Bson updates = Updates.set("team_id", request.getTeam_id());
                        UpdateOptions options = new UpdateOptions().upsert(true);

                        // >>> Update to mongo <<<
                        try {
                            studentCollection.updateOne(studentDoc, updates, options);
                            courseCollection.updateOne(
                                    new Document(courseID, request.getCourse_id()),
                                    new Document("$set", new Document("teams", teams))
                            );
                            return 0;
                        } catch (Exception e) {
                            return -1;
                        }
                    } else {
                        return 1;
                    }

                } else {
                    /*
                     - the team is not added yet
                     - make new teamDAO
                     - update teamSize passed from FE
                     - add student to newTeamDoc
                     - make the studentID be team lead
                     - add teamID to studentDoc
                     - add the new team
                    */

                    // >>> Team Document <<<
                    TeamDAO newTeam = new TeamDAO(request.getTeam_id());
                    Jsonb jsonb = JsonbBuilder.create();
                    Entity<String> teamDAOEntity = Entity.entity(jsonb.toJson(newTeam), MediaType.APPLICATION_JSON_TYPE);
                    Document newTeamDoc = Document.parse(teamDAOEntity.getEntity());
                    newTeamDoc.replace("team_size", 0, request.getTeam_size());
                    teamMember = (Document) newTeamDoc.get("team_members");
                    teamMember.append(request.getStudent_id(), false);
                    teams.add(newTeamDoc);

                    // >>> Student Document <<<
                    Bson updates = Updates.combine(
                            Updates.set("team_lead", true),
                            Updates.set("team_id", request.getTeam_id())
                    );
                    UpdateOptions options = new UpdateOptions().upsert(true);

                    // >>> Update to mongo <<<
                    try {
                        studentCollection.updateOne(studentDoc, updates, options);
                        courseCollection.updateOne(new Document(courseID, request.getCourse_id()), new Document("$set", new Document("teams", teams)));
                        return 0;

                    } catch (Exception e) {
                        return -1;
                    }
                }
            } else {
                return 2;
            }

        } catch (Exception e) {
            return -1;
        }
    }

    public List<Document> getAllTeamsHandler(String courseID) {
        /* desc: get A list of all teams */
        try {
            Document courseDoc = courseCollection.find(new Document(this.courseID, courseID)).first();
            List<Document> teams = courseDoc.getList("teams", Document.class);
            return teams;
        } catch (Exception e) {
            List<Document> errors = new ArrayList<>();
            errors.add(new Document(e.toString(), Exception.class));
            return errors;
        }
    }

    public Document getTeamByTeamIDHandler(TeamParam request) {
        /* desc: get team with teamID */
        try {
            Document courseDoc = courseCollection.find(new Document(courseID, request.getCourse_id())).first();
            List<Document> teams = courseDoc.getList("teams", Document.class);
            Document targetTeam = new Document();

            for (Document team : teams) {
                if (team.getString("team_id").equals(request.getTeam_id())) targetTeam = team;
            }
            return targetTeam;
        } catch (Exception e) {
            return new Document(e.toString(), Exception.class);
        }
    }

    public int switchTeamHandler(SwitchTeamParam request) {
        /* desc: get A list of all teams to join teams */

        /* logic
            Student can switch to other teams as long as the team is not finalized

            A student switch from teamA to teamB

            teamA:
                + remove student
                + if the removed student was a team lead => randomly grant the team lead to another member
                + update removed student from team lead => false
                + if team is full -> update not full
            teamB:
                + call joinTeam() handles team with members already and empty team
        */

        try {
            // Initialize.
            Document courseDoc = courseCollection.find(new Document(courseID, request.getCourse_id())).first();
            Document studentDoc = studentCollection.find(new Document(studentID, request.getStudent_id())).first();
            List<Document> teams = courseDoc.getList("teams", Document.class);
            Document oldTeam = new Document();
            Document teamMember = new Document();
            boolean isFull = false;
            Boolean isTeamLead = studentDoc.getBoolean("team_lead");

            // Get oldTeam.
            for (Document team : teams) {
                if (team.get("team_id").equals(request.getOld_team_id())) {
                    oldTeam = team;
                    isFull = oldTeam.getBoolean("is_full");
                }
            }

            // Remove student from oldTeam.
            teamMember = (Document) oldTeam.get("team_members");
            teamMember.remove(request.getStudent_id(), false);

            // Update isFull for oldTeam.
            if (isFull) oldTeam.replace("is_full", true, false);
            courseCollection.updateOne(
                    new Document(courseID, request.getCourse_id()),
                    new Document("$set", new Document("teams", teams))
            );

            if (isTeamLead) {
                Bson teamLeadUpdates = Updates.set("team_lead", false);
                UpdateOptions teamLeadOptions = new UpdateOptions().upsert(true);
                studentCollection.updateOne(studentDoc, teamLeadUpdates, teamLeadOptions);

                // Attempt to pass team lead to the next member.
                List<String> membersID = new ArrayList<>(teamMember.keySet());
                if (membersID.size() > 0) {
                    String nextLeadID;
                    nextLeadID = membersID.get(0);
                    Document nextLeadDoc = studentCollection.find(new Document(studentID, nextLeadID)).first();
                    Bson nextLeadUpdates = Updates.set("team_lead", true);
                    UpdateOptions nextLeadOptions = new UpdateOptions().upsert(true);
                    studentCollection.updateOne(nextLeadDoc, nextLeadUpdates, nextLeadOptions);
                }
            }
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }
}