package edu.oswego.cs.rest.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import edu.oswego.cs.rest.daos.AssignmentDAO;
import edu.oswego.cs.rest.daos.FileDAO;
import org.apache.commons.io.FileUtils;
import org.bson.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AssignmentInterface {
    MongoDatabase assignmentDatabase;
    private final List<AssignmentDAO> assignments = new ArrayList<>();

    String assCol = "assignments";
    String reg;
    String CID;
    FileDAO fileDAO;
    AssignmentDAO assignmentDAO;
    int nextPos = 0;

    public AssignmentInterface() throws Exception {
        try{
            DatabaseManager manager = new DatabaseManager();
            assignmentDatabase = manager.getAssDB();
        }catch(Exception e){
            e.printStackTrace(System.out);
            throw new Exception("No connection to the ass DB");
        }
    }

    public void createAssignment(AssignmentDAO assignmentDAO){
        this.assignmentDAO = assignmentDAO;
        this.CID = assignmentDAO.getCourseID();
        makeFileStructure();
        makeNewDataBaseEntry();
    }

    private void makeNewDataBaseEntry(){
        if (!collectionExists())
            assignmentDatabase.createCollection(assCol);

        MongoCollection assignmentCollection = assignmentDatabase.getCollection(assCol);
        Document assignment = new Document()
                .append("course_id",CID)
                .append("assignment_id",nextPos)
                .append("assignment_name",assignmentDAO.getAssignmentName())
                .append("instructions", assignmentDAO.getAssignmentInstructions())
                .append("due_date", assignmentDAO.getAssignmentDueDate())
                .append("points", assignmentDAO.getAssignmentDueDate());
        assignmentCollection.insertOne(assignment);
    }

    public void writeToAssignment(FileDAO fileDAO) throws IOException {
        this.CID = fileDAO.getCourseID();
        this.fileDAO = fileDAO;
        String relativePathPrefix = getRelPath();
        String FileStructure = relativePathPrefix + "Courses" + reg + CID + reg + fileDAO.getAssignmentID();
        fileDAO.writeFile(FileStructure + reg + fileDAO.getFilename());
    }

    public String findAssignment(String courseID, int assID){
        String relativePath = getRelPath();
        return relativePath + reg + courseID + reg + assID;

    }

    public Document addToDataBaseEntry(){
        MongoIterable<String> list = assignmentDatabase.listCollectionNames();

        MongoCollection assignmentCollection = assignmentDatabase.getCollection(assCol);
        Document assignment = new Document()
                .append("course_id",CID)
                .append("assignment_id",nextPos)
                .append("assignment_name",fileDAO.getFilename());
        assignmentCollection.insertOne(assignment);
        return assignment;
    }

    public boolean collectionExists(){
        MongoIterable<String> list = assignmentDatabase.listCollectionNames();
        for(String s : list){
            if(s.equals(assCol)){
                return true;
            }
        }
        return false;
    }

    public String makeFileStructure(){
        String FileStructure = getRelPath()+"Courses"+reg+CID+reg;
        File dir = new File(FileStructure);
        dir.mkdirs();

        if(dir.list().length != 0){
            nextPos = Arrays.asList(dir.list()).stream().map(z->Integer.parseInt(z)).max(Integer::compare).get().intValue()+1;
        }
        FileStructure += nextPos;
        new File(FileStructure + reg + "TeamSubmissions").mkdirs();
        new File(FileStructure + reg + "PeerReviews").mkdirs();
        return FileStructure;
    }

    public String getRelPath(){
        String path = (System.getProperty("user.dir").contains("\\")) ? System.getProperty("user.dir").replace("\\", "/") : System.getProperty("user.dir");
        String[] slicedPath = path.split("/");
        String targetDir = "professor-assignment-microservice";
        int i;
        String relativePathPrefix = "";
        System.out.println(Arrays.toString(slicedPath));
        for (i = slicedPath.length - 1; ! slicedPath[i].equals(targetDir); i--) {
            relativePathPrefix = relativePathPrefix + "../";
        }
        reg = "/";
        if (System.getProperty("user.dir").contains("\\")){
            reg = "\\";
            relativePathPrefix.replace("/", "\\");
        }
        return relativePathPrefix;
    }

    public List<AssignmentDAO> getAssignmentsByCourse(String courseID){
        MongoCollection<Document> assignmentCollection = assignmentDatabase.getCollection("assignments");
        for (Document document : assignmentCollection.find()) {
            if (document.get("course_id").equals(courseID)) {
                AssignmentDAO assignmentDAO = new AssignmentDAO(
                        (String) document.get("course_id"),
                        (String) document.get("assignment_name"),
                        (String) document.get("assignment_id"),
                        (String) document.get("assignment_instructions"),
                        (String) document.get("peer_review_instructions"),
                        (String) document.get("assignment_due_date"),
                        (String) document.get("peer_review_due_date"),
                        (int) document.get("assignment_points"),
                        (int) document.get("peer_review_points")
                );
                assignments.add(assignmentDAO);
            }
        }
        return assignments;
    }

    public List<AssignmentDAO> getAllAssignments() {

        MongoCollection<Document> assignmentCollection = assignmentDatabase.getCollection("assignments");

        for (Document document : assignmentCollection.find()) {
            AssignmentDAO assignmentDAO = new AssignmentDAO(
                    (String) document.get("course_id"),
                    (String) document.get("assignment_name"),
                    (String) document.get("assignment_id"),
                    (String) document.get("assignment_instructions"),
                    (String) document.get("peer_review_instructions"),
                    (String) document.get("assignment_due_date"),
                    (String) document.get("peer_review_due_date"),
                    (int) document.get("assignment_points"),
                    (int) document.get("peer_review_points")
            );
            assignments.add(assignmentDAO);
        }
        return assignments;
    }

    public void remove(int AssignmentID,String courseID) throws IOException {
        MongoCursor<Document> results = assignmentDatabase.getCollection(assCol).find(new Document()
                .append("course_id",courseID)
                .append("assignment_id",AssignmentID)).iterator();
        if(!results.hasNext()) throw new IOException("No Assignment by this name found");
        String relativePathPrefix = getRelPath();

        while(results.hasNext()){
            Document ass = results.next();
            String Destination = relativePathPrefix+ "Courses"+reg+courseID+reg+ass.get("assignment_id");
            FileUtils.deleteDirectory(new File(Destination));
            assignmentDatabase.getCollection(assCol).findOneAndDelete(ass);
        }

    }

}