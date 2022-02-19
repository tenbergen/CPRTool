package edu.oswego.cs.resources;

import edu.oswego.cs.daos.CourseDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("professor")
public class CourseManager {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/create/")
    public Response createCourse(CourseDAO course) {
        return Response.status(Response.Status.CREATED).entity(course.toString()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/delete/")
    public Response deleteCourse(CourseDAO course) {
        return Response.status(Response.Status.OK).entity(course.toString()).build();
    }

}