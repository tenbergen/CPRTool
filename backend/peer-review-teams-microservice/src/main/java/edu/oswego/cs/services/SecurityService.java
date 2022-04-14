package edu.oswego.cs.services;

import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import edu.oswego.cs.database.TeamInterface;
import edu.oswego.cs.requests.SwitchTeamParam;
import edu.oswego.cs.requests.TeamParam;

public class SecurityService {

    /* ----Securities---- */

    /**
     * Security checks on passed in params for createTeam interface
     * @param courseDocument
     * @param request 
     * @param mode
     */
    public void createTeamSecurity(MongoCollection<Document> teamCollection, Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID())) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());

        if (isStudentAlreadyInATeam(teamCollection, request.getStudentID(), request.getCourseID())) 
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Student is already in a team.").build());
    }

    /**
     * Security checks on passed in params for joinTeam interface
     * @param courseDocument
     * @param request 
     * @param mode
     */
    public void joinTeamSecurity(MongoCollection<Document> teamCollection, Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID())) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());

        if (isStudentAlreadyInATeam(teamCollection, request.getStudentID(), request.getCourseID())) 
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Student is already in a team.").build());
        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID())) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (isTeamLock(teamCollection, request.getTeamID(), request.getCourseID())) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Team is locked.").build());
        if (isTeamFull(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Team is already full.").build());

    }

    /**
     * Security checks on passed in params for switchTeam interface
     * @param teamCollection
     * @param courseDocument
     * @param request
     */
    public void switchTeamSecurity(MongoCollection<Document> teamCollection, Document courseDocument, SwitchTeamParam request) {
        MongoCursor<Document> cursor = teamCollection.find().iterator();
        if (cursor == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        if (!isStudentValid(courseDocument, request.getStudentID())) 
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Student not found in this course.").build());
        if (!isTeamCreated(teamCollection, request.getCurrentTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Current team not found.").build());
        if (!isTeamCreated(teamCollection, request.getTargetTeamID(), request.getCourseID())) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Target team not found.").build());
        if (!isStudentAlreadyInATeam(teamCollection, request.getStudentID(), request.getCourseID())) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in any team.").build());
        if (!isStudentInThisTeam(teamCollection, request.getCurrentTeamID(), request.getStudentID(), request.getCourseID() ))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in current team.").build());
        if (isStudentInThisTeam(teamCollection, request.getTargetTeamID(), request.getStudentID(), request.getCourseID() ))
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Student already in target team.").build());
        if (isTeamLock(teamCollection, request.getCurrentTeamID(), request.getCourseID())) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Current team is locked.").build());
        if (isTeamLock(teamCollection, request.getTargetTeamID(), request.getCourseID())) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Target team is locked.").build());
        if (isTeamFull(teamCollection, request.getTargetTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Target team already full.").build());
    }

    /**
     * Security checks on passed in params in generateTeamName interface
     * @param teamCollection
     * @param studentCollection
     * @param courseDocument
     * @param request
     */
    public void generateTeamNameSecurity(MongoCollection<Document> teamCollection, MongoCollection<Document> studentCollection, Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID())) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());

        MongoCursor<Document> cursor = teamCollection.find().iterator();
        if (cursor == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID())) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (isTeamLock(teamCollection, request.getTeamID(), request.getCourseID())) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Team is locked.").build());
        if (!isStudentAlreadyInATeam(teamCollection, request.getStudentID(), request.getCourseID())) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in any team.").build());
        if (!isStudentInThisTeam(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this team.").build());
        if (!isTeamLead(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized. Not team lead.").build());
        if (!isTeamNameUnique(courseDocument, request))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Not acceptable. Team name not unique.").build());
        if (!isTeamNameValid(studentCollection, request.getTeamName(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Not acceptable. Team Name contains student's name.").build());
    }




    /* ---- Professor Role ---- */
    /**
     * Security checks on passed in params in editTeamName interface
     * @param teamCollection
     * @param studentCollection
     * @param courseDocument
     * @param request
     */
    public void editTeamNameSecurity(MongoCollection<Document> teamCollection,  MongoCollection<Document> studentCollection, Document courseDocument, TeamParam request) {
        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (isTeamLock(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Team is locked.").build());
        if (!isTeamNameUnique(courseDocument, request))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Not acceptable. Team name not unique.").build());
        if (!isTeamNameValid(studentCollection, request.getTeamName(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Not acceptable. Team contains student's name.").build());
    }

    /**
     * Security checks on passed in params in assignTeamLead interface
     * @param teamCollection
     * @param courseDocument
     * @param request
     */
    public void assignTeamLeadSecurity(MongoCollection<Document> teamCollection, Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());
        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (isTeamLock(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Team is locked.").build());
        if (!isStudentInThisTeam(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this team.").build());
        if (isTeamLead(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Student already a team lead.").build());
    }

    /**
     * Security checks on passed in params in addStudentToTeam interface
     * @param teamCollection
     * @param courseDocument
     * @param request
     */
    public void addStudentToTeamSecurity(MongoCollection<Document> teamCollection, Document courseDocument, TeamParam request) {
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());
        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (isTeamLock(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Team is locked.").build());
        if (isStudentInThisTeam(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID() ))
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Student already in this team.").build());
        if (isTeamFull(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Team already full.").build());
    }

    /**
     * Security checks on passed in params in removeStudent interface
     * @param teamCollection
     * @param courseDocument
     * @param request
     */
    public void removeStudent(MongoCollection<Document> teamCollection, Document courseDocument, TeamParam request){
        if (!isStudentValid(courseDocument, request.getStudentID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());
        if (!isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (isTeamLock(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Team is locked.").build());
        if (!isStudentInThisTeam(teamCollection, request.getTeamID(), request.getStudentID(), request.getCourseID() ))
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Student not found in this team.").build());
    }


    /* ---Security Utils---*/

    /**
     * Checks if the passed in studentID is in the right course
     * @param courseDocument Document
     * @param request TeamParam
     * @return boolean
     */
    public boolean isStudentValid(Document courseDocument, String studentID ) {
        List<String> students = courseDocument.getList("students", String.class);
        if (students == null)
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to retrieve students field.").build());
        for (String student : students) 
            if (studentID.equals(student)) return true;
        return false;
    }


    /**
     * Checks if the passed in studentID is already in a team
     * @param teamCollection MongoCollection<Document>
     * @param request TeamParam:{"team_id", "course_id", "student_id", "team_size"}
     * @return boolean
     */
    public boolean isStudentAlreadyInATeam(MongoCollection<Document> teamCollection, String studentID, String courseID) {
        List<Document> teamDocuments = new TeamInterface().getAllTeams(courseID);
        if (teamDocuments == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments ) {
            List<String> members = teamDocument.getList("team_members", String.class);
            for (String member : members) 
                if (studentID.equals(member))
                        return true;
        }
        return false;
       
    }

    /**
     * Checks if the student is in the current team
     * @param teamCollection
     * @param teamID
     * @param studentID
     * @param courseID
     * @return boolean
     */
    public boolean isStudentInThisTeam(MongoCollection<Document> teamCollection, String teamID, String studentID, String courseID ) {
        List<Document> teamDocuments = new TeamInterface().getAllTeams(courseID);
        if (teamDocuments == null || teamDocuments.size() == 0) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments ) {
            List<String> members = teamDocument.getList("team_members", String.class);
            String teamDocumentTeamID = teamDocument.getString("team_id");

            for (String member : members) 
                if (studentID.equals(member) && teamID.equals(teamDocumentTeamID))
                        return true;
        }
        return false;
    }

    /**
     * Checks if passed in studentID is a team lead
     * @param teamCollection
     * @param teamID
     * @param studentID
     * @param courseID
     * @return boolean
     */
    public boolean isTeamLead(MongoCollection<Document> teamCollection, String teamID, String studentID, String courseID ) {
        List<Document> teamDocuments = new TeamInterface().getAllTeams(courseID);
        if (teamDocuments == null || teamDocuments.size() == 0) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments ) {
            String teamDocumentTeamID = teamDocument.getString("team_id");
            String teamLead = teamDocument.getString("team_lead");

            if (studentID.equals(teamLead) && teamID.equals(teamDocumentTeamID))
                    return true;
        }
        return false;
    }


    /**
     * Checks if the team is already full
     * @param teamCollection
     * @param teamID
     * @param courseID
     * @return boolean
     */
    public boolean isTeamFull(MongoCollection<Document> teamCollection, String teamID, String courseID ) {
        List<Document> teamDocuments = new TeamInterface().getAllTeams(courseID);
        if (teamDocuments == null || teamDocuments.size() == 0) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments ) {
            String teamDocumentTeamID = teamDocument.getString("team_id");
            if (teamDocument.getBoolean("is_full") && teamID.equals(teamDocumentTeamID))
                    return true;
        }
        return false;
    }

    /**
     * Checks team status
     * @param teamCollection
     * @param teamID
     * @param courseID
     * @return boolean
     */
    public boolean isTeamLock(MongoCollection<Document> teamCollection, String teamID, String courseID ) {
        List<Document> teamDocuments = new TeamInterface().getAllTeams(courseID);
        if (teamDocuments == null || teamDocuments.size() == 0) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments ) {
            String teamDocumentTeamID = teamDocument.getString("team_id");
            if (teamDocument.getBoolean("team_lock") && teamID.equals(teamDocumentTeamID))
                    return true;
        }
        return false;
    }

    /**
     * Checks if the team with the request.teamID() is already created
     * @param teamCollection MongoCollection<Document>
     * @param request TeamParam:{"team_id", "course_id", "student_id", "team_size"}
     * @return boolean
     */
    public boolean isTeamCreated(MongoCollection<Document> teamCollection, String teamID, String courseID) {
        List<Document> teamDocuments = new TeamInterface().getAllTeams(courseID);
        if (teamDocuments == null || teamDocuments.size() == 0) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments ) {
            String teamDocumentTeamID = teamDocument.getString("team_id");
            String teamDocumentCourseID = teamDocument.getString("course_id");
            if (teamDocumentTeamID.equals(teamID) && teamDocumentCourseID.equals(courseID))
                    return true;
        }
            return false;
    }

    /**
     * Checks if team name is unique amongst the other teams
     * @param teamCollection
     * @param courseDocument
     * @param request
     * @return boolean
     */
    public boolean isTeamNameUnique(Document courseDocument, TeamParam request) {
        List<Document> teamDocuments = new TeamInterface().getAllTeams(request.getCourseID());
        if (teamDocuments == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments ) {
            String teamDocumentTeamName = teamDocument.getString("team_id");
            String teamDocumentTeamID = teamDocument.getString("course_id");
            if (request.getCourseID().equals(teamDocumentTeamID)) 
                if (request.getTeamName().toLowerCase().equals(teamDocumentTeamName.toLowerCase()))
                    return false;
        }
        return true;
    }

    /**
     * Checks if the team name passed in is a valid team name
     * @param studentCollection
     * @param teamName
     * @param courseID
     * @return boolean
     */
    public boolean isTeamNameValid(MongoCollection<Document> studentCollection , String teamName, String courseID) {
        List<Document> studentDocuments = new TeamInterface().getAllStudentsInThisCourse(courseID);
        if (studentDocuments == null || studentDocuments.size() == 0) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No students found.").build());

        for (Document studentDocument : studentDocuments ) {
            String lastName = studentDocument.getString("last_name").toLowerCase().trim();
            String firstName = studentDocument.getString("first_name").split(" ")[0].toLowerCase().trim();
            if (teamName.toLowerCase().contains(lastName) || teamName.toLowerCase().contains(firstName))
                return false;
        }
        return true;
    }
}
