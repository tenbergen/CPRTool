package edu.oswego.cs.rest.resources;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import edu.oswego.cs.rest.daos.FileDAO;
import edu.oswego.cs.rest.database.AssignmentInterface;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.List;

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
    @Path("/courses/{courseID}/assignments/{assignmentID}/download/{fileName}")
    public Response downloadAssignment(@PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID, @PathParam("fileName") String fileName) {
        File file = new File(AssignmentInterface.findFile(courseID, assignmentID, fileName));
        if (!file.exists())
            return Response.status(Response.Status.NOT_FOUND).entity("Assignment Does Not Exist").build();

        Response.ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachment; filename=" + file.getName());
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
    @Path("/courses/{courseID}/assignments/{assignmentID}/upload")
    public Response addFileToAssignment(List<IAttachment> attachments, @PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID) throws IOException {
        for (IAttachment attachment : attachments) {
            if (attachment == null) continue;
            String fileName = attachment.getDataHandler().getName();

            if (!fileName.endsWith("pdf") && !fileName.endsWith("docx"))
                return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();
            new AssignmentInterface().writeToAssignment(FileDAO.fileFactory(fileName, courseID, attachment, assignmentID));
        }
        return Response.status(Response.Status.OK).entity("Successfully uploaded assignment.").build();
    }

}