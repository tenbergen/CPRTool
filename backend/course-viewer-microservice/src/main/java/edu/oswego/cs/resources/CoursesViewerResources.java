package edu.oswego.cs.resources;

import edu.oswego.cs.database.CourseInterface;
import org.bson.Document;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("professor")
//@DenyAll
public class CoursesViewerResources {
    @GET
    @RolesAllowed("professor")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses")
    public Response viewAllCourses() {
        List<Document> courses = new CourseInterface().getAllCourses();
        return Response.status(Response.Status.OK).entity(courses).build();
    }

    @GET
    @RolesAllowed("professor")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/{courseID}")
    public Response viewCourse(@PathParam("courseID") String courseID) {
        Document document = new CourseInterface().getCourse(courseID);
        return Response.status(Response.Status.OK).entity(document).build();
    }

    @GET
    @RolesAllowed({"professor","student"})
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{studentID}/courses")
    public Response viewStudentCourses(@PathParam("studentID") String studentID) {
        List<Document> courses = new CourseInterface().getStudentCourses(studentID);
        return Response.status(Response.Status.OK).entity(courses).build();
    }

    @GET
    @RolesAllowed("professor")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("students")
    public Response viewAllStudents() {
        List<Document> students = new CourseInterface().getAllStudents();
        return Response.status(Response.Status.OK).entity(students).build();
    }

    @GET
    @RolesAllowed("professor")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("students/{studentID}")
    public Response viewStudent(@PathParam("studentID") String studentID) {
        Document document = new CourseInterface().getStudent(studentID);
        return Response.status(Response.Status.OK).entity(document).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/{courseID}/students")
    public Response viewStudentsInCourse(@PathParam("courseID") String courseID) {
        List<Document> studentDocuments = new CourseInterface().getStudentsInCourse(courseID);
        return Response.status(Response.Status.OK).entity(studentDocuments).build();
    }
}