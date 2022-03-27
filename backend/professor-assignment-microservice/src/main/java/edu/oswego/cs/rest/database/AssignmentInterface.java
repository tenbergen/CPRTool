package edu.oswego.cs.rest.database;

import com.mongodb.client.*;
import edu.oswego.cs.rest.daos.AssignmentDAO;
import edu.oswego.cs.rest.daos.FileDAO;
import org.apache.commons.io.FileUtils;
import org.bson.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AssignmentInterface {

    static MongoDatabase assignmentDatabase;
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
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw new Exception("No connection to the ass DB");
        }
    }

    public void createAssignment(AssignmentDAO assignmentDAO) {
        this.assignmentDAO = assignmentDAO;
        makeFileStructure();
        makeNewDataBaseEntry();
    }

    private void makeNewDataBaseEntry() {
        if (!collectionExists())
            assignmentDatabase.createCollection(asgmtCollection);

        MongoCollection<Document> assignmentCollection = assignmentDatabase.getCollection(asgmtCollection);
        Document assignment = new Document()
                .append("course_id", assignmentDAO.getCourseID())
                .append("assignment_id", nextPos)
                .append("assignment_name", assignmentDAO.getAssignmentName())
                .append("instructions", assignmentDAO.getInstructions())
                .append("due_date", assignmentDAO.getDueDate())
                .append("points", assignmentDAO.getPoints());
        assignmentCollection.insertOne(assignment);
    }

    public static void updateAssignment(AssignmentDAO assignmentDAO, String courseID, int assignmentID) {
        MongoCollection<Document> assignmentCollection = assignmentDatabase.getCollection(asgmtCollection);
        Document assignment = new Document()
                .append("course_id", assignmentDAO.getCourseID())
                .append("assignment_id", nextPos)
                .append("assignment_name", assignmentDAO.getAssignmentName())
                .append("instructions", assignmentDAO.getInstructions())
                .append("due_date", assignmentDAO.getDueDate())
                .append("points", assignmentDAO.getPoints());

        for (Document document : assignmentCollection.find()) {
            boolean courseMatch = document.get("course_id").equals(courseID);
            boolean assignmentMatch = document.get("assignment_id").equals(assignmentID);
            if (courseMatch && assignmentMatch) {
                assignmentCollection.updateOne(document, assignment);
            }
        }
    }

    public void writeToAssignment(FileDAO fileDAO) throws IOException {
        this.fileDAO = fileDAO;
        String FileStructure = getRelPath() + "Courses" + reg + fileDAO.getCourseID() + reg + fileDAO.getAssignmentID();
        fileDAO.writeFile(FileStructure + reg + fileDAO.getFilename());
    }

    public static String findAssignment(String courseID, int assID) {
        return getRelPath() + "Courses" + reg + courseID + reg + assID;
    }

    private boolean collectionExists() {
        MongoIterable<String> list = assignmentDatabase.listCollectionNames();
        for (String s : list) {
            if (s.equals(asgmtCollection)) {
                return true;
            }
        }
        return false;
    }

    private void makeFileStructure() {
        String FileStructure = getRelPath() + "Courses" + reg + assignmentDAO.getCourseID() + reg;
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
            String Destination = relativePathPrefix + "Courses" + reg + courseID + reg + ass.get("assignment_id");
            FileUtils.deleteDirectory(new File(Destination));
            assignmentDatabase.getCollection(asgmtCollection).findOneAndDelete(ass);
        }
    }

}