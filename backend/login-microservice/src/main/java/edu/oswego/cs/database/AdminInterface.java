package edu.oswego.cs.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import edu.oswego.cs.dao.ProfanitySettings;
import edu.oswego.cs.dao.User;
import edu.oswego.cs.util.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.util.CPRException;

import org.apache.http.protocol.HTTP;
import org.bson.Document;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.text.DecimalFormat;

import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class AdminInterface {

    private final MongoCollection<Document> professorCollection;
    private final MongoCollection<Document> studentCollection;
    private final MongoCollection<Document> courseCollection;
    private final MongoCollection<Document> profanitySettings;

    // Make a generic method to receive a mongo collection and check connection
    public AdminInterface() {
        DatabaseManager databaseManager = new DatabaseManager();
        try {
            MongoDatabase studentDB = databaseManager.getStudentDB();
            // Professors and Admins are in the same database, Admins are elevated
            MongoDatabase profAdminDb = databaseManager.getProfessorDB();
            MongoDatabase courseDB = databaseManager.getCourseDB();
            studentCollection = studentDB.getCollection("students");
            professorCollection = profAdminDb.getCollection("professors");
            courseCollection = courseDB.getCollection("courses");
            profanitySettings = courseDB.getCollection("profanitySettings");
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
            studentCollection = studentDB.getCollection("students");
            professorCollection = profAdminDb.getCollection("professors");
            courseCollection = courseDB.getCollection("courses");
            profanitySettings = courseDB.getCollection("profanitySettings");
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
                .append("admin", true);
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
                .append("admin", false);
        professorCollection.insertOne(newProfessor);
    }

    public void addStudentUser(String firstName, String lastName, String user_id) {
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
        if (adminDocument == null) {
            return false;
        }
        return true;
    }

    private Boolean checkStudent(String user_id) {
        Document studentDocument = studentCollection.find(eq("student_id", user_id)).first();
        if (studentDocument == null) {
            return false;
        }
        return true;
    }

    private Boolean checkProfessor(String user_id) {
        Document professorDocument = professorCollection.find(eq("professor_id", user_id)).first();
        if (professorDocument == null) {
            return false;
        }
        return true;
    }

    private void checkIfUserIdExists(String user_id) {
        if (user_id == null || user_id.isEmpty()) {
            throw new CPRException(Response.Status.BAD_REQUEST, "User ID cannot be null.");
        }
    }

    public void addBlockedWord(String word) throws Exception {
        // Get specific document from MongoDB for profanity settings
        Document ps = profanitySettings.find().first();
    try {
        // Get list of blocked words from profanity settings
        assert ps != null;
        HashSet<String> blockedWords = (HashSet<String>) ps.get("blocked_words");
        // Add new blocked word to list
        blockedWords.add(word);
        // Update MongoDB with new list of blocked words
        profanitySettings.updateOne(eq("blocked_words", blockedWords), set("blocked_words", blockedWords));
    } catch (Exception e) {
        throw new CPRException(Response.Status.BAD_REQUEST, "Failed to add blocked word to MongoDB");
    }
        }

    public void deleteBlockedWord(String word) throws Exception {
        // Get specific document from MongoDB for profanity settings
        Document ps = profanitySettings.find().first();
        try {
            // Get list of blocked words from profanity settings
            assert ps != null;
            HashSet<String> blockedWords = (HashSet<String>) ps.get("blocked_words");
            // Remove blocked word from list
            blockedWords.remove(word);
            // Update MongoDB with new list of blocked words
            profanitySettings.updateOne(eq("blocked_words", blockedWords), set("blocked_words", blockedWords));
        } catch (Exception e) {
            throw new CPRException(Response.Status.BAD_REQUEST, "Failed to delete blocked word from MongoDB");
        }
    }

    public void addAllowedWord(String word) throws Exception {
        Document ps = profanitySettings.find().first();
        try {
            assert ps != null;
            HashSet<String> allowedWords = (HashSet<String>) ps.get("allowed_words");
            allowedWords.add(word);
            profanitySettings.updateOne(eq("allowed_words", allowedWords), set("allowed_words", allowedWords));
        } catch (Exception e) {
            throw new CPRException(Response.Status.BAD_REQUEST, "Failed to add allowed word to MongoDB");
        }
    }

    public void deleteAllowedWord(String word) throws Exception {
        Document ps = profanitySettings.find().first();
        try {
            assert ps != null;
            HashSet<String> allowedWords = (HashSet<String>) ps.get("allowed_words");
            allowedWords.remove(word);
            profanitySettings.updateOne(eq("allowed_words", allowedWords), set("allowed_words", allowedWords));
        } catch (Exception e) {
            throw new CPRException(Response.Status.BAD_REQUEST, "Failed to delete allowed word from MongoDB");
        }
    }

// TODO Not 100 on this implementation.  May be better offer working with a list of words
public ProfanitySettings getProfanitySettings() {
    // Get specific document from MongoDB for profanity settings
    Document ps = profanitySettings.find().first();
    // Get list of blocked words from profanity settings
    assert ps != null;
    SortedSet<String> blockedWords = (SortedSet<String>) ps.get("blocked_words");
    SortedSet<String> allowedWords = (SortedSet<String>) ps.get("allowed_words");
    ProfanitySettings profanitySettings = new ProfanitySettings(blockedWords, allowedWords);
    return new  ProfanitySettings((SortedSet<String>) ps.get("blocked_words"), (SortedSet<String>) ps.get("allowed_words"));
}

    public Object getUsersView() {
        List<User> users = new ArrayList<User>();
        // iterate though mongodb users and add to list
        for (Document user : studentCollection.find()) {
            User u = new User(user.getString("student_id"), "student",user.getString("first_name"), user.getString("last_name"));
            users.add(u);
        }

        for (Document user : professorCollection.find()) {
            User u = new User(user.getString("professor_id"), "professor",user.getString("first_name"), user.getString("last_name"));
            if (user.getBoolean("admin")) {
                u.setRole("admin");
            }
            users.add(u);
        }

        return users;
    }

    public Object getCoursesView() {
        List<Document> courses = new ArrayList<Document>();

        return courses;
    }
}