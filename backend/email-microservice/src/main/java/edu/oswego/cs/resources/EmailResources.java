package edu.oswego.cs.resources;

import edu.oswego.cs.daos.AssignmentDAO;
import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.services.EmailService;
import org.bson.Document;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class EmailResources {

    EmailService emailService;
    @GET
    @RolesAllowed({"professor","student"})
    @Produces(MediaType.APPLICATION_JSON)
    @Path("testemail")
    public Response viewStudentCourses() {
        emailService.assignmentCreatedEmail(new CourseDAO(), new AssignmentDAO());
        return Response.status(Response.Status.OK).build();
    }
}
