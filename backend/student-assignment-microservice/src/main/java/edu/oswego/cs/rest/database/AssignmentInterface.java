package edu.oswego.cs.rest.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.rest.daos.AssignmentDAO;
import edu.oswego.cs.rest.daos.FileDAO;
import org.bson.Document;

import javax.print.Doc;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class AssignmentInterface {

    static MongoDatabase assignmentDatabase;
    static MongoDatabase teamsDatabase;
    static MongoCollection<Document> assignmentsCollection;
    static MongoCollection<Document> teamsCollection;
    static MongoCollection<Document> submissionCollection;
    private final List<AssignmentDAO> assignments = new ArrayList<>();


    static String reg = "/";
    static int nextPos = 0;

    public AssignmentInterface() {
        try {
            DatabaseManager manager = new DatabaseManager();
            assignmentDatabase = manager.getAssignmentDB();

        } catch (WebApplicationException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to retrieve collections.").build());
        }
    }

    public void writeToAssignment(FileDAO fileDAO) throws IOException {
        String path = "courses" + reg
                + fileDAO.getCourseID() + reg
                + fileDAO.getAssignmentID() + reg
                + "team-submissions";

        if (!new File(path).exists()) {
            new File(path).mkdirs();
        }
        fileDAO.writeFile(path + reg + fileDAO.getFilename());
    }

    /**
     * Retrieves the relative location of the root Directory
     *
     * @return String directory location the hw files should be saved to
     */
    public static String getRelPath() {
        String path = (System.getProperty("user.dir").contains("\\")) ? System.getProperty("user.dir").replace("\\", "/") : System.getProperty("user.dir");
        String[] slicedPath = path.split("/");
        String targetDir = "student-assignment-microservice";
        int i;
        StringBuilder relativePathPrefix = new StringBuilder();
        System.out.println(Arrays.toString(slicedPath));
        for (i = slicedPath.length - 1; !slicedPath[i].equals(targetDir); i--) {
            relativePathPrefix.append("../");
        }
        reg = "/";
        if (System.getProperty("user.dir").contains("\\")) {
            reg = "\\";
            relativePathPrefix = new StringBuilder(relativePathPrefix.toString().replace("/", "\\"));
        }
        return relativePathPrefix.toString();
    }


    public List<AssignmentDAO> getAssignmentsByCourse(String courseID) {
        for (Document document : assignmentsCollection.find()) {
            if (document.get("course_id").equals(courseID)) {
                AssignmentDAO assignmentDAO = new AssignmentDAO(
                        document.getString("assignment_name"),
                        document.getString("instructions"),
                        document.getString("due_date"),
                        document.getString("course_id"),
                        document.getInteger("points")
                );
                assignments.add(assignmentDAO);
            }
        }
        return assignments;
    }

    public List<AssignmentDAO> getAllAssignments() {
        for (Document document : assignmentsCollection.find()) {
            AssignmentDAO assignmentDAO = new AssignmentDAO(
                    document.getString("assignment_name"),
                    document.getString("course_id"),
                    document.getString("due_date"),
                    document.getString("instructions"),
                    document.getInteger("points")
            );
            assignments.add(assignmentDAO);
        }
        return assignments;
    }

    public static String findFile(String courseID, int assignmentID, String fileName){
        return getRelPath() + "courses" + reg + courseID + reg + assignmentID + reg + "assignments" + reg + fileName;
    }

    public static String findAssignment(String courseID, int assID) {
        return getRelPath() + "courses" + reg + courseID + reg + assID + reg + "assignments";
    }
}