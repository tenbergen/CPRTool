package edu.oswego.cs.resources;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import edu.oswego.cs.database.TeamInterface;
import edu.oswego.cs.requests.TeamParam;

@Path("teams/professor/team")
@DenyAll
public class ProfessorTeamResources {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("get/all/{course_id}")
    @RolesAllowed("admin")
    /* Deprecated */
    public Response getAllTeams(@Context SecurityContext securityContext, @PathParam("course_id") String courseID) {
        return Response.status(Response.Status.OK).entity(new TeamInterface().getAllTeams(securityContext,courseID)).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/team-lock/toggle")
    @RolesAllowed("admin")
    /* Deprecated */
    public Response toggleTeamLock(TeamParam request) {
        new TeamInterface().toggleTeamLock(request);
        return Response.status(Response.Status.OK).entity("Team status successfully updated.").build(); 
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/team-lock/all/lock")
    @RolesAllowed("admin")
    /* Deprecated */
    public Response lockAllTeams(TeamParam request) {
        new TeamInterface().lockAllTeams(request);
        return Response.status(Response.Status.OK).entity("Successfully lock all teams.").build(); 
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/add-student")
    @RolesAllowed("professor")
    public Response addStudent(@Context SecurityContext securityContext, TeamParam request) {
        new TeamInterface().joinTeam(securityContext, request);
        return Response.status(Response.Status.OK).entity("Student successfully added to team.").build(); 
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/remove-student")
    @RolesAllowed("professor")
    public Response removeTeamMember(@Context SecurityContext securityContext, TeamParam request) {
        new TeamInterface().removeTeamMember(securityContext, request);
        return Response.status(Response.Status.OK).entity("Student successfully removed from team.").build(); 
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/team-name/edit")
    @RolesAllowed("admin")
    /* Deprecated */
    public Response editTeamName(TeamParam request) {
        new TeamInterface().editTeamName(request);
        return Response.status(Response.Status.OK).entity("Team name successfully updated.").build(); 
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/team-size/edit/bulk")
    @RolesAllowed("admin")
    /* Deprecated */
    public Response editTeamSizeInBulk(TeamParam request) {
        new TeamInterface().editTeamSizeInBulk(request);
        return Response.status(Response.Status.OK).entity("Team size successfully updated.").build(); 
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/team-size/edit")
    @RolesAllowed("admin")
    /* Deprecated */
    public Response editTeamSize(TeamParam request) {
        new TeamInterface().editTeamSize(request);
        return Response.status(Response.Status.OK).entity("Team size successfully updated.").build(); 
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/team-lead/assign")
    @RolesAllowed("admin")
    /* Deprecated */
    public Response assignTeamLead(TeamParam request) {
        new TeamInterface().assignTeamLead(request);
        return Response.status(Response.Status.OK).entity("Team lead successfully updated.").build(); 
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("delete")
    @RolesAllowed("professor")
    public Response deleteTeam(TeamParam request) {
        new TeamInterface().deleteTeam(request);
        return Response.status(Response.Status.OK).entity("Team successfully deleted.").build(); 
    }
}
