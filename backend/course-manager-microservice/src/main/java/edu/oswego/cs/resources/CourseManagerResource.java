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
        try {
            new CourseInterface().addCourse(course);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Course Already Added").build();
        }
        return Response.status(Response.Status.OK).entity("Course Successfully Added").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/delete")
    public Response deleteCourse(CourseDAO course) {
        try {
            new CourseInterface().removeCourse(course);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Course Does Not Exist").build();
        }
        return Response.status(Response.Status.OK).entity("Course Successfully Deleted").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/student/add")
    public Response addStudent(StudentDAO studentDAO) {
        CourseDAO course = new CourseDAO(studentDAO.courseName, studentDAO.courseSection, studentDAO.semester, studentDAO.abbreviation);
        try {
            new CourseInterface().addStudent(studentDAO.email, course);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Student/Course Does Not Exist").build();
        }
        return Response.status(Response.Status.OK).entity("Student Successfully Added").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/student/delete")
    public Response deleteStudent(StudentDAO studentDAO) {
        CourseDAO course = new CourseDAO(studentDAO.courseName, studentDAO.courseSection, studentDAO.semester, studentDAO.abbreviation);
        try {
            new CourseInterface().removeStudent(studentDAO.email, course);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Student/Course Does Not Exist").build();
        }
        return Response.status(Response.Status.OK).entity("Student Successfully Removed").build();
    }

    @POST
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/student/massadd")
    public Response addStudentByCSVFile(IMultipartBody body) throws Exception {
        FileDAO fileDAO;
        try {
            fileDAO = FileDAO.FileFactory(body.getAllAttachments());
        } catch (Exception e) {
            System.out.println("File corruption.");
            return Response.status(Response.Status.BAD_REQUEST).entity("File Corrupted. Try Again").build();
        }
        try {
            new CourseInterface().addStudentsFromCSV(fileDAO);
        } catch (Exception e) {
            System.out.println("Student Not added.");
            return Response.status(Response.Status.BAD_REQUEST).entity("Students Not Successfully Added.").build();
        }
        return Response.status(Response.Status.OK).build();
    }
}