package edu.oswego.cs.rest.database;

import com.mongodb.client.MongoDatabase;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class AssignmentInterface {
    MongoDatabase userDatabase;// = new DatabaseManager().getUserDB();
    MongoDatabase assDatabase;

    String studentCol = "Students";
    String profCol = "Professor";
    String assCol = "Assignments";
    String reg;
    String CID;

    boolean isSynced = true;

    public AssignmentInterface(String CID) throws Exception {
        this.CID = CID;
        initiliseDatabases();
        String relativePathPrefix = getRelPath();
        String FileStructure = makeFileStructure(relativePathPrefix);
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
        int nextPos = 0;
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



}
