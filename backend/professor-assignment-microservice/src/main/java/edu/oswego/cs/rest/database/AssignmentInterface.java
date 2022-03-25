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
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class AssignmentInterface {
    MongoDatabase assDatabase;

    String assCol = "assignments";
    String reg;
    String CID;
    FileDAO fileDAO;
    AssignmentDAO assignmentDAO;
    int nextPos = 0;

    public AssignmentInterface() throws Exception {
        try{
            DatabaseManager manager = new DatabaseManager();
            assDatabase = manager.getAssDB();
        }catch(Exception e){
            e.printStackTrace(System.out);
            throw new Exception("No connection to the ass DB");
        }
    }

    public void createAssignment(AssignmentDAO assignmentDAO){
        this.assignmentDAO = assignmentDAO;
        this.CID = assignmentDAO.getCourseID();
        makeNewDataBaseEntry();
    }

    private Document makeNewDataBaseEntry(){
        MongoIterable<String> list = assDatabase.listCollectionNames();

        if (!collectionExists())
            assDatabase.createCollection(assCol);

        MongoCollection assignmentCollection = assDatabase.getCollection(assCol);
        Document assignment = new Document()
                .append("course_id",CID)
                .append("assignment_id",nextPos)
                .append("assignment_name",assignmentDAO.getAssignmentName())
                .append("instructions", assignmentDAO.getInstructions())
                .append("due_date", assignmentDAO.getDueDate());
        assignmentCollection.insertOne(assignment);
        return assignment;
    }

    public void addToAssignment(FileDAO fileDAO) throws IOException {
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
        MongoIterable<String> list = assDatabase.listCollectionNames();

        MongoCollection assignmentCollection = assDatabase.getCollection(assCol);
        Document assignment = new Document()
                .append("course_id",CID)
                .append("assignment_id",nextPos)
                .append("assignment_name",fileDAO.getFilename());
        assignmentCollection.insertOne(assignment);
        return assignment;
    }

    public boolean collectionExists(){
        MongoIterable<String> list = assDatabase.listCollectionNames();
        boolean hasCollection = false;
        for(String s : list){
            if(s.equals(assCol)){
                return true;
            }
        }
        return false;
    }

    public String makeFileStructure(String relativePathPrefix){
        String FileStructure = relativePathPrefix+"Courses"+reg+CID+reg;
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

    public InputStream[] getAllAssignments(String CID){
        File assignmentFolder = new File(getRelPath() + "Courses" + reg + CID);
        if (!assignmentFolder.exists())
            return null;

        File[] assignmentFiles = assignmentFolder.listFiles();
        if (assignmentFiles == null)
            return null;

        InputStream[] assignments = new InputStream[assignmentFiles.length];
        AtomicInteger index = new AtomicInteger(0);
        Arrays.asList(assignmentFiles).forEach(file -> {
            try {
                assignments[index.getAndIncrement()] = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        return assignments;
    }

    public void remove(int AssID,String courseID) throws IOException {
        MongoCursor<Document> results = assDatabase.getCollection(assCol).find(new Document()
                .append("course_id",courseID)
                .append("assgnment_id",AssID)).iterator();
        if(!results.hasNext()) throw new IOException("No Assignment by this name found");
        String relativePathPrefix = getRelPath();

        while(results.hasNext()){
            Document ass = results.next();
            String Destination = relativePathPrefix+ "Courses"+reg+courseID+reg+ass.get("assignment_id");
            FileUtils.deleteDirectory(new File(Destination));
            assDatabase.getCollection(assCol).findOneAndDelete(ass);
        }

    }
//    public Document makeDBEntry(String fileName, String discription){
//        MongoIterable<String> list = assDatabase.listCollectionNames();
//        boolean hasCollection = false;
//        for(String s : list){
//            if(s.equals(assCol)){
//                hasCollection = true;
//                break;
//            }
//        }
//        MongoCollection assCollection = null;
//        if(!hasCollection){
//            assDatabase.createCollection(assCol);
//        }
//        assCollection = assDatabase.getCollection(assCol);
//        Document ass = new Document()
//                .append("course_id",CID)
//                .append("assignment_id",nextPos)
//                .append("assignment_name",fileName);
//        assCollection.insertOne(ass);
//        return ass;
//    }
    //    public int add(FileDAO fileDAO)throws Exception{
//        this.CID = fileDAO.getCourseID();
//        this.fileDAO = fileDAO;
//        String relativePathPrefix = getRelPath();
//        String FileStructure = makeFileStructure(relativePathPrefix);
//        fileDAO.writeFile(FileStructure+reg+fileDAO.getFilename());
//        makeDBEntry(fileDAO.getFilename(),assignmentDAO.getInstructions());
//        return nextPos;
//    }
}