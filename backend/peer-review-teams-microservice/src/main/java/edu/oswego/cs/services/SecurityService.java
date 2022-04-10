package edu.oswego.cs.services;

import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import org.bson.Document;
import edu.oswego.cs.requests.TeamParam;


public class SecurityService {

    /**
     * Checks if the passed in studentID is in the right course
     * @param courseDocument Document
     * @param request TeamParam
     * @return boolean
     */
    public boolean isStudentValid(Document courseDocument,TeamParam request) {
        List<String> students = courseDocument.getList("students", String.class);

        for (String student : students) 
            if (request.getStudentID().equals(student)) return true;
        
        return false;
    }

    /**
     * Checks if the passed in studentID is already in a team
     * @param teamCollection MongoCollection<Document>
     * @param request TeamParam:{"team_id", "course_id", "student_id", "team_size"}
     * @return boolean
     */
    public boolean isStudentAlreadyInATeam(MongoCollection<Document> teamCollection, TeamParam request) {
        MongoCursor<Document> cursor = teamCollection.find().iterator();
        if (cursor == null) 
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to retrieve team collection.").build());
        
        try { 
            while(cursor.hasNext()) { 
                Document teamDocument = cursor.next();
                List<String> members = teamDocument.getList("team_members", String.class);
                String courseID = teamDocument.get("course_id").toString();
                for (String member : members) {
                    if (request.getStudentID().equals(member)){
                        if (request.getCourseID().equals(courseID))
                            return true;
                    }
                }
            } 
        } finally { 
            cursor.close();
        } 
        return false;
    }

    /**
     * Checks if the team with the request.teamID() is already created
     * @param teamCollection MongoCollection<Document>
     * @param request TeamParam:{"team_id", "course_id", "student_id", "team_size"}
     * @return boolean
     */
    public boolean isTeamCreated(MongoCollection<Document> teamCollection, TeamParam request) {
        MongoCursor<Document> cursor = teamCollection.find().iterator();
        if (cursor == null) 
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to retrieve team collection.").build());
        
        try { 
            while(cursor.hasNext()) {
                Document teamDocument = cursor.next();
                String courseID = teamDocument.get("course_id").toString();
                String teamID = teamDocument.get("team_id").toString();
                
                if (request.getCourseID().equals(courseID) && request.getTeamID().equals(teamID))
                        return true;

            }
        } finally { 
            cursor.close();
        } 

        return false;
    }

    /**
     * allows security checks on passed in information
     * @param courseDocument
     * @param request 
     * @param mode
     */
    public void securityChecks(MongoCollection<Document> teamCollection, Document courseDocument, TeamParam request, String mode) {
        if (!isStudentValid(courseDocument, request)) 
        throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Student not found in this course.").build());

        MongoCursor<Document> cursor = teamCollection.find().iterator();
        if (cursor == null) 
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to retrieve team collection.").build());

        if (cursor.hasNext()) {
            if (isStudentAlreadyInATeam(teamCollection, request)) 
                throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Student is already in a team.").build());
            if (mode.equals("CREATE")) {
                if (isTeamCreated(teamCollection, request)) 
                    throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Team is already created.").build());
            } else if (mode.equals("JOIN")) {
                if (!isTeamCreated(teamCollection, request)) 
                    throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
            }
        }
    }

}
