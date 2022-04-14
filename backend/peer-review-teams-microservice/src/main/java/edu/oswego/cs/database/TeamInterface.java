package edu.oswego.cs.database;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;

import edu.oswego.cs.daos.TeamDAO;
import edu.oswego.cs.requests.SwitchTeamParam;
import edu.oswego.cs.requests.TeamParam;
import edu.oswego.cs.services.SecurityService;
import edu.oswego.cs.services.TeamService;

import org.bson.Document;
import org.bson.conversions.Bson;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class TeamInterface {
    private final MongoCollection<Document> courseCollection;
    private final MongoCollection<Document> studentCollection;
    private final MongoCollection<Document> teamCollection;

    /**
     * Default constructor initializes database connections and retrieves needed collections 
     */
    public TeamInterface() {
        DatabaseManager databaseManager = new DatabaseManager();
        try {
            MongoDatabase studentDB = databaseManager.getStudentDB();
            MongoDatabase courseDB = databaseManager.getCourseDB();
            MongoDatabase teamDB = databaseManager.getTeamDB();
            studentCollection = studentDB.getCollection("students");
            courseCollection = courseDB.getCollection("courses");
            teamCollection = teamDB.getCollection("teams");
        } catch (WebApplicationException e) {
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to retrieve collections.").build());
        }
    }

    /**
     * Allows user to create and write a team to TeamDatabase
     * @param request TeamDAO:{"course_id", "team_lead", "team_size"}
     */
    public void createTeam(TeamParam request) {
        /* Course Security Checks */
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        
        /* Param Security Checks */
        new SecurityService().createTeamSecurity(teamCollection, courseDocument, request);
            
        /* Create Team */
        String teamID = new TeamService().generateTeamID(teamCollection);
        int teamSize = new TeamService().getTeamSize(courseDocument, request);
        TeamDAO newTeam = new TeamDAO(teamID, request.getCourseID(), teamSize, request.getStudentID() );
        newTeam.getTeamMembers().add(request.getStudentID());
        newTeam.setTeamMembers(newTeam.getTeamMembers());

        /* Write to DB */
        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> courseDAOEntity = Entity.entity(jsonb.toJson(newTeam), MediaType.APPLICATION_JSON_TYPE);
        Document teamDocument = Document.parse(courseDAOEntity.getEntity());
        teamCollection.insertOne(teamDocument);
    }

    /**
     * Gets all teams in a course
     * @param request TeamParam:{"course_id"}
     * @return List<Document> of all team
     */
    public List<Document> getAllTeams(String courseID) {
        /* Course Security Checks */
        Document courseDocument = courseCollection.find(eq("course_id", courseID)).first();
        if (courseDocument == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        
        /* Get Team */
        MongoCursor<Document> cursor = teamCollection.find().iterator();
        
        List<Document> teams = new ArrayList<>();

        try { 
            while(cursor.hasNext()) { 
                Document teamDocument = cursor.next();
                if (teamDocument.getString("course_id").equals(courseID)) 
                    teams.add(teamDocument);     
            } 
        return teams;
        } finally { 
            cursor.close(); 
        } 
    }

    /**
     * If requested student is not already in a team, shows non-full-teams
     * If requested student is already in a team, shows their team
     * @param request TeamParam:{"course_id", "student_id"}
     * @return List<Document> of teams
     */
    public List<Document> getTeamByStudentID(TeamParam request) {
        /* Course Security check */
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        
        /* Param Security Checks */
        if (!new SecurityService().isStudentValid(courseDocument, request.getStudentID())) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found in this course.").build());

        /* Get Team */
        List<Document> nonFullTeams = new ArrayList<>();
        MongoCursor<Document> cursor = teamCollection.find().iterator();
        if (cursor == null) 
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to retrieve team collection.").build());

        try { 
            while(cursor.hasNext()) { 
                Document teamDocument = cursor.next();
                List<String> members = teamDocument.getList("team_members", String.class);
                String courseID = teamDocument.getString("course_id");
                
                for (String member : members) {
                    List<Document> teams = new ArrayList<>();
                    if (request.getStudentID().equals(member) && request.getCourseID().equals(courseID)){
                        teams.add(teamDocument);
                        return teams; 
                    }
                }
                if (!teamDocument.getBoolean("is_full") && request.getCourseID().equals(courseID)) 
                    nonFullTeams.add(teamDocument);
            }
        } finally { 
            cursor.close();
        } 
        return nonFullTeams;
    }

    /**
     * Gets team by teamID
     * @param request TeamParam:{"course_id", "team_id"}
     * @return Document
     */
    public Document getTeamByTeamID(TeamParam request) {
        /* Course Security check */
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        
        /* Get Team */
        MongoCursor<Document> cursor = teamCollection.find().iterator();
        if (cursor == null) 
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to retrieve team collection.").build());
        
        try { 
            while(cursor.hasNext()) { 
                Document teamDocument = cursor.next();
                String courseID = teamDocument.getString("course_id");
                String teamID = teamDocument.getString("team_id");
                
                if (request.getTeamID().equals(teamID) && request.getCourseID().equals(courseID))
                    return teamDocument;
            }
        } finally { 
            cursor.close();
        } 

        throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
    }

    /**
     * Allows student user to join a team
     * @param request TeamDAO:{"team_id", "course_id", "team_lead"}
     */
    public void joinTeam(TeamParam request) {
        /* Course Security check */
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        
        /* Param Security Checks */
        new SecurityService().joinTeamSecurity(teamCollection, courseDocument, request);

        /* Update Team */
        List<Document> teamDocuments = getAllTeams(request.getCourseID());
        if (teamDocuments == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            String teamDocumentTeamID = teamDocument.getString("team_id");
            String teamDocumentCouurseID = teamDocument.getString("course_id");
            List<String> teamMembers = teamDocument.getList("team_members", String.class);
            
            if (request.getTeamID().equals(teamDocumentTeamID) && request.getCourseID().equals(teamDocumentCouurseID)) {
                teamMembers.add(request.getStudentID());
                
                Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
                teamCollection.findOneAndUpdate(teamDocumentFilter, Updates.set("team_members", teamMembers));
                if (teamMembers.size() >= teamDocument.getInteger("team_size")) 
                    teamCollection.updateOne(teamDocumentFilter, Updates.set("is_full", true));
            }
        } 
    }

    /**
     * Allows student users to switch teams
     * @param request SwitchTeamParam:{"course_id", "student_id", "current_team_id", "target_team_id"}
     */
    public void switchTeam(SwitchTeamParam request) {
        /* Course Security check */
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());

        /* Param Security Checks */
        new SecurityService().switchTeamSecurity(teamCollection, courseDocument, request);
        
        /* Update currentTeamDocument */
        List<Document> currentTeamDocuments = getAllTeams(request.getCourseID());
        if (currentTeamDocuments == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document currentTeamDocument : currentTeamDocuments) {
            String teamDocumentTeamID = currentTeamDocument.getString("team_id");
            String teamDocumentCouurseID = currentTeamDocument.getString("course_id");
            
            if (request.getCurrentTeamID().equals(teamDocumentTeamID) && request.getCourseID().equals(teamDocumentCouurseID)) {
                Bson currentTeamDocumentFilter = Filters.and(eq("team_id", request.getCurrentTeamID()), eq("course_id", request.getCourseID()));
                List<String> currentTeamMembers = currentTeamDocument.getList("team_members", String.class);
                currentTeamMembers.remove(request.getStudentID());
                
                if (currentTeamMembers.size() < 1) 
                    teamCollection.deleteOne(currentTeamDocumentFilter);
                else {
                    Bson currentTeamUpdates = Updates.combine(
                        Updates.set("team_members", currentTeamMembers), 
                        Updates.set("team_lead", currentTeamMembers.get(0)), 
                        Updates.set("is_full", false));

                    UpdateOptions currentTeamOptions = new UpdateOptions().upsert(true);
                    
                    try {
                        teamCollection.updateOne(currentTeamDocumentFilter, currentTeamUpdates, currentTeamOptions);
                    } catch (MongoException error){
                        error.printStackTrace();
                    }
                }
            }
        } 

        /* Update targetTeamDocument */
        List<Document> targetTeamDocuments = getAllTeams(request.getCourseID());
        if (targetTeamDocuments == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document targetTeamDocument : targetTeamDocuments) {
            String teamDocumentTeamID = targetTeamDocument.getString("team_id");
            String teamDocumentCouurseID = targetTeamDocument.getString("course_id");
            
            if (request.getTargetTeamID().equals(teamDocumentTeamID) && request.getCourseID().equals(teamDocumentCouurseID)) {
                Bson targetTeamDocumentFilter = Filters.and(eq("team_id", request.getTargetTeamID()), eq("course_id", request.getCourseID()));
                List<String> targetTeamMembers = targetTeamDocument.getList("team_members", String.class);
                targetTeamMembers.add(request.getStudentID());

                Bson targetTeamUpdates = Updates.combine(
                    Updates.set("team_members", targetTeamMembers),
                    Updates.set("is_full", false));
                
                if (targetTeamMembers.size() >= targetTeamDocument.getInteger("team_size")) 
                    targetTeamUpdates = Updates.combine(targetTeamUpdates, Updates.set("is_full", true));

                UpdateOptions targetTeamOptions = new UpdateOptions().upsert(true);
                try {
                    teamCollection.updateOne(targetTeamDocumentFilter, targetTeamUpdates, targetTeamOptions);
                } catch (MongoException error){
                    error.printStackTrace();
                }
            }
        }
    }

    /**
     * Generates a new name for the team
     * @param request TeamParam:{"team_id", "course_id", "student_id", "team_name"}
     */
    public void generateTeamName(TeamParam request) {
        /* Course Security check */
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());

        /* Param Security Checks */
        new SecurityService().generateTeamNameSecurity(teamCollection, studentCollection, courseDocument, request);

        /* Update team name */
        List<Document> teamDocuments = getAllTeams(request.getCourseID());
        if (teamDocuments == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
            String teamDocumentTeamID = teamDocument.getString("team_id");
            String teamDocumentCouurseID = teamDocument.getString("course_id");
            if (request.getTeamID().equals(teamDocumentTeamID) && request.getCourseID().equals(teamDocumentCouurseID)) {
                Bson teamNameUpdates = Updates.combine(
                    Updates.set("team_id", request.getTeamName()),
                    Updates.set("team_lock", true)
                );
                UpdateOptions teamNameOptions = new UpdateOptions().upsert(true);
                try {
                    teamCollection.updateOne(teamDocumentFilter, teamNameUpdates, teamNameOptions);
                } catch (MongoException error){
                    error.printStackTrace();
                }
            }
        }
    }


    /* ---- Professor ---- */

    /**
     * Toggles the team status
     * @param request TeamParam:{"team_id", "course_id"}
     */
    public void toggleTeamLock(TeamParam request) {
        /* Course Security check */
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        
        /* Param Security Checks */
        if (!new SecurityService().isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID())) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        
        /* Update team status */
        List<Document> teamDocuments = getAllTeams(request.getCourseID());
        if (teamDocuments == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
            String teamDocumentTeamID = teamDocument.getString("team_id");
            String teamDocumentCouurseID = teamDocument.getString("course_id");
            if (request.getTeamID().equals(teamDocumentTeamID) && request.getCourseID().equals(teamDocumentCouurseID)) {
                boolean teamLock = teamDocument.getBoolean("team_lock");
                teamCollection.findOneAndUpdate(teamDocumentFilter, Updates.set("team_lock", !teamLock));
            }
        }
    }

    /**
     * Adds a student to team
     * @param request
     */
    public void addStudentToTeam(TeamParam request) {
        /* Course Security check */
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        
        /* Param Security Checks */
        new SecurityService().addStudentToTeamSecurity(teamCollection, courseDocument, request);

        /* Add/move student */
        if (!new SecurityService().isStudentAlreadyInATeam(teamCollection, request.getStudentID(), request.getCourseID())) 
            joinTeam(request);
        else if (new SecurityService().isStudentAlreadyInATeam(teamCollection, request.getStudentID(), request.getCourseID())){
            String currentTeamID = new TeamService().retrieveTeamID( courseDocument, request);
            SwitchTeamParam switchTeamParam = new SwitchTeamParam(request.getCourseID(), request.getStudentID(), currentTeamID, request.getTeamID());
            switchTeam(switchTeamParam);
        }
        
    }

    /**
     * Removes a student from a team
     * @param request TeamParam:{"team_id", "course_id", "student_id"}
     */
    public void removeStudent(TeamParam request) {
        /* Course Security check */
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        
        /* Param Security Checks */
        new SecurityService().removeStudent(teamCollection, courseDocument, request);

        /* Remove Student */
        List<Document> teamDocuments = getAllTeams(request.getCourseID());
        if (teamDocuments == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
            String teamDocumentTeamID = teamDocument.getString("team_id");
            String teamDocumentCouurseID = teamDocument.getString("course_id");

            if (request.getTeamID().equals(teamDocumentTeamID) && request.getCourseID().equals(teamDocumentCouurseID)) {
                List<String> teamMembers = teamDocument.getList("team_members", String.class);
                teamMembers.remove(request.getStudentID());

                if (teamMembers.size() < 1) 
                    teamCollection.deleteOne(teamDocumentFilter);
                else {
                    Bson teamUpdates = Updates.combine(
                        Updates.set("team_members", teamMembers), 
                        Updates.set("team_lead", teamMembers.get(0)), 
                        Updates.set("is_full", false));
                    UpdateOptions teamOptions = new UpdateOptions().upsert(true);
            
                    try {
                        teamCollection.updateOne(new Document("team_id", request.getTeamID()), teamUpdates, teamOptions);
                    } catch (MongoException error){
                        error.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Edits a Team Name
     * @param request TeamParam: {"team_id", "course_id", "team_name"}
     */
    public void editTeamName(TeamParam request) {
        /* Course Security check */
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        
        /* Param Security Checks */
        new SecurityService().editTeamNameSecurity(teamCollection, studentCollection, courseDocument, request);
        
        /* Update Team name */
        List<Document> teamDocuments = getAllTeams(request.getCourseID());
        if (teamDocuments == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
            String teamDocumentTeamID = teamDocument.getString("team_id");
            String teamDocumentCouurseID = teamDocument.getString("course_id");

            if (request.getTeamID().equals(teamDocumentTeamID) && request.getCourseID().equals(teamDocumentCouurseID)) {
                Bson editTeamNameUpdates = Updates.combine(
                    Updates.set("team_id", request.getTeamName()),
                    Updates.set("team_lock", true)
                );
                UpdateOptions editTeamNameOptions = new UpdateOptions().upsert(true);
            
                try {
                    teamCollection.updateOne(teamDocumentFilter, editTeamNameUpdates, editTeamNameOptions);
                } catch (MongoException error){
                    error.printStackTrace();
                }
            }
        }
    }

    /**
     * Assigns a new Team lead for the team
     * @param request
     */
    public void assignTeamLead(TeamParam request) {
         /* Course Security check */
         Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
         if (courseDocument == null)
             throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
         
         /* Param Security Checks */
         new SecurityService().assignTeamLeadSecurity(teamCollection, courseDocument, request);

         /* Update Team Lead */
        List<Document> teamDocuments = getAllTeams(request.getCourseID());
        if (teamDocuments == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
            String teamDocumentTeamID = teamDocument.getString("team_id");
            String teamDocumentCouurseID = teamDocument.getString("course_id");

            if (request.getTeamID().equals(teamDocumentTeamID) && request.getCourseID().equals(teamDocumentCouurseID)) {
                List<String> students = teamDocument.getList("team_members", String.class);
                
                students.remove(request.getStudentID());
                Collections.reverse(students);
                students.add(request.getStudentID());
                Collections.reverse(students);

                Bson assignTeamLeadUpdates = Updates.combine(
                    Updates.set("team_lead", request.getStudentID()),
                    Updates.set("team_members", students)
                );

                UpdateOptions assignTeamLeadOptions = new UpdateOptions().upsert(true);
            
                try {
                    teamCollection.updateOne(teamDocumentFilter, assignTeamLeadUpdates, assignTeamLeadOptions);
                } catch (MongoException error){
                    error.printStackTrace();
                }
               
            }
        }
    }

    /**
     * Deletes a team
     * @param request TeamParam:{"team_id", "course_id"}
     */
    public void deleteTeam(TeamParam request) {
        /* Course Security check */
        Document courseDocument = courseCollection.find(eq("course_id", request.getCourseID())).first();
        if (courseDocument == null)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        
        /* Param Security Checks */
        if (!new SecurityService().isTeamCreated(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Team not found.").build());
        if (new SecurityService().isTeamLock(teamCollection, request.getTeamID(), request.getCourseID()))
            throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Team is locked.").build());

        /* Delete team*/
        List<Document> teamDocuments = getAllTeams(request.getCourseID());
        if (teamDocuments == null) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            Bson teamDocumentFilter = Filters.and(eq("team_id", request.getTeamID()), eq("course_id", request.getCourseID()));
            String teamDocumentTeamID = teamDocument.getString("team_id");
            String teamDocumentCouurseID = teamDocument.getString("course_id");

            if (request.getTeamID().equals(teamDocumentTeamID) && request.getCourseID().equals(teamDocumentCouurseID)) {
                teamCollection.findOneAndDelete(teamDocumentFilter);
            }
        }

    }


    /* Util */
    
    /**
     * Gets all the students in this courseID
     * @param courseID
     * @return List<Document>
     */
    public List<Document> getAllStudentsInThisCourse(String courseID) {
        /* Course Security check */
        Document courseDocument = courseCollection.find(eq("course_id", courseID)).first();
        if (courseDocument == null)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Course not found.").build());
        
        /* Get Students */
        MongoCursor<Document> cursor = studentCollection.find().iterator();
        
        List<Document> students = new ArrayList<>();

        try { 
            while(cursor.hasNext()) { 
                Document studentDocument = cursor.next();
                List<String> courses = studentDocument.getList("courses", String.class);
                for (String course : courses) {
                    if (course.equals(courseID))
                        students.add(studentDocument);     
                }
            } 
        return students;
        } finally { 
            cursor.close(); 
        } 
    }

}