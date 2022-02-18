package edu.oswego.cs.resources;

import edu.oswego.cs.daos.CourseDAO;

import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/professor")
public class CourseManager {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{professorName}/courses/create/{courseName}/{section}")
    public Response createCourse( @PathParam("professorName") String professorName, @PathParam("courseName") String courseName, @PathParam("section") int courseSection) {
        CourseDAO course = new CourseDAO(courseName, courseSection);
        return Response.status(Response.Status.CREATED).entity(course.toString()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{professorName}/courses/delete/{courseName}/{section}")
    public Response deleteCourse(@PathParam("professorName") String professorName,
                                 @PathParam("courseName") String courseName, @PathParam("section") int courseSection) {
        return Response.status(Response.Status.OK).build();
    }
}