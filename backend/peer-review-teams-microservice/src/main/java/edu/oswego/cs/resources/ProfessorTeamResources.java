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
    public Response toggleTeamLock(TeamParam request) {
        new TeamInterface().toggleTeamLock(request);
        return Response.status(Response.Status.OK).entity("Team status successfully updated.").build(); 
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/add-student")
    public Response likeThePRofessorCanAddAStudentToATeamIfThatSudentIsNotCurrentInAnyTeamButIfTheStudentIsAlreadyInATeamThenMoveTheStudentFromTheOldTeamToTheNewTeam(TeamParam request) {
        new TeamInterface().addStudentToTeam(request);
        return Response.status(Response.Status.OK).entity("Student successfully added to team.").build(); 
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/remove-student")
    public Response removeMember(TeamParam request) {
        new TeamInterface().removeStudent(request);
        return Response.status(Response.Status.OK).entity("Student successfully removed from team.").build(); 
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/team-name/edit")
    public Response editTeamName(TeamParam request) {
        new TeamInterface().editTeamName(request);
        return Response.status(Response.Status.OK).entity("Team name successfully updated.").build(); 
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/team-lead/assign")
    public Response assignTeamLead(TeamParam request) {
        new TeamInterface().assignTeamLead(request);
        return Response.status(Response.Status.OK).entity("Team lead successfully updated.").build(); 
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("delete")
    public Response deleteTeam(TeamParam request) {
        new TeamInterface().deleteTeam(request);
        return Response.status(Response.Status.OK).entity("Team successfully deleted.").build(); 
    }

    // update team size bulk and single
}
