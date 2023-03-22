package edu.oswego.cs.resources;

import edu.oswego.cs.daos.AssignmentDAO;
import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.database.AssignmentInterface;
import edu.oswego.cs.services.EmailService;
import edu.oswego.cs.util.DeadlineTracker;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;

@Path("send")
@DenyAll
public class EmailResources {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{courseID}/{assignmentID}/assignment-created")
    @RolesAllowed("professor")
    public Response assignmentCreatedEmail(@Context SecurityContext securityContext,
                                           @PathParam("courseID") String courseID,
                                           @PathParam("assignmentID") int assignmentID) throws IOException{
        new EmailService().assignmentCreatedEmail(securityContext, courseID, assignmentID);
        DeadlineTracker.addAssignment(new AssignmentInterface().getSpecifiedAssignment(courseID, assignmentID));
        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{courseID}/{teamID}/{assignmentID}/assignment-submitted")
    @RolesAllowed({"professor", "student"})
    public Response assignmentSubmittedEmail(@PathParam("courseID") String courseID,
                                                 @PathParam("teamID") String teamID,
                                                 @PathParam("assignmentID") int assignmentID) throws IOException {
        //both allAssignmentsSubmitted and assignmentSubmitted can fire from the same submission so there's no reason
        //not to bundle them in the same HTTP method
        new EmailService().allAssignmentsSubmittedEmail(courseID, assignmentID);
        new EmailService().assignmentSubmittedEmail(courseID, teamID, assignmentID);
        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{courseID}/{teamID}/{assignmentID}/peer-review-assigned")
    @RolesAllowed("professor")
    public Response peerReviewAssignedEmail(@PathParam("courseID") String courseID,
                                             @PathParam("teamID") String teamID,
                                             @PathParam("assignmentID") int assignmentID) throws IOException {
        new EmailService().peerReviewAssignedEmail(courseID, teamID, assignmentID);
        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{courseID}/{teamID}/{assignmentID}/peer-review-submitted")
    @RolesAllowed({"professor", "student"})
    public Response peerReviewSubmittedEmail(@Context SecurityContext securityContext,
                                             @PathParam("courseID") String courseID,
                                             @PathParam("teamID") String teamID,
                                             @PathParam("assignmentID") int assignmentID) throws IOException {
        //both allPeerReviewsSubmitted and assignmentSubmitted can fire from the same submission so there's no reason
        //not to bundle them in the same HTTP method
        new EmailService().allPeerReviewsSubmittedEmail(courseID, assignmentID);
        new EmailService().peerReviewSubmittedEmail(securityContext, courseID, teamID, assignmentID);
        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{courseID}/{teamID}/{assignmentID}/graded")
    @RolesAllowed("professor")
    public Response gradeReceivedEmail(@PathParam("courseID") String courseID,
                                       @PathParam("teamID") String teamID,
                                       @PathParam("assignmentID") int assignmentID) throws IOException {
        new EmailService().gradeReceivedEmail(courseID, assignmentID, teamID);
        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{courseID}/{teamID}/{assignmentID}/outlier")
    @RolesAllowed({"professor","student"})
    public Response outlierDetectedEmail(@PathParam("courseID") String courseID,
                                         @PathParam("teamID") String teamID,
                                         @PathParam("assignmentID") int assignmentID) throws IOException {
        new EmailService().outlierDetectedEmail(courseID, teamID, assignmentID);
        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{courseID}/{teamID}/{assignmentID}/profanity")
    @RolesAllowed({"professor","student"})
    public Response profanityEmail(@PathParam("courseID") String courseID,
                                         @PathParam("teamID") String teamID,
                                         @PathParam("assignmentID") int assignmentID) throws IOException {
        new EmailService().profanityEmail(courseID, teamID, assignmentID);
        return Response.status(Response.Status.OK).build();
    }
}
