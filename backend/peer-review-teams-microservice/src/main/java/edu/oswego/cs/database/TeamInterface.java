package edu.oswego.cs.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import edu.oswego.cs.daos.TeamDAO;
import edu.oswego.cs.requests.SwitchTeamParam;
import edu.oswego.cs.requests.TeamParam;
import edu.oswego.cs.services.SecurityService;
import edu.oswego.cs.services.TeamService;
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
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to retrieve collections.").build());
        }
    }

    public void createTeam(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        new SecurityService().createTeamSecurity(courseDocument, request);

        String teamID = new TeamService().generateTeamID(request.getCourseID());
        int teamSize = new TeamService().getTeamSize(courseDocument);
        TeamDAO newTeam = new TeamDAO(teamID, request.getCourseID(), teamSize, request.getStudentID());
        newTeam.getTeamMembers().add(request.getStudentID());
        newTeam.setTeamMembers(newTeam.getTeamMembers());

        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> courseDAOEntity = Entity.entity(jsonb.toJson(newTeam), MediaType.APPLICATION_JSON_TYPE);
        Document teamDocument = Document.parse(courseDAOEntity.getEntity());
        teamCollection.insertOne(teamDocument);
    }

    public List<Document> getAllUnlockedTeamByStudentID(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        if (!new SecurityService().isStudentValid(courseDocument, request.getStudentID())) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());
        List<Document> allUnlockedTeams = new ArrayList<>();
        Bson teamDocumentFilter = Filters.and(eq("course_id", request.getCourseID()), eq("team_lock", false));
        MongoCursor<Document> cursor = teamCollection.find(teamDocumentFilter).iterator();
        while (cursor.hasNext()) {
            Document teamDocument = cursor.next();
            allUnlockedTeams.add(teamDocument);
        }
        cursor.close();
        return allUnlockedTeams;
    }

    public Document getTeamByTeamID(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        if (!new SecurityService().isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
        return teamCollection.find(teamDocumentFilter).first();
    }

        MongoCursor<Document> cursor = teamCollection.find(eq("course_id", request.getCourseID())).iterator();
        while (cursor.hasNext()) {
            Document teamDocument = cursor.next();
            String teamID = teamDocument.getString("team_id");
            if (request.getTeamID().equals(teamID)) {
                cursor.close();
                return teamDocument;
            }
        }
        throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
    }

    public void joinTeam(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        new SecurityService().joinTeamSecurity(teamCollection, courseDocument, request);

        Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
        Document teamDocument = teamCollection.find(teamDocumentFilter).first();
        List<String> teamMembers = teamDocument.getList("team_members", String.class);
        teamMembers.add(request.getStudentID());
        teamCollection.updateOne(teamDocumentFilter, Updates.set("team_members", teamMembers));
        if (teamMembers.size() >= teamDocument.getInteger("team_size")) 
            teamCollection.updateOne(teamDocumentFilter, Updates.set("team_full", true));
    }
    
    public void switchTeam(SwitchTeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        new SecurityService().switchTeamSecurity(teamCollection, courseDocument, request);

        Bson currentTeamDocumentFilter = Filters.and(eq("team_id", request.getCurrentTeamID()), eq("course_id", request.getCourseID()));
        Document currentTeamDocument = teamCollection.find(currentTeamDocumentFilter).first();
        List<String> currentTeamMembers = currentTeamDocument.getList("team_members", String.class);
        currentTeamMembers.remove(request.getStudentID());
        if (currentTeamMembers.size() < 1) 
            teamCollection.deleteOne(currentTeamDocumentFilter);
        else {
            Bson currentTeamUpdates = Updates.combine(
                Updates.set("team_members", currentTeamMembers),
                Updates.set("team_lead", currentTeamMembers.get(0)),
                Updates.set("team_full", false));
            UpdateOptions currentTeamOptions = new UpdateOptions().upsert(true);
            teamCollection.updateOne(currentTeamDocumentFilter, currentTeamUpdates, currentTeamOptions);
        }

        Bson targetTeamDocumentFilter = Filters.and(eq("team_id", request.getTargetTeamID()), eq("course_id", request.getCourseID()));
        Document targetTeamDocument = teamCollection.find(targetTeamDocumentFilter).first();
        List<String> targetTeamMembers = targetTeamDocument.getList("team_members", String.class);
        targetTeamMembers.add(request.getStudentID());
        Bson targetTeamUpdates = Updates.combine(
            Updates.set("team_members", targetTeamMembers),
            Updates.set("team_full", false));
        if (targetTeamMembers.size() >= targetTeamDocument.getInteger("team_size"))
            targetTeamUpdates = Updates.combine(targetTeamUpdates, Updates.set("team_full", true));
        UpdateOptions targetTeamOptions = new UpdateOptions().upsert(true);
        teamCollection.updateOne(targetTeamDocumentFilter, targetTeamUpdates, targetTeamOptions);
    }


            if (request.getTargetTeamID().equals(teamDocumentTeamID) && request.getCourseID().equals(teamDocumentCouurseID)) {
                List<String> targetTeamMembers = targetTeamDocument.getList("team_members", String.class);
                targetTeamMembers.add(request.getStudentID());
                
                Bson targetTeamDocumentFilter = Filters.and(eq("team_id", request.getTargetTeamID()), eq("course_id", request.getCourseID()));
                Bson targetTeamUpdates = Updates.combine(
                        Updates.set("team_members", targetTeamMembers),
                        Updates.set("is_full", false));

                if (targetTeamMembers.size() >= targetTeamDocument.getInteger("team_size"))
                    targetTeamUpdates = Updates.combine(targetTeamUpdates, Updates.set("is_full", true));

                UpdateOptions targetTeamOptions = new UpdateOptions().upsert(true);
                teamCollection.updateOne(targetTeamDocumentFilter, targetTeamUpdates, targetTeamOptions);
            }
        }
    }

    public void generateTeamName(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        new SecurityService().generateTeamNameSecurity(teamCollection, courseDocument, request);

        Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
        Bson teamNameUpdates = Updates.combine(
            Updates.set("team_id", request.getTeamName()),
            Updates.set("team_lock", true));
        UpdateOptions teamNameOptions = new UpdateOptions().upsert(true);
        teamCollection.updateOne(teamDocumentFilter, teamNameUpdates, teamNameOptions);
    }

        }
    }

    public void toggleTeamLock(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        if (!new SecurityService().isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());

        Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
        Document teamDocument = teamCollection.find(teamDocumentFilter).first();
        boolean teamLock = teamDocument.getBoolean("team_lock");
        teamCollection.updateOne(teamDocumentFilter, Updates.set("team_lock", !teamLock));
    }

    public void lockAllTeams(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());

        Bson teamDocumentFilter = eq("course_id", request.getCourseID());
        Bson teamDocumentUpdate = Updates.set("team_lock", true);
        teamCollection.updateMany(teamDocumentFilter, teamDocumentUpdate);
    }

    public void addStudentToTeam(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        new SecurityService().addStudentToTeamSecurity(teamCollection, courseDocument, request);

        if (!new SecurityService().isStudentAlreadyInATeam(request.getStudentID(), request.getCourseID())) 
            joinTeam(request);
        else if (new SecurityService().isStudentAlreadyInATeam(request.getStudentID(), request.getCourseID())) {
            String currentTeamID = new TeamService().retrieveTeamID(request);
            SwitchTeamParam switchTeamParam = new SwitchTeamParam(request.getCourseID(), request.getStudentID(), currentTeamID, request.getTeamID());
            switchTeam(switchTeamParam);
        }
    }

    public void removeStudent(TeamParam request) {
    public void editTeamName(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        new SecurityService().editTeamNameSecurity(teamCollection, request);

        Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
        Bson editTeamNameUpdates = Updates.combine(
            Updates.set("team_id", request.getTeamName()),
            Updates.set("team_lock", true)
        );
        UpdateOptions editTeamNameOptions = new UpdateOptions().upsert(true);
        teamCollection.updateOne(teamDocumentFilter, editTeamNameUpdates, editTeamNameOptions);
    }

    public void editTeamName(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
    }

    public void assignTeamLead(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
    }

    public void deleteTeam(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Team is locked.").build());
    }

    public List<Document> getAllStudentsInThisCourse(String courseID) {
        MongoCursor<Document> cursor = studentCollection.find().iterator();
        List<Document> students = new ArrayList<>();
        while (cursor.hasNext()) {
            Document studentDocument = cursor.next();
            List<String> courses = studentDocument.getList("courses", String.class);
            for (String course : courses) {
                if (course.equals(courseID)) students.add(studentDocument);
            }
        }
        cursor.close();
        return students;
    }
}