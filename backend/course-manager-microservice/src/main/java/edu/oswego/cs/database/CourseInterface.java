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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.push;
import static com.mongodb.client.model.Updates.set;
import static edu.oswego.cs.util.CSVUtil.parseStudentCSV;

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

    /**
     * Update the course DAO's courseID, then add the course if it is not already existed in the database. At the same
     * time, update the students' course list in the student database if a student list in the request is specified.
     */
    public void addCourse(CourseDAO dao) {
        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> courseDAOEntity = Entity.entity(jsonb.toJson(dao), MediaType.APPLICATION_JSON_TYPE);
        Document course = Document.parse(courseDAOEntity.getEntity());

        MongoCursor<Document> courseQuery = courseCollection.find(eq("course_id", dao.courseID)).iterator();
        if (courseQuery.hasNext()) {
            courseQuery.close();
            throw new WebApplicationException(Response.status(Response.Status.OK).entity("Course already existed.").build());
        }
        courseCollection.insertOne(course);

        List<String> students = course.getList("students", String.class);
        for (String student : students) {
            MongoCursor<Document> studentQuery = studentCollection.find(eq("student_id", student)).iterator();
            if (!studentQuery.hasNext()) {
                studentCollection.updateOne(eq("student_id", student), push("courses", dao.courseID));
            }
        }
    }

    /**
     * Find the course document from Mongo using the current course ID, then update the course document using the new information
     * passed from Frontend.
     */
    public String updateCourse(CourseDAO dao) {
        Document courseDocument = courseCollection.find(eq("course_id", dao.getCourseID())).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This course does not exist.").build());

        String courseID = dao.courseID;
        dao.courseID = dao.abbreviation + "-" + dao.courseSection + "-" + dao.crn + "-" + dao.semester + "-" + dao.year;

        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> courseDAOEntity = Entity.entity(jsonb.toJson(dao), MediaType.APPLICATION_JSON_TYPE);
        Document course = Document.parse(courseDAOEntity.getEntity());
        courseCollection.replaceOne(eq("course_id", courseID), course);
        return dao.courseID;
    }

    /**
     * Add the student into the student array in the course using their name from the email and into the student
     * database at the same time with the student's course array updated to have the new course respectively.
     */
    public void addStudent(StudentDAO student, String courseID) {
        String studentId = student.email.split("@")[0];
        String studentLastName = student.fullName.split(", ")[0];
        String studentFirstName = student.fullName.split(", ")[1];
        Document courseDocument = courseCollection.find(eq("course_id", courseID)).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This course does not exist.").build());

        List<String> students = courseDocument.getList("students", String.class);
        if (students.contains(studentId)) throw new WebApplicationException(Response.status(Response.Status.OK).entity("This student is already in the course.").build());
        courseCollection.updateOne(eq("course_id", courseID), push("students", studentId));

        MongoCursor<Document> query = studentCollection.find(eq("student_id", studentId)).iterator();
        if (query.hasNext()) {
            Document studentDocument = query.next();
            List<String> courseList = studentDocument.getList("courses", String.class);
            for (String course : courseList) {
                if (course.equals(courseID)) {
                    query.close();
                    throw new WebApplicationException(Response.status(Response.Status.OK).entity("This student is already in the course.").build());
                }
            }
            studentCollection.updateOne(eq("student_id", studentId), push("courses", courseID));
            query.close();
        } else {
            List<String> courseList = new ArrayList<>();
            courseList.add(courseID);
            Document newStudent = new Document()
                    .append("first_name", studentFirstName)
                    .append("last_name", studentLastName)
                    .append("student_id", studentId)
                    .append("courses", courseList);
            studentCollection.insertOne(newStudent);
        }
    }

    /**
     * Remove the course from the student's list of courses, and then remove the course itself from the course database.
     */
    public void removeCourse(String courseID) {
        MongoCursor<Document> courseQuery = courseCollection.find(eq("course_id", courseID)).iterator();
        if (!courseQuery.hasNext()) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This course does not exist.").build());

        Document courseDocument = courseQuery.next();
        List<String> students = courseDocument.getList("students", String.class);
        for (String student : students) {
            MongoCursor<Document> studentQuery = studentCollection.find(eq("student_id", student)).iterator();
            if (studentQuery.hasNext()) {
                Document studentDocument = studentQuery.next();
                List<String> courses = studentDocument.getList("courses", String.class);
                courses.remove(courseID);
                studentCollection.updateOne(eq("student_id", student), set("courses", courses));
                studentQuery.close();
            }
        }
        courseCollection.findOneAndDelete(eq("course_id", courseID));
        courseQuery.close();
    }

    /**
     * Remove the student from the course's arraylist of students, and then remove the course from the student's course
     * arraylist in the student database.
     */
    public void removeStudent(String studentID, String courseID) {
        MongoCursor<Document> studentQuery = studentCollection.find(and(eq("student_id", studentID),
                                                                        eq("courses", courseID))).iterator();
        if (!studentQuery.hasNext()) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This student does not exist in this course.").build());
        Document studentDocument = studentQuery.next();
        List<String> courses = studentDocument.getList("courses", String.class);
        courses.remove(courses.indexOf(courseID));
        studentCollection.updateOne(eq("student_id", studentID), set("courses", courses));
        studentQuery.close();

        MongoCursor<Document> courseQuery = courseCollection.find(eq("course_id", courseID)).iterator();
        if (!courseQuery.hasNext()) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This course does not contain this student").build());
        Document courseDocument = courseQuery.next();
        List<String> students = courseDocument.getList("students", String.class);
        students.remove(students.indexOf(studentID));
        courseCollection.updateOne(eq("course_id", courseID), set("students", students));

        courseQuery.close();
    }

    public void addStudentsFromCSV(FileDAO fileDAO) {
        List<StudentDAO> allStudents = parseStudentCSV(fileDAO.getCsvLines());

        String cid = fileDAO.getFilename();
        cid = cid.substring(0, cid.length() - 4);
        Document course = courseCollection.find(eq("course_id", cid)).first();
        if (course == null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This course does not exist.").build());

        List<String> oldStudentList = course.getList("students", String.class);
        String courseID = course.getString("course_id");
        ArrayList<String> newStudentList = new ArrayList<>();
        ArrayList<String> studentsToRemove = new ArrayList<>();
        ArrayList<String> studentsToAdd = new ArrayList<>();

        for (StudentDAO studentDAO : allStudents) newStudentList.add(studentDAO.email.split("@")[0]);

        for (String student : oldStudentList) {
            if (!newStudentList.contains(student)) studentsToRemove.add(student);
        }

        for (String student : newStudentList) {
            if (!oldStudentList.contains(student)) studentsToAdd.add(student);
        }

        for (String student : studentsToRemove) removeStudent(student, courseID);

        for (StudentDAO student : allStudents.stream()
                .filter(s -> studentsToAdd.contains(s.email.split("@")[0]))
                .collect(Collectors.toList())) {
            addStudent(student, courseID);
        }
    }
}
