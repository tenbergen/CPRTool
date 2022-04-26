package edu.oswego.cs.database;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class GradeInterface {
    private final MongoCollection<Document> submissionsCollection;

    public GradeInterface() {
        DatabaseManager manage = new DatabaseManager();
        try {
            submissionsCollection = manage.getAssignmentDB().getCollection("submissions");
        } catch (WebApplicationException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to connect to the submissions collection").build());
        }
    }

    public Document getGrade(String course_id, int assignment_id, String student_id) {
        Document result = submissionsCollection.find(
                and(
                        eq("course_id", course_id),
                        eq("assignment_id", assignment_id),
                        eq("members", student_id),
                        eq("type", "team_submission")
                )
        ).first();
        if (result == null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("team submission does not exist").build());
        if (result.getInteger("grade") == null) {
            return new Document("grade", -1);
        } else return new Document("grade", result.getInteger("grade"));
    }

}