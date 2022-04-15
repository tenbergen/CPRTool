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
        if (!new SecurityService().isStudentValid(courseDocument, request.getStudentID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());
        List<Document> allUnlockedTeams = new ArrayList<>();
        Bson teamDocumentFilter = Filters.and(eq("course_id", request.getCourseID()), eq("team_lock", false));
        MongoCursor<Document> cursor = teamCollection.find(teamDocumentFilter).iterator();
        while (cursor.hasNext()) {
            Document teamDocument = cursor.next();
            allUnlockedTeams.add(teamDocument);
        }
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

    public void studentJoinTeam(TeamParam request) {
        if (!new SecurityService().isStudentAlreadyInATeam(request.getStudentID(), request.getCourseID()))
            joinTeam(request);
        else if (new SecurityService().isStudentAlreadyInATeam(request.getStudentID(), request.getCourseID())) {
            String currentTeamID = new TeamService().retrieveTeamID(request);
            SwitchTeamParam switchTeamParam = new SwitchTeamParam(request.getCourseID(), request.getStudentID(), currentTeamID, request.getTeamID());
            switchTeam(switchTeamParam);
        }
    }

    public void joinTeam(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        new SecurityService().joinTeamSecurity(teamCollection, courseDocument, request);

        Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
        Document teamDocument = teamCollection.find(teamDocumentFilter).first();
        if (teamDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        
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
        if (currentTeamDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());

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
        if (targetTeamDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());

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

    public void giveUpTeamLead(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        new SecurityService().giveUpTeamLeadSecurity(teamCollection, courseDocument, request);

        Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
        Document teamDocument = teamCollection.find(teamDocumentFilter).first();
        if (teamDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());

        List<String> students = teamDocument.getList("team_members", String.class);
        students.remove(request.getStudentID());
        students.add(request.getStudentID());
        Bson assignTeamLeadUpdates = Updates.combine(
                Updates.set("team_lead", students.get(0)),
                Updates.set("team_members", students)
        );
        UpdateOptions assignTeamLeadOptions = new UpdateOptions().upsert(true);
        teamCollection.updateOne(teamDocumentFilter, assignTeamLeadUpdates, assignTeamLeadOptions);
    }

    public void nominateTeamLead(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        new SecurityService().nominateTeamLeadSecurity(teamCollection, courseDocument, request);

        Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
        Document teamDocument = teamCollection.find(teamDocumentFilter).first();
        if (teamDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());

        List<String> students = teamDocument.getList("team_members", String.class);
        students.remove(request.getNominatedTeamLead());
        Collections.reverse(students);
        students.add(request.getNominatedTeamLead());
        Collections.reverse(students);
        Bson assignTeamLeadUpdates = Updates.combine(
                Updates.set("team_lead", request.getNominatedTeamLead()),
                Updates.set("team_members", students)
        );
        UpdateOptions assignTeamLeadOptions = new UpdateOptions().upsert(true);
        teamCollection.updateOne(teamDocumentFilter, assignTeamLeadUpdates, assignTeamLeadOptions);
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

    public List<Document> getAllTeams(String courseID) {
        Document courseDocument = courseCollection.find(eq("course_id", courseID)).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());

        MongoCursor<Document> cursor = teamCollection.find(eq("course_id", courseID)).iterator();
        List<Document> teams = new ArrayList<>();
        while (cursor.hasNext()) {
            Document teamDocument = cursor.next();
            teams.add(teamDocument);
        }
        return teams;
    }

    public void leaveTeam(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        new SecurityService().leaveTeamSecurity(teamCollection, courseDocument, request);

        Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
        Document teamDocument = teamCollection.find(teamDocumentFilter).first();
        if (teamDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());

        List<String> teamMembers = teamDocument.getList("team_members", String.class);
        teamMembers.remove(request.getStudentID());

        if (teamMembers.size() < 1)
            teamCollection.deleteOne(teamDocumentFilter);
        else {
            Bson teamUpdates = Updates.combine(
                    Updates.set("team_members", teamMembers),
                    Updates.set("team_lead", teamMembers.get(0)),
                    Updates.set("team_full", false));
            UpdateOptions teamOptions = new UpdateOptions().upsert(true);
            teamCollection.updateOne(teamDocumentFilter, teamUpdates, teamOptions);
        }
    }

    public void toggleTeamLock(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        if (!new SecurityService().isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());

        Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
        Document teamDocument = teamCollection.find(teamDocumentFilter).first();
        if (teamDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());

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

    public void editTeamSizeInBulk(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        Bson editTeamSizeInBulkUpdates = Updates.set("team_size", request.getTeamSize());
        teamCollection.updateMany(eq("course_id", request.getCourseID()), editTeamSizeInBulkUpdates);
    }

    public void editTeamSize(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        new SecurityService().editTeamSizeSecurity(teamCollection, request);
        Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
        Bson editTeamSizeUpdates = Updates.set("team_size", request.getTeamSize());
        teamCollection.updateOne(teamDocumentFilter, editTeamSizeUpdates);
    }

    public void assignTeamLead(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        new SecurityService().assignTeamLeadSecurity(teamCollection, courseDocument, request);

        Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
        Document teamDocument = teamCollection.find(teamDocumentFilter).first();
        if (teamDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());

        List<String> students = teamDocument.getList("team_members", String.class);
        students.remove(request.getStudentID());
        Collections.reverse(students);
        students.add(request.getStudentID());
        Collections.reverse(students);
        Bson assignTeamLeadUpdates = Updates.combine(
                Updates.set("team_lead", request.getStudentID()),
                Updates.set("team_members", students)
        );
        UpdateOptions assignTeamLeadOptions = new UpdateOptions().upsert(true);
        teamCollection.updateOne(teamDocumentFilter, assignTeamLeadUpdates, assignTeamLeadOptions);
    }

    public void deleteTeam(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        if (!new SecurityService().isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (new SecurityService().isTeamLock(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Team is locked.").build());
        Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
        teamCollection.deleteOne(teamDocumentFilter);
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
        return students;
    }
}