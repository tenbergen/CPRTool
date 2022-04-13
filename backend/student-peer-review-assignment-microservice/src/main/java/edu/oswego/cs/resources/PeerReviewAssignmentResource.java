package edu.oswego.cs.resources;

import edu.oswego.cs.database.PeerReviewAssignmentInterface;
import edu.oswego.cs.distribution.AssignmentDistribution;
import org.bson.Document;

import javax.json.Json;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("assignments")
public class PeerReviewAssignmentResource {

    @GET
    @Path("{courseID}/{assignmentID}/assign/{count_to_review}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignTeams(
            @PathParam("courseID") String courseID,
            @PathParam("assignmentID") int assignmentID,
            @PathParam("count_to_review") int count) throws Exception {
        PeerReviewAssignmentInterface peerReviewAssignmentInterface = new PeerReviewAssignmentInterface();

        List<String> teamNames = peerReviewAssignmentInterface.getCourseTeams(courseID);
        Map<String, List<String>> assignedTeams;
        try {
             assignedTeams = AssignmentDistribution.distribute(teamNames, count);
        } catch (IndexOutOfBoundsException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Number of reviews peer team is greater than the number of teams in the course.").build();
        }

        Document assignedTeamDocument = peerReviewAssignmentInterface.addAssignedTeams(assignedTeams, courseID, assignmentID);
        return Response.status(Response.Status.OK).entity(assignedTeamDocument).build();
    }
    @GET
    @Path("test")
    public void filePath(){
        Set<String>nameset = new HashSet<>();
        nameset.add("teamA");
        nameset.add("teamB");
        nameset.add("teamC");
        System.out.println(new PeerReviewAssignmentInterface().makeFileStructure(nameset,"csc480",0));
    }

}
