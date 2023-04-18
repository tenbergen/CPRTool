package edu.oswego.cs.rest.resources;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import edu.oswego.cs.rest.daos.AssignmentDAO;
import edu.oswego.cs.rest.daos.AssignmentNoPeerReviewDAO;
import edu.oswego.cs.rest.daos.FileDAO;
import edu.oswego.cs.rest.daos.PeerReviewAddOnDAO;
import edu.oswego.cs.rest.database.AssignmentInterface;
import org.bson.Document;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Path("professor")
@DenyAll
public class ProfessorAssignmentResource {

    @POST
    @RolesAllowed("professor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/courses/create-assignment")
    public Response createAssignment(AssignmentDAO assignmentDAO) throws IOException {
        Document assignmentDocument = new AssignmentInterface().createAssignment(assignmentDAO);
        return Response.status(Response.Status.OK).entity(assignmentDocument).build();
    }

    /**
     * Create an assignment with no initial peer review data
     *
     * @param assignmentNoPeerReviewDAO
     * @return
     * @throws IOException
     */

    @POST
    @RolesAllowed("professor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/courses/create-assignment-no-peer-review")
    public Response createAssignmentNoPeerReview(AssignmentNoPeerReviewDAO assignmentNoPeerReviewDAO) throws IOException {
        Document assignmentDocument = new AssignmentInterface().createAssignmentNoPeerReview(assignmentNoPeerReviewDAO);
        return Response.status(Response.Status.OK).entity(assignmentDocument).build();
    }


    @DELETE
    @RolesAllowed("professor")
    @Path("/courses/{courseID}/assignments/{assignmentID}/remove")
    public Response removeAssignment(@PathParam("assignmentID") int assignmentID, @PathParam("courseID") String courseID) throws IOException {
        new AssignmentInterface().removeAssignment(assignmentID, courseID);
        return Response.status(Response.Status.OK).entity("Assignment successfully deleted.").build();
    }

    @PUT
    @RolesAllowed("professor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/courses/{courseID}/assignments/{assignmentID}/edit")
    public Response updateAssignment(AssignmentDAO assignmentDAO, @PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID) {
        new AssignmentInterface().updateAssignment(assignmentDAO, courseID, assignmentID);
        String response = assignmentDAO.courseID + ": " + assignmentDAO.assignmentName + " successfully updated.";
        return Response.status(Response.Status.OK).entity(response).build();
    }

    /**
     * Edit an assignment with no peer review data
     *
     * @param assignmentNoPeerReviewDAO
     * @param courseID
     * @param assignmentID
     * @return
     */

    @PUT
    @RolesAllowed("professor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/courses/{courseID}/assignments/{assignmentID}/editNoPeerReview")
    public Response updateAssignmentNoPeerReview(AssignmentNoPeerReviewDAO assignmentNoPeerReviewDAO, @PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID) {
        new AssignmentInterface().updateAssignmentWithNoPeerReview(assignmentNoPeerReviewDAO, courseID, assignmentID);
        String response = assignmentNoPeerReviewDAO.courseID + ": " + assignmentNoPeerReviewDAO.assignmentName + " successfully updated.";
        return Response.status(Response.Status.OK).entity(response).build();
    }


    /**
     * Add peer review data to an assignment that has none
     *
     * @param peerReviewAddOnDAO
     * @param courseID
     * @param assignmentID
     * @return
     */
    @PUT
    @RolesAllowed("professor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/courses/{courseID}/assignments/{assignmentID}/addPeerReviewData")
    public Response addPeerReviewData(PeerReviewAddOnDAO peerReviewAddOnDAO, @PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID) {
        String assignmentName = new AssignmentInterface().addPeerReviewDataToAssignment(courseID, assignmentID, peerReviewAddOnDAO);
        return Response.status(Response.Status.OK).entity("Successfully added peer review data to " + courseID + ":" + assignmentName).build();
    }

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
     * File's Base64 string is uploaded as form-data and passed back as a List<IAttachment>. The file's name and
     * extension is saved in the form-data's name
     * The attachment is processed and turned back into its binary representation. The binary data and file name is then
     * saved with its respective assignment document in the DB.
     *
     * @param attachments  type List<IAttachment>: file(s) Base64 Strings passed back as form-data
     * @param courseID     type String
     * @param assignmentID type int
     * @return Response
     */

    @POST
    @RolesAllowed("professor")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/courses/{courseID}/assignments/{assignmentID}/upload")
    public Response addFileToAssignment
    (List<IAttachment> attachments,
     @PathParam("courseID") String courseID,
     @PathParam("assignmentID") int assignmentID)
            throws Exception {
        for (IAttachment attachment : attachments) {
            if (attachment == null) continue;
            String fileName = attachment.getDataHandler().getName();
            if (!fileName.endsWith("pdf") && !fileName.endsWith("zip") && !fileName.endsWith("docx"))
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
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/courses/{courseID}/assignments/{assignmentID}/peer-review/rubric/upload")
    public Response addRubricToPeerReview(List<IAttachment> attachments, @PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID) throws Exception {
        for (IAttachment attachment : attachments) {
            if (attachment == null) continue;
            String fileName = attachment.getDataHandler().getName();
            if (!fileName.endsWith("pdf") && !fileName.endsWith("zip") && !fileName.endsWith("docx"))
                return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();
            new AssignmentInterface().writeRubricToPeerReviews(FileDAO.fileFactory(fileName, courseID, attachment, assignmentID));
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
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/courses/{courseID}/assignments/{assignmentID}/peer-review/template/upload")
    public Response addTemplateToPeerReview(List<IAttachment> attachments, @PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID) throws Exception {
        for (IAttachment attachment : attachments) {
            if (attachment == null) continue;
            String fileName = attachment.getDataHandler().getName();
            if (!fileName.endsWith("pdf") && !fileName.endsWith("zip") && !fileName.endsWith("docx"))
                return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();
            new AssignmentInterface().writeTemplateToPeerReviews(FileDAO.fileFactory(fileName, courseID, attachment, assignmentID));
        }
        return Response.status(Response.Status.OK).entity("Successfully added file to assignment.").build();
    }

    /**
     * Retrieves the assignment instructions file from the DB and passes its Base64 representation to the front end via
     * the request header.
     *
     * @param courseID     String
     * @param assignmentID int
     * @return response
     **/
    @GET
    @RolesAllowed({"professor", "student"})
    @Produces(MediaType.MULTIPART_FORM_DATA)
    @Path("/courses/{courseID}/assignments/{assignmentID}/download")
    public Response downloadAssignment(@PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID) {
        byte[] fileData = new AssignmentInterface().getInstructionFileData(courseID, assignmentID);
        String fileName = new AssignmentInterface().getInstructionFileName(courseID, assignmentID);

        Response.ResponseBuilder response = Response.ok(Base64.getEncoder().encode(fileData));
        response.header("Content-Disposition", "attachment; filename=" + fileName);
        return response.build();
    }

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
    @Produces(MediaType.MULTIPART_FORM_DATA)
    @Path("/courses/{courseID}/assignments/{assignmentID}/peer-review/template/download")
    public Response downloadPeerReviewTemplate(@PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID) {
        byte[] fileData = new AssignmentInterface().getPeerReviewTemplateData(courseID, assignmentID);
        String fileName = new AssignmentInterface().getTemplateFileName(courseID, assignmentID);

        Response.ResponseBuilder response = Response.ok(Base64.getEncoder().encode(fileData));
        response.header("Content-Disposition", "attachment; filename=" + fileName);
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
    @Path("/courses/{courseID}/assignments/{assignmentID}/peer-review/rubric/download")
    public Response downloadPeerReview(@PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID) {
        byte[] fileData = new AssignmentInterface().getRubricFileData(courseID, assignmentID);
        String fileName = new AssignmentInterface().getRubricFileName(courseID, assignmentID);

        Response.ResponseBuilder response = Response.ok(Base64.getEncoder().encode(fileData));
        response.header("Content-Disposition", "attachment; filename=" + fileName);
        return response.build();
    }


    //Change
    @DELETE
    @RolesAllowed("professor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/courses/{course-id}/assignments/{assignment-id}/remove-file")
    public Response removeFileFromAssignment(@PathParam("course-id") String courseID, @PathParam("assignment-id") int assignmentID) {
        new AssignmentInterface().removeFile(courseID, assignmentID);
        return Response.status(Response.Status.OK).entity("File successfully deleted.").build();
    }


    @DELETE
    @RolesAllowed("professor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/courses/{course-id}/assignments/{assignment-id}/peer-review/template/remove-file")
    public Response removeFileFromPeerReviewTemplate(@PathParam("course-id") String courseID, @PathParam("assignment-id") int assignmentID) {
        new AssignmentInterface().removePeerReviewTemplate(courseID, assignmentID);
        return Response.status(Response.Status.OK).entity("File successfully deleted.").build();
    }

    @DELETE
    @RolesAllowed("professor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/courses/{course-id}/assignments/{assignment-id}/peer-review/rubric/remove-file")
    public Response removeFileFromPeerReviewRubric(@PathParam("course-id") String courseID, @PathParam("assignment-id") int assignmentID) {
        new AssignmentInterface().removePeerReviewRubric(courseID, assignmentID);
        return Response.status(Response.Status.OK).entity("File successfully deleted.").build();
    }

    @DELETE
    @RolesAllowed("professor")
    @Path("/courses/{courseID}/remove")
    public Response removeCourse(@PathParam("courseID") String courseID) throws IOException {
        new AssignmentInterface().removeCourse(courseID);
        return Response.status(Response.Status.OK).entity("Course successfully deleted from assignments database and assignments folder.").build();
    }

}
