package edu.oswego.cs.resources;

import edu.oswego.cs.daos.AssignmentDAO;
import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.services.EmailService;

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
        return Response.status(Response.Status.CREATED).entity("Email Successfully Sent.").build();
    }
}
