package edu.oswego.cs.services;

import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import org.bson.Document;
import edu.oswego.cs.requests.TeamParam;
import static com.mongodb.client.model.Filters.eq;


public class SecurityService {

    /**
     * Checks if the student is in the right course
     * @param courseDocument Document
     * @param request TeamParam
     */
    public void isStudentValid(Document courseDocument,TeamParam request) {
        List<String> students = courseDocument.getList("students", String.class);

        for (String student : students) 
            if (request.getStudentID().equals(student)) return;
        
        throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Student not found in this course.").build());
    }

    /**
     * Checks if a student is already in a team
     * @param teamCollection MongoCollection<Document>
     * @param request TeamParam:{"team_id", "course_id", "student_id", "team_size"}
     */
    public void isStudentAlreadyInATeam(MongoCollection<Document> teamCollection, TeamParam request) {
        MongoCursor<Document> cursor = teamCollection.find().iterator();
        try { 
            while(cursor.hasNext()) { 
                List<String> members = cursor.next().getList("team_members", String.class);
                for (String member : members) {
                    if (request.getStudentID().equals(member)){
                        Response response = Response.status(Response.Status.CONFLICT).entity("Student is already in a team.").build();
                        throw new WebApplicationException(response);
                    }
                }
            } 
        } finally { 
            cursor.close(); 
        } 
    }

    /**
     * Checks if the team with the request.teamID() is already created
     * @param teamCollection MongoCollection<Document>
     * @param request TeamParam:{"team_id", "course_id", "student_id", "team_size"}
     */
    public void isTeamCreated(MongoCollection<Document> teamCollection, TeamParam request) {
        Document teamDocument = teamCollection.find(eq("team_id", request.getTeamID())).first();
        if (teamDocument != null)
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Team is already created.").build());
    }

   
}
