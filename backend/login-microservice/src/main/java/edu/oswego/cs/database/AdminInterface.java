package edu.oswego.cs.database;

import edu.oswego.cs.dao.Course;
import edu.oswego.cs.dao.ProfanitySettings;
import edu.oswego.cs.dao.User;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.util.CPRException;

   import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;
import org.bson.Document;

import javax.ws.rs.core.Response;

import com.mongodb.client.model.Filters;
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
            profanitySettings = profAdminDb.getCollection("profanitySettings");
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

    public void deleteCourse(String course_id) {
        if (!checkCourse(course_id)) {
            throw new CPRException(Response.Status.NOT_FOUND, "Course not found.");
        }
        courseCollection.deleteOne(eq("course_id", course_id));
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

    

    public void addBlockedWord(String word) throws CPRException {
        // Validate input
        if (word == null || word.isEmpty()) {
            throw new CPRException(Response.Status.BAD_REQUEST, "Blocked word cannot be null or empty");
        }
        // Search for existing document with matching word
        Document existingDoc = profanitySettings.find(eq("blocked_word", word)).first();
        if (existingDoc != null) {
            throw new CPRException(Response.Status.CONFLICT, "Blocked word already exists");
        }
        // Create a new document with the word to be added
        Document doc = new Document("blocked_word", word);
        // Insert the document into the blocked_words collection
        try {
            profanitySettings.insertOne(doc);
        } catch (Exception e) {
            throw new CPRException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to add blocked word to MongoDB");
        }
    }
    

    public void deleteBlockedWord(String word) throws CPRException {
        // Validate input
        if (word == null || word.isEmpty()) {
            throw new CPRException(Response.Status.BAD_REQUEST, "Blocked word cannot be null or empty");
        }
        // Search for existing document with matching word
        Document existingDoc = profanitySettings.find(eq("blocked_word", word)).first();
        if (existingDoc == null) {
            throw new CPRException(Response.Status.NOT_FOUND, "Blocked word not found");
        }
        // Delete the document with the matching word
        try {
            profanitySettings.deleteOne(existingDoc);
        } catch (Exception e) {
            throw new CPRException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to delete blocked word from MongoDB");
        }
    }
    

    public void addAllowedWord(String word) throws CPRException {
        // Validate input
        if (word == null || word.isEmpty()) {
            throw new CPRException(Response.Status.BAD_REQUEST, "allowed word cannot be null or empty");
        }
        // Search for existing document with matching word
        Document existingDoc = profanitySettings.find(eq("allowed_word", word)).first();
        if (existingDoc != null) {
            throw new CPRException(Response.Status.CONFLICT, "allowed word already exists");
        }
        // Create a new document with the word to be added
        Document doc = new Document("allowed_word", word);
        // Insert the document into the allowed_words collection
        try {
            profanitySettings.insertOne(doc);
        } catch (Exception e) {
            throw new CPRException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to add allowed word to MongoDB");
        }
    }
    
    
    public void deleteAllowedWord(String word) throws CPRException {
        // Validate input
        if (word == null || word.isEmpty()) {
            throw new CPRException(Response.Status.BAD_REQUEST, "allowed word cannot be null or empty");
        }
        // Search for existing document with matching word
        Document existingDoc = profanitySettings.find(eq("allowed_word", word)).first();
        if (existingDoc == null) {
            throw new CPRException(Response.Status.NOT_FOUND, "allowed word not found");
        }
        // Delete the document with the matching word
        try {
            profanitySettings.deleteOne(existingDoc);
        } catch (Exception e) {
            throw new CPRException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to delete allowed word from MongoDB");
        }
    }


    public ProfanitySettings getProfanitySettings() {
       Document profanitySettingsDocument = profanitySettings.find().first();
        assert profanitySettingsDocument != null;
        ArrayList<String> blockedWords = profanitySettingsDocument.get("words", ArrayList.class);
        ProfanitySettings profanitySettings = new ProfanitySettings();
        profanitySettings.setWords(blockedWords);
        return profanitySettings;
    }


    public List<Course> getCoursesView() {
        List<Course> courses = new ArrayList<>();
        // iterate through MongoDB courses and add to list
        for (Document course : courseCollection.find()) {
            Course c = new Course(
                course.getString("course_name"),
                course.getString("crn"),
                course.getString("professor"),
                course.getInteger("year"),
                course.getString("semester")
            );
            courses.add(c);
        }
        return courses;
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
            if (user.getBoolean("admin")) {  // if is true override role to admin
                u.setRole("admin");
            }
            users.add(u);
        }

        return users;
    }


    public Boolean checkCourse(String course_id){
        Document courseDocument = courseCollection.find(eq("course_id", course_id)).first();
        if (courseDocument == null) {
            return false;
        }
        return true;
    }

public String getBlockedWords() {
    // Find the first document in the collection
    Document profanitySettingsDocument = profanitySettings.find().first();

    // Extract the blocked words from the document
    List<String> blockedWords = profanitySettingsDocument.getList("words", String.class);

    // Convert the List<String> to a JSON string using Gson
    Gson gson = new Gson();

    return gson.toJson(blockedWords);
}

public void updateBlockedWords(String jsonBlockedWords) {
    // Convert the JSON string to a List<String> using Gson
    Gson gson = new Gson();
    List<String> blockedWords = gson.fromJson(jsonBlockedWords, List.class);

    // Replace the blocked words in the collection with the new list
    Document updateDocument = new Document("$set", new Document("words", blockedWords));
    profanitySettings.updateOne(new Document(), updateDocument);
}

}