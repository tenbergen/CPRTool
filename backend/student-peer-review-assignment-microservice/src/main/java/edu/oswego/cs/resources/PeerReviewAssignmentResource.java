package edu.oswego.cs.resources;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import edu.oswego.cs.daos.FileDAO;
import edu.oswego.cs.database.PeerReviewAssignmentInterface;
import edu.oswego.cs.distribution.AssignmentDistribution;
import org.bson.Document;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Path("assignments")
@DenyAll
public class PeerReviewAssignmentResource {

    /**
     * Endpoint to start the round robin team assignments
     *
     * @param courseID     Course the peer review is being assigned in
     * @param assignmentID The assignment that the peer review is for
     * @param count        The number of teams a team can be assigned
     * @return Response if the teams were assigned
     * @throws Exception If count > the number of teams in the course.
     */
    @GET
    @RolesAllowed("professor")
    @Path("{courseID}/{assignmentID}/assign/{count_to_review}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignTeams(
            @PathParam("courseID") String courseID,
            @PathParam("assignmentID") int assignmentID,
            @PathParam("count_to_review") int count
    ) throws Exception {
        PeerReviewAssignmentInterface peerReviewAssignmentInterface = new PeerReviewAssignmentInterface();

        List<String> teamNames = peerReviewAssignmentInterface.getCourseTeams(courseID);
        List<String> finalTeams = peerReviewAssignmentInterface.filterBySubmitted(teamNames, courseID, assignmentID);
        Map<String, List<String>> assignedTeams;
        try {
            assignedTeams = AssignmentDistribution.distribute(finalTeams, count);
        } catch (IndexOutOfBoundsException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Number of reviews peer team is greater than the number of teams in the course.").build();
        }
        Document teamAssignmentsDocument = peerReviewAssignmentInterface.addAssignedTeams(assignedTeams, courseID, assignmentID);
        peerReviewAssignmentInterface.addAllTeams(teamNames, courseID, assignmentID);
        peerReviewAssignmentInterface.addDistroToSubmissions(assignedTeams, courseID, assignmentID);
        return Response.status(Response.Status.OK).entity(teamAssignmentsDocument).build();
    }

    /**
     * Endpoint to get all of the teams for a given assignment
     *
     * @param courseID     The course for the assignment
     * @param assignmentID The assignment that is being looked up
     * @return A list of teams that exist for a given assignment
     */
    @GET
    @RolesAllowed("professor")
    @Path("{courseID}/{assignmentID}/allTeams")
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignTeams(
            @PathParam("courseID") String courseID,
            @PathParam("assignmentID") int assignmentID
    ) throws Exception {
        PeerReviewAssignmentInterface peerReviewAssignmentInterface = new PeerReviewAssignmentInterface();
        return Response.status(Response.Status.OK).entity(peerReviewAssignmentInterface.getTeams(courseID, assignmentID)).build();
    }

    /**
     * Endpoint to get all of the teams for a given assignment
     *
     * @param courseID     The course for the assignment
     * @param assignmentID The assignment that is being looked up
     * @param team_name    The team name for the team looked up
     * @return A list of teams that graded the input team and the grades given
     */
    @GET
    @RolesAllowed("professor")
    @Path("{courseID}/{assignmentID}/{team_name}/getTeamGrades")
    @Produces(MediaType.APPLICATION_JSON)
    public Response team_name(
            @PathParam("courseID") String courseID,
            @PathParam("assignmentID") int assignmentID,
            @PathParam("team_name") String team_name
    ) {
        PeerReviewAssignmentInterface peerReviewAssignmentInterface = new PeerReviewAssignmentInterface();
        return Response.status(Response.Status.OK).entity(peerReviewAssignmentInterface.getTeamGrades(courseID, assignmentID, team_name)).build();
    }

    /**
     * Endpoint to get all of the teams for a given assignment
     *
     * @param courseID     The course for the assignment
     * @param assignmentID The assignment that is being looked up
     * @param team_name    The team name for the team looked up
     * @param grade        the grade to be updated for the team
     * @return the team that was edited
     */
    @POST
    @RolesAllowed("professor")
    @Path("{courseID}/{assignmentID}/{team_name}/{grade}/professor_update")
    @Produces(MediaType.APPLICATION_JSON)
    public Response professorUpdate(
            @PathParam("courseID") String courseID,
            @PathParam("assignmentID") int assignmentID,
            @PathParam("team_name") String team_name,
            @PathParam("grade") int grade
    ) {
        PeerReviewAssignmentInterface peerReviewAssignmentInterface = new PeerReviewAssignmentInterface();
        return Response.status(Response.Status.OK).entity(peerReviewAssignmentInterface.professorUpdate(courseID, assignmentID, team_name, grade)).build();
    }


    /**
     * Endpoint to get the teams that a team was assigned to peer review
     *
     * @param courseID     The course that is peer review is assigned in.
     * @param assignmentID The assignment is the peer review is for.
     * @param teamName     The team name that is requesting what teams to review
     * @return A list of teams name that a team is going to review
     */
    @GET
    @RolesAllowed({"professor", "student"})
    @Path("{courseID}/{assignmentID}/peer-review-team-assignments/{teamName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeamAssignments(
            @PathParam("courseID") String courseID,
            @PathParam("assignmentID") int assignmentID,
            @PathParam("teamName") String teamName
    ) {
        PeerReviewAssignmentInterface peerReviewAssignmentInterface = new PeerReviewAssignmentInterface();
        List<String> assignedTeams = peerReviewAssignmentInterface.getAssignedTeams(courseID, assignmentID, teamName);
        if (assignedTeams == null) return Response.status(Response.Status.BAD_REQUEST).entity("Team name does not exist.").build();
        return Response.status(Response.Status.OK).entity(assignedTeams).build();
    }


    /**
     * An endpoint for a team to download another team's assignment submission to be peer reviewed.
     *
     * @param courseID     The course id that assigned the peer review.
     * @param assignmentID The assignment that the peer review is for
     * @param teamName     The team that submitted an assignment that is need of a peer review
     * @return A file that contains the team's submitted assignment
     * @throws WebApplicationException A endpoint parameter error
     */
    @GET
    @RolesAllowed({"professor", "student"})
    @Path("{courseID}/{assignmentID}/{teamName}/download")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response downloadOtherTeamsAssignment(
            @PathParam("courseID") String courseID,
            @PathParam("assignmentID") int assignmentID,
            @PathParam("teamName") String teamName
    ) {

        File file = new File(FileDAO.peer_review_path + courseID + "/" + assignmentID + "/for-" + teamName.concat(".pdf"));

        Response.ResponseBuilder response = Response.ok(file);
        response.header("Content-Disposition", "attachment; filename=" + file.getName());
        return response.build();
    }

    /**
     * An endpoint for uploading a peer review for another team's uploaded assignment.
     *
     * @param attachments  Multipart-Form data that contains the uploaded file content.
     * @param courseID     The course id that assigned the peer review.
     * @param assignmentID The assignment that the peer review is for
     * @param srcTeamName  The team that is reviewing the assignment
     * @param destTeamName The team that is receiving the peer review
     * @return OK response if the file was successfully added
     * @throws WebApplicationException A endpoint parameter error
     */
    @POST
    @RolesAllowed({"professor", "student"})
    @Path("{courseID}/{assignmentID}/{srcTeamName}/{destTeamName}/{grade}/upload")
    @Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_OCTET_STREAM})
    @Produces({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_OCTET_STREAM})
    public Response uploadPeerReview(
            List<IAttachment> attachments,
            @PathParam("courseID") String courseID,
            @PathParam("assignmentID") int assignmentID,
            @PathParam("srcTeamName") String srcTeamName,
            @PathParam("destTeamName") String destTeamName,
            @PathParam("grade") int grade
    ) throws IOException {
        PeerReviewAssignmentInterface peerReviewAssignmentInterface = new PeerReviewAssignmentInterface();
        for (IAttachment attachment : attachments) {
            if (attachment == null) continue;
            String fileName = attachment.getDataHandler().getName();
            if (!fileName.endsWith("pdf")) return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();
            peerReviewAssignmentInterface.uploadPeerReview(courseID, assignmentID, srcTeamName, destTeamName, attachment);
            fileName = "from-" + srcTeamName + "-to-" + destTeamName + fileName.substring(fileName.indexOf("."));
            peerReviewAssignmentInterface.addPeerReviewSubmission(courseID, assignmentID, srcTeamName, destTeamName, fileName, grade);
        }
        return Response.status(Response.Status.OK).entity("Successfully uploaded peer review.").build();
    }

    /**
     * Endpoint for a team to download the peer reviews that were made for the assignment that the team submitted.
     * The downloaded file from this endpoint is from another teams submitted peer review
     *
     * @param courseID     The course that assigned the peer review
     * @param assignmentID The assignment that the peer review is for
     * @param srcTeamName  The team that reviewed the assignment
     * @param destTeamName The team that is receiving the peer review
     * @return A file that contains the peer review for the team's submitted assignment
     * @throws WebApplicationException A endpoint parameter error
     */
    @Deprecated
    @GET
    @RolesAllowed("student")
    @Path("{courseID}/{assignmentID}/{srcTeamName}/{destTeamName}/download")
    @Produces({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_OCTET_STREAM})
    public Response downloadFinishedTeamPeerReview(
            @PathParam("courseID") String courseID,
            @PathParam("assignmentID") int assignmentID,
            @PathParam("srcTeamName") String srcTeamName,
            @PathParam("destTeamName") String destTeamName) {
        PeerReviewAssignmentInterface peerReviewAssignmentInterface = new PeerReviewAssignmentInterface();

        // check if the peer review due date is past
        // if not then return a response saying peer review is not ready
        File file = peerReviewAssignmentInterface.downloadFinishedPeerReview(courseID, assignmentID, srcTeamName, destTeamName);

        Response.ResponseBuilder response = Response.ok(file);
        response.header("Content-Disposition", "attachment; filename=" + "peer-review-" + file.getName());
        return response.build();
    }

    @GET
    @RolesAllowed({"student", "professor"})
    @Path("{course_id}/{assignment_id}/reviews-by/{student_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response viewReviewsByUser(@PathParam("course_id") String courseID,
                                      @PathParam("assignment_id") int assignmentID,
                                      @PathParam("student_id") String studentID) {
        List<Document> documents = new PeerReviewAssignmentInterface().getAssignmentsReviewedByUser(courseID, studentID);
        return Response.status(Response.Status.OK).entity(documents).build();
    }

    @GET
    @RolesAllowed({"student", "professor"})
    @Path("{course_id}/{assignment_id}/reviews-of/{student_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response viewReviewsOfUser(@PathParam("course_id") String courseID,
                                      @PathParam("assignment_id") int assignmentID,
                                      @PathParam("student_id") String studentID) {
        List<Document> documents = new PeerReviewAssignmentInterface().getUsersReviewedAssignment(courseID, assignmentID, studentID);
        return Response.status(Response.Status.OK).entity(documents).build();
    }

    @GET
    @RolesAllowed({"student", "professor"})
    @Path("{course_id}/reviews-of/{student_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response viewReviewsOfUser(@PathParam("course_id") String courseID,
                                      @PathParam("student_id") String studentID) {
        List<Document> documents = new PeerReviewAssignmentInterface().getUsersReviewedAssignment(courseID, studentID);
        return Response.status(Response.Status.OK).entity(documents).build();
    }
}