package edu.oswego.cs.resources;

import edu.oswego.cs.daos.AssignmentDAO;
import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.services.EmailService;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("email")
@DenyAll
public class EmailResources {

    EmailService emailService;
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("email/testemail")
    @RolesAllowed({"professor","student"})
    public Response testEmail() throws IOException {
        emailService.assignmentCreatedEmail(new CourseDAO(), new AssignmentDAO());
        return Response.status(Response.Status.CREATED).entity("Email Successfully Sent.").build();
    }
}
