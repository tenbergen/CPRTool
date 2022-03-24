package edu.oswego.cs.rest.resources;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import edu.oswego.cs.rest.daos.FileDAO;
import edu.oswego.cs.rest.database.AssignmentInterface;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.List;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("professor")
public class ProfessorAssignmentResource {

    public ProfessorAssignmentResource() {
    }

    @DELETE
//    @Path("/courses/course/assignment/remove")
    @Path("/courses/{courseID}/assignments/{assignmentID}/remove")
    public Response removeAssignment(@PathParam("assignmentID") String assignment, @PathParam("courseID") String courseID) throws Exception {
//        String assName = "CSC580-800-spring-2022.pdf";
//        String CID = "CSC580-800-spring-2022";
        try {
            new AssignmentInterface().remove(assignment,courseID);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.OK).entity("Assignment Successfully Deleted").build();
    }

    /**
     * File is uploaded as form-data and passed back as a List<IAttachment>
     * The attachment is processed in FileDao.FileFactory, which reads and
     * reconstructs the file through inputStream and outputStream respectively
     *
     * @param attachments type List<IAttachment>: file(s) passed back as form-data
     * @param courseID type String
     * @param assignmentID type int
     * @return Response
     */
    @POST
    @Produces({MediaType.MULTIPART_FORM_DATA, "application/pdf"})
    @Path("/courses/{courseID}/assignments/{assignmentID}/upload")
    public Response uploadAssignment(List<IAttachment> attachments, @PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID) throws Exception {

        InputStream stream = null;
        for (IAttachment attachment : attachments) {
            if (attachment == null) {continue;}
            String fileName = attachment.getDataHandler().getName();
            if (!fileName.endsWith("pdf") && !fileName.endsWith("zip"))
                return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();

            if (fileName == null) {
                FileDAO.nullFiles(stream);
            } else {
                if (assignmentID == -1)
                    new AssignmentInterface().add(FileDAO.fileFactory(fileName, courseID, attachment, assignmentID));
                else
                    FileDAO.fileFactory(fileName,courseID,attachment,assignmentID);
//                    addToExistingAssignment
            }
            if (stream != null) {
                stream.close();
            }
        }
        return Response.status(Response.Status.OK).build();
    }
}
