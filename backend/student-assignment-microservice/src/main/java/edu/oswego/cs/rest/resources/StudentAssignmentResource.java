package edu.oswego.cs.rest.resources;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import edu.oswego.cs.rest.daos.FileDAO;
import edu.oswego.cs.rest.database.AssignmentInterface;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.List;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("student")
@DenyAll
public class StudentAssignmentResource {
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
    @Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_OCTET_STREAM})
    @Produces({MediaType.MULTIPART_FORM_DATA, "application/pdf"})
    @Path("/courses/{courseID}/{assignmentID}/{teamName}/upload")
    public Response addFileToAssignment(
            List<IAttachment> attachments,
            @PathParam("courseID") String courseID,
            @PathParam("assignmentID") int assignmentID,
            @PathParam("teamName") String teamName
    ) throws IOException {
        AssignmentInterface assignmentInterface = new AssignmentInterface();
        for (IAttachment attachment : attachments) {
            if (attachment == null) continue;
            String fileName = attachment.getDataHandler().getName();
            String fileExt = fileName.substring(fileName.indexOf("."));
            if (!fileExt.equals("pdf") && !fileExt.equals("docx")) return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();
            assignmentInterface.writeToAssignment(FileDAO.fileFactory(teamName.concat(fileExt), courseID, attachment, assignmentID));
            return Response.status(Response.Status.OK).entity("Assignment Added.").build();
            //Document assignmentDocument = assignmentInterface.addAssignmentToSubmissions(courseID, assignmentID, teamName, teamName.concat(fileExt));
//            return Response.status(Response.Status.OK).entity(assignmentDocument).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("File Corrupted.").build();
    }

}
