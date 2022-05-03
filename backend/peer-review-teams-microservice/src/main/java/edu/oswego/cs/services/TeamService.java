package edu.oswego.cs.services;

import edu.oswego.cs.database.TeamInterface;
import edu.oswego.cs.requests.TeamParam;
import org.bson.Document;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TeamService {

    public String generateTeamID(@Context SecurityContext securityContext, String courseID){
        List<Document> teamDocuments = new TeamInterface().getAllTeams(securityContext, courseID);
        
        Set<String> teamIDs = new HashSet<>();
        for (Document teamDocument : teamDocuments) 
            teamIDs.add(teamDocument.getString("team_id"));
        for (int i = 0; i < teamIDs.size(); i++)
            if (!teamIDs.contains(String.valueOf(i))) 
                return String.valueOf(i);
        return String.valueOf(teamIDs.size());
    }

    public int getTeamSize(Document courseDocument) {
        int teamSize = Integer.parseInt(courseDocument.getInteger("team_size").toString());
        if (teamSize == 0) throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Team size not initialized.").build());
        return teamSize;
    }

    public String retrieveTeamID(@Context SecurityContext securityContext, TeamParam request) {
        List<Document> teamDocuments = new TeamInterface().getAllTeams(securityContext, request.getCourseID());
        if (teamDocuments == null || teamDocuments.size() == 0) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("No teams found.").build());

        for (Document teamDocument : teamDocuments) {
            String teamDocumentCourseID = teamDocument.getString("course_id");
            if (request.getCourseID().equals(teamDocumentCourseID)) {
                List<String> students = teamDocument.getList("team_members", String.class);
                if (students.contains(request.getStudentID())) return teamDocument.getString("team_id");
            }
        }
        throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student not found.").build());
    }
}