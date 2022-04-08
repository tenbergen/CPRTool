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
    static MongoCollection<Document> assignmentsCollection;
    static String reg;
    int nextPos = 0;

    // set this to true if testing on windows. QA, ask Team DB a bit more about this one...
    static boolean isWindows = false;

    public AssignmentInterface() {
        try {
            DatabaseManager manager = new DatabaseManager();
            MongoDatabase assignmentDatabase = manager.getAssignmentDB();
            assignmentsCollection = assignmentDatabase.getCollection("assignments");
        } catch (WebApplicationException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to retrieve collections.").build());
        }
    }

    public void createAssignment(AssignmentDAO assignmentDAO) {
        String FileStructure = getRelPath() + "assignments" + reg + assignmentDAO.getCourseID();

        File dir = new File(FileStructure);
        if (!dir.mkdirs() && !dir.exists())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to create directory at " + dir.getAbsolutePath()).build());

        String[] dirList = dir.list();
        if (dirList == null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Directory must exist to make file structure.").build());

        if (dirList.length != 0) {
            nextPos = Arrays.stream(dirList)
                    .map(Integer::parseInt)
                    .max(Integer::compare)
                    .orElse(-9999) + 1;
        }

        Document assignment = new Document()
                .append("course_id", assignmentDAO.getCourseID())
                .append("assignment_id", nextPos)
                .append("assignment_name", assignmentDAO.getAssignmentName())
                .append("instructions", assignmentDAO.getInstructions())
                .append("peer_review_instructions", assignmentDAO.getPeerReviewInstructions())
                .append("due_date", assignmentDAO.getDueDate())
                .append("points", assignmentDAO.getPoints());
        MongoCursor<Document> query = assignmentsCollection.find(assignment).iterator();
        if (query.hasNext()) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This assignment already exists.").build());
        assignmentsCollection.insertOne(assignment);

        FileStructure += reg + nextPos;
        if (!new File(FileStructure + reg + "TeamSubmissions").mkdirs())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to create team submission directory.").build());
        if (!new File(FileStructure + reg + "PeerReviews").mkdirs())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to create peer review directory.").build());
        if (!new File(FileStructure + reg + "assignments").mkdirs())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to create assignments directory.").build());
    }

    public static void updateAssignment(AssignmentDAO assignmentDAO, String courseID, int assignmentID) {
        assignmentDAO.setAssignment_id(assignmentID);
        Document assignmentDocument = assignmentsCollection.find(eq("assignment_id", assignmentID)).first();
        if (assignmentDocument == null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This course does not exist.").build());
        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> courseDAOEntity = Entity.entity(jsonb.toJson(assignmentDAO), MediaType.APPLICATION_JSON_TYPE);
        Document course = Document.parse(courseDAOEntity.getEntity());
        assignmentsCollection.replaceOne(eq("course_id", courseID), course);
    }

    public void writeToAssignment(FileDAO fileDAO) throws IOException {
        String FileStructure = getRelPath() + "assignments" + reg + fileDAO.getCourseID() + reg + fileDAO.getAssignmentID() + reg + "assignments";
        System.out.println(FileStructure);
        fileDAO.writeFile(FileStructure + reg + fileDAO.getFilename());
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
        System.out.println(Arrays.toString(slicedPath));
        System.out.println(slicedPath[0]);
        for (int i = slicedPath.length - 1; !slicedPath[i].equals(targetDir); i--) {
            relativePathPrefix.append("../");
        }
        System.out.println(relativePathPrefix);
        reg = "\\";
        System.out.println(System.getProperty("user.dir"));
        if (!isWindows) {
            System.out.println("Linux");
            reg = "/";
            relativePathPrefix = new StringBuilder(relativePathPrefix.toString().replace("\\", "/"));
        }
        System.out.println(relativePathPrefix);
        return relativePathPrefix.toString();
    }

    public List<Document> getAssignmentsByCourse(String courseID) {
        MongoCursor<Document> query = assignmentsCollection.find(eq("course_id", courseID)).iterator();
        List<Document> assignments = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            assignments.add(document);
        }
        if (assignments.isEmpty()) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This course does not exist").build());

        return assignments;
    }

    public static Document getSpecifiedAssignment(String courseID, int AssignmentID){
        MongoCursor<Document> results = assignmentsCollection.find(new Document()
                .append("course_id", courseID)
                .append("assignment_id", AssignmentID)).iterator();
        if (!results.hasNext()) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No assignment by this name found.").build());
        Document assignment = results.next();
        return assignment;
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

    public static void removeFile(FileDAO fileDAO){
        String fileLocation = findFile(fileDAO.getCourseID(), fileDAO.getAssignmentID(), fileDAO.getFilename());
        File file = new File(fileLocation);
        System.out.println(file.getAbsolutePath());
        if (!file.delete()) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment does not exist or could not be deleted.").build());
    }

    public void removeAssignment(int AssignmentID, String courseID) throws IOException {
        MongoCursor<Document> results = assignmentsCollection.find(new Document()
                .append("course_id", courseID)
                .append("assignment_id", AssignmentID)).iterator();
        if (!results.hasNext()) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No assignment by this name found.").build());

        while (results.hasNext()) {
            Document ass = results.next();
            String Destination = getRelPath() + "assignments" + reg + courseID + reg + ass.get("assignment_id");
            FileUtils.deleteDirectory(new File(Destination));
            assignmentsCollection.findOneAndDelete(ass);
        }
    }

    public void removeCourse(String courseID) throws IOException {
        MongoCursor<Document> results = assignmentsCollection.find(new Document()
                .append("course_id", courseID)).iterator();
        if (!results.hasNext()) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No assignment by this name found.").build());

        while (results.hasNext()) {
            Document ass = results.next();
            assignmentsCollection.findOneAndDelete(ass);
        }

        String Destination = getRelPath() + "assignments" + reg + courseID;
        FileUtils.deleteDirectory(new File(Destination));
    }

    public static String findFile(String courseID, int assignmentID, String fileName) {
        return getRelPath() + "assignments" + reg + courseID + reg + assignmentID + reg + "assignments" + reg + fileName;
    }

    public static String findAssignment(String courseID, int assID) {
        return getRelPath() + "assignments" + reg + courseID + reg + assID + reg + "assignments";
    }
}