package edu.oswego.cs.rest.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.rest.daos.AssignmentDAO;
import edu.oswego.cs.rest.daos.FileDAO;
import edu.oswego.cs.rest.util.CPRException;
import org.apache.commons.io.FileUtils;
import org.bson.Document;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class AssignmentInterface {
    private final MongoCollection<Document> assignmentsCollection;
    private final MongoCollection<Document> courseCollection;
    private final MongoCollection<Document> submissionCollection;
    private static String reg;

    // Set this to true if running on Windows.
    private static final boolean isWindows = false;

    public AssignmentInterface() {
        try {
            DatabaseManager manager = new DatabaseManager();
            MongoDatabase assignmentDatabase = manager.getAssignmentDB();
            assignmentsCollection = assignmentDatabase.getCollection("assignments");
            submissionCollection = assignmentDatabase.getCollection("submissions");
            MongoDatabase courseDatabase = manager.getCourseDB();
            courseCollection = courseDatabase.getCollection("courses");
        } catch (CPRException e) {
            throw new CPRException(Response.Status.BAD_REQUEST,"Failed to retrieve collections.");
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

    public static String findFile(String courseID, int assignmentID, String fileName) {
        return getRelPath() + "assignments" + reg + courseID + reg + assignmentID + reg + "assignments" + reg + fileName;
    }

    public static String findPeerReviewFile(String courseID, int assignmentID, String fileName) {
        String filePath = getRelPath() + "assignments" + reg + courseID + reg + assignmentID + reg + "peer-reviews" + reg + fileName;
        if (!new File(filePath).exists())
            throw new CPRException(Response.Status.BAD_REQUEST,filePath + "does not exist");
        return filePath;
    }

    public void writeToAssignment(FileDAO fileDAO) throws IOException {
        String FileStructure = getRelPath() + "assignments" + reg + fileDAO.courseID + reg + fileDAO.assignmentID + reg + "assignments";
        fileDAO.writeFile(FileStructure + reg + fileDAO.fileName);
        assignmentsCollection.updateOne(and(
                        eq("course_id", fileDAO.courseID),
                        eq("assignment_id", fileDAO.assignmentID)),
                set("assignment_instructions", fileDAO.fileName));
    }

    public void writeRubricToPeerReviews(FileDAO fileDAO) throws IOException {
        String FileStructure = getRelPath() + "assignments" + reg + fileDAO.courseID + reg + fileDAO.assignmentID + reg + "peer-reviews";
        fileDAO.writeFile(FileStructure + reg + fileDAO.fileName);
        assignmentsCollection.updateOne(and(
                        eq("course_id", fileDAO.courseID),
                        eq("assignment_id", fileDAO.assignmentID)),
                set("peer_review_rubric", fileDAO.fileName));
    }

    public void writeTemplateToPeerReviews(FileDAO fileDAO) throws IOException {
        String FileStructure = getRelPath() + "assignments" + reg + fileDAO.courseID + reg + fileDAO.assignmentID + reg + "peer-reviews";
        fileDAO.writeFile(FileStructure + reg + fileDAO.fileName);
        assignmentsCollection.updateOne(and(
                        eq("course_id", fileDAO.courseID),
                        eq("assignment_id", fileDAO.assignmentID)),
                set("peer_review_template", fileDAO.fileName));
    }

    public void removeFile(String courseID, String fileName, int assignmentID) {
        String fileLocation = findFile(courseID, assignmentID, fileName);
        File file = new File(fileLocation);
        if (!file.delete())
            throw new CPRException(Response.Status.BAD_REQUEST,"Assignment does not exist or could not be deleted.");
        assignmentsCollection.updateOne(and(eq("course_id", courseID),
                        eq("assignment_id", assignmentID)),
                set("assignment_instructions", ""));
    }

    public void removePeerReviewTemplate(String courseID, String fileName, int assignmentID) {
        String fileLocation = findPeerReviewFile(courseID, assignmentID, fileName);
        File file = new File(fileLocation);
        if (!file.delete())
            throw new CPRException(Response.Status.BAD_REQUEST,"Assignment does not exist or could not be deleted.");
        assignmentsCollection.updateOne(and(
                        eq("course_id", courseID),
                        eq("assignment_id", assignmentID)),
                set("peer_review_template", ""));
    }

    public void removePeerReviewRubric(String courseID, String fileName, int assignmentID) {
        String fileLocation = findPeerReviewFile(courseID, assignmentID, fileName);
        File file = new File(fileLocation);
        if (!file.delete())
            throw new CPRException(Response.Status.BAD_REQUEST,"Assignment does not exist or could not be deleted.");
        assignmentsCollection.updateOne(and(
                        eq("course_id", courseID),
                        eq("assignment_id", assignmentID)),
                set("peer_review_rubric", ""));
    }

    public Document createAssignment(AssignmentDAO assignmentDAO) throws IOException {
        Document courseDocument = courseCollection.find(eq("course_id", assignmentDAO.courseID)).first();
        if (courseDocument == null) throw new CPRException(Response.Status.BAD_REQUEST,"Course not found.");
        String FileStructure = getRelPath() + "assignments" + reg + assignmentDAO.courseID;

        File dir = new File(FileStructure);
        if (!dir.mkdirs() && !dir.exists()) throw new CPRException(Response.Status.BAD_REQUEST,"Failed to create directory at" + dir.getAbsolutePath());

        String[] dirList = dir.list();
        if (dirList == null) throw new CPRException(Response.Status.BAD_REQUEST,"Directory must exist to make file structure.");

        int nextPos = generateAssignmentID();
        assignmentDAO.assignmentID = nextPos;

        FileStructure += reg + nextPos;
        if (!new File(FileStructure + reg + "team-submissions").mkdirs()) throw new CPRException(Response.Status.BAD_REQUEST,"Failed to create team-submission directory.");

        if (!new File(FileStructure + reg + "peer-reviews").mkdirs()) {
            deleteFile(FileStructure + reg + "team-submissions");
            throw new CPRException(Response.Status.BAD_REQUEST,"Failed to create peer-review directory.");
        }

        if (!new File(FileStructure + reg + "assignments").mkdirs()) {
            deleteFile(FileStructure + reg + "team-submissions");
            deleteFile(FileStructure + reg + "peer-reviews");
            throw new CPRException(Response.Status.BAD_REQUEST,"Failed to create assignments directory");
        }

        if (!new File(FileStructure + reg + "peer-review-submission").mkdirs()) {
            deleteFile(FileStructure + reg + "team-submissions");
            deleteFile(FileStructure + reg + "peer-reviews");
            deleteFile(FileStructure + reg + "assignments");
            throw new CPRException(Response.Status.BAD_REQUEST,"Failed to create peer-review-submission directory");
        }

        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> assignmentDAOEntity = Entity.entity(jsonb.toJson(assignmentDAO), MediaType.APPLICATION_JSON_TYPE);
        Document assignmentDocument = Document.parse(assignmentDAOEntity.getEntity());
        assignmentDocument
                .append("submission_is_past_due", false)
                .append("peer_review_is_past_due", false)
                .append("grade_finalized", false);

        MongoCursor<Document> query = assignmentsCollection.find(assignmentDocument).iterator();
        if (query.hasNext()) {
            query.close();
            deleteFile(FileStructure + reg + "team-submissions");
            deleteFile(FileStructure + reg + "peer-reviews");
            deleteFile(FileStructure + reg + "assignments");
            deleteFile(FileStructure + reg + "peer-review-submission");

            throw new CPRException(Response.Status.BAD_REQUEST,"This assignment already exists.");
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
        if (!query.hasNext()) throw new CPRException(Response.Status.BAD_REQUEST,"This course does not exist.");

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
        if (assignment == null) throw new CPRException(Response.Status.BAD_REQUEST,"No assignment by this name found");
        return assignment;
    }

    public void updateAssignment(AssignmentDAO assignmentDAO, String courseID, int assignmentID) {
        Document assignmentDocument = assignmentsCollection.find(and(eq("assignment_id", assignmentID),eq("course_id", courseID))).first();
        if (assignmentDocument == null) throw new CPRException(Response.Status.BAD_REQUEST,"This assignment does not exist.");
        assignmentDocument.replace("assignment_name", assignmentDAO.assignmentName);
        assignmentDocument.replace("due_date", assignmentDAO.dueDate);
        assignmentDocument.replace("instructions", assignmentDAO.instructions);
        assignmentDocument.replace("points", assignmentDAO.points);
        assignmentDocument.replace("peer_review_instructions", assignmentDAO.peerReviewInstructions);
        assignmentDocument.replace("peer_review_due_date", assignmentDAO.peerReviewDueDate);
        assignmentDocument.replace("peer_review_points", assignmentDAO.peerReviewPoints);

        assignmentsCollection.replaceOne(and(eq("assignment_id", assignmentID),eq("course_id", courseID)), assignmentDocument);
    }

    public void removeAssignment(int AssignmentID, String courseID) throws IOException {
        MongoCursor<Document> results = assignmentsCollection.find(and(
                eq("assignment_id", AssignmentID),
                eq("course_id", courseID))).iterator();
        if (!results.hasNext()) throw new CPRException(Response.Status.BAD_REQUEST,"No assignment by this name found.");

        while (results.hasNext()) {
            Document assignment = results.next();
            deleteFile(getRelPath() + "assignments" + reg + courseID + reg + assignment.get("assignment_id"));
            assignmentsCollection.findOneAndDelete(assignment);
        }
        removeSubmissions(AssignmentID, courseID);
    }

    public void removeSubmissions(int AssignmentID, String courseID) throws IOException {
        for (Document submissionDoc : submissionCollection.find(and(eq("assignment_id", AssignmentID), eq("course_id", courseID))))
             submissionCollection.findOneAndDelete(submissionDoc);
    }

    public void removeCourse(String courseID) throws IOException {
        MongoCursor<Document> results = assignmentsCollection.find(eq("course_id", courseID)).iterator();
        if (!results.hasNext()) throw new CPRException(Response.Status.BAD_REQUEST,"No assignment by this name found.");

        while (results.hasNext()) {
            Document assignmentDocument = results.next();
            assignmentsCollection.findOneAndDelete(assignmentDocument);
        }

        deleteFile(getRelPath() + "assignments" + reg + courseID);
    }

    private static void deleteFile(String destination) throws IOException {
        FileUtils.deleteDirectory(new File(destination));
    }

    public int generateAssignmentID() {
        List<Document> assignmentsDocuments = getAllAssignments();

        Set<Integer> assignmentIDs = new HashSet<>();
        for (Document assignmentDocument : assignmentsDocuments)
            assignmentIDs.add(assignmentDocument.getInteger("assignment_id"));
        int max = 0;
        for (Integer assignmentID : assignmentIDs) {
            if (max < assignmentID)
                max = assignmentID;
        }
        return ++max;
    }
}