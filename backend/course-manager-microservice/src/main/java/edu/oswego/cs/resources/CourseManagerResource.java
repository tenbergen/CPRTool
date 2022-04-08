package edu.oswego.cs.resources;

import com.ibm.websphere.jaxrs20.multipart.IMultipartBody;
import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.daos.FileDAO;
import edu.oswego.cs.daos.StudentDAO;
import edu.oswego.cs.database.CourseInterface;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("professor")
@DenyAll
public class CourseManagerResource {
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/create")
    @RolesAllowed("professor")
    public Response createCourse(CourseDAO course) {
        new CourseInterface().addCourse(course);
        return Response.status(Response.Status.OK).entity("Course successfully added.").build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/delete")
    @RolesAllowed("professor")
    public Response deleteCourse(CourseDAO course) {
        new CourseInterface().removeCourse(course);
        return Response.status(Response.Status.OK).entity("Course successfully deleted.").build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/update")
    @RolesAllowed("professor")
    public Response updateCourse(CourseDAO course) {
        String courseID = new CourseInterface().updateCourse(course);
        return Response.status(Response.Status.OK).entity(courseID).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/student/add")
    @RolesAllowed("professor")
    public Response addStudent(StudentDAO studentDAO) {
        CourseDAO courseDAO = new CourseDAO(
                studentDAO.abbreviation,
                studentDAO.courseName,
                studentDAO.courseSection,
                studentDAO.crn,
                studentDAO.semester,
                studentDAO.year
        );
        new CourseInterface().addStudent(studentDAO.email, courseDAO);
        return Response.status(Response.Status.OK).entity("Student successfully added.").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/student/delete")
    @RolesAllowed("professor")
    public Response deleteStudent(StudentDAO studentDAO) {
        CourseDAO courseDAO = new CourseDAO(
                studentDAO.abbreviation,
                studentDAO.courseName,
                studentDAO.courseSection,
                studentDAO.crn,
                studentDAO.semester,
                studentDAO.year
        );
        new CourseInterface().removeStudent(studentDAO.email, courseDAO);
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
        return Response.status(Response.Status.OK).build();
    }
}