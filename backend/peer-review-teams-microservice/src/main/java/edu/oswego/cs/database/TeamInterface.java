package edu.oswego.cs.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import edu.oswego.cs.daos.TeamDAO;
import edu.oswego.cs.requests.SwitchTeamParam;
import edu.oswego.cs.requests.TeamParam;
import edu.oswego.cs.services.SecurityService;

import org.bson.Document;
import org.bson.conversions.Bson;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class TeamInterface {
    private final MongoCollection<Document> courseCollection;
    private final MongoCollection<Document> studentCollection;
    private final MongoCollection<Document> teamCollection;

    /**
     * Default constructor initializes database connections and retrieves needed collections 
     */
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
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to retrieve collections.").build());
        }
    }

    /**
     * Allows user to create and write a team to TeamDatabase
     * @param request TeamDAO:{"team_id", "course_id", "team_lead", "team_size"}
     */
    public void createTeam(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        
        if (!new SecurityService().isStudentValid(courseDocument, request)) 
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Student not found in this course.").build());

        MongoCursor<Document> cursor = teamCollection.find().iterator();
        if (cursor == null) 
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to retrieve team collection.").build());

        if (cursor.hasNext()) {
            if (new SecurityService().isStudentAlreadyInATeam(teamCollection, request)) 
                throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Student is already in a team.").build());
                
            if (new SecurityService().isTeamCreated(teamCollection, request)) {
                throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Team is already created.").build());
            }
        }
            
            
        TeamDAO newTeam = new TeamDAO(request.getTeamID(), request.getCourseID(), request.getMaxSize(), request.getStudentID() );
        newTeam.getTeamMembers().add(request.getStudentID());
        newTeam.setTeamMembers(newTeam.getTeamMembers());
        
        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> courseDAOEntity = Entity.entity(jsonb.toJson(newTeam), MediaType.APPLICATION_JSON_TYPE);
        Document teamDocument = Document.parse(courseDAOEntity.getEntity());
        teamCollection.insertOne(teamDocument);
    }

    /**
     * Gets all teams in a course
     * @param request TeamParam:{"course_id"}
     * @return
     */
    public List<Document> getAllTeams(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        
        MongoCursor<Document> cursor = teamCollection.find().iterator();
        if (cursor == null)
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to retrieve team collection.").build());
        
        List<Document> teams = new ArrayList<>();

        try { 
            while(cursor.hasNext()) { 
                Document teamDocument = cursor.next();
                if (teamDocument.get("course_id").toString().equals(request.getCourseID())) {
                    teams.add(teamDocument);
                }
            } 
        } finally { 
            cursor.close(); 
        } 
        return teams;
    }

    /**
     * If requested student is not already in a team, shows non-full-teams
     * If requested student is already in a team, shows their team
     * @param request
     * @return
     */
    public List<Document> getTeamByStudentID(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        
        if (!new SecurityService().isStudentValid(courseDocument, request)) 
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Student not found in this course.").build());

        List<Document> nonFullTeams = new ArrayList<>();
        MongoCursor<Document> cursor = teamCollection.find().iterator();
        if (cursor == null) 
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to retrieve team collection.").build());

        try { 
            while(cursor.hasNext()) { 
                Document teamDocument = cursor.next();
                List<String> members = teamDocument.getList("team_members", String.class);
                String courseID = teamDocument.get("course_id").toString();
                
                for (String member : members) {
                    List<Document> teams = new ArrayList<>();
                    if (request.getStudentID().equals(member) && request.getCourseID().equals(courseID)){
                        teams.add(teamDocument);
                        return teams; 
                    }
                }

                if (!teamDocument.getBoolean("is_full") && request.getCourseID().equals(courseID)) 
                    nonFullTeams.add(teamDocument);
                
            }
        } finally { 
            cursor.close();
        } 

        return nonFullTeams;
    }


    public void joinTeam(TeamParam request) {
        Document teamDocument = teamCollection.find(eq("team_id", request.getTeamID())).first();
        if (teamDocument == null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This team does not exist.").build());

        List<String> teamMembers = teamDocument.getList("team_members", String.class);
        for (String member : teamMembers) {
            if (teamMembers.size() >= request.getMaxSize()) throw new WebApplicationException(Response.status(Response.Status.OK).entity("This team is already full.").build());
            if (request.getStudentID().equals(member)) throw new WebApplicationException(Response.status(Response.Status.OK).entity("This student is already in the team.").build());
        }
        teamMembers.add(request.getStudentID());
    }

    

    public Document getTeamByTeamIDHandler(TeamParam request) {
        /* desc: get team with teamID */
        try {
            Document courseDoc = courseCollection.find(new Document("course_id", request.getCourseID())).first();
            List<Document> teams = courseDoc.getList("teams", Document.class);
            Document targetTeam = new Document();

            for (Document team : teams) {
                if (team.getString("team_id").equals(request.getTeamID())) targetTeam = team;
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
            Document courseDoc = courseCollection.find(new Document("course_id", request.getCourse_id())).first();
            Document studentDoc = studentCollection.find(new Document("student_id", request.getStudent_id())).first();
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
                    new Document("course_id", request.getCourse_id()),
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
                    Document nextLeadDoc = studentCollection.find(new Document("student_id", nextLeadID)).first();
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