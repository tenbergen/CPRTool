package edu.oswego.cs.rest.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import edu.oswego.cs.rest.daos.FileDAO;
import org.apache.commons.io.FileUtils;
import org.bson.Document;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class AssignmentInterface {
    MongoDatabase userDatabase;// = new DatabaseManager().getUserDB();
    MongoDatabase assDatabase;

    String studentCol = "Student";
    String profCol = "Professor";
    String assCol = "Ass";
    String reg;
    String CID;

    FileDAO fileDAO;

    int nextPos = 0;

    boolean isSynced = true;

    public AssignmentInterface() throws Exception {
        initiliseDatabases();
    }
    public void add(FileDAO fileDAO)throws Exception{
        this.CID = fileDAO.getCourse();
        this.fileDAO = fileDAO;
        String relativePathPrefix = getRelPath();
        String FileStructure = makeFileStructure(relativePathPrefix);
        fileDAO.writeFile(FileStructure+reg+fileDAO.getFilename());
        Document d = makeDBEntry(fileDAO.getFilename());
        if(isSynced)addToUsers(d);
    }
    public void remove(String AssName,String courseID) throws IOException {
        String relativePathPrefix = getRelPath();
        Document ass = assDatabase.getCollection(assCol).findOneAndDelete(new Document("AssName",AssName).append("courseID",courseID));
        if(ass==null) return;
        String FileStructure = relativePathPrefix+"Courses"+reg+courseID+reg+ass.get("AssID");
        FileUtils.deleteDirectory(new File(FileStructure));
        if(isSynced)removeFromUsers(AssName,courseID);
    }
    public void removeFromUsers(String AssName, String courseID){
        Document professor = userDatabase.getCollection(profCol).findOneAndDelete(new Document());
        ArrayList asses = new ArrayList();
        if(professor.get("Assignments")!=null){
            asses = (ArrayList)professor.get("Assignments");
        }
        for(Object ass: asses){
            if(((Document)ass).get("AssName").equals(AssName)){
                asses.remove(ass);
                break;
            }
        }
        professor.put("Assignments",asses);
        userDatabase.getCollection(profCol).insertOne(professor);

        ArrayList courses = (ArrayList) professor.get("Courses");
        ArrayList roster = new ArrayList();
        for(Object o: courses){
            if(((Document)o).get("courseID").equals(courseID)){
                roster = (ArrayList)((Document)o).get("Students");
                break;
            }
        }

        for(Object o: roster){
            Document student = userDatabase.getCollection(studentCol).findOneAndDelete(new Document("StudentID",o.toString()));
            ArrayList studentasses = new ArrayList<>();
            if(student.get("Assignments") != null){
                studentasses = (ArrayList)student.get("Assignments");
            }
            for(Object ass:studentasses){
                if(((Document)ass).get("AssName").equals(AssName)){
                    asses.remove(ass);
                    break;
                }
            }
            student.put("Assignments",asses);
            userDatabase.getCollection(studentCol).insertOne(student);
        }

    }


    public void initiliseDatabases() throws Exception {
        try{
            DatabaseManager manager = new DatabaseManager();
            assDatabase = manager.getAssDB();
        }catch(Exception e){
            e.printStackTrace(System.out);
            throw new Exception("No connection to the ass DB");
        }
        try{
            DatabaseManager manager = new DatabaseManager();
            userDatabase = manager.getUserDB();
        }catch(Exception e){
            e.printStackTrace(System.out);
            isSynced = false;
        }
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
                .append("courseID",CID)
                .append("AssID",nextPos)
                .append("AssName",fileName)
                .append("Synced",isSynced);
        assCollection.insertOne(ass);
        return ass;
    }
    public void addToUsers(Document ass){
        MongoCollection studentCollection = userDatabase.getCollection(studentCol);
        MongoCollection profCollection = userDatabase.getCollection(profCol);

        Document professor = (Document) profCollection.findOneAndDelete(new Document());

        ArrayList profAsses = null;
        if(professor.get("Assignments")!= null){
            profAsses = (ArrayList)professor.get("Assignments");
        }else profAsses = new ArrayList<>();
        profAsses.add(ass);
        professor.put("Assignments",profAsses);
        System.out.println(professor);
        profCollection.insertOne(professor);

        ArrayList courses = (ArrayList) professor.get("Courses");
        ArrayList classList = null;
        for(Object o: courses){
            if(((Document)o).get("courseID").equals(CID)){
                classList = (ArrayList)((Document)o).get("Students");
                break;
            }
        }
        System.out.println(classList);
        for(Object o:classList){
            System.out.println(o.toString());
            Document student = (Document) studentCollection.findOneAndDelete(new Document("StudentID",o.toString()));
            ArrayList asses = null;
            if(student.get("Assignments")!=null){
                asses = (ArrayList) student.get("Assignments");
            }else asses = new ArrayList<>();
            asses.add(ass);
            student.put("Assignments",asses);
            studentCollection.insertOne(student);
        }
    }



}
