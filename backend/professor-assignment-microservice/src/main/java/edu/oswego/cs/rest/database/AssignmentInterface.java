package edu.oswego.cs.rest.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.rest.daos.AssignmentDAO;
import edu.oswego.cs.rest.daos.FileDAO;
import org.apache.commons.io.FileUtils;
import org.bson.Document;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class AssignmentInterface {
    private final MongoCollection<Document> assignmentsCollection;
    private static String reg;

    // Set this to true if running on Windows.
    private static final boolean isWindows = false;

    public AssignmentInterface() {
        try {
            DatabaseManager manager = new DatabaseManager();
            MongoDatabase assignmentDatabase = manager.getAssignmentDB();
            assignmentsCollection = assignmentDatabase.getCollection("assignments");
        } catch (WebApplicationException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to retrieve collections.").build());
        }
    }

    /**
     * Retrieves the relative location of the root Directory
     *
     * @return String directory location the hw files should be saved to
     */
    public static String getRelPath() {
        String path = (System.getProperty("user.dir").contains("\\")) ? System.getProperty("user.dir").replace("\\", "/") : System.getProperty("user.dir");
        String[] slicedPath = path.split("/");
        String targetDir = "defaultServer";
        StringBuilder relativePathPrefix = new StringBuilder();
        for (int i = slicedPath.length - 1; !slicedPath[i].equals(targetDir); i--) {
            relativePathPrefix.append("../");
        }
        reg = "\\";
        if (!isWindows) {
            reg = "/";
            relativePathPrefix = new StringBuilder(relativePathPrefix.toString().replace("\\", "/"));
        }
        return relativePathPrefix.toString();
    }

    public static String findAssignment(String courseID, int assignmentID) {
        return getRelPath() + "assignments" + reg + courseID + reg + assignmentID + reg + "assignments";
    }

    public static String findPeerReview(String courseID, int assignmentID) {
        return getRelPath() + "assignments" + reg + courseID + reg + assignmentID + reg + "peer-reviews";
    }

    public static String findFile(String courseID, int assignmentID, String fileName) {
        return getRelPath() + "assignments" + reg + courseID + reg + assignmentID + reg + "assignments" + reg + fileName;
    }

    public static String findPeerReviewFile(String courseID, int assignmentID, String fileName) {
        String filePath = getRelPath() + "assignments" + reg + courseID + reg + assignmentID + reg + "peer-reviews" + reg + fileName;
        if (!new File(filePath).exists())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(filePath + " does not exist.").build());
        return filePath;
    }

    public static void writeToAssignment(FileDAO fileDAO) throws IOException {
        String FileStructure = getRelPath() + "assignments" + reg + fileDAO.courseID + reg + fileDAO.assignmentID + reg + "assignments";
        fileDAO.writeFile(FileStructure + reg + fileDAO.fileName);
    }

    public static void writeToPeerReviews(FileDAO fileDAO) throws IOException {
        String FileStructure = getRelPath() + "assignments" + reg + fileDAO.courseID + reg + fileDAO.assignmentID + reg + "peer-reviews";
        fileDAO.writeFile(FileStructure + reg + fileDAO.fileName);
    }

    public static void removeFile(String courseID, String fileName, int assignmentID) {
        String fileLocation = findFile(courseID, assignmentID, fileName);
        File file = new File(fileLocation);
        if (!file.delete())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment does not exist or could not be deleted.").build());
    }

    public static void removePeerReviewFile(String courseID, String fileName, int assignmentID) {
        String fileLocation = findPeerReviewFile(courseID, assignmentID, fileName);
        File file = new File(fileLocation);
        if (!file.delete())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment does not exist or could not be deleted.").build());
    }

    public int createAssignment(AssignmentDAO assignmentDAO) {
        String FileStructure = getRelPath() + "assignments" + reg + assignmentDAO.courseID;

        File dir = new File(FileStructure);
        if (!dir.mkdirs() && !dir.exists())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to create directory at " + dir.getAbsolutePath()).build());

        String[] dirList = dir.list();
        if (dirList == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Directory must exist to make file structure.").build());

        int nextPos = 0;
        if (dirList.length != 0) {
            nextPos = Arrays.stream(dirList)
                    .map(Integer::parseInt)
                    .max(Integer::compare)
                    .orElse(-9999) + 1;
        }
        assignmentDAO.assignmentID = nextPos;

        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> assignmentDAOEntity = Entity.entity(jsonb.toJson(assignmentDAO), MediaType.APPLICATION_JSON_TYPE);
        Document assignmentDocument = Document.parse(assignmentDAOEntity.getEntity());

        MongoCursor<Document> query = assignmentsCollection.find(assignmentDocument).iterator();
        if (query.hasNext())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This assignment already exists.").build());
        assignmentsCollection.insertOne(assignmentDocument);

        FileStructure += reg + nextPos;
        if (!new File(FileStructure + reg + "team-submissions").mkdirs())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to create team-submission directory.").build());
        if (!new File(FileStructure + reg + "peer-reviews").mkdirs())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to create peer-review directory.").build());
        if (!new File(FileStructure + reg + "assignments").mkdirs())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to create assignments directory.").build());
        if (!new File(FileStructure + reg + "peer-review-submission").mkdirs())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to create peer-review-submission directory.").build());
        return nextPos;
    }

    public List<Document> getAllAssignments() {
        MongoCursor<Document> query = assignmentsCollection.find().iterator();
        List<Document> assignments = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            assignments.add(document);
        }
        return assignments;
    }

    public List<Document> getAssignmentsByCourse(String courseID) {
        MongoCursor<Document> query = assignmentsCollection.find(eq("course_id", courseID)).iterator();
        List<Document> assignments = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            assignments.add(document);
        }
        if (assignments.isEmpty())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This course does not exist").build());

        return assignments;
    }

    public Document getSpecifiedAssignment(String courseID, int AssignmentID) {
        MongoCursor<Document> results = assignmentsCollection.find(new Document()
                .append("course_id", courseID)
                .append("assignment_id", AssignmentID)).iterator();
        if (!results.hasNext())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No assignment by this name found.").build());
        return results.next();
    }

    public void updateAssignment(AssignmentDAO assignmentDAO, String courseID, int assignmentID) {
        assignmentDAO.assignmentID = assignmentID;
        Document assignmentDocument = assignmentsCollection.find(eq("assignment_id", assignmentID)).first();
        if (assignmentDocument == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This course does not exist.").build());
        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> courseDAOEntity = Entity.entity(jsonb.toJson(assignmentDAO), MediaType.APPLICATION_JSON_TYPE);
        Document course = Document.parse(courseDAOEntity.getEntity());
        assignmentsCollection.replaceOne(eq("course_id", courseID), course);
    }

    public void removeAssignment(int AssignmentID, String courseID) throws IOException {
        MongoCursor<Document> results = assignmentsCollection.find(new Document()
                .append("assignment_id", AssignmentID)
                .append("course_id", courseID)).iterator();
        if (!results.hasNext())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No assignment by this name found.").build());

        while (results.hasNext()) {
            Document assignment = results.next();
            String Destination = getRelPath() + "assignments" + reg + courseID + reg + assignment.get("assignment_id");
            FileUtils.deleteDirectory(new File(Destination));
            assignmentsCollection.findOneAndDelete(assignment);
        }
    }

    public void removeCourse(String courseID) throws IOException {
        MongoCursor<Document> results = assignmentsCollection.find(eq("course_id", courseID)).iterator();
        if (!results.hasNext())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No assignment by this name found.").build());

        while (results.hasNext()) {
            Document assignmentDocument = results.next();
            assignmentsCollection.findOneAndDelete(assignmentDocument);
        }

        String Destination = getRelPath() + "assignments" + reg + courseID;
        FileUtils.deleteDirectory(new File(Destination));
    }
}