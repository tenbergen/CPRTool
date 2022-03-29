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

    static MongoDatabase assignmentDatabase;
    static MongoCollection<Document> assignmentsCollection;
    private final List<AssignmentDAO> assignments = new ArrayList<>();

    static String reg;
    static int nextPos = 0;

    public AssignmentInterface() {
        try {
            DatabaseManager manager = new DatabaseManager();
            assignmentDatabase = manager.getAssignmentDB();
            assignmentsCollection = assignmentDatabase.getCollection("assignments");
        } catch (WebApplicationException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to retrieve collections.").build());
        }
    }

    public void createAssignment(AssignmentDAO assignmentDAO) {
        Document assignment = new Document()
                .append("course_id", assignmentDAO.getCourseID())
                .append("assignment_id", nextPos)
                .append("assignment_name", assignmentDAO.getAssignmentName())
                .append("instructions", assignmentDAO.getInstructions())
                .append("due_date", assignmentDAO.getDueDate())
                .append("points", assignmentDAO.getPoints());
        assignmentsCollection.insertOne(assignment);
        getRelPath();
        String FileStructure =  "courses" + reg + assignmentDAO.getCourseID() + reg;

        File dir = new File(FileStructure);
        if (!dir.mkdirs()) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to create directory at " + dir.getAbsolutePath()).build());

        String[] dirList = dir.list();
        if (dirList == null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Directory must exist to make file structure.").build());

        if (dirList.length != 0) {
            nextPos = Arrays.stream(dirList)
                    .map(Integer::parseInt)
                    .max(Integer::compare)
                    .orElse(-9999) + 1;
        }

        FileStructure += nextPos;
        if (!new File(FileStructure + reg + "TeamSubmissions").mkdirs())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to create team submission directory.").build());
        if (!new File(FileStructure + reg + "PeerReviews").mkdirs())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to create peer review directory.").build());
    }

    public static void updateAssignment(AssignmentDAO assignmentDAO, String courseID, int assignmentID) {
        Document assignmentDocument = assignmentsCollection.find(eq("assignment_id", assignmentID)).first();
        if (assignmentDocument == null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This course does not exist.").build());
        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> courseDAOEntity = Entity.entity(jsonb.toJson(assignmentDAO), MediaType.APPLICATION_JSON_TYPE);
        Document course = Document.parse(courseDAOEntity.getEntity());
        assignmentDatabase.getCollection("assignments").replaceOne(eq("course_id", courseID), course);
    }

    public void writeToAssignment(FileDAO fileDAO) throws IOException {
        String FileStructure = getRelPath() +reg +"courses" + reg + fileDAO.getCourseID() + reg + fileDAO.getAssignmentID();
        fileDAO.writeFile(FileStructure + reg + fileDAO.getFilename());
    }

    public static String findAssignment(String courseID, int assID) {
        return getRelPath() + "courses" + reg + courseID + reg + assID;
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
        for (int i = slicedPath.length-1; !slicedPath[i].equals(targetDir);i--) {
            relativePathPrefix.append("../");
        }
        System.out.println(relativePathPrefix.toString());
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

    public void remove(int AssignmentID, String courseID) throws IOException {
        MongoCursor<Document> results = assignmentDatabase.getCollection("assignments").find(new Document()
                .append("course_id", courseID)
                .append("assignment_id", AssignmentID)).iterator();
        if (!results.hasNext()) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No assignment by this name found.").build());
        String relativePathPrefix = getRelPath();

        while (results.hasNext()) {
            Document ass = results.next();
            String Destination = relativePathPrefix + "courses" + reg + courseID + reg + ass.get("assignment_id");
            FileUtils.deleteDirectory(new File(Destination));
            assignmentDatabase.getCollection("assignments").findOneAndDelete(ass);
        }
    }
}