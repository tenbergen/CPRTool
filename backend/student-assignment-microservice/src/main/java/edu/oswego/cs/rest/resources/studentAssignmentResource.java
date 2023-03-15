package edu.oswego.cs.rest.resources;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import edu.oswego.cs.rest.daos.FileDAO;
import edu.oswego.cs.rest.database.AssignmentInterface;
import org.apache.tika.exception.TikaException;
import org.bson.Document;
import org.bson.types.Binary;
import org.xml.sax.SAXException;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Path("student")
@DenyAll
public class studentAssignmentResource {

    //change
    /**
     * Retrieves the assignment from its location on the server and passes it to the front end via the request header
     * as a stream. The request entity passes an InputStream[] with the assignment files in each array.
     *
     * @param courseID     String
     * @param assignmentID int
     * @return response
     **/
    @GET
    @RolesAllowed({"professor", "student"})
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.MULTIPART_FORM_DATA)
    @Path("/courses/{courseID}/assignments/{assignmentID}/{teamName}/download/")
    public Response downloadAssignment(
            @PathParam("courseID") String courseID,
            @PathParam("assignmentID") int assignmentID,
            @PathParam("teamName") String teamName) {
        Document teamSubmission = new AssignmentInterface().getSpecifiedTeamSubmission(courseID, assignmentID, teamName);
        if(teamSubmission == null) return Response.status(Response.Status.BAD_REQUEST).entity("Assignment Does Not Exist").build();

        Binary fileData = (Binary) teamSubmission.get("submission_data");
        Response.ResponseBuilder response = Response.ok(Base64.getEncoder().encode(fileData.getData()));
        response.header("Content-Disposition", "attachment; filename=" + teamSubmission.get("submission_name"));
        return response.build();
    }

    //change
    /**
     * File is uploaded as form-data and passed back as a List<IAttachment>
     * The attachment is processed in FileDao.FileFactory, which reads and
     * reconstructs the file through inputStream and outputStream respectively
     *
     * @param attachments  type List<IAttachment>: file(s) passed back as form-data
     * @param courseID     type String
     * @param assignmentID type int
     * @return Response
     */
    @POST
    @RolesAllowed({"professor", "student"})
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.MULTIPART_FORM_DATA)
    @Path("/courses/{courseID}/assignments/{assignmentID}/{teamName}/upload")
    public Response addFileToAssignment(
            List<IAttachment> attachments,
            @PathParam("courseID") String courseID,
            @PathParam("assignmentID") int assignmentID,
            @PathParam("teamName") String teamName
    ) throws TikaException, SAXException {
        for (IAttachment attachment : attachments) {
            if (attachment == null) continue;
            String fileName = attachment.getDataHandler().getName();
            String fileExt = fileName.substring(fileName.indexOf("."));
            if (!fileName.endsWith("pdf") && !fileName.endsWith("docx"))
                return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();
            try {
                new AssignmentInterface().writeToAssignment(FileDAO.fileFactory(teamName.concat(fileExt), courseID, attachment, assignmentID, teamName));
            } catch (IOException e) {
                return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
            }
        }
        return Response.status(Response.Status.OK).entity("Successfully uploaded assignment.").build();
    }

    @GET
    @RolesAllowed({"student", "professor"})
    @Path("{course_id}/{student_id}/submissions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response viewUsersSubmissions(@PathParam("course_id") String courseID,
                                         @PathParam("student_id") String teamName) {
        List<Document> documents = new AssignmentInterface().getAllUserAssignments(courseID, teamName);
        return Response.status(Response.Status.OK).entity(documents).build();
    }

    @GET
    @RolesAllowed({"student", "professor"})
    @Path("{course_id}/{assignment_id}/{student_id}/submission")
    @Produces(MediaType.APPLICATION_JSON)
    public Response viewUsersSubmission(@PathParam("course_id") String courseID,
                                        @PathParam("assignment_id") int assignmentID,
                                        @PathParam("student_id") String studentID) {
        List<Document> documents = new AssignmentInterface().getSpecifiedUserAssignment(courseID, assignmentID, studentID);
        return Response.status(Response.Status.OK).entity(documents).build();
    }

    @GET
    @RolesAllowed("professor")
    @Path("{course_id}/{assignment_id}/submissions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response viewAllSubmissions(@PathParam("course_id") String courseID,
                                       @PathParam("assignment_id") int assignmentID) {
        List<Document> documents = new AssignmentInterface().getAssignmentSubmissions(courseID, assignmentID);
        return Response.status(Response.Status.OK).entity(documents).build();
    }

    @GET
    @RolesAllowed("student")
    @Path("{course_id}/{laker_id}/submission")
    @Produces(MediaType.APPLICATION_JSON)
    public Response viewSubmissions(
            @PathParam("course_id") String course_id,
            @PathParam("laker_id") String student_id) {
        Document result = new AssignmentInterface().allAssignments(course_id, student_id);
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @RolesAllowed({"professor", "student"})
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/courses/{courseID}/{student_id}/to-dos")
    public Response viewToDos(@PathParam("courseID") String courseID, @PathParam("student_id") String student_id) {
        return Response.status(Response.Status.OK).entity(new AssignmentInterface().getToDosByCourse(courseID, student_id)).build();
    }
}