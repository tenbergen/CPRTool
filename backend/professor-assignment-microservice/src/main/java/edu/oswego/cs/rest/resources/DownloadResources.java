package edu.oswego.cs.rest.resources;

import edu.oswego.cs.rest.database.AssignmentInterface;
//import edu.oswego.cs.rest.database.AssignmentInterface;

import javax.ws.rs.Path;
import javax.ws.rs.GET;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@Path("professor")
public class DownloadResources {

    /**
     * Retrieves the assignment from its location on the server and passes it to the front end via the request entity.
     * The request entity passes an InputStream[] with the assignment files in each array.
     *
     * @param courseID String
     * @param assignmentID int
     * @return response
     * **/
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/courses/{courseID}/assignments/{assignmentID}/download")
    public Response downloadAssignment(@PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID) throws Exception {
        File assignmentFolder = new File(new AssignmentInterface().findAssignment(courseID, assignmentID));
        if (!assignmentFolder.exists())
            return Response.status(Response.Status.NOT_FOUND).entity("Assignment Does Not Exist").build();

        File[] assignmentFiles = assignmentFolder.listFiles();
        if (assignmentFiles == null)
            return Response.status(Response.Status.NOT_FOUND).entity("Folder contains no files").build();

        InputStream[] assignments = new InputStream[assignmentFiles.length];
        AtomicInteger index = new AtomicInteger(0);
        Arrays.asList(assignmentFiles).forEach(file -> {
                try {
                    assignments[index.getAndIncrement()] = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });

            return Response.status(Response.Status.OK).entity(assignments).build();
    }



}