package edu.oswego.cs.rest.database;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import edu.oswego.cs.rest.daos.TeamDAO;
import edu.oswego.cs.rest.requests.InitTeamRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

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


    public ArrayList<Integer> initTeamHandler(InitTeamRequest teamInit) {
        /*
            desc: Initialize teams
            return: An array of integer represents the team size for each team.
        */
        try {
            Document tempCourse = courseDB.getCollection("Courses").find(new Document("courseID", teamInit.getCourseID())).first();
            // ArrayList<StudentDAO> students = (ArrayList) tempCourse.get("Students");
            
            int totalStudent = 21; // students.size();
            int teamSize = teamInit.getTeamSize(); 
            int maxTeams = totalStudent / teamSize; // maximum number of teams in a course 
            int remainingStudents = totalStudent % teamSize; 
            
            if (remainingStudents > 0) maxTeams += 1; // The remaining student can add up to one more team
            
            ArrayList<Integer> teamsMemArray = new ArrayList<>(Collections.nCopies(maxTeams, teamSize)); // fill the ArrayList with teamSize value
            
            for (int i = 0; i < (maxTeams * teamSize - totalStudent); i++) {
                teamsMemArray.set(i, teamsMemArray.get(i) - 1); // take one from each full time to add up to the last reamining team
            }

            Collections.sort(teamsMemArray, Collections.reverseOrder());
            return teamsMemArray;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }

}





















