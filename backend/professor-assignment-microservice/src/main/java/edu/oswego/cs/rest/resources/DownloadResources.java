
package edu.oswego.cs.rest.resources;

import edu.oswego.cs.rest.database.AssignmentInterface;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.zip.ZipInputStream;

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
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.MULTIPART_FORM_DATA)
    @Path("/courses/{courseID}/assignments/{assignmentID}/download/{fileName}")
    public Response downloadAssignment(@PathParam("courseID") String courseID, @PathParam("assignmentID") int assignmentID, @PathParam("fileName") String fileName) throws Exception {
        File file = new File(AssignmentInterface.findFile(courseID, assignmentID, fileName));
        if (!file.exists())
            return Response.status(Response.Status.NOT_FOUND).entity("Assignment Does Not Exist").build();

        Response.ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition","attachment; filename=" + file.getName());
        return response.build();

    }

}