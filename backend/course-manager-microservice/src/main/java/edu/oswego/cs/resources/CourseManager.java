package edu.oswego.cs.resources;

import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.daos.UserDAO;

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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/student/add")
    public Response addStudent(UserDAO user, CourseDAO course) {
        return Response.status(Response.Status.CREATED).entity(user.toString()).build();


    }


    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/student/delete")
    public Response deleteStudent(UserDAO user,CourseDAO course) {
        return Response.status(Response.Status.OK).entity(user.toString()).build();

    }
}