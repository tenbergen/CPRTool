package edu.oswego.cs.resources;

import com.ibm.websphere.jaxrs20.multipart.IMultipartBody;
import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.daos.FileDAO;
import edu.oswego.cs.daos.StudentDAO;
import edu.oswego.cs.database.CourseInterface;
import edu.oswego.cs.util.CSVUtil;

import javax.ws.rs.*;
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


    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/{courseID}/delete")
    public Response deleteCourse(@PathParam("courseID") String courseID) {
        new CourseInterface().removeCourse(courseID);
        return Response.status(Response.Status.OK).entity("Course successfully deleted.").build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/update")
    public Response updateCourse(CourseDAO course) {
        String courseID = new CourseInterface().updateCourse(course);
        return Response.status(Response.Status.OK).entity(courseID).build();
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
    public Response addStudentByCSVFile(IMultipartBody body) {
        FileDAO fileDAO;
        try {
            fileDAO = FileDAO.FileFactory(body.getAllAttachments());
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("File corrupted. Try again.").build();
        }
        try {
            System.out.println(fileDAO.getFilename().substring(0, fileDAO.getFilename().length() - 4));
            new CourseInterface().addStudentsFromCSV(fileDAO);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to add students.").build();
        }
        return Response.status(Response.Status.OK).entity("Student(s) successfully added.").build();
    }
}