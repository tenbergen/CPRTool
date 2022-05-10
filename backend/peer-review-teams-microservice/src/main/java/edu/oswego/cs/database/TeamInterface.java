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
import edu.oswego.cs.services.IdentifyingService;
import edu.oswego.cs.services.SecurityService;
import edu.oswego.cs.services.TeamService;
import edu.oswego.cs.util.CPRException;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
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
        } catch (CPRException e) {
            throw new CPRException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to retrieve collections.");
        }
    }

    public void createTeam(@Context SecurityContext securityContext, TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Course not found");
        new IdentifyingService().identifyingStudentService(securityContext, request.getStudentID());
        new IdentifyingService().identifyingProfessorService(securityContext, courseCollection, request.getCourseID());
        new SecurityService().generateTeamNameSecurity(securityContext, teamCollection, courseDocument, request);

        int teamSize = new TeamService().getTeamSize(courseDocument);
        TeamDAO newTeam = new TeamDAO(request.getTeamName(), request.getCourseID(), teamSize, request.getStudentID());
        newTeam.getTeamMembers().add(request.getStudentID());
        newTeam.setTeamMembers(newTeam.getTeamMembers());

        if (teamSize == 1) newTeam.setTeamFull(true);

        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> courseDAOEntity = Entity.entity(jsonb.toJson(newTeam), MediaType.APPLICATION_JSON_TYPE);
        Document teamDocument = Document.parse(courseDAOEntity.getEntity());
        teamCollection.insertOne(teamDocument);
    }

    public List<Document> getAllTeams(SecurityContext securityContext, String courseID) {
        Document courseDocument = courseCollection.find(eq("course_id", courseID)).first();
        if (courseDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Course not found");
        new IdentifyingService().identifyingProfessorService(securityContext, courseCollection, courseID);

        MongoCursor<Document> cursor = teamCollection.find(eq("course_id", courseID)).iterator();
        List<Document> teams = new ArrayList<>();
        while (cursor.hasNext()) {
            Document teamDocument = cursor.next();
            if (securityContext.isUserInRole("student")) {
                teamDocument.remove("team_members");
                teamDocument.remove("team_lead");
            }
            teams.add(teamDocument);
        }
        cursor.close();
        return teams;
    }

    public Document getTeamByStudentID(SecurityContext securityContext, String courseID, String studentID) {
        Document courseDocument = courseCollection.find(eq("course_id", courseID)).first();
        if (courseDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Course not found");
        if (!new SecurityService().isStudentValid(courseDocument, studentID))
            throw new CPRException(Response.Status.NOT_FOUND, "Student not found in this course.");
        new IdentifyingService().identifyingStudentService(securityContext, studentID);
        new IdentifyingService().identifyingProfessorService(securityContext, courseCollection, courseID);

        if (new SecurityService().isStudentAlreadyInATeam(teamCollection, securityContext, studentID, courseID)) {
            MongoCursor<Document> cursor = teamCollection.find(eq("course_id", courseID)).iterator();
            while (cursor.hasNext()) {
                Document teamDocument = cursor.next();
                String teamDocumentTeamID = teamDocument.getString("team_id");
                if (new SecurityService().isStudentInThisTeam(teamCollection, teamDocumentTeamID, studentID, courseID)) {
                    cursor.close();
                    return teamDocument;
                }
            }
            cursor.close();
        }
        throw new CPRException(Response.Status.NOT_FOUND, "Student not found in any team.");
    }

    public Document getTeamByTeamID(SecurityContext securityContext, String courseID, String teamID) {
        String userID = securityContext.getUserPrincipal().getName().split("@")[0];
        Document courseDocument = courseCollection.find(eq("course_id", courseID)).first();
        if (courseDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Course not found");
        new IdentifyingService().identifyingProfessorService(securityContext, courseCollection, courseID);
        if (!new SecurityService().isTeamCreated(teamCollection, teamID, courseID))
            throw new CPRException(Response.Status.NOT_FOUND, "Team not found.");
        if (securityContext.isUserInRole("student"))
            if (!new SecurityService().isStudentInThisTeam(teamCollection, teamID, userID, courseID))
                throw new CPRException(Response.Status.FORBIDDEN, "Principal User is not in this team.");

        Bson teamDocumentFilter = Filters.and(eq("team_id", teamID), eq("course_id", courseID));
        return teamCollection.find(teamDocumentFilter).first();
    }

    /* Deprecated */
    public void studentJoinTeam(SecurityContext securityContext, TeamParam request) {
        if (!new SecurityService().isStudentAlreadyInATeam(teamCollection, securityContext, request.getStudentID(), request.getCourseID()))
            joinTeam(securityContext, request);
        else if (new SecurityService().isStudentAlreadyInATeam(teamCollection, securityContext, request.getStudentID(), request.getCourseID())) {
            String currentTeamID = new TeamService().retrieveTeamID(securityContext, request);
            SwitchTeamParam switchTeamParam = new SwitchTeamParam(request.getCourseID(), request.getStudentID(), currentTeamID, request.getTeamID());
            switchTeam(switchTeamParam);
        }
    }

    public void joinTeam(SecurityContext securityContext, TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Course not found.");
        new IdentifyingService().identifyingStudentService(securityContext, request.getStudentID());
        new IdentifyingService().identifyingProfessorService(securityContext, courseCollection, request.getCourseID());
        new SecurityService().joinTeamSecurity(securityContext, teamCollection, courseDocument, request);

        Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
        Document teamDocument = teamCollection.find(teamDocumentFilter).first();

        List<String> teamMembers = teamDocument.getList("team_members", String.class);
        teamMembers.add(request.getStudentID());
        Bson teamUpdates = Updates.set("team_members", teamMembers);
        if (teamMembers.size() >= teamDocument.getInteger("team_size"))
            teamUpdates = Updates.combine(teamUpdates, Updates.set("team_full", true));
        UpdateOptions teamOptions = new UpdateOptions().upsert(true);
        teamCollection.updateOne(teamDocumentFilter, teamUpdates, teamOptions);
    }

    /* Deprecated */
    public void switchTeam(SwitchTeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Course not found.");
        new SecurityService().switchTeamSecurity(teamCollection, courseDocument, request);

        Bson currentTeamDocumentFilter = Filters.and(eq("team_id", request.getCurrentTeamID()), eq("course_id", request.getCourseID()));
        Document currentTeamDocument = teamCollection.find(currentTeamDocumentFilter).first();
        if (currentTeamDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Team not found.");

        List<String> currentTeamMembers = currentTeamDocument.getList("team_members", String.class);
        currentTeamMembers.remove(request.getStudentID());
        List<String> currentTeamConfirmedMembers = currentTeamDocument.getList("team_confirmed_members", String.class);
        currentTeamConfirmedMembers.remove(request.getStudentID());

        if (currentTeamMembers.size() < 1)
            teamCollection.deleteOne(currentTeamDocumentFilter);
        else {
            currentTeamConfirmedMembers.remove(currentTeamMembers.get(0));
            currentTeamConfirmedMembers.add(0, currentTeamMembers.get(0));
            Bson currentTeamUpdates = Updates.combine(
                    Updates.set("team_members", currentTeamMembers),
                    Updates.set("team_confirmed_members", currentTeamConfirmedMembers),
                    Updates.set("team_lead", currentTeamMembers.get(0)),
                    Updates.set("team_full", false));
            UpdateOptions currentTeamOptions = new UpdateOptions().upsert(true);
            teamCollection.updateOne(currentTeamDocumentFilter, currentTeamUpdates, currentTeamOptions);
        }

        Bson targetTeamDocumentFilter = Filters.and(eq("team_id", request.getTargetTeamID()), eq("course_id", request.getCourseID()));
        Document targetTeamDocument = teamCollection.find(targetTeamDocumentFilter).first();
        if (targetTeamDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Team not found.");

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
        if (courseDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Course not found.");
        new SecurityService().giveUpTeamLeadSecurity(teamCollection, courseDocument, request);

        Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
        Document teamDocument = teamCollection.find(teamDocumentFilter).first();
        if (teamDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Team not found.");

        List<String> teamMembers = teamDocument.getList("team_members", String.class);
        teamMembers.remove(request.getStudentID());
        teamMembers.add(request.getStudentID());
        List<String> teamConfirmedMembers = teamDocument.getList("team_confirmed_members", String.class);
        if (!new SecurityService().isTeamLock(teamCollection, request.getTeamID(), request.getCourseID()))
            teamConfirmedMembers.remove(request.getStudentID());
        teamConfirmedMembers.remove(teamMembers.get(0));
        teamConfirmedMembers.add(0, teamMembers.get(0));

        Bson assignTeamLeadUpdates = Updates.combine(
                Updates.set("team_lead", teamMembers.get(0)),
                Updates.set("team_members", teamMembers),
                Updates.set("team_confirmed_members", teamConfirmedMembers)
        );
        UpdateOptions assignTeamLeadOptions = new UpdateOptions().upsert(true);
        teamCollection.updateOne(teamDocumentFilter, assignTeamLeadUpdates, assignTeamLeadOptions);
    }

    public void nominateTeamLead(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Course not found.");
        new SecurityService().nominateTeamLeadSecurity(teamCollection, courseDocument, request);

        Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
        Document teamDocument = teamCollection.find(teamDocumentFilter).first();
        if (teamDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Team not found.");

        List<String> teamMembers = teamDocument.getList("team_members", String.class);
        teamMembers.remove(request.getStudentID());
        teamMembers.add(request.getStudentID());
        teamMembers.remove(request.getNominatedTeamLead());
        teamMembers.add(0, request.getNominatedTeamLead());
        List<String> teamConfirmedMembers = teamDocument.getList("team_confirmed_members", String.class);
        if (!new SecurityService().isTeamLock(teamCollection, request.getTeamID(), request.getCourseID()))
            teamConfirmedMembers.remove(request.getStudentID());
        teamConfirmedMembers.remove(request.getNominatedTeamLead());
        teamConfirmedMembers.add(0, request.getNominatedTeamLead());

        Bson assignTeamLeadUpdates = Updates.combine(
                Updates.set("team_lead", request.getNominatedTeamLead()),
                Updates.set("team_members", teamMembers),
                Updates.set("team_confirmed_members", teamConfirmedMembers)
        );
        UpdateOptions assignTeamLeadOptions = new UpdateOptions().upsert(true);
        teamCollection.updateOne(teamDocumentFilter, assignTeamLeadUpdates, assignTeamLeadOptions);
    }

    public void memberConfirmToggle(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Course not found.");
        new SecurityService().memberConfirmToggleSecurity(teamCollection, courseDocument, request);

        Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
        Document teamDocument = teamCollection.find(teamDocumentFilter).first();
        List<String> teamConfirmedMembers = teamDocument.getList("team_confirmed_members", String.class);

        if (!new SecurityService().isStudentConfirmed(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID())) {
            teamConfirmedMembers.add(request.getStudentID());
            teamCollection.updateOne(teamDocumentFilter, Updates.set("team_confirmed_members", teamConfirmedMembers));
        } else {
            teamConfirmedMembers.remove(request.getStudentID());
            teamCollection.updateOne(teamDocumentFilter, Updates.set("team_confirmed_members", teamConfirmedMembers));
        }
    }

    public void generateTeamName(SecurityContext securityContext, TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Course not found.");
        new SecurityService().generateTeamNameSecurity(securityContext, teamCollection, courseDocument, request);

        Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
        Bson teamNameUpdates = Updates.combine(
                Updates.set("team_id", request.getTeamName()),
                Updates.set("team_lock", true));
        UpdateOptions teamNameOptions = new UpdateOptions().upsert(true);
        teamCollection.updateOne(teamDocumentFilter, teamNameUpdates, teamNameOptions);
    }

    public void removeTeamMember(@Context SecurityContext securityContext, TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Course not found.");
        new IdentifyingService().identifyingProfessorService(securityContext, courseCollection, request.getCourseID());
        new SecurityService().removeTeamMemberSecurity(teamCollection, courseDocument, request);

        Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
        Document teamDocument = teamCollection.find(teamDocumentFilter).first();
        if (teamDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Team not found.");

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
        if (courseDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Course not found.");
        if (!new SecurityService().isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_FOUND, "Team not found.");

        Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
        Document teamDocument = teamCollection.find(teamDocumentFilter).first();
        if (teamDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Team not found.");

        boolean teamLock = teamDocument.getBoolean("team_lock");
        teamCollection.updateOne(teamDocumentFilter, Updates.set("team_lock", !teamLock));
    }

    public void lockAllTeams(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Course not found.");

        Bson teamDocumentFilter = eq("course_id", request.getCourseID());
        Bson teamDocumentUpdate = Updates.set("team_lock", true);
        teamCollection.updateMany(teamDocumentFilter, teamDocumentUpdate);
    }

    public void editTeamName(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Course not found.");
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
        if (courseDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Course not found.");
        Bson editTeamSizeInBulkUpdates = Updates.set("team_size", request.getTeamSize());
        teamCollection.updateMany(eq("course_id", request.getCourseID()), editTeamSizeInBulkUpdates);
    }

    public void editTeamSize(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Course not found.");
        new SecurityService().editTeamSizeSecurity(teamCollection, request);
        Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
        Bson editTeamSizeUpdates = Updates.set("team_size", request.getTeamSize());
        teamCollection.updateOne(teamDocumentFilter, editTeamSizeUpdates);
    }

    public void assignTeamLead(TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Course not found.");
        new SecurityService().assignTeamLeadSecurity(teamCollection, courseDocument, request);

        Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
        Document teamDocument = teamCollection.find(teamDocumentFilter).first();
        if (teamDocument == null) throw new CPRException(Response.Status.NOT_FOUND, "Team not found.");

        List<String> teamMembers = teamDocument.getList("team_members", String.class);
        teamMembers.remove(request.getStudentID());
        teamMembers.add(0, request.getStudentID());
        List<String> teamConfirmedMembers = teamDocument.getList("team_confirmed_members", String.class);
        if (!new SecurityService().isTeamLock(teamCollection, request.getTeamID(), request.getCourseID()))
            teamConfirmedMembers.remove(teamDocument.getString("team_lead"));
        teamConfirmedMembers.remove(request.getStudentID());
        teamConfirmedMembers.add(0, request.getStudentID());

        Bson assignTeamLeadUpdates = Updates.combine(
                Updates.set("team_lead", request.getStudentID()),
                Updates.set("team_members", teamMembers),
                Updates.set("team_confirmed_members", teamConfirmedMembers)
        );
        UpdateOptions assignTeamLeadOptions = new UpdateOptions().upsert(true);
        teamCollection.updateOne(teamDocumentFilter, assignTeamLeadUpdates, assignTeamLeadOptions);
    }

    public void deleteTeam(SecurityContext securityContext, TeamParam request) {
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null)
            throw new CPRException(Response.Status.NOT_FOUND, "Course not found.");
        new IdentifyingService().identifyingProfessorService(securityContext, courseCollection, request.getCourseID());
        if (!new SecurityService().isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_FOUND, "Team not found.");
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