package edu.oswego.cs.resources;

import edu.oswego.cs.distribution.AssignmentDistribution;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Path("assignments")
public class PeerReviewAssignmentResource {

    @GET
    @Path("assign")
    public Response assignTeams() {
        return Response.status(Response.Status.OK).build();
    }

}
