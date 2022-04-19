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
import java.util.*;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class AssignmentInterface {
    private final MongoCollection<Document> assignmentsCollection;
    private final MongoCollection<Document> courseCollection;
    private static String reg;

    // Set this to true if running on Windows.
    private static final boolean isWindows = false;

    public AssignmentInterface() {
        try {
            DatabaseManager manager = new DatabaseManager();
            MongoDatabase assignmentDatabase = manager.getAssignmentDB();
            assignmentsCollection = assignmentDatabase.getCollection("assignments");
            MongoDatabase courseDatabase = manager.getCourseDB();
            courseCollection = courseDatabase.getCollection("courses");
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

    public void writeToAssignment(FileDAO fileDAO) throws IOException {
        String FileStructure = getRelPath() + "assignments" + reg + fileDAO.courseID + reg + fileDAO.assignmentID + reg + "assignments";
        fileDAO.writeFile(FileStructure + reg + fileDAO.fileName);
        assignmentsCollection.updateOne(and(eq("course_id", fileDAO.courseID),
                                            eq("assignment_id", fileDAO.assignmentID)),
                                            set("assignment_instructions", fileDAO.fileName));
    }

    public void writeRubricToPeerReviews(FileDAO fileDAO) throws IOException {
        String FileStructure = getRelPath() + "assignments" + reg + fileDAO.courseID + reg + fileDAO.assignmentID + reg + "peer-reviews";
        fileDAO.writeFile(FileStructure + reg + fileDAO.fileName);
        assignmentsCollection.updateOne(and(eq("course_id", fileDAO.courseID),
                                            eq("assignment_id", fileDAO.assignmentID)),
                                            set("peer_review_rubric", fileDAO.fileName));
    }

    public void writeTemplateToPeerReviews(FileDAO fileDAO) throws IOException {
        String FileStructure = getRelPath() + "assignments" + reg + fileDAO.courseID + reg + fileDAO.assignmentID + reg + "peer-reviews";
        fileDAO.writeFile(FileStructure + reg + fileDAO.fileName);
        assignmentsCollection.updateOne(and(eq("course_id", fileDAO.courseID),
                                            eq("assignment_id", fileDAO.assignmentID)),
                                            set("peer_review_template", fileDAO.fileName));
    }

    public void removeFile(String courseID, String fileName, int assignmentID) {
        String fileLocation = findFile(courseID, assignmentID, fileName);
        File file = new File(fileLocation);
        if (!file.delete())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment does not exist or could not be deleted.").build());
        assignmentsCollection.updateOne(and(eq("course_id", courseID),
                                            eq("assignment_id", assignmentID)),
                                            set("assignment_instructions", ""));
    }

    public void removePeerReviewTemplate(String courseID, String fileName, int assignmentID) {
        String fileLocation = findPeerReviewFile(courseID, assignmentID, fileName);
        File file = new File(fileLocation);
        if (!file.delete())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment does not exist or could not be deleted.").build());
        assignmentsCollection.updateOne(and(eq("course_id", courseID),
                                            eq("assignment_id", assignmentID)),
                                            set("peer_review_template", ""));
    }

    public void removePeerReviewRubric(String courseID, String fileName, int assignmentID) {
        String fileLocation = findPeerReviewFile(courseID, assignmentID, fileName);
        File file = new File(fileLocation);
        if (!file.delete())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment does not exist or could not be deleted.").build());
        assignmentsCollection.updateOne(and(eq("course_id", courseID),
                                            eq("assignment_id", assignmentID)),
                                            set("peer_review_rubric", ""));
    }

    public Document createAssignment(AssignmentDAO assignmentDAO) throws IOException {
        Document courseDocument = courseCollection.find(eq("course_id", assignmentDAO.courseID)).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Course not found.").build());

        String FileStructure = getRelPath() + "assignments" + reg + assignmentDAO.courseID;

        File dir = new File(FileStructure);
        if (!dir.mkdirs() && !dir.exists())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to create directory at " + dir.getAbsolutePath()).build());

        String[] dirList = dir.list();
        if (dirList == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Directory must exist to make file structure.").build());

        int nextPos = generateAssignmentID();
        assignmentDAO.assignmentID = nextPos;

        FileStructure += reg + nextPos;
        if (!new File(FileStructure + reg + "team-submissions").mkdirs())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to create team-submission directory.").build());

        if (!new File(FileStructure + reg + "peer-reviews").mkdirs()) {
            deleteFile(FileStructure + reg + "team-submissions");
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to create peer-review directory.").build());
        }

        if (!new File(FileStructure + reg + "assignments").mkdirs()) {
            deleteFile(FileStructure + reg + "team-submissions");
            deleteFile(FileStructure + reg + "peer-reviews");
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to create assignments directory.").build());
        }

        if (!new File(FileStructure + reg + "peer-review-submission").mkdirs()) {
            deleteFile(FileStructure + reg + "team-submissions");
            deleteFile(FileStructure + reg + "peer-reviews");
            deleteFile(FileStructure + reg + "assignments");
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to create peer-review-submission directory.").build());
        }

        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> assignmentDAOEntity = Entity.entity(jsonb.toJson(assignmentDAO), MediaType.APPLICATION_JSON_TYPE);
        Document assignmentDocument = Document.parse(assignmentDAOEntity.getEntity());

        MongoCursor<Document> query = assignmentsCollection.find(assignmentDocument).iterator();
        if (query.hasNext()) {
            query.close();
            deleteFile(FileStructure + reg + "team-submissions");
            deleteFile(FileStructure + reg + "peer-reviews");
            deleteFile(FileStructure + reg + "assignments");
            deleteFile(FileStructure + reg + "peer-review-submission");

            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This assignment already exists.").build());
        }

        assignmentsCollection.insertOne(assignmentDocument);


        return assignmentDocument;
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
        if (!query.hasNext()) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This course does not exist").build());

        List<Document> assignments = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            assignments.add(document);
        }
        return assignments;
    }

    public Document getSpecifiedAssignment(String courseID, int AssignmentID) {
        Document assignment = assignmentsCollection.find(and(
                eq("course_id", courseID),
                eq("assignment_id", AssignmentID))).first();
        if (assignment == null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No assignment by this name found.").build());
        return assignment;
    }

    public void updateAssignment(AssignmentDAO assignmentDAO, String courseID, int assignmentID) {
        assignmentDAO.assignmentID = assignmentID;
        Document assignmentDocument = assignmentsCollection.find(eq("assignment_id", assignmentID)).first();
        if (assignmentDocument == null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This course does not exist.").build());
        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> courseDAOEntity = Entity.entity(jsonb.toJson(assignmentDAO), MediaType.APPLICATION_JSON_TYPE);
        Document course = Document.parse(courseDAOEntity.getEntity());
        assignmentsCollection.replaceOne(eq("course_id", courseID), course);
    }

    public void removeAssignment(int AssignmentID, String courseID) throws IOException {
        MongoCursor<Document> results = assignmentsCollection.find(and(
                eq("assignment_id", AssignmentID),
                eq("course_id", courseID))).iterator();
        if (!results.hasNext()) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No assignment by this name found.").build());

        while (results.hasNext()) {
            Document assignment = results.next();
            deleteFile(getRelPath() + "assignments" + reg + courseID + reg + assignment.get("assignment_id"));
            assignmentsCollection.findOneAndDelete(assignment);
        }
    }

    public void removeCourse(String courseID) throws IOException {
        MongoCursor<Document> results = assignmentsCollection.find(eq("course_id", courseID)).iterator();
        if (!results.hasNext()) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No assignment by this name found.").build());

        while (results.hasNext()) {
            Document assignmentDocument = results.next();
            assignmentsCollection.findOneAndDelete(assignmentDocument);
        }

        deleteFile(getRelPath() + "assignments" + reg + courseID);
    }

    private static void deleteFile(String destination) throws IOException {
        FileUtils.deleteDirectory(new File(destination));
    }

    public int generateAssignmentID(){
        List<Document> assignmentsDocuments = getAllAssignments();

        Set<String> assignmentIDs = new HashSet<>();
        for (Document assignmentDocument : assignmentsDocuments)
            assignmentIDs.add(assignmentDocument.getString("assignment_id"));
        for (int i = 0; i < assignmentIDs.size(); i++)
            if (!assignmentIDs.contains(String.valueOf(i)))
                return i;
        return assignmentIDs.size();
    }

}
