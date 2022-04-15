package edu.oswego.cs.rest.resources;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import edu.oswego.cs.rest.daos.AssignmentDAO;
import edu.oswego.cs.rest.daos.FileDAO;
import edu.oswego.cs.rest.database.AssignmentInterface;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Path("professor")
@DenyAll
public class ProfessorAssignmentResource {

    @GET
    @RolesAllowed("professor")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/assignments")
    public Response viewAllAssignments() {
        return Response.status(Response.Status.OK).entity(new AssignmentInterface().getAllAssignments()).build();
    }

    @GET
    @RolesAllowed({"professor", "student"})
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/courses/{courseID}/assignments")
    public Response viewAssignmentsByCourse(@PathParam("courseID") String courseID) {
        return Response.status(Response.Status.OK).entity(new AssignmentInterface().getAssignmentsByCourse(courseID)).build();
    }

    @GET
    @RolesAllowed({"professor", "student"})
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/courses/{courseID}/assignments/{assignmentID}")
    public Response viewSpecifiedAssignment(@PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID) {
        return Response.status(Response.Status.OK).entity(new AssignmentInterface().getSpecifiedAssignment(courseID, assignmentID)).build();
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
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.MULTIPART_FORM_DATA, "application/pdf"})
    @Path("/courses/{courseID}/assignments/{assignmentID}/upload")
    public Response addFileToAssignment(List<IAttachment> attachments, @PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID) throws Exception {
        for (IAttachment attachment : attachments) {
            if (attachment == null) continue;
            String fileName = attachment.getDataHandler().getName();

            if (!fileName.endsWith("pdf") && !fileName.endsWith("zip"))
                return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();
            new AssignmentInterface().writeToAssignment(FileDAO.fileFactory(fileName, courseID, attachment, assignmentID));
        }
        return Response.status(Response.Status.OK).entity("Successfully added file to assignment.").build();
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
    @RolesAllowed("professor")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.MULTIPART_FORM_DATA, "application/pdf"})
    @Path("/courses/{courseID}/assignments/{assignmentID}/peer-review/rubric/upload")
    public Response addRubricToPeerReview(List<IAttachment> attachments, @PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID) throws Exception {
        for (IAttachment attachment : attachments) {
            if (attachment == null) continue;
            String fileName = attachment.getDataHandler().getName();

            if (!fileName.endsWith("pdf") && !fileName.endsWith("zip"))
                return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();
            new AssignmentInterface().writeRubricToPeerReviews(FileDAO.fileFactory(fileName, courseID, attachment, assignmentID));
        }
        return Response.status(Response.Status.OK).entity("Successfully added file to peer reviews.").build();
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
    @RolesAllowed("professor")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.MULTIPART_FORM_DATA, "application/pdf"})
    @Path("/courses/{courseID}/assignments/{assignmentID}/peer-review/template/upload")
    public Response addTemplateToPeerReview(List<IAttachment> attachments, @PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID) throws Exception {
        for (IAttachment attachment : attachments) {
            if (attachment == null) continue;
            String fileName = attachment.getDataHandler().getName();

            if (!fileName.endsWith("pdf") && !fileName.endsWith("zip"))
                return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();
            new AssignmentInterface().writeTemplateToPeerReviews(FileDAO.fileFactory(fileName, courseID, attachment, assignmentID));
        }
        return Response.status(Response.Status.OK).entity("Successfully added file to peer reviews.").build();
    }

    @DELETE
    @RolesAllowed("professor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/courses/{course-id}/assignments/{assignment-id}/remove-file/{file-name}")
    public Response removeFileFromAssignment(@PathParam("course-id") String courseID, @PathParam("assignment-id") int assignmentID, @PathParam("file-name") String fileName) {
        AssignmentInterface.removeFile(courseID, fileName, assignmentID);
        return Response.status(Response.Status.OK).entity("File successfully deleted.").build();
    }

    @DELETE
    @RolesAllowed("professor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/courses/{course-id}/assignments/{assignment-id}/peer-review/remove-file/{file-name}")
    public Response removeFileFromPeerReview(@PathParam("course-id") String courseID, @PathParam("assignment-id") int assignmentID, @PathParam("file-name") String fileName) {
        AssignmentInterface.removePeerReviewFile(courseID, fileName, assignmentID);
        return Response.status(Response.Status.OK).entity("File successfully deleted.").build();
    }

    @POST
    @RolesAllowed({"professor", "student"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/courses/create-assignment")
    public Response createAssignment(AssignmentDAO assignmentDAO) {
        int assignmentID = new AssignmentInterface().createAssignment(assignmentDAO);
        String response = assignmentDAO.courseID + ": " + assignmentDAO.assignmentName + " successfully created.";
        return Response.status(Response.Status.OK).entity(response).header("assignment_id", assignmentID).build();
    }

    @DELETE
    @RolesAllowed({"professor", "student"})
    @Path("/courses/{courseID}/assignments/{assignmentID}/remove")
    public Response removeAssignment(@PathParam("assignmentID") int assignmentID, @PathParam("courseID") String courseID) throws IOException {
        new AssignmentInterface().removeAssignment(assignmentID, courseID);
        return Response.status(Response.Status.OK).entity("Assignment successfully deleted.").build();
    }

    @PUT
    @RolesAllowed({"professor", "student"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/courses/{courseID}/assignments/{assignmentID}/edit")
    public Response updateAssignment(AssignmentDAO assignmentDAO, @PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID) {
        new AssignmentInterface().updateAssignment(assignmentDAO, courseID, assignmentID);
        String response = assignmentDAO.courseID + ": " + assignmentDAO.assignmentName + " successfully updated.";
        return Response.status(Response.Status.OK).entity(response).build();
    }

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
    @Produces(MediaType.MULTIPART_FORM_DATA)
    @Path("/courses/{courseID}/assignments/{assignmentID}/download/{fileName}")
    public Response downloadAssignment(@PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID, @PathParam("fileName") String fileName) {
        new AssignmentInterface();
        File file = new File(AssignmentInterface.findFile(courseID, assignmentID, fileName));
        if (!file.exists())
            return Response.status(Response.Status.BAD_REQUEST).entity("Assignment does not exist.").build();

        Response.ResponseBuilder response = Response.ok(file);
        response.header("Content-Disposition", "attachment; filename=" + file.getName());
        return response.build();
    }

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
    @Produces(MediaType.MULTIPART_FORM_DATA)
    @Path("/courses/{courseID}/assignments/{assignmentID}/peer-review/download/{fileName}")
    public Response downloadPeerReview(@PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID, @PathParam("fileName") String fileName) {
        File file = new File(AssignmentInterface.findPeerReviewFile(courseID, assignmentID, fileName));

        return Response.ok(file).header("Content-Disposition", "attachment; filename=" + file.getName()).build();
    }

    @DELETE
    @RolesAllowed("professor")
    @Path("/courses/{courseID}/remove")
    public Response removeCourse(@PathParam("courseID") String courseID) throws IOException {
        new AssignmentInterface().removeCourse(courseID);
        return Response.status(Response.Status.OK).entity("Course successfully deleted from assignments database and assignments folder.").build();
    }
}
