package edu.oswego.cs.rest.database;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import edu.oswego.cs.rest.daos.StudentDAO;
import edu.oswego.cs.rest.daos.TeamDAO;
import edu.oswego.cs.rest.requests.TeamInitRequest;

import java.io.IOException;
import java.util.ArrayList;

public class TeamInterface {
    private String courseID;
    private int max_teams;
    private int max_member;
    private ArrayList<TeamDAO> teams;


    private boolean isSynced = true;
    private MongoDatabase courseDB;
    private MongoDatabase studentDB;

    public TeamInterface() throws IOException {
        DatabaseManager databaseManager = new DatabaseManager();
        try{
            courseDB = databaseManager.getCourseDB();
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


    public ArrayList<Integer> teamInitHandler(TeamInitRequest teamInit) {
        /*
                
        */
        try {
            
            Document tempCourse = courseDB.getCollection("Courses").find(new Document("courseID", teamInit.getCourseID())).first();
            int teamSize = teamInit.getTeamSize(); // = 4
            int totalStudent = 21; 
            // ArrayList<StudentDAO> students = (ArrayList) tempCourse.get("Students");
            // int totalStudent = students.size();

            int teamCount = totalStudent / teamSize; // 5 : [4 4 4 4 4] => 6 : [4 4 4 4 3 4]
            int remainingStudents = totalStudent % teamSize; // 3

            
            if (remainingStudents > 0) {
                teamCount += 1;
            }
            
            ArrayList<Integer> teamsMemArray = new ArrayList<>(); 

            for (int i = 0; i < teamCount; i++) { 
                teamsMemArray.add(teamSize); //  [3 3 3 4 4 4]
            } 

            
            for (int i = 0; i < (teamCount * teamSize - totalStudent); i++) {
                teamsMemArray.set(i, teamsMemArray.get(i) - 1);
            }

            return teamsMemArray;

        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
            return null;
        }
        
    }

}





















