package edu.oswego.cs.database;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.daos.ProfanitySettings;
import edu.oswego.cs.daos.UserDAO;
import edu.oswego.cs.util.CPRException;

import java.util.List;
import java.util.ArrayList;

import com.google.gson.Gson;
import edu.oswego.cs.util.CourseUtil;
import org.bson.Document;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.mongodb.client.model.Filters;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class AdminInterface {

    private final MongoCollection<Document> professorCollection;
    private final MongoCollection<Document> studentCollection;
    private final MongoCollection<Document> courseCollection;
    private final MongoCollection<Document> profanitySettings;
    private final MongoCollection<Document> assignmentCollection;
    private final MongoCollection<Document> submissionCollection;
    private final MongoCollection<Document> teamCollection;


    // Make a generic method to receive a mongo collection and check connection
    public AdminInterface() {
        DatabaseManager databaseManager = new DatabaseManager();
        try {
            MongoDatabase studentDB = databaseManager.getStudentDB();
            // Professors and Admins are in the same database, Admins are elevated
            MongoDatabase profAdminDb = databaseManager.getProfessorDB();
            MongoDatabase courseDB = databaseManager.getCourseDB();
            MongoDatabase assignmentDB = databaseManager.getAssignmentDB();
            MongoDatabase teamDB = databaseManager.getTeamDB();
            studentCollection = studentDB.getCollection("students");
            professorCollection = profAdminDb.getCollection("professors");
            courseCollection = courseDB.getCollection("courses");
            profanitySettings = profAdminDb.getCollection("profanitySettings");
            assignmentCollection = assignmentDB.getCollection("assignments");
            submissionCollection = assignmentDB.getCollection("submissions");
            teamCollection = teamDB.getCollection("teams");
        } catch (CPRException e) {
            throw new CPRException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to retrieve collections.");
        }
    }

    public AdminInterface(String user_id) {
        checkIfUserIdExists(user_id);
        DatabaseManager databaseManager = new DatabaseManager();
        try {
            MongoDatabase studentDB = databaseManager.getStudentDB();
            // Professors and Admins are in the same database, Admins are elevated
            MongoDatabase profAdminDb = databaseManager.getProfessorDB();
            MongoDatabase courseDB = databaseManager.getCourseDB();
            MongoDatabase assignmentDB = databaseManager.getAssignmentDB();
            MongoDatabase teamDB = databaseManager.getTeamDB();
            studentCollection = studentDB.getCollection("students");
            professorCollection = profAdminDb.getCollection("professors");
            courseCollection = courseDB.getCollection("courses");
            profanitySettings = profAdminDb.getCollection("profanitySettings");
            assignmentCollection = assignmentDB.getCollection("assignments");
            submissionCollection = assignmentDB.getCollection("submissions");
            teamCollection = teamDB.getCollection("teams");
        } catch (CPRException e) {
            throw new CPRException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to retrieve collections.");
        }
    }

    public void deleteAdminUser(String user_id) {
        if (professorCollection.countDocuments() == 1) {
            throw new CPRException(Response.Status.BAD_REQUEST, "Cannot delete last admin user.");
        }

        if (!checkAdmin(user_id)) {
            throw new CPRException(Response.Status.NOT_FOUND, "Admin user not found.");
        }

        professorCollection.deleteOne(eq("professor_id", user_id));
    }

    public void deleteProfessorUser(String user_id) {

        if (!checkProfessor(user_id)) {
            throw new CPRException(Response.Status.NOT_FOUND, "Professor user not found.");
        }
        professorCollection.deleteOne(eq("professor_id", user_id));
    }


    public void deleteStudentUser(String user_id) {

        if (!checkStudent(user_id)) {
            throw new CPRException(Response.Status.NOT_FOUND, "Student user not found.");
        }
        studentCollection.deleteOne(eq("student_id", user_id));
    }

    public void addAdminUser(String firstName, String lastName, String user_id) {

        if (checkAdmin(user_id)) {
            throw new CPRException(Response.Status.BAD_REQUEST,
                    "User already exists as Admin.");
        }

        if (checkProfessor(user_id)) {
            throw new CPRException(Response.Status.BAD_REQUEST,
                    "User exists as professor, use Elevate  to admin instead.");
        }

        if (checkStudent(user_id)) {
            throw new CPRException(Response.Status.BAD_REQUEST,
                    "User exists as student, use Elevate student to admin instead.");
        }

        Document newAdmin = new Document("professor_id", user_id)
                .append("first_name", firstName)
                .append("last_name", lastName)
                .append("admin", true)
                .append("courses", new ArrayList<String>());
        professorCollection.insertOne(newAdmin);
    }

    public void addProfessorUser(String firstName, String lastName, String user_id) {
        if (checkAdmin(user_id)) {
            System.out.println("User already exists as Admin.");
            throw new CPRException(Response.Status.BAD_REQUEST,
                    "User already exists as professor.");
        }

        if (checkProfessor(user_id)) {
            throw new CPRException(Response.Status.BAD_REQUEST,
                    "User exists as professor, use Elevate to Admin instead.");
        }

        if (checkStudent(user_id)) {
            throw new CPRException(Response.Status.BAD_REQUEST,
                    "User exists as student, use Elevate to Admin instead.");
        }

        Document newProfessor = new Document("professor_id", user_id)
                .append("first_name", firstName)
                .append("last_name", lastName)
                .append("admin", false)
                .append("courses", new ArrayList<String>());

        professorCollection.insertOne(newProfessor);
    }

    public void addStudentUser(String firstName, String lastName, String user_id) {
        System.out.println(user_id + " " + firstName + " " + lastName);
        if (checkAdmin(user_id)) {
            throw new CPRException(Response.Status.BAD_REQUEST,
                    "User exists as Admin, use Demote to Student instead.");
        }

        if (checkProfessor(user_id)) {
            throw new CPRException(Response.Status.BAD_REQUEST,
                    "User exists as Professor, use Demote to Student instead.");
        }

        if (checkStudent(user_id)) {
            throw new CPRException(Response.Status.BAD_REQUEST,
                    "User already exists.");
        }

        Document newStudent = new Document("student_id", user_id)
                .append("first_name", firstName)
                .append("last_name", lastName);
        studentCollection.insertOne(newStudent);
    }

    public void promoteProfessorToAdmin(String user_id) {
        if (!checkProfessor(user_id)) {
            throw new CPRException(Response.Status.NOT_FOUND, "Professor user not found.");
        }

        professorCollection.updateOne(eq("professor_id", user_id), set("admin", true));
    }

    public void promoteStudentToProfessor(String user_id) {
        if (!checkStudent(user_id)) {
            throw new CPRException(Response.Status.NOT_FOUND, "Student user not found.");
        }

        Document studentDocument = studentCollection.find(eq("student_id", user_id)).first();
        String firstName = studentDocument.getString("first_name");
        String lastName = studentDocument.getString("last_name");

        Document newProfessor = new Document("professor_id", user_id)
                .append("first_name", firstName)
                .append("last_name", lastName)
                .append("admin", false);
        professorCollection.insertOne(newProfessor);
        studentCollection.deleteOne(eq("student_id", user_id));
    }

    public void promoteStudentToAdmin(String user_id) {
        if (!checkStudent(user_id)) {
            throw new CPRException(Response.Status.NOT_FOUND, "Student user not found.");
        }

        Document studentDocument = studentCollection.find(eq("student_id", user_id)).first();
        String firstName = studentDocument.getString("first_name");
        String lastName = studentDocument.getString("last_name");

        Document newProfessor = new Document("professor_id", user_id)
                .append("first_name", firstName)
                .append("last_name", lastName)
                .append("admin", true);
        professorCollection.insertOne(newProfessor);
        studentCollection.deleteOne(eq("student_id", user_id));
    }

    public void demoteProfessorToStudent(String user_id) {
        if (!checkProfessor(user_id)) {
            throw new CPRException(Response.Status.NOT_FOUND, "Professor user not found.");
        }

        Document professorDocument = professorCollection.find(eq("professor_id", user_id)).first();
        String firstName = professorDocument.getString("first_name");
        String lastName = professorDocument.getString("last_name");

        Document newStudent = new Document("student_id", user_id)
                .append("first_name", firstName)
                .append("last_name", lastName);
        studentCollection.insertOne(newStudent);
        professorCollection.deleteOne(eq("professor_id", user_id));
    }

    public void demoteAdminToProfessor(String user_id) {
        if (!checkAdmin(user_id)) {
            throw new CPRException(Response.Status.NOT_FOUND, "Admin user not found.");
        }
        professorCollection.updateOne(eq("professor_id", user_id), set("admin", false));
    }

    public void demoteAdminToStudent(String user_id) {
        if (!checkAdmin(user_id)) {
            throw new CPRException(Response.Status.NOT_FOUND, "Admin user not found.");
        }

        Document professorDocument = professorCollection.find(eq("professor_id", user_id)).first();
        String firstName = professorDocument.getString("first_name");
        String lastName = professorDocument.getString("last_name");

        Document newStudent = new Document("student_id", user_id)
                .append("first_name", firstName)
                .append("last_name", lastName);
        studentCollection.insertOne(newStudent);
        professorCollection.deleteOne(eq("professor_id", user_id));
    }

    private Boolean checkAdmin(String user_id) {
        Document adminDocument = professorCollection.find(
                        Filters.and(
                                eq("professor_id", user_id),
                                eq("admin", true)))
                .first();
        return adminDocument != null;
    }

    private Boolean checkStudent(String user_id) {
        Document studentDocument = studentCollection.find(eq("student_id", user_id)).first();
        return studentDocument != null;
    }

    private Boolean checkProfessor(String user_id) {
        Document professorDocument = professorCollection.find(eq("professor_id", user_id)).first();
        return professorDocument != null;
    }

    private void checkIfUserIdExists(String user_id) {
        if (user_id == null || user_id.isEmpty()) {
            throw new CPRException(Response.Status.BAD_REQUEST, "User ID cannot be null.");
        }
    }

    // TODO:  Add constructor paremeters for CourseDAO
    public List<CourseDAO> getCourseView() {
        List<CourseDAO> courses = new ArrayList<>();
        // iterate through MongoDB CourseDAOs and add to list
        for (Document course : courseCollection.find()) {
            CourseDAO c = new CourseDAO(
                    course.getString("abbreviation"),
                    course.getString("course_name"),
                    course.getString("course_section"),
                    course.getString("crn"),
                    course.getString("semester"),
                    course.getString("year"),
                    course.getString("professor_id")
            );
            courses.add(c);
        }
        return courses;
    }


    public Object getUsersView() {
        List<UserDAO> users = new ArrayList<UserDAO>();
        // iterate though mongodb users and add to list
        for (Document user : studentCollection.find()) {
            UserDAO u = new UserDAO(user.getString("student_id"), "student", user.getString("first_name"), user.getString("last_name"));
            users.add(u);
        }

        for (Document user : professorCollection.find()) {
            UserDAO u = new UserDAO(user.getString("professor_id"), "professor", user.getString("first_name"), user.getString("last_name"));
            if (user.getBoolean("admin")) {  // if is true override role to admin
                u.setRole("admin");
            }
            users.add(u);
        }

        return users;
    }


    public Boolean checkCourse(String crn) {
        Document CourseDAODocument = courseCollection.find(eq("crn", crn)).first();
        if (CourseDAODocument == null) {
            return false;
        }
        return true;
    }

    public String getBlockedWords() {
        // Find the first document in the collection
        Document profanitySettingsDocument = profanitySettings.find().first();
        if (profanitySettingsDocument == null) {
            ProfanitySettings initProfanitySettings = new ProfanitySettings(new ArrayList<String>());
            Document initialProfanitySettings = new Document("words", initProfanitySettings.getWords());
            profanitySettings.insertOne(initialProfanitySettings);
            Gson gson = new Gson();
            return gson.toJson(new ArrayList<String>());
        }

        // Extract the blocked words from the document
        List<String> blockedWords = profanitySettingsDocument.getList("words", String.class);

        // Convert the List<String> to a JSON string using Gson
        Gson gson = new Gson();
        String jsonBlockedWords = gson.toJson(blockedWords);

        return jsonBlockedWords;
    }

    public void updateBlockedWords(String jsonBlockedWords) {
        // Convert the JSON string to a List<String> using Gson
        System.out.printf("jsonBlockedWords: %s%n", jsonBlockedWords);
        Gson gson = new Gson();
        List<String> blockedWords = gson.fromJson(jsonBlockedWords, List.class);
        System.out.printf("blockedWords: %s%n", blockedWords);
        ProfanitySettings temp = new ProfanitySettings(blockedWords);

        System.out.printf("profanitySettingsCollection: %s%n", profanitySettings);

        profanitySettings.deleteMany(new Document());
        System.out.println("Deleted all documents from collection");

        Document profanitySettingsDocument = new Document("words", temp.getWords());
        profanitySettings.insertOne(profanitySettingsDocument);
    }

    public void removeCourseAsAdmin(SecurityContext securityContext, String courseID) {
        Document courseDocument = courseCollection.find(eq("course_id", courseID)).first();
        if (courseDocument == null) throw new CPRException(Response.Status.BAD_REQUEST, "This course does not exist.");
        new CourseUtil().updateCoursesArrayInProfessorDb(securityContext, professorCollection, courseID, null, "DELETE");
        new CourseUtil().updateCoursesArrayInStudentDb(studentCollection, courseID, null, "DELETE");
        new CourseUtil().updateCoursesKeyInDBs(assignmentCollection, courseID, null, "DELETE");
        new CourseUtil().updateCoursesKeyInDBs(submissionCollection, courseID, null, "DELETE");
        new CourseUtil().updateCoursesKeyInDBs(teamCollection, courseID, null, "DELETE");
        courseCollection.deleteOne(eq("course_id", courseID));
    }
}