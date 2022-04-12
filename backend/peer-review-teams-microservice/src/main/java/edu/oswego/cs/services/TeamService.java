package edu.oswego.cs.services;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import org.bson.Document;

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
                teamIDs.add(teamDocument.get("team_id").toString());
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
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Team size not initialized.").build());    
        return teamSize;
    }
}
