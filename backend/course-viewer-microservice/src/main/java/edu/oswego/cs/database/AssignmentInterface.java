package edu.oswego.cs.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class AssignmentInterface {

    static MongoDatabase assignmentDatabase;
    static MongoCollection<Document> assignmentsCollection;

    public AssignmentInterface() {
        try {
            DatabaseManager manager = new DatabaseManager();
            assignmentDatabase = manager.getAssignmentDB();
            assignmentsCollection = assignmentDatabase.getCollection("assignments");
        } catch (WebApplicationException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to retrieve collections.").build());
        }
    }

    public List<Document> getAllGrades() {
        MongoCursor<Document> query = assignmentsCollection.find().iterator();
        List<Document> grades = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            grades.add(document);
        }
        query.close();
        return grades;
    }

    public Document getGrade(String assignmentName) {
        Document document = assignmentsCollection.find(eq("assignment_name", assignmentName)).first();
        if (document == null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This assignment does not exist.").build());
        return document;
    }
}