package edu.oswego.cs.rest.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class studentAssignmentInterface {

    public class CourseInterface {
        private final MongoCollection<Document> studentCollection;
        private final MongoCollection<Document> assignmentCollection;

        public CourseInterface() {
            DatabaseManager databaseManager = new DatabaseManager();
            try {
                MongoDatabase studentDB = databaseManager.getStudentDB();
                MongoDatabase courseDB = databaseManager.getCourseDB();
                studentCollection = studentDB.getCollection("students");
                assignmentCollection = courseDB.getCollection("assignments");
            } catch (WebApplicationException e) {
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to retrieve collections.").build());
            }
        }
    }
}
