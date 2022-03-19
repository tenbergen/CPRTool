package edu.oswego.cs.rest.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;

import org.bson.Document;
import org.bson.conversions.Bson;

import edu.oswego.cs.rest.daos.TeamDAO;
import edu.oswego.cs.rest.requests.TeamParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

public class TeamInterface {
    private MongoDatabase courseDB;
    private MongoDatabase studentDB;
    private MongoCollection<Document> courseCollection;
    private MongoCollection<Document> studentCollection; 
    private final String CID = "courseID";
    private final String SID = "StudentID";

    public TeamInterface() {
        DatabaseManager databaseManager = new DatabaseManager();
        try{
            courseDB = databaseManager.getCourseDB();
            studentDB = databaseManager.getStudentDB();
            courseCollection = courseDB.getCollection("Courses");
            studentCollection = studentDB.getCollection("Student");
        }catch(Exception e){
            e.printStackTrace(System.out);
        }
    }


    public ArrayList<Integer> initTeamHandler(TeamParam request) {
        /*
         - desc: Initialize teams
         - return: An array of integer represents the team size for each team. 
        */
        try {
            Document courseDoc = courseDB.getCollection("Courses").find(new Document(CID, request.getCourseID())).first();
            @SuppressWarnings("unchecked") 
            ArrayList<String> students =  (ArrayList<String>) courseDoc.get("Students");
            
            int totalStudent = students.size();
            int teamSize = request.getTeamSize(); 
            int maxTeams = totalStudent / teamSize; // maximum number of teams in a course 
            int remainingStudents = totalStudent % teamSize; 
            
            if (remainingStudents > 0) maxTeams += 1; // The remaining students can add up to one more team
            
            ArrayList<Integer> teamsMemArray = new ArrayList<>(Collections.nCopies(maxTeams, teamSize)); // fill the ArrayList with the value of teamSize
            
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

    public int joinTeamHandler(TeamParam request) {
        /* 
         - desc: allow students to join teams 
         - Notes: 
            + fix isStudentIDvalid => wrong studentID still works (just for safety purpose. FE should make sure the studentID is correct before send it to BE)
        */

        try {
            // >>> Initialization <<<
            Document courseDoc = courseCollection.find(new Document(CID, request.getCourseID())).first();
            Document studentDoc = studentCollection.find(new Document(SID, request.getStudentID())).first();  
            List<Document> teams =  courseDoc.getList("Teams", Document.class);
            Document targetTeamDoc = new Document();
            Document teamMember = new Document();
            Integer teamSize = 0;
            boolean isTeamAdded = false;
            Boolean isFull = false;


            for (Document team : teams) {
                if (team.get("TeamID").equals(request.getTeamID())) {
                    targetTeamDoc = team;
                    isTeamAdded = true;
                    teamSize = (Integer) targetTeamDoc.get("TeamSize");
                    isFull = (Boolean) targetTeamDoc.get("IsFull");
                }
            }
            if (!isFull) {
                if (isTeamAdded) {
                    /* 
                     - the team with newTeamID is already added
                     - the student does not have a chance to be a team lead
                     - just add the student to the teams.teamMember and set value of isStudentFinalized to false
                     - update isFull
                    */

                    if (teamSize == request.getTeamSize()) { // safety purpose
                        // >>> Team Document <<<
                        teamMember = (Document) targetTeamDoc.get("TeamMembers");
                        teamMember.append(request.getStudentID(), false); // automatically update targetTeamDoc Document
                        if (teamMember.size() == teamSize) {
                            targetTeamDoc.replace("IsFull", false, true);
                        } 

                        // >>> Student Document <<<
                        Bson updates = Updates.combine(
                            Updates.set("TeamID", request.getTeamID())
                        );
                        UpdateOptions options = new UpdateOptions().upsert(true);
                        
                        // >>> Update to mongo <<<
                        try {
                            studentCollection.updateOne(studentDoc, updates, options);
                            courseCollection.updateOne(new Document(CID, request.getCourseID()), new Document("$set", new Document("Teams", teams)));
                            return 0;
                        } catch (Exception e) {
                            return -1;
                        }
                    } else {
                        return 1; 
                    }
    
                } else {
                    /* 
                     - the team is not added yet
                     - make new teamDAO 
                     - update teamSize passed from FE
                     - add student to newTeamDoc
                     - make the studentID be team lead
                     - add teamID to studentDoc
                     - add the new team 
                    */
                    
                    // >>> Team Document <<<
                    TeamDAO newTeam = new TeamDAO(request.getTeamID());
                    Jsonb jsonb = JsonbBuilder.create();
                    Entity<String> teamDAOEntity = Entity.entity(jsonb.toJson(newTeam), MediaType.APPLICATION_JSON_TYPE);
                    Document newTeamDoc = Document.parse(teamDAOEntity.getEntity());
                    newTeamDoc.replace("TeamSize", 0, request.getTeamSize());
                    teamMember = (Document) newTeamDoc.get("TeamMembers");
                    teamMember.append(request.getStudentID(), false);
                    teams.add(newTeamDoc);
    
                    // >>> Student Document <<<
                    Bson updates = Updates.combine(
                        Updates.set("TeamLead", true),
                        Updates.set("TeamID", request.getTeamID())
                    );
                    UpdateOptions options = new UpdateOptions().upsert(true);
                    
                    // >>> Update to mongo <<<
                    try {
                        studentCollection.updateOne(studentDoc, updates, options);
                        courseCollection.updateOne(new Document(CID, request.getCourseID()), new Document("$set", new Document("Teams", teams)));
                        return 0;
                    } catch (Exception e) {
                        return -1; //e.toString();
                    }
                }
            } else {
                return 2; 
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public List<Document> getAllTeamsHandler(String courseID) {
        /* desc: get A list of all teams to join teams */
        try {
            Document courseDoc = courseCollection.find(new Document(CID, courseID)).first();
            List<Document> teams =  courseDoc.getList("Teams", Document.class);

            return teams;
        } catch (Exception e) {
            List<Document> errors = new ArrayList<Document>();
            errors.add(new Document(e.toString(), Exception.class));
            return errors;
        }
    }

    public Document getTeamByTeamIDHandler(TeamParam request) {
        /* desc: get A list of all teams to join teams */
        try {
            Document courseDoc = courseCollection.find(new Document(CID, request.getCourseID())).first();
            List<Document> teams =  courseDoc.getList("Teams", Document.class);
            Document targetTeam = new Document();

            for (Document team : teams) {
                if (team.getString("TeamID").equals(request.getTeamID())) targetTeam = team;
            }
            return targetTeam;
        } catch (Exception e) {
            return new Document(e.toString(), Exception.class);
        }
    }

}





















