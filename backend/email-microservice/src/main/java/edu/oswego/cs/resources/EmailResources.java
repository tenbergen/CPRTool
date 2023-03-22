package edu.oswego.cs.resources;

import edu.oswego.cs.database.AssignmentInterface;
import edu.oswego.cs.database.CourseInterface;
import edu.oswego.cs.services.EmailService;
import edu.oswego.cs.util.DeadlineTracker;
import org.bson.Document;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.util.List;


/**
 * Resource class for the email microservice. All paths for the methods in this class start with "email/send".
 * All methods in this class throw IOException because they call methods that read the email templates from an HTML file.
 * If this microservice functions correctly these methods should never throw IOException.
 */
@Path("send")
@DenyAll
public class EmailResources {

    /**
     * Called from CreateAssignmentPage.js in the frontend when the professor finished creating the assignment.
     *
     * @param securityContext context from which the professor id can be obtained
     * @param courseID course in which the assignment has been made
     * @param assignmentID id of the newly created assignment
     * @return OK Response
     */
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

    /**
     * Called from RegularAssignmentComponent.js after the student clicks the submit button for an assignment
     *
     * @param courseID ID of course in which assignment is submitted
     * @param teamID team that submitted assignment
     * @param assignmentID ID of assignment that got submitted
     * @return OK Response
     */
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

    /**
     * Called from ProfessorAllSubmissionsComponent.js where the peer reviews get distributed. Since all teams get
     * the peer reviews at the same time, we can send the email to the entire class.
     *
     * @param securityContext context for getting all students in the course
     * @param courseID ID of course in which the peer review is assigned
     * @param assignmentID ID of assignment for which reviews have been assigned
     * @return OK Response
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{courseID}/{assignmentID}/peer-review-assigned")
    @RolesAllowed("professor")
    public Response peerReviewAssignedEmail(@Context SecurityContext securityContext,
                                            @PathParam("courseID") String courseID,
                                            @PathParam("assignmentID") int assignmentID) throws IOException {
        new EmailService().peerReviewAssignedEmail(securityContext, courseID, assignmentID);
        return Response.status(Response.Status.OK).build();
    }

    /**
     * Called from StudentPeerReviewComponent.js after the student clicks submit.
     *
     * @param securityContext context from which we can get the student who submitted
     * @param courseID id of the course for which the review was submitted
     * @param teamID id of the team being reviewed by the submitter
     * @param assignmentID id of the assignment for which the peer review is
     * @return OK Response
     */
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
        //makeshift workaround for current grading situation. Once the professor can manually finalize the grades, please
        //migrate functionality to the gradeReceivedEmail method below and remove this entire if statement.
        if(new EmailService().allPRSubmitted(new AssignmentInterface().getSpecifiedAssignment(courseID, assignmentID))){
            List<Document> teams = new CourseInterface().getTeamsInCourse(courseID);
            for(Document team : teams) {
                new EmailService().gradeReceivedEmail(courseID, assignmentID, team.getString("team_id"));
            }
        }
        return Response.status(Response.Status.OK).build();
    }

    /**
     * This method is not called anywhere in the frontend right now because grades are finalized as soon as all peer
     * reviews are submitted, therefore peerReviewSubmittedEmail is used instead. Once more professor privileges are
     * implemented this method should be invoked and the EmailService.gradeReceivedEmail() method call should be
     * removed from peerReviewSubmittedEmail above.
     *
     * @param courseID id of course for which the grade is given
     * @param teamID id of team receiving grade
     * @param assignmentID id of assignment being graded
     * @return OK Response
     */
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

    /**
     * FRONTEND CALL YET TO BE IMPLEMENTED; awaiting implementation of frontend matrix
     *
     * @param courseID id of course in which outlier grade was given
     * @param teamID id of team that submitted the outlier grade
     * @param assignmentID id of assignment for which the outlier grade was given
     * @return OK Response
     */
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
}
