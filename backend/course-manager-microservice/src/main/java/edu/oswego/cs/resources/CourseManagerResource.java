package edu.oswego.cs.resources;

import com.ibm.websphere.jaxrs20.multipart.IMultipartBody;
import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.daos.FileDAO;
import edu.oswego.cs.daos.StudentDAO;
import edu.oswego.cs.database.CourseInterface;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("professor")
@DenyAll
public class CourseManagerResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/create")
    @RolesAllowed("professor")
    public Response createCourse(@Context SecurityContext securityContext, CourseDAO course) {
        new CourseInterface().addCourse(securityContext, course);
        return Response.status(Response.Status.OK).entity("Course successfully added.").build();
    }


    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/{courseID}/delete")
    @RolesAllowed("professor")
    public Response deleteCourse(@Context SecurityContext securityContext, @PathParam("courseID") String courseID) {
        new CourseInterface().removeCourse(securityContext, courseID);
        return Response.status(Response.Status.OK).entity("Course successfully deleted.").build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/update")
    @RolesAllowed("professor")
    public Response updateCourse(@Context SecurityContext securityContext, CourseDAO course) {
        String courseID = new CourseInterface().updateCourse(securityContext, course);
        return Response.status(Response.Status.OK).entity(courseID).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("professor")
    @Path("courses/{courseID}/students/{studentInfo}/add")
    public Response addStudent(
            @Context SecurityContext securityContext,
            @PathParam("courseID") String courseID,
            @PathParam("studentInfo") String studentInfo) {
        String[] parsedStudentInfo = studentInfo.split("-");
        if (parsedStudentInfo.length < 3)
            return Response.status(Response.Status.BAD_REQUEST).entity("Add student field was not filled out properly.").build();
        StudentDAO studentDAO = new StudentDAO(parsedStudentInfo[0], parsedStudentInfo[1], parsedStudentInfo[2]);
        new CourseInterface().addStudent(securityContext, studentDAO, courseID);
        return Response.status(Response.Status.OK).entity("Student successfully added.").build();
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("professor")
    @Path("courses/{courseID}/students/{studentID}/delete")
    public Response deleteStudent(
            @PathParam("courseID") String courseID,
            @PathParam("studentID") String studentID) {

        new CourseInterface().removeStudent(studentID, courseID);
        return Response.status(Response.Status.OK).entity("Student successfully removed.").build();
    }

    @POST
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/student/mass-add")
    @RolesAllowed("professor")
    public Response addStudentByCSVFile(IMultipartBody body) {
        FileDAO fileDAO;
        try {
            fileDAO = FileDAO.FileFactory(body.getAllAttachments());
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("File corrupted. Try again.").build();
        }
        try {
            new CourseInterface().addStudentsFromCSV(fileDAO);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to add students.").build();
        }
        return Response.status(Response.Status.OK).entity("Student(s) successfully added.").build();
    }

    @DELETE
    @Path("collection/wipe-off")
    @RolesAllowed("admin")
    public Response collectionWipeOff() {
        new CourseInterface().collectionWipeOff();
        return Response.status(Response.Status.OK).entity("Collection successfully wiped off.").build();
    }
}