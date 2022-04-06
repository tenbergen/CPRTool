package edu.oswego.cs.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import edu.oswego.cs.daos.TeamDAO;
import edu.oswego.cs.requests.SwitchTeamParam;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class TeamInterface {
    private final MongoCollection<Document> courseCollection;
    private final MongoCollection<Document> studentCollection;
    private final MongoCollection<Document> teamCollection;

    public TeamInterface() {
        DatabaseManager databaseManager = new DatabaseManager();
        try {
            MongoDatabase studentDB = databaseManager.getStudentDB();
            MongoDatabase courseDB = databaseManager.getCourseDB();
            MongoDatabase teamDB = databaseManager.getTeamDB();
            studentCollection = studentDB.getCollection("students");
            courseCollection = courseDB.getCollection("courses");
            teamCollection = teamDB.getCollection("teams");
        } catch (WebApplicationException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to retrieve collections.").build());
        }
    }

    /**
     * Calculates the maximum number of teams in a course by dividing the total number of students by the determined
     * team size. If there are left out students, create a new team and take one student from each of other full teams
     * and add to the new team. Finally, initialize the teams in the database with such team sizes accordingly.
     */
    public void initializeTeams(TeamDAO dao) {
        Document courseDocument = courseCollection.find(eq("course_id", dao.courseID)).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to retrieve collections.").build());
        @SuppressWarnings("unchecked") ArrayList<String> students = (ArrayList<String>) courseDocument.get("students");

        int totalStudent = students.size();
        int teamSize = dao.teamSize;
        int maxTeams = totalStudent / teamSize;
        int remainingStudents = totalStudent % teamSize;

        if (remainingStudents > 0) maxTeams += 1;
        ArrayList<Integer> team = new ArrayList<>(Collections.nCopies(maxTeams, teamSize));
        for (int i = 0; i < ((maxTeams * teamSize) - totalStudent); i++) {
            team.set(i, team.get(i) - 1);
        }

        team.sort(Collections.reverseOrder());

        int index = 0;
        for (int size : team) {
            TeamDAO newTeam = new TeamDAO();
            newTeam.courseID = dao.courseID;
            newTeam.teamID = index;
            newTeam.teamSize = size;
            index += 1;

            Jsonb jsonb = JsonbBuilder.create();
            Entity<String> courseDAOEntity = Entity.entity(jsonb.toJson(newTeam), MediaType.APPLICATION_JSON_TYPE);
            Document teamDocument = Document.parse(courseDAOEntity.getEntity());
            teamCollection.insertOne(teamDocument);
        }
    }

    public void joinTeam(TeamDAO dao) {
        Document teamDocument = teamCollection.find(eq("team_id", dao.teamID)).first();
        if (teamDocument == null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This team does not exist.").build());

        List<String> teamMembers = teamDocument.getList("team_members", String.class);
        for (String member : teamMembers) {
            if (teamMembers.size() >= dao.teamSize) throw new WebApplicationException(Response.status(Response.Status.OK).entity("This team is already full.").build());
            if (dao.studentID.equals(member)) throw new WebApplicationException(Response.status(Response.Status.OK).entity("This student is already in the team.").build());
        }
        teamMembers.add(dao.studentID);
    }

    public List<Document> getAllTeams(String courseID) {
        Document courseDoc = courseCollection.find(eq(this.courseID, courseID)).iterator();
        try {

            List<Document> teams = courseDoc.getList("teams", Document.class);
            return teams;
        } catch (Exception e) {
            List<Document> errors = new ArrayList<>();
            errors.add(new Document(e.toString(), Exception.class));
            return errors;
        }
    }

    public Document getTeam(TeamParam request) {
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