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
    @RolesAllowed({"professor","student"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("testemail")
    public Response viewStudentCourses() {
        emailService.assignmentCreatedEmail(new CourseDAO(), new AssignmentDAO());
        return Response.status(Response.Status.OK).build();
    }
}
