package edu.oswego.cs.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import edu.oswego.cs.database.TeamInterface;
import edu.oswego.cs.requests.SwitchTeamParam;
import edu.oswego.cs.requests.TeamParam;
import edu.oswego.cs.util.CPRException;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class SecurityService {
        
    public void generateTeamNameSecurity(SecurityContext securityContext, MongoCollection<Document> teamCollection, Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new CPRException(Response.Status.NOT_FOUND,"Student not found in this course.");
        if (isStudentAlreadyInATeam(teamCollection, securityContext, request.getStudentID(), request.getCourseID()))
            throw new CPRException(Response.Status.CONFLICT,"Student is already in a team.");
        if (!isTeamNameUnique(teamCollection, request))
            throw new CPRException(Response.Status.NOT_ACCEPTABLE,"Not acceptable. Team name not unique");
        if (!isTeamNameValid(request.getTeamName(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_ACCEPTABLE,"Not acceptable. Team Name contains student's name.");
    }

    public void joinTeamSecurity(SecurityContext securityContext, MongoCollection<Document> teamCollection, Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new CPRException(Response.Status.NOT_FOUND,"Student not found in this course.");
        if (isStudentAlreadyInATeam(teamCollection, securityContext, request.getStudentID(), request.getCourseID()))
            throw new CPRException(Response.Status.CONFLICT,"Student is already in a team.");
        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_FOUND,"Team not found.");
        if (isTeamFull(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new CPRException(Response.Status.CONFLICT,"Team is already full.");
    }

    /* Deprecated */
    public void switchTeamSecurity(MongoCollection<Document> teamCollection, Document courseDocument, SwitchTeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new CPRException(Response.Status.NOT_FOUND,"Student not found in this course.");
        if (!isTeamCreated(teamCollection, request.getCurrentTeamID(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_FOUND,"Current team not found.");
        if (!isTeamCreated(teamCollection, request.getTargetTeamID(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_FOUND,"Target team not found");
        if (isStudentInThisTeam(teamCollection, request.getTargetTeamID(), request.getStudentID(), request.getCourseID()))
            throw new CPRException(Response.Status.CONFLICT,"Student already in target team.");
        if (isTeamLock(teamCollection, request.getCurrentTeamID(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_ACCEPTABLE,"Current team is locked.");
        if (isTeamLock(teamCollection, request.getTargetTeamID(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_ACCEPTABLE,"Target team is locked.");
        if (isTeamFull(teamCollection, request.getTargetTeamID(), request.getCourseID()))
            throw new CPRException(Response.Status.CONFLICT,"Target team already full.");
    }

    public void giveUpTeamLeadSecurity(MongoCollection<Document> teamCollection, Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new CPRException(Response.Status.NOT_FOUND,"Student not found in this course");
        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_FOUND,"Team not found.");
        if (!isStudentInThisTeam(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_FOUND,"Student not found in this team.");
        if (!isTeamLead(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new CPRException(Response.Status.UNAUTHORIZED,"Unauthorized. Not team lead.");
    }

    public void nominateTeamLeadSecurity(MongoCollection<Document> teamCollection, Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()) || !isStudentValid(courseDocument, request.getNominatedTeamLead()))
            throw new CPRException(Response.Status.NOT_FOUND,"Student not found in this course.");
        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_FOUND,"Team not found.");
        if (!isStudentInThisTeam(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_FOUND,"Current team lead not found in this team.");
        if (!isTeamLead(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new CPRException(Response.Status.UNAUTHORIZED,"Unauthorized. Not team lead.");
        if (!isStudentInThisTeam(teamCollection, request.getTeamID(), request.getNominatedTeamLead(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_FOUND,"Nominated team lead not found in this team.");
        if (isTeamLead(teamCollection, request.getTeamID(), request.getNominatedTeamLead(), request.getCourseID()))
            throw new CPRException(Response.Status.CONFLICT,"Student already a team lead");
    }
    
    public void memberConfirmToggleSecurity(MongoCollection<Document> teamCollection, Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new CPRException(Response.Status.NOT_FOUND,"Student not found in this course.");
        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_FOUND,"Team not found.");
        if (isTeamLock(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_ACCEPTABLE,"Team is locked.");
        if (!isStudentInThisTeam(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_FOUND,"Student not found in this team.");
        if (isTeamLead(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_ACCEPTABLE,"Team Lead can not be unconfirmed.");
    }

    public void removeTeamMemberSecurity(MongoCollection<Document> teamCollection, Document courseDocument, TeamParam request) {
        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_FOUND,"Team not found.");
        if (!isStudentInThisTeam(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_FOUND,"Student not found in this team.");
    }

    public void editTeamNameSecurity(MongoCollection<Document> teamCollection, TeamParam request) {
        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_FOUND,"Team not found.");
        if (!isTeamNameUnique(teamCollection, request))
            throw new CPRException(Response.Status.NOT_ACCEPTABLE,"Not acceptable. Team name not unique.");
        if (!isTeamNameValid(request.getTeamName(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_ACCEPTABLE,"Not acceptable. Team contains student's name.");
    }

    public void editTeamSizeSecurity(MongoCollection<Document> teamCollection, TeamParam request) {
        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_FOUND,"Team not found.");
        if (isTeamLock(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_ACCEPTABLE,"Team is locked.");
    }

    public void assignTeamLeadSecurity(MongoCollection<Document> teamCollection, Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new CPRException(Response.Status.NOT_FOUND,"Student not found in this course.");
        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_FOUND,"Team not found.");
        if (!isStudentInThisTeam(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new CPRException(Response.Status.NOT_FOUND,"Student not found in this team.");
        if (isTeamLead(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new CPRException(Response.Status.CONFLICT,"Student already a team lead.");
    }

    public boolean isStudentValid(Document courseDocument, String studentID) {
        List<String> students = courseDocument.getList("students", String.class);
        if (students == null) throw new CPRException(Response.Status.INTERNAL_SERVER_ERROR,"Failed to retrieve students field");
        if (students.contains(studentID)) return true;
        return false;
    }

    public boolean isStudentAlreadyInATeam(MongoCollection<Document> teamCollection, SecurityContext securityContext, String studentID, String courseID) {
        Bson teamFilter = Filters.eq("course_id", courseID);
        MongoCursor<Document> cursor = teamCollection.find(teamFilter).iterator();

        while(cursor.hasNext()) {
            Document teamDocument = cursor.next();
            List<String> members = teamDocument.getList("team_members", String.class);
            if (members.contains(studentID)) return true;
        }
        cursor.close();
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
            throw new CPRException(Response.Status.NOT_FOUND,"No students found.");
        for (Document studentDocument : studentDocuments) {
            String lastName = studentDocument.getString("last_name").toLowerCase().trim();
            String firstName = studentDocument.getString("first_name").split(" ")[0].toLowerCase().trim();
            if (teamName.toLowerCase().contains(lastName) || teamName.toLowerCase().contains(firstName))
                return false;
        }
        return true;
    }
}
