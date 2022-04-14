package edu.oswego.cs.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import org.bson.Document;

import edu.oswego.cs.database.TeamInterface;
import edu.oswego.cs.requests.TeamParam;

public class TeamService {

    /**
     * Generates a new teamID
     * @param teamCollection
     * @return String
     */
    public String generateTeamID(MongoCollection<Document> teamCollection) {
        MongoCursor<Document> cursor = teamCollection.find().iterator();
        if (cursor == null) 
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to retrieve team collection.").build());

        Set<String> teamIDs = new HashSet<>();

        try { 
            while(cursor.hasNext()) { 
                Document teamDocument = cursor.next();
                teamIDs.add(teamDocument.getString("team_id"));
            } 
            for (int i = 0; i < teamIDs.size(); i++) 
                if (!teamIDs.contains(String.valueOf(i))) return String.valueOf(i);
            
            return String.valueOf(teamIDs.size());
        } finally { 
            cursor.close();
        } 
    }

    /**
     * Get team size from course database
     * @param courseDocument
     * @param request
     * @return integer
     */
    public int getTeamSize(Document courseDocument, TeamParam request) {
        int teamSize = Integer.parseInt(courseDocument.getInteger("team_size").toString());
        if (teamSize == 0) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_MODIFIED).entity("Team size not initialized.").build());    
        return teamSize;
    }

    /**
     * Retrieves a team's teamID
     * @param teamCollection
     * @param courseDocument
     * @param request
     * @return String
     */
    public String retrieveTeamID(Document courseDocument, TeamParam request) {
        List<Document> teamDocuments = new TeamInterface().getAllTeams(request.getCourseID());
        if (teamDocuments == null || teamDocuments.size() == 0) 
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments ) {
            String teamDocumentCourseID = teamDocument.getString("course_id");
            if (request.getCourseID().equals(teamDocumentCourseID)) {
                List<String> students = teamDocument.getList("team_members", String.class);
                if (students.contains(request.getStudentID()))
                    return teamDocument.getString("team_id");
            }
        }
        throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found.").build());
    }

    /**
     * Gets a list of TeamIDs in this course
     * @param request
     * @return List<String>
     */
    public List<String> getTeamIDsInCourse(TeamParam request) {
        List<Document> teams = new TeamInterface().getAllTeams(request.getCourseID());
        List<String> teamIDs = new ArrayList<>();

        for (Document team : teams) {
            String teamID = team.getString("team_id");
            teamIDs.add(teamID);
        }
        return teamIDs;
    }
}
