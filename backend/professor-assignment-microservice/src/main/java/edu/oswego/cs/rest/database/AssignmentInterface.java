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

    boolean isSynced = true;

    public AssignmentInterface(String CID,String AssID) throws Exception {
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
        String path = (System.getProperty("user.dir").contains("\\")) ? System.getProperty("user.dir").replace("\\", "/") : System.getProperty("user.dir");
        String[] slicedPath = path.split("/");
        String targetDir = "professor-assignment-microservice";
        int i;
        String relativePathPrefix = "";
        System.out.println(Arrays.toString(slicedPath));
        for (i = slicedPath.length - 1; ! slicedPath[i].equals(targetDir); i--) {
            relativePathPrefix = relativePathPrefix + "../";
        }
        String reg = "/";
        if (System.getProperty("user.dir").contains("\\")){
             reg = "\\";
            relativePathPrefix.replace("/", "\\");
        }

        String FileStructure = relativePathPrefix+"Courses"+reg+CID+reg;
        new File(FileStructure).mkdirs();
        int x = 1;
        while(!(new File(FileStructure + x).mkdirs())){
            x++;
        }
        FileStructure += x;
        new File(FileStructure + reg + "TeamSubmissions").mkdirs();
        new File(FileStructure + reg + "PeerReviews").mkdirs();



    }




}
