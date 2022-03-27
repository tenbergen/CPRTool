package edu.oswego.cs.rest.resources;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import edu.oswego.cs.rest.daos.AssignmentDAO;
import edu.oswego.cs.rest.daos.FileDAO;
import edu.oswego.cs.rest.database.AssignmentInterface;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("professor")
public class ProfessorAssignmentResource {

    public ProfessorAssignmentResource() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/assignments")
    public Response viewAllAssignments() {
        try {
            List<AssignmentDAO> allAssignments = new AssignmentInterface().getAllAssignments();
            return Response.status(Response.Status.OK).entity(allAssignments).build();
        } catch (Exception e){
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to fetch assignments.").build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/courses/{courseID}/assignments/")
    public Response viewAssignment(@PathParam("courseID") String courseID) throws Exception {
        try {
            List<AssignmentDAO> specifiedAssignments = new AssignmentInterface().getAssignmentsByCourse(courseID);
            if (specifiedAssignments.isEmpty())
                return Response.status(Response.Status.NOT_FOUND).entity("This assignment does not exist").build();
            return Response.status(Response.Status.OK).entity(specifiedAssignments).build();
        } catch (Exception e){
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to fetch assignments.").build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/assignments/description/add")
    public Response addCourseDescription(AssignmentDAO course) throws Exception {
        return Response.status(Response.Status.OK).build();
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
    public Response addFileToAssignment(List<IAttachment> attachments, @PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID) throws Exception {

        InputStream stream = null;
        for (IAttachment attachment : attachments) {
            if (attachment == null) {continue;}
            String fileName = attachment.getDataHandler().getName();
            if (!fileName.endsWith("pdf") && !fileName.endsWith("zip"))
                return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();

            if (fileName == null) {
                FileDAO.nullFiles(stream);
            } else {
                    new AssignmentInterface().writeToAssignment(FileDAO.fileFactory(fileName,courseID,attachment,assignmentID));
            }
            if (stream != null) {
                stream.close();
            }
        }
        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/courses/{courseID}/assignments/createAssignment")
    public Response createAssignment(AssignmentDAO assignmentDAO, @PathParam("courseID") String courseID) throws Exception {
        assignmentDAO.setCourseID(courseID);
        System.out.println("-----------------------------------------------------\n"+
                assignmentDAO.getAssignmentName() + "\n" + assignmentDAO.getCourseID()+"\n"+
                assignmentDAO.getDueDate() + "\n" + assignmentDAO.getInstructions()+"\n"+assignmentDAO.getPoints());
        new AssignmentInterface().createAssignment(assignmentDAO);
        return Response.status(Response.Status.OK).entity("Assignment Successfully Created").build();
    }

    @DELETE
    @Path("/courses/{courseID}/assignments/{assignmentID}/remove")
    public Response removeAssignment(@PathParam("assignmentID") int assignmentID, @PathParam("courseID") String courseID) throws Exception {
        try {
            new AssignmentInterface().remove(assignmentID,courseID);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.OK).entity("Assignment Successfully Deleted").build();
    }

}
