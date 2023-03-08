package edu.oswego.cs.resources;

import edu.oswego.cs.daos.AssignmentDAO;
import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.services.EmailService;
import org.bson.Document;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class EmailResources {

    EmailService emailService;
    @POST
    @Path("/testemail")
    public Response testEmail() {
        emailService.assignmentCreatedEmail(new CourseDAO(), new AssignmentDAO());
        return Response.status(Response.Status.OK).build();
    }
}
