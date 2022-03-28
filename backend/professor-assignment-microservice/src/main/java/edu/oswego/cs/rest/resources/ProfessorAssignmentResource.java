package edu.oswego.cs.rest.resources;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import edu.oswego.cs.rest.daos.AssignmentDAO;
import edu.oswego.cs.rest.daos.FileDAO;
import edu.oswego.cs.rest.database.AssignmentInterface;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@Path("professor")
public class ProfessorAssignmentResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/assignments")
    public Response viewAllAssignments() {
        List<AssignmentDAO> allAssignments = new AssignmentInterface().getAllAssignments();
        return Response.status(Response.Status.OK).entity(allAssignments).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/courses/{courseID}/assignments")
    public Response viewAssignmentsByCourse(@PathParam("courseID") String courseID) {
        List<AssignmentDAO> specifiedAssignments = new AssignmentInterface().getAssignmentsByCourse(courseID);
        if (specifiedAssignments.isEmpty()) return Response.status(Response.Status.NOT_FOUND).entity("This assignment does not exist").build();
        return Response.status(Response.Status.OK).entity(specifiedAssignments).build();
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
    @Produces({MediaType.MULTIPART_FORM_DATA, "application/pdf"})
    @Path("/courses/{courseID}/{assignmentID}/upload")
    public Response addFileToAssignment(List<IAttachment> attachments, @PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID) throws Exception {
        for (IAttachment attachment : attachments) {
            if (attachment == null) continue;
            String fileName = attachment.getDataHandler().getName();

            if (!fileName.endsWith("pdf") && !fileName.endsWith("zip")) return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();
            new AssignmentInterface().writeToAssignment(FileDAO.fileFactory(fileName, courseID, attachment, assignmentID));
        }
        return Response.status(Response.Status.OK).entity("Successfully added file to assignment.").build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/courses/create-assignment")
    public Response createAssignment(AssignmentDAO assignmentDAO) {
        new AssignmentInterface().createAssignment(assignmentDAO);
        String assignmentSuccessfullyCreated = assignmentDAO.getCourseID() + ": " + assignmentDAO.getAssignmentName() + " successfully created.";
        return Response.status(Response.Status.OK).entity(assignmentSuccessfullyCreated).build();
    }

    @DELETE
    @Path("/courses/{courseID}/{assignmentID}/remove")
    public Response removeAssignment(@PathParam("assignmentID") int assignmentID, @PathParam("courseID") String courseID) throws IOException {
        new AssignmentInterface().remove(assignmentID, courseID);
        return Response.status(Response.Status.OK).entity("Assignment successfully deleted.").build();
    }
}