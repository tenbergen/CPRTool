package edu.oswego.cs.resources;

import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.daos.StudentDAO;
import edu.oswego.cs.database.CourseInterface;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("professor")
public class CoursesViewerResources {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses")
    public Response viewAllCourses() {
        try {
            List<CourseDAO> courses = new CourseInterface().getAllCourses();
            return Response.status(Response.Status.OK).entity(courses).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to fetch courses.").build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/{courseID}")
    public Response viewCourse(@PathParam("courseID") String courseID) {
        try {
            CourseDAO courseDAO = new CourseInterface().getCourse(courseID);
            return Response.status(Response.Status.OK).entity(courseDAO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to fetch course.").build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("students")
    public Response viewAllStudents() {
        try {
            List<StudentDAO> students = new CourseInterface().getAllStudents();
            return Response.status(Response.Status.OK).entity(students).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to fetch students.").build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("students/{studentID}")
    public Response viewStudent(@PathParam("studentID") String studentID) {
        try {
            StudentDAO studentDAO = new CourseInterface().getStudent(studentID);
            return Response.status(Response.Status.OK).entity(studentDAO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to fetch student.").build();
        }
    }
}