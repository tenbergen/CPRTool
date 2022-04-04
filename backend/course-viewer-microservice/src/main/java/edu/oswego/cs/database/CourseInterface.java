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

public class CourseInterface {
    private final MongoCollection<Document> studentCollection;
    private final MongoCollection<Document> courseCollection;

    public CourseInterface() {
        DatabaseManager databaseManager = new DatabaseManager();
        try {
            MongoDatabase studentDB = databaseManager.getStudentDB();
            MongoDatabase courseDB = databaseManager.getCourseDB();
            studentCollection = studentDB.getCollection("students");
            courseCollection = courseDB.getCollection("courses");
        } catch (WebApplicationException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to retrieve collections.").build());
        }
    }

    public List<Document> getAllCourses() {
        MongoCursor<Document> query = courseCollection.find().iterator();
        List<Document> courses = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            courses.add(document);
        }
        return courses;
    }

    public Document getCourse(String courseID) {
        Document document = courseCollection.find(eq("course_id", courseID)).first();
        if (document == null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This course does not exist.").build());
        return document;
    }

    public List<Document> getAllStudents() {
        MongoCursor<Document> query = studentCollection.find().iterator();
        List<Document> students = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            students.add(document);
        }
        return students;
    }

    public Document getStudent(String studentID) {
        Document document = studentCollection.find(eq("student_id", studentID)).first();
        if (document == null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This student does not exist.").build());
        return document;
    }
}