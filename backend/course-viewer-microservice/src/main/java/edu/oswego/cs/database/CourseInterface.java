package edu.oswego.cs.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.daos.CourseDAO;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class CourseInterface {
    private final MongoDatabase courseDB;
    private final List<CourseDAO> courses = new ArrayList<>();

    public CourseInterface() throws Exception {
        DatabaseManager databaseManager = new DatabaseManager();
        try {
            courseDB = databaseManager.getCourseDB();
        } catch (Exception e) {
            throw new Exception();
        }
    }

    public List<CourseDAO> getAllCourses() {
        MongoCollection<Document> courseCollection = courseDB.getCollection("courses");
        for (Document document : courseCollection.find()) {
            CourseDAO courseDAO = new CourseDAO(
                    (String) document.get("abbreviation"),
                    (String) document.get("course_name"),
                    (String) document.get("course_section"),
                    (String) document.get("semester"),
                    (String) document.get("year")
            );
            courses.add(courseDAO);
        }
        return courses;
    }

    public CourseDAO getCourse(String courseID) {
        MongoCollection<Document> courseCollection = courseDB.getCollection("courses");
        Document document = courseCollection.find(eq("course_id", courseID)).first();
        assert document != null;
        CourseDAO courseDAO = new CourseDAO(
                (String) document.get("abbreviation"),
                (String) document.get("course_name"),
                (String) document.get("course_section"),
                (String) document.get("semester"),
                (String) document.get("year")
        );
        @SuppressWarnings("unchecked") List<String> students = (List<String>) document.get("students");
        @SuppressWarnings("unchecked") List<String> teams = (List<String>) document.get("teams");
        courseDAO.students = students;
        courseDAO.teams = teams;
        return courseDAO;
    }
}