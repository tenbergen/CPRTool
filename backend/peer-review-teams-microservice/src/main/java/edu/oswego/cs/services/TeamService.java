package edu.oswego.cs.services;

import edu.oswego.cs.database.TeamInterface;
import edu.oswego.cs.requests.TeamParam;
import edu.oswego.cs.util.CPRException;
import org.bson.Document;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import java.util.List;

public class TeamService {

    public int getTeamSize(Document courseDocument) {
        int teamSize = Integer.parseInt(courseDocument.getInteger("team_size").toString());
        if (teamSize == 0) throw new CPRException(Response.Status.CONFLICT,"Team size not initialized");
        return teamSize;
    }

    public String retrieveTeamID(@Context SecurityContext securityContext, TeamParam request) {
        List<Document> teamDocuments = new TeamInterface().getAllTeams(securityContext, request.getCourseID());
        if (teamDocuments == null || teamDocuments.size() == 0) throw new CPRException(Response.Status.NOT_FOUND,"No teams found.");
        for (Document teamDocument : teamDocuments) {
            String teamDocumentCourseID = teamDocument.getString("course_id");
            if (request.getCourseID().equals(teamDocumentCourseID)) {
                List<String> students = teamDocument.getList("team_members", String.class);
                if (students.contains(request.getStudentID())) return teamDocument.getString("team_id");
            }
        }
        throw new CPRException(Response.Status.NOT_FOUND,"Student not found.");
    }
}