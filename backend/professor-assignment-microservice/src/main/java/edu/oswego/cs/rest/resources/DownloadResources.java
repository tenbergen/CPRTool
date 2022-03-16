package edu.oswego.cs.rest.resources;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import com.ibm.websphere.jaxrs20.multipart.IMultipartBody;
import edu.oswego.cs.rest.daos.FileDAO;
import javax.ws.rs.Path;

import javax.activation.DataHandler;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Path("download")
public class DownloadResources {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/course/assignment/download")
    public Response downloadAssignment(FileDAO assignment) {
        try {
            new CourseInterface().downloadAssignment(assignment);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Assignment Does Not Exist").build();
        }
        return Response.status(Response.Status.OK).entity("Assignment Successfully Downloaded").build();
    }

}