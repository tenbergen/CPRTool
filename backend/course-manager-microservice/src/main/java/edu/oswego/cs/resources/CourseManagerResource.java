package edu.oswego.cs.resources;

import com.ibm.websphere.jaxrs20.multipart.IMultipartBody;
import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.daos.FileDAO;
import edu.oswego.cs.daos.StudentDAO;
import edu.oswego.cs.database.CourseInterface;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("professor")
public class CourseManagerResource {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/create")
    public Response createCourse(CourseDAO course) {
        new CourseInterface().addCourse(course);
        return Response.status(Response.Status.OK).entity("Course successfully added.").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/delete")
    public Response deleteCourse(CourseDAO course) {
        new CourseInterface().removeCourse(course);
        return Response.status(Response.Status.OK).entity("Course successfully deleted.").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/student/add")
    public Response addStudent(StudentDAO studentDAO) {
        CourseDAO courseDAO = new CourseDAO(
                studentDAO.abbreviation,
                studentDAO.courseName,
                studentDAO.courseSection,
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
    public Response deleteStudent(StudentDAO studentDAO) {
        CourseDAO courseDAO = new CourseDAO(
                studentDAO.abbreviation,
                studentDAO.courseName,
                studentDAO.courseSection,
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
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to add student.").build();
        }
        return Response.status(Response.Status.OK).build();
    }
}