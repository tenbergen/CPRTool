package edu.oswego.cs.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.daos.FileDAO;
import edu.oswego.cs.daos.StudentDAO;
import org.bson.Document;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.push;
import static com.mongodb.client.model.Updates.set;
import static edu.oswego.cs.util.CSVUtil.parseStudentCSV;

public class CourseInterface {
    private MongoDatabase studentDB;
    private MongoDatabase courseDB;

    public CourseInterface() {
        DatabaseManager databaseManager = new DatabaseManager();
        try {
            studentDB = databaseManager.getStudentDB();
            courseDB = databaseManager.getCourseDB();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    /**
     * Update the course DAO's courseID, then add the course if it is not already existed in the database. At the same
     * time, update the students' course list in the student database if a student list in the request is specified.
     */
    public void addCourse(CourseDAO dao) throws Exception {
        MongoCollection<Document> studentCollection = studentDB.getCollection("students");
        MongoCollection<Document> courseCollection = courseDB.getCollection("courses");

        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> courseDAOEntity = Entity.entity(jsonb.toJson(dao), MediaType.APPLICATION_JSON_TYPE);
        Document course = Document.parse(courseDAOEntity.getEntity());

        if (courseCollection.find(eq("course_id", dao.courseID)).iterator().hasNext()) {
            throw new Exception();
        } else {
            courseCollection.insertOne(course);
        }

        @SuppressWarnings("unchecked") List<String> students = (List<String>) course.get("students");
        for (String student : students) {
            MongoCursor<Document> query = studentCollection.find(eq("student_id", student)).iterator();
            if (query.hasNext()) {
                studentCollection.updateOne(eq("student_id", student), push("courses", dao.courseID));
            }
        }
    }

    /**
     * A course DAO is made from the student DAO. Attempt to create the course, then add the student into the student
     * array in the course using their name from the email and into the student database at the same time with the
     * student's course array updated to have the new course respectively.
     */
    public void addStudent(String email, CourseDAO dao) throws Exception {
        MongoCollection<Document> studentCollection = studentDB.getCollection("students");
        MongoCollection<Document> courseCollection = courseDB.getCollection("courses");

        if (!courseCollection.find(eq("course_id", dao.courseID)).iterator().hasNext()) {
            addCourse(dao);
        }

        Document courseDocument = courseCollection.find(eq("course_id", dao.courseID)).first();
        assert courseDocument != null;
        @SuppressWarnings("unchecked") List<String> students = (List<String>) courseDocument.get("students");
        String studentName = email.split("@")[0];
        if (!students.contains(studentName)) {
            courseCollection.updateOne(eq("course_id", dao.courseID), push("students", studentName));
        }

        MongoCursor<Document> query = studentCollection.find(eq("student_id", studentName)).iterator();
        if (query.hasNext()) {
            Document studentDocument = query.next();
            @SuppressWarnings("unchecked") List<String> courseList = (List<String>) studentDocument.get("courses");
            for (String course : courseList) {
                if (course.equals(dao.courseID)) throw new Exception();
            }
            studentCollection.updateOne(eq("student_id", studentName), push("courses", dao.courseID));
        } else {
            List<String> courseList = new ArrayList<>();
            courseList.add(dao.courseID);
            Document newStudent = new Document()
                    .append("student_id", studentName)
                    .append("courses", courseList);
            studentCollection.insertOne(newStudent);
        }
    }

    /**
     * Remove the course from the student's arraylist of courses, and then remove the course itself from the course database.
     */
    public void removeCourse(CourseDAO dao) throws Exception {
        MongoCollection<Document> studentCollection = studentDB.getCollection("students");
        MongoCollection<Document> courseCollection = courseDB.getCollection("courses");

        MongoCursor<Document> courseQuery = courseCollection.find(eq("course_id", dao.courseID)).iterator();
        if (courseQuery.hasNext()) {
            Document courseDocument = courseQuery.next();
            @SuppressWarnings("unchecked") List<String> students = (List<String>) courseDocument.get("students");
            for (String student : students) {
                MongoCursor<Document> studentQuery = studentCollection.find(eq("student_id", student)).iterator();
                if (studentQuery.hasNext()) {
                    Document studentDocument = studentQuery.next();
                    @SuppressWarnings("unchecked") List<String> courses = (List<String>) studentDocument.get("courses");
                    courses.remove(dao.courseID);
                    studentCollection.updateOne(eq("student_id", student), set("courses", courses));
                }
            }
        } else throw new Exception();
        courseCollection.findOneAndDelete(eq("course_id", dao.courseID));
    }

    /**
     * Remove the student from the course's arraylist of students, and then remove the course from the student's course
     * arraylist in the student database.
     */
    public void removeStudent(String email, CourseDAO dao) throws Exception {
        MongoCollection<Document> studentCollection = studentDB.getCollection("students");
        MongoCollection<Document> courseCollection = courseDB.getCollection("courses");
        String studentName = email.split("@")[0];

        MongoCursor<Document> studentQuery = studentCollection.find(eq("student_id", studentName)).iterator();
        if (studentQuery.hasNext()) {
            Document studentDocument = studentQuery.next();
            @SuppressWarnings("unchecked") List<String> courses = (List<String>) studentDocument.get("courses");
            for (String course : courses) {
                MongoCursor<Document> courseQuery = courseCollection.find(eq("course_id", course)).iterator();
                if (courseQuery.hasNext()) {
                    Document courseDocument = courseQuery.next();
                    @SuppressWarnings("unchecked") List<String> students = (List<String>) courseDocument.get("students");
                    students.remove(studentName);
                    courseCollection.updateOne(eq("course_id", dao.courseID), set("students", students));
                }
            }
            courses.remove(dao.courseID);
            studentCollection.updateOne(eq("student_id", studentName), set("courses", courses));
        } else throw new Exception();
    }

    public void addStudentsFromCSV(FileDAO f) throws Exception {
        List<StudentDAO> allStudents = parseStudentCSV(f.getCsvLines());

        String cid = f.getFilename();
        cid = cid.substring(0, cid.length() - 4);
        System.out.println(cid);
        Document course = courseDB.getCollection("courses").find(new Document("course_id", cid)).first();
        assert course != null;
        CourseDAO courseDAO = new CourseDAO(
                course.get("abbreviation").toString(),
                course.get("course_name").toString(),
                course.get("course_section").toString(),
                course.get("semester").toString(),
                course.get("year").toString()
        );

        ArrayList oldStudentList = (ArrayList) course.get("Students");
        ArrayList<String> newStudentList = new ArrayList<>();
        ArrayList<String> studentsToRemove = new ArrayList<>();
        ArrayList<String> studentsToAdd = new ArrayList<>();
        System.out.println(oldStudentList);
        for (StudentDAO s : allStudents) {
            newStudentList.add(s.email.split("@")[0]);
        }
        for (Object d : oldStudentList) {
            if (!newStudentList.contains(d.toString())) {
                studentsToRemove.add(d.toString());
            }
        }
        for (String s : newStudentList) {
            if (!oldStudentList.contains(s)) {
                studentsToAdd.add(s);
            }
        }
        for (String s : studentsToRemove) {
            removeStudent(s, courseDAO);
        }
        for (String s : studentsToAdd) {
            addStudent(s, courseDAO);
        }
    }
}
