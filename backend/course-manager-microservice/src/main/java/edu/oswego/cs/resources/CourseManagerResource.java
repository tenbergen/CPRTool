package edu.oswego.cs.resources;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import com.ibm.websphere.jaxrs20.multipart.IMultipartBody;
import edu.oswego.cs.daos.FileDAO;
import edu.oswego.cs.daos.StudentDAO;
import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.database.CourseInterface;
//import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.activation.DataHandler;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
        CourseDAO course = new CourseDAO(studentDAO.courseName, studentDAO.courseSection, studentDAO.semester ,studentDAO.abbreviation);
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
        CourseDAO course = new CourseDAO(studentDAO.courseName, studentDAO.courseSection, studentDAO.semester,studentDAO.abbreviation);
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
        // Checking for if the submitting file is the right file type, only accepting csv files.
        for (IAttachment attachment : body.getAllAttachments()) {
            String filename = attachment.getDataHandler().getName();
            if (filename != null) {
                if (filename.endsWith(".csv")) continue;
                return Response.status(Response.Status.BAD_REQUEST).entity("Wrong File Type/Extension").build();
            }
        }

        try {
            fileDAO = FileDAO.FileFactory(body.getAllAttachments());
            fileDAO.getCsvLines().forEach(System.out::println);

        } catch (Exception e) {
         return Response.status(Response.Status.BAD_REQUEST).entity("File Corrupted. Try Again").build();
        }
        new CourseInterface().addStudentsFromCSV(fileDAO);
        return Response.status(Response.Status.OK).build();
    }

}