package edu.oswego.cs.rest.resources;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import com.ibm.websphere.jaxrs20.multipart.IMultipartBody;
import edu.oswego.cs.rest.daos.FileDAO;
//import edu.oswego.cs.rest.database.AssignmentInterface;

import javax.ws.rs.Path;
import javax.ws.rs.GET;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.*;
import java.net.URL;

@Path("download")
public class DownloadResources {

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/courses/{courseID}/assignments/{assignmentID}/download")
    public Response downloadAssignment(FileDAO assignment, @PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID) {
        try {
            //new AssignmentInterface().downloadAssignment(assignment);

            try (BufferedInputStream in = new BufferedInputStream(new URL("/courses/course/assignment").openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream("/courses/course/assignment")) {
                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer,0,1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            } catch(Exception e) {
                return Response.status(Response.Status.NOT_FOUND).entity("Assignment Does Not Exist").build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Assignment Did Not Download").build();
        } catch(Exception e) {
            return Response.status(Response.Status.OK).entity("Assignment Successfully Downloaded").build();
        }
    }

}