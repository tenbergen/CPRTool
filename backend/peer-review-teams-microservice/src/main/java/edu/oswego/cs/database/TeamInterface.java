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

        String teamID = new TeamService().generateTeamID(teamCollection);
        int teamSize = new TeamService().getTeamSize(courseDocument);
        TeamDAO newTeam = new TeamDAO(teamID, request.getCourseID(), teamSize, request.getStudentID());
        newTeam.getTeamMembers().add(request.getStudentID());
        newTeam.setTeamMembers(newTeam.getTeamMembers());

        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> courseDAOEntity = Entity.entity(jsonb.toJson(newTeam), MediaType.APPLICATION_JSON_TYPE);
        Document teamDocument = Document.parse(courseDAOEntity.getEntity());
        teamCollection.insertOne(teamDocument);
    }

    public List<Document> getAllTeams(String courseID) {
        Document courseDocument = courseCollection.find(eq("course_id", courseID)).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());

        MongoCursor<Document> cursor = teamCollection.find().iterator();
        List<Document> teams = new ArrayList<>();
        while (cursor.hasNext()) {
            Document teamDocument = cursor.next();
            if (teamDocument.getString("course_id").equals(courseID)) teams.add(teamDocument);
        }
        return teams;
    }

    /**
     * If requested student is not already in a team, shows non-full-teams.
     * If requested student is already in a team, shows their team.
     *
     * @param request TeamParam:{"course_id", "student_id"}
     * @return List<Document> of teams
     */
    public List<Document> getTeamByStudentID(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());

        if (!new SecurityService().isStudentValid(courseDocument, request.getStudentID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());

        List<Document> nonFullTeams = new ArrayList<>();
        MongoCursor<Document> cursor = teamCollection.find().iterator();

        while (cursor.hasNext()) {
            Document teamDocument = cursor.next();
            List<String> members = teamDocument.getList("team_members", String.class);
            String courseID = teamDocument.getString("course_id");

            for (String member : members) {
                List<Document> teams = new ArrayList<>();
                if (request.getStudentID().equals(member) && request.getCourseID().equals(courseID)) {
                    teams.add(teamDocument);
                    cursor.close();
                    return teams;
                }
            }
            if (!teamDocument.getBoolean("is_full") && request.getCourseID().equals(courseID)) nonFullTeams.add(teamDocument);
        }
        return nonFullTeams;
    }

    public Document getTeamByTeamID(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());

        MongoCursor<Document> cursor = teamCollection.find().iterator();
        while (cursor.hasNext()) {
            Document teamDocument = cursor.next();
            String courseID = teamDocument.getString("course_id");
            String teamID = teamDocument.getString("team_id");
            if (request.getTeamID().equals(teamID) && request.getCourseID().equals(courseID)) {
                cursor.close();
                return teamDocument;
            }
        }
        throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
    }

    public void joinTeam(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        new SecurityService().joinTeamSecurity(courseDocument, request);

        List<Document> teamDocuments = getAllTeams(request.getCourseID());
        if (teamDocuments == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            String teamDocumentTeamID = teamDocument.getString("team_id");
            String teamDocumentCourseID = teamDocument.getString("course_id");
            List<String> teamMembers = teamDocument.getList("team_members", String.class);

            if (request.getTeamID().equals(teamDocumentTeamID) && request.getCourseID().equals(teamDocumentCourseID)) {
                teamMembers.add(request.getStudentID());
                Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
                teamCollection.findOneAndUpdate(teamDocumentFilter, Updates.set("team_members", teamMembers));
                if (teamMembers.size() >= teamDocument.getInteger("team_size")) teamCollection.updateOne(teamDocumentFilter, Updates.set("is_full", true));
            }
        }
    }

    public void switchTeam(SwitchTeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        new SecurityService().switchTeamSecurity(courseDocument, request);

        List<Document> currentTeamDocuments = getAllTeams(request.getCourseID());
        if (currentTeamDocuments == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document currentTeamDocument : currentTeamDocuments) {
            String teamDocumentTeamID = currentTeamDocument.getString("team_id");
            String teamDocumentCourseID = currentTeamDocument.getString("course_id");

            if (request.getCurrentTeamID().equals(teamDocumentTeamID) && request.getCourseID().equals(teamDocumentCourseID)) {
                Bson currentTeamDocumentFilter = Filters.and(eq("team_id", request.getCurrentTeamID()), eq("course_id", request.getCourseID()));
                List<String> currentTeamMembers = currentTeamDocument.getList("team_members", String.class);
                currentTeamMembers.remove(request.getStudentID());

                if (currentTeamMembers.size() < 1) teamCollection.deleteOne(currentTeamDocumentFilter);
                else {
                    Bson currentTeamUpdates = Updates.combine(
                            Updates.set("team_members", currentTeamMembers),
                            Updates.set("team_lead", currentTeamMembers.get(0)),
                            Updates.set("is_full", false));

                    UpdateOptions currentTeamOptions = new UpdateOptions().upsert(true);
                    teamCollection.updateOne(currentTeamDocumentFilter, currentTeamUpdates, currentTeamOptions);
                }
            }
        }

        List<Document> targetTeamDocuments = getAllTeams(request.getCourseID());
        if (targetTeamDocuments == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document targetTeamDocument : targetTeamDocuments) {
            String teamDocumentTeamID = targetTeamDocument.getString("team_id");
            String teamDocumentCouurseID = targetTeamDocument.getString("course_id");

            if (request.getTargetTeamID().equals(teamDocumentTeamID) && request.getCourseID().equals(teamDocumentCouurseID)) {
                Bson targetTeamDocumentFilter = Filters.and(eq("team_id", request.getTargetTeamID()), eq("course_id", request.getCourseID()));
                List<String> targetTeamMembers = targetTeamDocument.getList("team_members", String.class);
                targetTeamMembers.add(request.getStudentID());

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
        new SecurityService().generateTeamNameSecurity(courseDocument, request);

        List<Document> teamDocuments = getAllTeams(request.getCourseID());
        if (teamDocuments == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
            String teamDocumentTeamID = teamDocument.getString("team_id");
            String teamDocumentCouurseID = teamDocument.getString("course_id");
            if (request.getTeamID().equals(teamDocumentTeamID) && request.getCourseID().equals(teamDocumentCouurseID)) {
                Bson teamNameUpdates = Updates.combine(
                        Updates.set("team_id", request.getTeamName()),
                        Updates.set("team_lock", true)
                );
                UpdateOptions teamNameOptions = new UpdateOptions().upsert(true);
                teamCollection.updateOne(teamDocumentFilter, teamNameUpdates, teamNameOptions);
            }
        }
    }

    public void toggleTeamLock(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());

        if (!new SecurityService().isTeamCreated(request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());

        List<Document> teamDocuments = getAllTeams(request.getCourseID());
        if (teamDocuments == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
            String teamDocumentTeamID = teamDocument.getString("team_id");
            String teamDocumentCourseID = teamDocument.getString("course_id");
            if (request.getTeamID().equals(teamDocumentTeamID) && request.getCourseID().equals(teamDocumentCourseID)) {
                boolean teamLock = teamDocument.getBoolean("team_lock");
                teamCollection.findOneAndUpdate(teamDocumentFilter, Updates.set("team_lock", !teamLock));
            }
        }
    }

    public void addStudentToTeam(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        new SecurityService().addStudentToTeamSecurity(courseDocument, request);

        if (!new SecurityService().isStudentAlreadyInATeam(request.getStudentID(), request.getCourseID())) joinTeam(request);
        else if (new SecurityService().isStudentAlreadyInATeam(request.getStudentID(), request.getCourseID())) {
            String currentTeamID = new TeamService().retrieveTeamID(request);
            SwitchTeamParam switchTeamParam = new SwitchTeamParam(request.getCourseID(), request.getStudentID(), currentTeamID, request.getTeamID());
            switchTeam(switchTeamParam);
        }
    }

    public void removeStudent(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        new SecurityService().removeStudent(courseDocument, request);

        List<Document> teamDocuments = getAllTeams(request.getCourseID());
        if (teamDocuments == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
            String teamDocumentTeamID = teamDocument.getString("team_id");
            String teamDocumentCourseID = teamDocument.getString("course_id");

            if (request.getTeamID().equals(teamDocumentTeamID) && request.getCourseID().equals(teamDocumentCourseID)) {
                List<String> teamMembers = teamDocument.getList("team_members", String.class);
                teamMembers.remove(request.getStudentID());

                if (teamMembers.size() < 1) teamCollection.deleteOne(teamDocumentFilter);
                else {
                    Bson teamUpdates = Updates.combine(
                            Updates.set("team_members", teamMembers),
                            Updates.set("team_lead", teamMembers.get(0)),
                            Updates.set("is_full", false));
                    UpdateOptions teamOptions = new UpdateOptions().upsert(true);
                    teamCollection.updateOne(new Document("team_id", request.getTeamID()), teamUpdates, teamOptions);
                }
            }
        }
    }

    public void editTeamName(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        new SecurityService().editTeamNameSecurity(request);

        List<Document> teamDocuments = getAllTeams(request.getCourseID());
        if (teamDocuments == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
            String teamDocumentTeamID = teamDocument.getString("team_id");
            String teamDocumentCourseID = teamDocument.getString("course_id");

            if (request.getTeamID().equals(teamDocumentTeamID) && request.getCourseID().equals(teamDocumentCourseID)) {
                Bson editTeamNameUpdates = Updates.combine(
                        Updates.set("team_id", request.getTeamName()),
                        Updates.set("team_lock", true)
                );
                UpdateOptions editTeamNameOptions = new UpdateOptions().upsert(true);
                teamCollection.updateOne(teamDocumentFilter, editTeamNameUpdates, editTeamNameOptions);
            }
        }
    }

    public void assignTeamLead(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        new SecurityService().assignTeamLeadSecurity(courseDocument, request);

        List<Document> teamDocuments = getAllTeams(request.getCourseID());
        if (teamDocuments == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
            String teamDocumentTeamID = teamDocument.getString("team_id");
            String teamDocumentCourseID = teamDocument.getString("course_id");

            if (request.getTeamID().equals(teamDocumentTeamID) && request.getCourseID().equals(teamDocumentCourseID)) {
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
        }
    }

    public void deleteTeam(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());

        if (!new SecurityService().isTeamCreated(request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());

        if (new SecurityService().isTeamLock(request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Team is locked.").build());

        List<Document> teamDocuments = getAllTeams(request.getCourseID());
        if (teamDocuments == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
            String teamDocumentTeamID = teamDocument.getString("team_id");
            String teamDocumentCourseID = teamDocument.getString("course_id");

            if (request.getTeamID().equals(teamDocumentTeamID) && request.getCourseID().equals(teamDocumentCourseID)) {
                teamCollection.findOneAndDelete(teamDocumentFilter);
            }
        }
    }

    public List<Document> getAllStudentsInThisCourse(String courseID) {
        Document courseDocument = courseCollection.find(eq("course_id", courseID)).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());

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