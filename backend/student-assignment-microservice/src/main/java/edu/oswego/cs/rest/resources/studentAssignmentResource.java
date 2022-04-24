package edu.oswego.cs.rest.resources;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import edu.oswego.cs.rest.daos.FileDAO;
import edu.oswego.cs.rest.database.AssignmentInterface;
import org.bson.Document;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Path("student")
@DenyAll
public class studentAssignmentResource {

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

        String path = "assignments" + "/" + courseID + "/" + assignmentID + "/" + "team-submissions" + "/";
        Optional<File> file = Arrays.stream(new File(path).listFiles()).filter(f -> f.getName().contains(teamName)).findFirst();
        if (file.isEmpty())
            return Response.status(Response.Status.BAD_REQUEST).entity("Assignment Does Not Exist").build();

        Response.ResponseBuilder response = Response.ok(file.get());
        response.header("Content-Disposition", "attachment; filename=" + file.get().getName());
        return response.build();
    }

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
    @Produces({MediaType.MULTIPART_FORM_DATA, "application/pdf"})
    @Path("/courses/{courseID}/assignments/{assignmentID}/{teamName}/upload")
    public Response addFileToAssignment(
            List<IAttachment> attachments,
            @PathParam("courseID") String courseID,
            @PathParam("assignmentID") int assignmentID,
            @PathParam("teamName") String teamName
    ) throws IOException {
        for (IAttachment attachment : attachments) {
            if (attachment == null) continue;
            String fileName = attachment.getDataHandler().getName();
            String fileExt = fileName.substring(fileName.indexOf("."));
            if (!fileName.endsWith("pdf") && !fileName.endsWith("docx"))
                return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();
            new AssignmentInterface().writeToAssignment(FileDAO.fileFactory(teamName.concat(fileExt), courseID, attachment, assignmentID, teamName));
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
    public Response viewAssignmentsByCourse(@PathParam("courseID") String courseID, @PathParam("student_id") String student_id) {
        return Response.status(Response.Status.OK).entity(new AssignmentInterface().getToDosByCourse(courseID, student_id)).build();
    }
}