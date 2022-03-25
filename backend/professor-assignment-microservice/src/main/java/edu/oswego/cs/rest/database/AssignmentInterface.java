package edu.oswego.cs.rest.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import edu.oswego.cs.rest.daos.FileDAO;
import org.apache.commons.io.FileUtils;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class AssignmentInterface {
    MongoDatabase assDatabase;

    String assCol = "assignments";
    String reg;
    String CID;
    FileDAO fileDAO;
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
    public int add(FileDAO fileDAO,String CID)throws Exception{
        this.CID = CID;
        this.fileDAO = fileDAO;
        String relativePathPrefix = getRelPath();
        String FileStructure = makeFileStructure(relativePathPrefix);
        fileDAO.writeFile(FileStructure+reg+fileDAO.getFilename());
        makeDBEntry(fileDAO.getFilename());
        return nextPos;
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
    public void addToAss(FileDAO fileDAO,String CID,int AssID) throws IOException {
        this.CID = CID;
        this.fileDAO = fileDAO;
        String relativePathPrefix = getRelPath();
        String FileStructure = relativePathPrefix+"Courses"+reg+CID+reg+AssID;
        fileDAO.writeFile(FileStructure+reg+fileDAO.getFilename());
    }
    public String findAss(String courseID,int assID){
        String relativePath = getRelPath();
        return relativePath + reg + courseID + reg + assID;
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

    public Document makeDBEntry(String fileName){
        MongoIterable<String> list = assDatabase.listCollectionNames();
        boolean hasCollection = false;
        for(String s : list){
            if(s.equals(assCol)){
                hasCollection = true;
                break;
            }
        }
        MongoCollection assCollection = null;
        if(!hasCollection){
            assDatabase.createCollection(assCol);
        }
        assCollection = assDatabase.getCollection(assCol);
        Document ass = new Document()
                .append("course_id",CID)
                .append("assignment_id",nextPos)
                .append("assignment_name",fileName);
        assCollection.insertOne(ass);
        return ass;
    }
}