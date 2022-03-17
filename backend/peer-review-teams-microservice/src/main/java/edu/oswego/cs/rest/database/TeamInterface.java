package edu.oswego.cs.rest.database;

import java.io.IOException;
import com.mongodb.client.MongoDatabase;



public class TeamInterface {
    
    private boolean isSynced = false; 
    private MongoDatabase teamDB;
    private MongoDatabase studentDB;
    
    public TeamInterface() throws IOException {
        DatabaseManager databaseManager = new DatabaseManager();
        try{
            teamDB = databaseManager.getTeamDB();
        }catch(Exception e){
            e.printStackTrace(System.out);
            throw new IOException();
        }

        try{
            studentDB = databaseManager.getStudentDB();
        }catch(Exception e){
            e.printStackTrace(System.out);
            isSynced = false;
        }
    }
}
