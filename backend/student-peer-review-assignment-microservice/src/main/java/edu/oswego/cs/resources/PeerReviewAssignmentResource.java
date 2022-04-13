package edu.oswego.cs.resources;

import edu.oswego.cs.daos.FileDAO;
import edu.oswego.cs.database.PeerReviewAssignmentInterface;
import edu.oswego.cs.distribution.AssignmentDistribution;
import org.bson.Document;

import javax.print.attribute.standard.Media;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.List;
import java.util.Map;

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

        //List<String> teamNames = peerReviewAssignmentInterface.getCourseTeams(courseID);
        List<String> teamNames = peerReviewAssignmentInterface.getCourseStudentIDs(courseID);
        Map<String, List<String>> assignedTeams;
        try {
             assignedTeams = AssignmentDistribution.distribute(teamNames, count);
        } catch (IndexOutOfBoundsException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Number of reviews peer team is greater than the number of teams in the course.").build();
        }

        FileDAO.zipPeerReview(assignedTeams, courseID, assignmentID);

        return Response.status(Response.Status.OK).build();
        //Document assignedTeamDocument = peerReviewAssignmentInterface.addAssignedTeams(assignedTeams, courseID, assignmentID);
        //return Response.status(Response.Status.OK).entity(assignedTeamDocument).build();
    }

    @GET
    @Path("{courseID}/{assignmentID}/{teamName}/download")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response downloadPeerReview(
            @PathParam("courseID") String courseID,
            @PathParam("assignmentID") int assignmentID,
            @PathParam("teamName") String teamName
    ) {

        File file = new File(FileDAO.peer_review_path + courseID + "/"+assignmentID+"/for-"+teamName.concat(".zip"));

        Response.ResponseBuilder response = Response.ok(file);
        response.header("Content-Disposition", "attachment; filename=" + file.getName());
        return response.build();
    }



}
