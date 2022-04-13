package edu.oswego.cs.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edu.oswego.cs.database.TeamInterface;
import edu.oswego.cs.requests.TeamParam;

@Path("teams/professor/team")
public class ProfessorTeamResources {
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/team-lock/toggle")
    public Response moveTeamMember(TeamParam request) {
        new TeamInterface().toggleTeamLock(request);
        return Response.status(Response.Status.OK).entity("Team status successfully updated.").build(); 
}
