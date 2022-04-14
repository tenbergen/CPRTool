package edu.oswego.cs.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class GradeInterface {

    static MongoDatabase gradesDatabase;
    static MongoCollection<Document> gradesCollection;

    public GradeInterface() {
        try {
            DatabaseManager manager = new DatabaseManager();
            gradesDatabase = manager.getAssignmentDB();
            gradesCollection = gradesDatabase.getCollection("grades");
        } catch (WebApplicationException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to retrieve collections.").build());
        }
    }

    public List<Document> getAllGrades(String courseID, String studentID) {
        MongoCursor<Document> query = gradesCollection.find(and(
                eq("course_id", courseID),
                eq("student_id", studentID))).iterator();
        List<Document> grades = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            grades.add(document);
        }
        if (grades.isEmpty()) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment does not exist.").build());
        return grades;
    }

    public Document getGrade(String courseID, int assignmentID, String studentID) {
        Document document = gradesCollection.find(and(
                eq("course_id", courseID),
                eq("assignment_id", assignmentID),
                eq("student_id", studentID))).first();
        if (document == null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This assignment does not exist.").build());
        return document;
    }
}