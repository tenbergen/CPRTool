package edu.oswego.cs.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.daos.StudentDAO;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class CourseInterface {
    private final MongoCollection<Document> studentCollection;
    private final MongoCollection<Document> courseCollection;

    public CourseInterface() throws Exception {
        DatabaseManager databaseManager = new DatabaseManager();
        try {
            MongoDatabase studentDB = databaseManager.getStudentDB();
            MongoDatabase courseDB = databaseManager.getCourseDB();
            studentCollection = studentDB.getCollection("students");
            courseCollection = courseDB.getCollection("courses");
        } catch (Exception e) {
            throw new Exception();
        }
    }

    public List<CourseDAO> getAllCourses() {
        List<CourseDAO> courses = new ArrayList<>();
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

    public List<StudentDAO> getAllStudents() {
        List<StudentDAO> students = new ArrayList<>();
        for (Document document : studentCollection.find()) {
            StudentDAO studentDAO = new StudentDAO((String) document.get("student_id"));
            @SuppressWarnings("unchecked") List<String> courses = (List<String>) document.get("courses");
            studentDAO.courses = courses;
            students.add(studentDAO);
        }
        return students;
    }

    public StudentDAO getStudent(String studentID) {
        Document document = studentCollection.find(eq("student_id", studentID)).first();
        assert document != null;
        StudentDAO studentDAO = new StudentDAO((String) document.get("student_id"));
        @SuppressWarnings("unchecked") List<String> courses = (List<String>) document.get("courses");
        studentDAO.courses = courses;
        return studentDAO;
    }
}