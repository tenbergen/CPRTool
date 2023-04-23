package edu.oswego.cs.resources;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import edu.oswego.cs.database.daos.FileDAO;
import edu.oswego.cs.database.PeerReviewAssignmentInterface;
import edu.oswego.cs.distribution.AssignmentDistribution;
import org.bson.Document;
import org.bson.types.Binary;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Path("assignments")
@DenyAll
public class PeerReviewAssignmentResource {

    /**
     * Endpoint to start the round-robin team assignments
     *
     * @param courseID     Course the peer review is being assigned in
     * @param assignmentID The assignment that the peer review is for
     * @param count        The number of teams a team can be assigned
     * @return Response if the teams were assigned
     * @throws Exception If count > the number of teams in the course.
     */
    @GET
    @RolesAllowed("professor")
    @Path("{courseID}/{assignmentID}/assign/{countToReview}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignTeams(@PathParam("courseID") String courseID,
                                @PathParam("assignmentID") int assignmentID,
                                @PathParam("countToReview") int count) throws Exception {
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
        peerReviewAssignmentInterface.addAllTeams(teamNames, courseID, assignmentID, count);
        peerReviewAssignmentInterface.addDistroToSubmissions(assignedTeams, courseID, assignmentID);
        return Response.status(Response.Status.OK).entity(teamAssignmentsDocument).build();
    }

    /**
     * Endpoint to get all the teams for a given assignment.
     *
     * @param courseID     The course for the assignment
     * @param assignmentID The assignment that is being looked up
     * @return A list of teams that exist for a given assignment
     */
    @GET
    @RolesAllowed("professor")
    @Path("{courseID}/{assignmentID}/allTeams")
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignTeams(@PathParam("courseID") String courseID,
                                @PathParam("assignmentID") int assignmentID) {
        PeerReviewAssignmentInterface peerReviewAssignmentInterface = new PeerReviewAssignmentInterface();
        return Response.status(Response.Status.OK).entity(peerReviewAssignmentInterface.getTeams(courseID, assignmentID)).build();
    }

    /**
     * Endpoint to get all the team grades.
     *
     * @param courseID     The course for the assignment
     * @param assignmentID The assignment that is being looked up
     * @param team_name    The team name for the team looked up
     * @return A list of teams that graded the input team and the grades given
     */
    @GET
    @RolesAllowed("professor")
    @Path("{courseID}/{assignmentID}/{teamName}/getTeamGrades")
    @Produces(MediaType.APPLICATION_JSON)
    public Response teamName(@PathParam("courseID") String courseID,
                             @PathParam("assignmentID") int assignmentID,
                             @PathParam("teamName") String team_name) {
        PeerReviewAssignmentInterface peerReviewAssignmentInterface = new PeerReviewAssignmentInterface();
        return Response.status(Response.Status.OK).entity(peerReviewAssignmentInterface.redoneGetTeamGrades(courseID, assignmentID, team_name)).build();//GetTeamGrades(courseID, assignmentID, team_name)).build();
    }

    /**
     * Endpoint to update grade for a team.
     *
     * @param courseID     The course for the assignment
     * @param assignmentID The assignment that is being looked up
     * @param team_name    The team name for the team looked up
     * @param grade        the grade to be updated for the team
     * @return the team that was edited
     */
    @POST
    @RolesAllowed("professor")
    @Path("{courseID}/{assignmentID}/{teamName}/{grade}/professor_update")
    @Produces(MediaType.APPLICATION_JSON)
    public Response professorUpdate(@PathParam("courseID") String courseID,
                                    @PathParam("assignmentID") int assignmentID,
                                    @PathParam("teamName") String team_name,
                                    @PathParam("grade") int grade) {
        PeerReviewAssignmentInterface peerReviewAssignmentInterface = new PeerReviewAssignmentInterface();
        return Response.status(Response.Status.OK).entity(peerReviewAssignmentInterface.professorUpdate(courseID, assignmentID, team_name, grade)).build();
    }

    @GET
    @RolesAllowed("professor")
    @Path("{courseID}/{assignmentID}/{teamID}/grade")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeamGrade(@PathParam("courseID") String courseID,
                                 @PathParam("assignmentID") int assignmentID,
                                 @PathParam("teamID") String teamID) {
        PeerReviewAssignmentInterface peerReviewAssignmentInterface = new PeerReviewAssignmentInterface();
        return Response.status(Response.Status.OK).entity(peerReviewAssignmentInterface.getGradeForTeam(courseID, assignmentID, teamID)).build();
    }

    /**
     * Endpoint to get all student grades.
     *
     * @param courseID     The course for the assignment
     * @param assignmentID The assignment that is being looked up
     * @param studentID    The team name for the team looked up
     * @return the student that was edited
     */
    @GET
    @RolesAllowed("professor")
    @Path("{courseID}/{assignmentID}/{teamID}/{studentID}/grade")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeamGrade(@PathParam("courseID") String courseID,
                                 @PathParam("assignmentID") int assignmentID,
                                 @PathParam("teamID") String teamID,
                                 @PathParam("studentID") String studentID) {
        PeerReviewAssignmentInterface peerReviewAssignmentInterface = new PeerReviewAssignmentInterface();
        return Response.status(Response.Status.OK).entity(peerReviewAssignmentInterface.getGradeForStudent(courseID, assignmentID, teamID, studentID)).build();
    }
    /**
     * Endpoint to get matrix of grades and outliers
     *
     * @param courseID     The course for the assignment
     * @return the matrix of grades with outliers as boolean value
     */
    @GET
    @RolesAllowed("professor")
    @Path("{courseID}/outlierDetectionOverTime")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMatrixOfGrades(@PathParam("courseID") String courseID){
        //grab instance of peer review interface
        PeerReviewAssignmentInterface peerReviewAssignmentInterface = new PeerReviewAssignmentInterface();
        //the function to grab all of the
        Document matrixOfGrades = peerReviewAssignmentInterface.getAllPotentialOutliersAndGrades(courseID);//allPotentialOutliers(courseID);

        if(peerReviewAssignmentInterface == null || matrixOfGrades == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error getting all the potential outliers").build();
        return Response.status(Response.Status.OK).entity(matrixOfGrades).build();
    }




    /**
     * Endpoint to get matrix of grades and outliers
     *
     * @param courseID     The course for the assignment
     * @param assignmentID The assignment that is being looked up
     * @return the matrix of grades with outliers as boolean value
     */
    @GET
    @RolesAllowed("professor")
    @Path("{courseID}/{assignmentID}/matrix")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMatrixOfGrades(@PathParam("courseID") String courseID,
                                      @PathParam("assignmentID") int assignmentID){
        //grab instance of peer review interface
        PeerReviewAssignmentInterface peerReviewAssignmentInterface = new PeerReviewAssignmentInterface();
        //the function to grab all of the
        Document matrixOfGrades = peerReviewAssignmentInterface.getMatrixOfOutlierAndGrades(courseID, assignmentID);//getMatrixOfGrades(courseID, assignmentID);

        if(peerReviewAssignmentInterface == null || matrixOfGrades == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error getting matrix of grades").build();
        return Response.status(Response.Status.OK).entity(matrixOfGrades).build();
    }



    /**
     * Endpoint to get the teams that a team was assigned to peer review.
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
    public Response getTeamAssignments(@PathParam("courseID") String courseID,
                                       @PathParam("assignmentID") int assignmentID,
                                       @PathParam("teamName") String teamName) {
        PeerReviewAssignmentInterface peerReviewAssignmentInterface = new PeerReviewAssignmentInterface();
        List<String> assignedTeams = peerReviewAssignmentInterface.getAssignedTeams(courseID, assignmentID, teamName);
        if (assignedTeams == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Team name does not exist.").build();
        return Response.status(Response.Status.OK).entity(assignedTeams).build();
    }

    /**
     * Endpoint to get the teams that were assigned to as specific target team to peer-review
     *
     * @param courseID course in which team exists
     * @param assignmentID the peer review assignment
     * @param teamName the target team
     * @return all teams assigned to review the target team for this peer review assignment
     */
    @GET
    @RolesAllowed({"professor", "student"})
    @Path("{courseID}/{assignmentID}/peer-review-team-reviewers/{teamName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeamReviewers(@PathParam("courseID") String courseID,
                                       @PathParam("assignmentID") int assignmentID,
                                       @PathParam("teamName") String teamName) {
        PeerReviewAssignmentInterface peerReviewAssignmentInterface = new PeerReviewAssignmentInterface();
        List<String> reviewTeams = peerReviewAssignmentInterface.getReviewTeams(courseID, assignmentID, teamName);
        if (reviewTeams == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Team name does not exist.").build();
        return Response.status(Response.Status.OK).entity(reviewTeams).build();
    }

    //deprecated so not even going to bother

    /**
     * An endpoint for a team to download another team's assignment submission to be peer reviewed.
     *
     * @param courseID     The course id that assigned the peer review.
     * @param assignmentID The assignment that the peer review is for
     * @param teamName     The team that submitted an assignment that is need of a peer review
     * @return A file that contains the team's submitted assignment
     * @throws WebApplicationException A endpoint parameter error
     */
    @Deprecated
    @GET
    @RolesAllowed({"professor", "student"})
    @Path("{courseID}/{assignmentID}/{teamName}/download")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response downloadOtherTeamsAssignment(@PathParam("courseID") String courseID,
                                                 @PathParam("assignmentID") int assignmentID,
                                                 @PathParam("teamName") String teamName) {
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
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadPeerReview(List<IAttachment> attachments,
                                     @PathParam("courseID") String courseID,
                                     @PathParam("assignmentID") int assignmentID,
                                     @PathParam("srcTeamName") String srcTeamName,
                                     @PathParam("destTeamName") String destTeamName,
                                     @PathParam("grade") int grade) throws IOException {
        PeerReviewAssignmentInterface peerReviewAssignmentInterface = new PeerReviewAssignmentInterface();
        for (IAttachment attachment : attachments) {
            if (attachment == null) continue;
            String fileName = attachment.getDataHandler().getName();
            if (fileName.endsWith("pdf") || fileName.endsWith("docx")) {
                fileName = "from-" + srcTeamName + "-to-" + destTeamName + fileName.substring(fileName.indexOf("."));
                peerReviewAssignmentInterface.addPeerReviewSubmission(courseID, assignmentID, srcTeamName, destTeamName, fileName, grade, attachment.getDataHandler().getInputStream());
            } else return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();
        }
        return Response.status(Response.Status.OK).entity("Successfully uploaded peer review.").build();
    }

    //change

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
    @GET
    @RolesAllowed({"professor", "student"})
    @Path("{courseID}/{assignmentID}/{srcTeamName}/{destTeamName}/download")
    @Produces({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_OCTET_STREAM})
    public Response downloadFinishedTeamPeerReview(@PathParam("courseID") String courseID,
                                                   @PathParam("assignmentID") int assignmentID,
                                                   @PathParam("srcTeamName") String srcTeamName,
                                                   @PathParam("destTeamName") String destTeamName) {
        PeerReviewAssignmentInterface peerReviewAssignmentInterface = new PeerReviewAssignmentInterface();
        // check if the peer review due date is past
        // if not then return a response saying peer review is not ready
        String fileName = peerReviewAssignmentInterface.downloadFinishedPeerReviewName(courseID, assignmentID, srcTeamName, destTeamName);
        Binary fileData = peerReviewAssignmentInterface.downloadFinishedPeerReview(courseID, assignmentID, srcTeamName, destTeamName);

        Response.ResponseBuilder response = Response.ok(Base64.getEncoder().encode(fileData.getData()));
        response.header("Content-Disposition", "attachment; filename=" + "peer-review-for-" + destTeamName + fileName.substring(fileName.indexOf(".")));
        return response.build();
    }

    @GET
    @RolesAllowed({"student", "professor"})
    @Path("{courseID}/{assignmentID}/reviews-by/{studentID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response viewReviewsByUser(@PathParam("courseID") String courseID,
                                      @PathParam("assignmentID") int assignmentID,
                                      @PathParam("studentID") String studentID) {
        List<Document> documents = new PeerReviewAssignmentInterface().getAssignmentsReviewedByUser(courseID, studentID);
        return Response.status(Response.Status.OK).entity(documents).build();
    }

    @GET
    @RolesAllowed({"student", "professor"})
    @Path("{courseID}/{assignmentID}/reviews-of/{studentID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response viewReviewsOfUser(@PathParam("courseID") String courseID,
                                      @PathParam("assignmentID") int assignmentID,
                                      @PathParam("studentID") String studentID) {
        List<Document> documents = new PeerReviewAssignmentInterface().getUsersReviewedAssignment(courseID, assignmentID, studentID);
        return Response.status(Response.Status.OK).entity(documents).build();
    }

    @GET
    @RolesAllowed({"student", "professor"})
    @Path("{courseID}/reviews-of/{studentID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response viewReviewsOfUser(@PathParam("courseID") String courseID,
                                      @PathParam("studentID") String studentID) {
        List<Document> documents = new PeerReviewAssignmentInterface().getUsersReviewedAssignment(courseID, studentID);
        return Response.status(Response.Status.OK).entity(documents).build();
    }

    @PUT
    @RolesAllowed("Professor")
    @Path("{course_id}/{assignment_id}/finalize-grades")
    public Response finalizeGrades(@PathParam("course_id") String courseID,
                                   @PathParam("assignment_id") int assignmentID) {
        new PeerReviewAssignmentInterface().makeFinalGrades(courseID, assignmentID);
        return Response.status(Response.Status.OK).entity("Peer reviews have been averaged to make final grades.").build();
    }

    /**
     * Endpoint to get a list of submissions for which a given team has been assigned to peer-review
     *
     * @param courseID course in which this is happening
     * @param assignmentID assignment for which these peer reviews are assigned
     * @param teamName team assigned to review these submissions
     * @return list of assignment submissions assigned to the given team
     */
    @GET
    @RolesAllowed({"professor", "student"})
    @Path("{courseID}/{assignmentID}/peer-reviews-given/{teamName}")
    public Response peerReviewsGiven(@PathParam("courseID") String courseID,
                                     @PathParam("assignmentID") int assignmentID,
                                     @PathParam("teamName") String teamName){
        List<Document> submissions = new PeerReviewAssignmentInterface().peerReviewsGiven(courseID, assignmentID, teamName);
        if(submissions == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("That assignment has not been distributed for peer reviews yet!").build();
        }
        return Response.status(Response.Status.OK).entity(submissions).build();
    }

    /**
     * Endpoint to get a list of peer review submissions made for a specified team's assignment submission
     *
     * @param courseID course in which this is happening
     * @param assignmentID assignment for which these peer reviews are assigned
     * @param teamName team for whom these reviews are made
     * @return list of peer-review submissions assigned for the specified team's submission
     */
    @GET
    @RolesAllowed({"professor", "student"})
    @Path("{courseID}/{assignmentID}/peer-reviews-received/{teamName}")
    public Response peerReviewsReceived(@PathParam("courseID") String courseID,
                                     @PathParam("assignmentID") int assignmentID,
                                     @PathParam("teamName") String teamName){
        List<Document> submissions = new PeerReviewAssignmentInterface()
                .peerReviewsReceived(courseID, assignmentID, teamName);
        if(submissions == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("That assignment has not been distributed for peer reviews yet!").build();
        }
        return Response.status(Response.Status.OK).entity(submissions).build();
    }
}

