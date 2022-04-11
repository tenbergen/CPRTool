package edu.oswego.cs.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("assignments")
public class PeerReviewAssignmentResource {

    @GET
    @Path("assign")
    public Response assignTeams() {
        return Response.status(Response.Status.OK).build();
    }

}
