package edu.oswego.cs.rest.database;

import com.mongodb.client.*;
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
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;

public class AssignmentInterface {

    static MongoDatabase assignmentDatabase;
    static MongoCollection<Document> assignmentsCollection;
    private final List<AssignmentDAO> assignments = new ArrayList<>();

    FileDAO fileDAO;
    AssignmentDAO assignmentDAO;
    static String asgmtCollection = "assignments";
    static String reg;
    static int nextPos = 0;

    public AssignmentInterface() throws Exception {
        try {
            DatabaseManager manager = new DatabaseManager();
            assignmentDatabase = manager.getAssignmentDB();
            assignmentsCollection = assignmentDatabase.getCollection("assignments");
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw new Exception("No connection to the ass DB");
        }
    }

    public void createAssignment(AssignmentDAO assignmentDAO) {
        this.assignmentDAO = assignmentDAO;
        if (makeNewDataBaseEntry())
            makeFileStructure();
    }

    private boolean makeNewDataBaseEntry() {
        if (!collectionExists())
            assignmentDatabase.createCollection(asgmtCollection);
        try {
            MongoCollection<Document> assignmentCollection = assignmentDatabase.getCollection(asgmtCollection);
            Document assignment = new Document()
                    .append("course_id", assignmentDAO.getCourseID())
                    .append("assignment_id", nextPos)
                    .append("assignment_name", assignmentDAO.getAssignmentName())
                    .append("instructions", assignmentDAO.getInstructions())
                    .append("due_date", assignmentDAO.getDueDate())
                    .append("points", assignmentDAO.getPoints());
            assignmentCollection.insertOne(assignment);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public static void updateAssignment(AssignmentDAO assignmentDAO, String courseID, int assignmentID) {
        Document assignmentDocument = assignmentsCollection.find(eq("assignment_id", assignmentID)).first();
        if (assignmentDocument == null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This course does not exist.").build());
        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> courseDAOEntity = Entity.entity(jsonb.toJson(assignmentDAO), MediaType.APPLICATION_JSON_TYPE);
        Document course = Document.parse(courseDAOEntity.getEntity());
        assignmentDatabase.getCollection(asgmtCollection).replaceOne(eq("course_id", courseID), course);
    }

    public void writeToAssignment(FileDAO fileDAO) throws IOException {
        this.fileDAO = fileDAO;
        String FileStructure = getRelPath() + "courses" + reg + fileDAO.getCourseID() + reg + fileDAO.getAssignmentID();
        fileDAO.writeFile(FileStructure + reg + fileDAO.getFilename());
    }

    public static String findAssignment(String courseID, int assID) {
        return getRelPath() + "courses" + reg + courseID + reg + assID;
    }

    private static boolean collectionExists() {
        MongoIterable<String> list = assignmentDatabase.listCollectionNames();
        for (String s : list) {
            if (s.equals(asgmtCollection)) {
                return true;
            }
        }
        return false;
    }

    private void makeFileStructure() {
        String FileStructure = getRelPath() + "courses" + reg + assignmentDAO.getCourseID() + reg;
        String notNullMsg = "directory must exist to make file structure";
        File dir = new File(FileStructure);

        if (dir.mkdirs())
            System.out.println("DIRECTORY SUCCESSFULLY CREATED AT: \n" + dir.getAbsolutePath());

        if (Objects.requireNonNull(dir.list(), notNullMsg).length != 0) {
            nextPos = Arrays.stream(Objects.requireNonNull(dir.list(), notNullMsg))
                    .map(Integer::parseInt)
                    .max(Integer::compare)
                    .orElse(-9999) + 1;

        }
        FileStructure += nextPos;
        if (new File(FileStructure + reg + "TeamSubmissions").mkdirs())
            System.out.println(" TEAM_SUBMISSIONS DIRECTORY SUCCESSFULLY CREATED");

        if (new File(FileStructure + reg + "PeerReviews").mkdirs())
            System.out.println(" PEER_REVIEWS DIRECTORY SUCCESSFULLY CREATED");

    }

    /**
     * Retrieves the relative location of the root Directory
     *
     * @return String directory location the hw files should be saved to
     */
    public static String getRelPath() {
        String path = (System.getProperty("user.dir").contains("\\")) ? System.getProperty("user.dir").replace("\\", "/") : System.getProperty("user.dir");
        String[] slicedPath = path.split("/");
        String targetDir = "professor-assignment-microservice";
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
        MongoCollection<Document> assignmentCollection = assignmentDatabase.getCollection(asgmtCollection);
        for (Document document : assignmentCollection.find()) {
            if (document.get("course_id").equals(courseID)) {
                AssignmentDAO assignmentDAO = new AssignmentDAO(
                        (String) document.get("assignment_name"),
                        (String) document.get("instructions"),
                        (String) document.get("due_date"),
                        (String) document.get("course_id"),
                        (int) document.get("points")

                );
                assignments.add(assignmentDAO);
            }
        }
        return assignments;
    }

    public List<AssignmentDAO> getAllAssignments() {

        MongoCollection<Document> assignmentCollection = assignmentDatabase.getCollection(asgmtCollection);
        for (Document document : assignmentCollection.find()) {
            AssignmentDAO assignmentDAO = new AssignmentDAO(
                    (String) document.get("assignment_name"),
                    (String) document.get("instructions"),
                    (String) document.get("due_date"),
                    (String) document.get("course_id"),
                    (int) document.get("points")
            );
            assignments.add(assignmentDAO);
        }
        return assignments;
    }

    public void remove(int AssignmentID, String courseID) throws IOException {
        MongoCursor<Document> results = assignmentDatabase.getCollection(asgmtCollection).find(new Document()
                .append("course_id", courseID)
                .append("assignment_id", AssignmentID)).iterator();
        if (!results.hasNext()) throw new IOException("No Assignment by this name found");
        String relativePathPrefix = getRelPath();

        while (results.hasNext()) {
            Document ass = results.next();
            String Destination = relativePathPrefix + "courses" + reg + courseID + reg + ass.get("assignment_id");
            FileUtils.deleteDirectory(new File(Destination));
            assignmentDatabase.getCollection(asgmtCollection).findOneAndDelete(ass);
        }
    }

}