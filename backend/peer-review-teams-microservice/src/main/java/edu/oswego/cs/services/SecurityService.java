package edu.oswego.cs.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import edu.oswego.cs.database.TeamInterface;
import edu.oswego.cs.requests.SwitchTeamParam;
import edu.oswego.cs.requests.TeamParam;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class SecurityService {
    public void createTeamSecurity(Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());
        if (isStudentAlreadyInATeam(request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Student is already in a team.").build());
    }

    public void joinTeamSecurity(MongoCollection<Document> teamCollection, Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());
        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (isTeamLock(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Team is locked.").build());
        if (isTeamFull(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Team is already full.").build());
    }

    public void switchTeamSecurity(MongoCollection<Document> teamCollection, Document courseDocument, SwitchTeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());
        if (!isTeamCreated(teamCollection, request.getCurrentTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Current team not found.").build());
        if (!isTeamCreated(teamCollection, request.getTargetTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Target team not found.").build());
        if (isStudentInThisTeam(teamCollection, request.getTargetTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Student already in target team.").build());
        if (isTeamLock(teamCollection, request.getCurrentTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Current team is locked.").build());
        if (isTeamLock(teamCollection, request.getTargetTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Target team is locked.").build());
        if (isTeamFull(teamCollection, request.getTargetTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Target team already full.").build());
    }

    public void giveUpTeamLeadSecurity(MongoCollection<Document> teamCollection, Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());
        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (!isStudentInThisTeam(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this team.").build());
        if (!isTeamLead(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized. Not team lead.").build());
    }

    public void nominateTeamLeadSecurity(MongoCollection<Document> teamCollection, Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()) || !isStudentValid(courseDocument, request.getNominatedTeamLead()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());
        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (!isStudentInThisTeam(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Current team lead not found in this team.").build());
        if (!isTeamLead(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized. Not team lead.").build());
        if (!isStudentInThisTeam(teamCollection, request.getTeamID(), request.getNominatedTeamLead(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Nominated team lead not found in this team.").build());
        if (isTeamLead(teamCollection, request.getTeamID(), request.getNominatedTeamLead(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Student already a team lead.").build());
    }
    
    public void memberConfirmToggleSecurity(MongoCollection<Document> teamCollection, Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());
        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (!isStudentInThisTeam(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this team.").build());
        if (isTeamLead(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Team Lead can not be unconfirmed.").build());
    }
    
    public void generateTeamNameSecurity(MongoCollection<Document> teamCollection, Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());
        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (isTeamLock(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Team is locked.").build());
        if (!isStudentInThisTeam(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this team.").build());
        if (!isTeamLead(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized. Not team lead.").build());
        if (!isTeamNameUnique(teamCollection, request))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Not acceptable. Team name not unique.").build());
        if (!isTeamNameValid(request.getTeamName(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Not acceptable. Team Name contains student's name.").build());
    }

    public void leaveTeamSecurity(MongoCollection<Document> teamCollection, Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());
        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (isTeamLock(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Team is locked.").build());
        if (!isStudentInThisTeam(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this team.").build());
    }

    public void editTeamNameSecurity(MongoCollection<Document> teamCollection, TeamParam request) {
        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (isTeamLock(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Team is locked.").build());
        if (!isTeamNameUnique(teamCollection, request))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Not acceptable. Team name not unique.").build());
        if (!isTeamNameValid(request.getTeamName(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Not acceptable. Team contains student's name.").build());
    }

    public void editTeamSizeSecurity(MongoCollection<Document> teamCollection, TeamParam request) {
        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (isTeamLock(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Team is locked.").build());
    }

    public void assignTeamLeadSecurity(MongoCollection<Document> teamCollection, Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());
        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (!isStudentInThisTeam(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this team.").build());
        if (isTeamLead(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Student already a team lead.").build());
    }

    public boolean isStudentValid(Document courseDocument, String studentID) {
        List<String> students = courseDocument.getList("students", String.class);
        if (students == null)
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to retrieve students field.").build());
        if (students.contains(studentID)) return true;
        return false;
    }

    public boolean isStudentAlreadyInATeam(String studentID, String courseID) {
        List<Document> teamDocuments = new TeamInterface().getAllTeams(courseID);
        if (teamDocuments == null)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            List<String> members = teamDocument.getList("team_members", String.class);
            if (members.contains(studentID)) return true;
        }
        return false;
    }

    public boolean isStudentInThisTeam(MongoCollection<Document> teamCollection, String teamID, String studentID, String courseID) {
        Bson teamDocumentFilter = Filters.and(eq("team_id", teamID), eq("course_id", courseID));
        Document teamDocument = teamCollection.find(teamDocumentFilter).first();
        List<String> teamDocumentMembers = teamDocument.getList("team_members", String.class);
        if (teamDocumentMembers.contains(studentID)) return true;
        return false;
    }

    public boolean isStudentConfirmed(MongoCollection<Document> teamCollection, String teamID, String studentID, String courseID) {
        Bson teamDocumentFilter = Filters.and(eq("team_id", teamID), eq("course_id", courseID));
        Document teamDocument = teamCollection.find(teamDocumentFilter).first();
        List<String> teamDocumentConfirmedMembers = teamDocument.getList("team_confirmed_members", String.class);
        if (teamDocumentConfirmedMembers.contains(studentID)) return true;
        return false;
    }

    public boolean isTeamLead(MongoCollection<Document> teamCollection, String teamID, String studentID, String courseID) {
        Bson teamDocumentFilter = Filters.and(eq("team_id", teamID), eq("course_id", courseID));
        Document teamDocument = teamCollection.find(teamDocumentFilter).first();
        return studentID.equals(teamDocument.getString("team_lead"));
    }

    public boolean isTeamFull(MongoCollection<Document> teamCollection, String teamID, String courseID) {
        Bson teamDocumentFilter = Filters.and(eq("team_id", teamID), eq("course_id", courseID));
        Document teamDocument = teamCollection.find(teamDocumentFilter).first();
        return teamDocument.getBoolean("team_full");
    }

    public boolean isTeamLock(MongoCollection<Document> teamCollection, String teamID, String courseID) {
        Bson teamDocumentFilter = Filters.and(eq("team_id", teamID), eq("course_id", courseID));
        Document teamDocument = teamCollection.find(teamDocumentFilter).first();
        return teamDocument.getBoolean("team_lock");
    }

    public boolean isTeamCreated(MongoCollection<Document> teamCollection, String teamID, String courseID) {
        Bson teamDocumentFilter = Filters.and(eq("team_id", teamID), eq("course_id", courseID));
        Document teamDocument = teamCollection.find(teamDocumentFilter).first();
        if (teamDocument == null) return false;
        return true;
    }

    public boolean isTeamNameUnique(MongoCollection<Document> teamCollection, TeamParam request) {
        Bson teamDocumentFilter = Filters.and(
                eq("team_id", request.getTeamName()),
                eq("course_id", request.getCourseID()));
        Document teamDocument = teamCollection.find(teamDocumentFilter).first();
        if (teamDocument == null) return true;
        return false;
    }

    public boolean isTeamNameValid(String teamName, String courseID) {
        List<Document> studentDocuments = new TeamInterface().getAllStudentsInThisCourse(courseID);
        if (studentDocuments == null || studentDocuments.size() == 0)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No students found.").build());

        for (Document studentDocument : studentDocuments) {
            String lastName = studentDocument.getString("last_name").toLowerCase().trim();
            String firstName = studentDocument.getString("first_name").split(" ")[0].toLowerCase().trim();
            if (teamName.toLowerCase().contains(lastName) || teamName.toLowerCase().contains(firstName))
                return false;
        }
        return true;
    }
}
