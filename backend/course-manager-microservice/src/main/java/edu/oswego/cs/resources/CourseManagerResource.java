package edu.oswego.cs.resources;

import edu.oswego.cs.daos.StudentDAO;
import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.database.CourseInterface;



import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("professor")
public class CourseManagerResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/create")
    public Response createCourse(CourseDAO course) {
        try {
            new CourseInterface().addCourse(course);
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/delete")
    public Response deleteCourse(CourseDAO course) throws IOException {
        new CourseInterface().removeCourse(course);
        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/student/add")
    public Response addStudent(StudentDAO studentDAO) throws IOException {
        CourseDAO course = new CourseDAO(studentDAO.courseName, studentDAO.courseSection, studentDAO.semester ,studentDAO.abbreviation);
        System.out.println(course.toString());
        new CourseInterface().addStudent(studentDAO.email, course);
        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/student/delete")
    public Response deleteStudent(StudentDAO studentDAO) throws IOException {
        CourseDAO course = new CourseDAO(studentDAO.courseName, studentDAO.courseSection, studentDAO.semester,studentDAO.abbreviation);
        new CourseInterface().removeStudent(studentDAO.email, course);
        return Response.status(Response.Status.OK).build();
    }

}