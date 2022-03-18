package edu.oswego.cs.rest.database;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bson.Document;
import org.bson.conversions.Bson;

import edu.oswego.cs.rest.daos.TeamDAO;
import edu.oswego.cs.rest.requests.InitTeamParam;
import edu.oswego.cs.rest.requests.JoinTeamParam;

import java.util.ArrayList;
import java.util.Collections;

public class TeamInterface {
    private MongoDatabase courseDB;
    private MongoDatabase studentDB;
    private final String CID = "courseID";
    private final String SID = "StudentID";
    private boolean isSynced = true;

    public TeamInterface() {
        DatabaseManager databaseManager = new DatabaseManager();
        try{
            courseDB = databaseManager.getCourseDB();
            studentDB = databaseManager.getStudentDB();
        }catch(Exception e){
            e.printStackTrace(System.out);
            isSynced = false;
        }
    }


    public ArrayList<Integer> initTeamHandler(InitTeamParam request) {
        /*
            desc: Initialize teams
            return: An array of integer represents the team size for each team.
        */
        try {
            Document courseDoc = courseDB.getCollection("Courses").find(new Document(CID, request.getCourseID())).first();
            @SuppressWarnings("unchecked") 
            ArrayList<String> students =  (ArrayList<String>) courseDoc.get("Students");
            
            int totalStudent = students.size();
            int teamSize = request.getTeamSize(); 
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

    public String joinTeamHandler(JoinTeamParam request) {
        /* desc: allow students to join teams */

        /* TODO
                TeamInterface::joinTeamHandler(String studentID, newTeamID) {
                        + make a new TeamDAO instance
                            * TeamDAO newTeamDAO = new TeamDAO(newTeamID);
                        + Find the student with the param studentID 
                            * make this student team lead => if DB.teams.size() == 0
                        + make a HashMap<String, boolean> members = {teamLead=false} 
                        + push the new hashmap members to the newTeamDAO
                            * newTeamDAO.teamMembers = members;
                        + add the team to DB
                        + DB.team_counts += 1
                        + HTTP 200 OK 

                    - if team_counts >= max_teams 
                        + HTTP 400 BAD REQUEST
                }
        */

        try {
            Document courseDoc = courseDB.getCollection("Courses").find(new Document(CID, request.getCourseID())).first();
            Document studentDoc = studentDB.getCollection("Student").find(new Document(SID, request.getStudentID())).first();  
            @SuppressWarnings("unchecked")
            ArrayList<Document> teams =  (ArrayList<Document>) courseDoc.get("Teams");
            
            boolean isTeamAdded = false;

            for (Document team : teams) {
                if (team.get("TeamID").equals(request.getNewTeamID())) isTeamAdded = true;
            }

            if (isTeamAdded) {
                /* the team with newTeamID is already added
                   the student does not have a chance to be team lead
                   just add the student to the team */
                Document newMember = new Document();
                newMember.put(request.getStudentID(), false);

                Bson filter = Filters.eq("CourseID", request.getCourseID());
                // Bson update = new Document().append("Teams.$.", value)
            } else {
                // the team is not added yet
                // make new teamDAO
                // make the studentID be team lead
                TeamDAO newTeam = new TeamDAO(request.getNewTeamID());
            }

            Document team = teams.get(0);
            Document teamMember = (Document) team.get("Team Member");
            Document newMember = new Document();
            newMember.put(request.getStudentID(), false);
            teamMember.append(request.getStudentID(), false);


            return teamMember.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }



    }

}





















