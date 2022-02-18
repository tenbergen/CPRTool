package edu.oswego.cs.resources;

import edu.oswego.cs.daos.CourseDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/professor")
public class CourseManager {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/courses/create/{courseName}/{section}")
    public Response createCourse(@PathParam("id") int id,
                                 @PathParam("courseName") String courseName, @PathParam("section") int courseSection) {
        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/courses/delete/{courseName}/{section}")
    public Response deleteCourse(@PathParam("id") int id,
                                 @PathParam("courseName") String courseName, @PathParam("section") int courseSection) {
        return Response.status(Response.Status.OK).build();

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/courses/add")


}
