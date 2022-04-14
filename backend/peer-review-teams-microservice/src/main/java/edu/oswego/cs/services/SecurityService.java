package edu.oswego.cs.services;

import org.bson.Document;

import edu.oswego.cs.database.TeamInterface;
import edu.oswego.cs.requests.SwitchTeamParam;
import edu.oswego.cs.requests.TeamParam;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

public class SecurityService {
    public void createTeamSecurity(Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());
        if (isStudentAlreadyInATeam(request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Student is already in a team.").build());
    }

    public void joinTeamSecurity(Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());
        if (isStudentAlreadyInATeam(request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Student is already in a team.").build());
        if (!isTeamCreated(request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (isTeamLock(request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Team is locked.").build());
        if (isTeamFull(request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Team is already full.").build());

    }

    public void switchTeamSecurity(Document courseDocument, SwitchTeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Student not found in this course.").build());
        if (!isTeamCreated(request.getCurrentTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Current team not found.").build());
        if (!isTeamCreated(request.getTargetTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Target team not found.").build());
        if (!isStudentAlreadyInATeam(request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in any team.").build());
        if (!isStudentInThisTeam(request.getCurrentTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in current team.").build());
        if (isStudentInThisTeam(request.getTargetTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Student already in target team.").build());
        if (isTeamLock(request.getCurrentTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Current team is locked.").build());
        if (isTeamLock(request.getTargetTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Target team is locked.").build());
        if (isTeamFull(request.getTargetTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Target team already full.").build());
    }

    public void generateTeamNameSecurity(Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());
        if (!isTeamCreated(request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (isTeamLock(request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Team is locked.").build());
        if (!isStudentAlreadyInATeam(request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in any team.").build());
        if (!isStudentInThisTeam(request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this team.").build());
        if (!isTeamLead(request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized. Not team lead.").build());
        if (!isTeamNameUnique(request))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Not acceptable. Team name not unique.").build());
        if (!isTeamNameValid(request.getTeamName(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Not acceptable. Team Name contains student's name.").build());
    }

    public void editTeamNameSecurity(TeamParam request) {
        if (!isTeamCreated(request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (isTeamLock(request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Team is locked.").build());
        if (!isTeamNameUnique(request))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Not acceptable. Team name not unique.").build());
        if (!isTeamNameValid(request.getTeamName(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Not acceptable. Team contains student's name.").build());
    }

    public void assignTeamLeadSecurity(Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());
        if (!isTeamCreated(request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (isTeamLock(request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Team is locked.").build());
        if (!isStudentInThisTeam(request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this team.").build());
        if (isTeamLead(request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Student already a team lead.").build());
    }

    public void addStudentToTeamSecurity(Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());
        if (!isTeamCreated(request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (isTeamLock(request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Team is locked.").build());
        if (isStudentInThisTeam(request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Student already in this team.").build());
        if (isTeamFull(request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Team already full.").build());
    }

    public void removeStudent(Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());
        if (!isTeamCreated(request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (isTeamLock(request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Team is locked.").build());
        if (!isStudentInThisTeam(request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Student not found in this team.").build());
    }

    public boolean isStudentValid(Document courseDocument, String studentID) {
        List<String> students = courseDocument.getList("students", String.class);
        if (students == null)
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to retrieve students field.").build());
        for (String student : students)
            if (studentID.equals(student)) return true;
        return false;
    }

    public boolean isStudentAlreadyInATeam(String studentID, String courseID) {
        List<Document> teamDocuments = new TeamInterface().getAllTeams(courseID);
        if (teamDocuments == null)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            List<String> members = teamDocument.getList("team_members", String.class);
            for (String member : members)
                if (studentID.equals(member))
                    return true;
        }
        return false;

    }

    public boolean isStudentInThisTeam(String teamID, String studentID, String courseID) {
        List<Document> teamDocuments = new TeamInterface().getAllTeams(courseID);
        if (teamDocuments == null || teamDocuments.size() == 0)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            List<String> members = teamDocument.getList("team_members", String.class);
            String teamDocumentTeamID = teamDocument.getString("team_id");

            for (String member : members)
                if (studentID.equals(member) && teamID.equals(teamDocumentTeamID))
                    return true;
        }
        return false;
    }

    public boolean isTeamLead(String teamID, String studentID, String courseID) {
        List<Document> teamDocuments = new TeamInterface().getAllTeams(courseID);
        if (teamDocuments == null || teamDocuments.size() == 0)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            String teamDocumentTeamID = teamDocument.getString("team_id");
            String teamLead = teamDocument.getString("team_lead");

            if (studentID.equals(teamLead) && teamID.equals(teamDocumentTeamID))
                return true;
        }
        return false;
    }

    public boolean isTeamFull(String teamID, String courseID) {
        List<Document> teamDocuments = new TeamInterface().getAllTeams(courseID);
        if (teamDocuments == null || teamDocuments.size() == 0)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            String teamDocumentTeamID = teamDocument.getString("team_id");
            if (teamDocument.getBoolean("is_full") && teamID.equals(teamDocumentTeamID))
                return true;
        }
        return false;
    }

    public boolean isTeamLock(String teamID, String courseID) {
        List<Document> teamDocuments = new TeamInterface().getAllTeams(courseID);
        if (teamDocuments == null || teamDocuments.size() == 0)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            String teamDocumentTeamID = teamDocument.getString("team_id");
            if (teamDocument.getBoolean("team_lock") && teamID.equals(teamDocumentTeamID))
                return true;
        }
        return false;
    }

    public boolean isTeamCreated(String teamID, String courseID) {
        List<Document> teamDocuments = new TeamInterface().getAllTeams(courseID);
        if (teamDocuments == null || teamDocuments.size() == 0)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            String teamDocumentTeamID = teamDocument.getString("team_id");
            String teamDocumentCourseID = teamDocument.getString("course_id");
            if (teamDocumentTeamID.equals(teamID) && teamDocumentCourseID.equals(courseID))
                return true;
        }
        return false;
    }

    public boolean isTeamNameUnique(TeamParam request) {
        List<Document> teamDocuments = new TeamInterface().getAllTeams(request.getCourseID());
        if (teamDocuments == null)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            String teamDocumentTeamName = teamDocument.getString("team_id");
            String teamDocumentTeamID = teamDocument.getString("course_id");
            if (request.getCourseID().equals(teamDocumentTeamID))
                if (request.getTeamName().toLowerCase().equals(teamDocumentTeamName.toLowerCase()))
                    return false;
        }
        return true;
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
