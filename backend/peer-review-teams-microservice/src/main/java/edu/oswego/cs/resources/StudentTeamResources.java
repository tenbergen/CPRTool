package edu.oswego.cs.resources;

import edu.oswego.cs.database.TeamInterface;
import edu.oswego.cs.requests.TeamParam;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/teams/team")
@DenyAll
public class StudentTeamResources {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("create")
    @RolesAllowed({"professor","student"})
    public Response createTeam(TeamParam request) {
        new TeamInterface().createTeam(request);
        return Response.status(Response.Status.CREATED).entity("Team successfully created.").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("get/unlocked-team/all/{course_id}")
    @RolesAllowed({"professor", "student"})
    public Response getAllUnlockedTeam(@PathParam("course_id") String courseID) {
        return Response.status(Response.Status.OK).entity(new TeamInterface().getAllUnlockedTeams(courseID)).build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("get/{course_id}/{student_id}")
    @RolesAllowed({"professor", "student"})
    public Response getTeamByStudentID(@PathParam("course_id") String courseID, @PathParam("student_id") String studentID) {
        return Response.status(Response.Status.OK).entity(new TeamInterface().getTeamByStudentID(courseID, studentID)).build();
    }

    @GET
    @Path("{course_id}/get/{team_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"professor","student"})
    public Response getTeamByTeamID(@PathParam("course_id") String courseID, @PathParam("team_id") String teamID) {
        return Response.status(Response.Status.OK).entity(new TeamInterface().getTeamByTeamID(courseID, teamID)).build();
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("join")
    @RolesAllowed("student")
    public Response joinTeam(TeamParam request) {
        new TeamInterface().studentJoinTeam(request);
        return Response.status(Response.Status.OK).entity("Student successfully join team.").build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("team-lead/step-down")
    @RolesAllowed("student")
    public Response giveUpTeamLead(TeamParam request) {
        new TeamInterface().giveUpTeamLead(request);
        return Response.status(Response.Status.OK).entity("Team Lead successfully updated.").build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("team-lead/cede")
    @RolesAllowed("student")
    public Response nominateTeamLead(TeamParam request) {
        new TeamInterface().nominateTeamLead(request);
        return Response.status(Response.Status.OK).entity("Team Lead successfully updated.").build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("member/confirm")
    @RolesAllowed("student")
    public Response memberConfirm(TeamParam request) {
        new TeamInterface().memberConfirm(request);
        return Response.status(Response.Status.OK).entity("Successfully confirm member.").build();
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("generate-team-name")
    @RolesAllowed("student")
    public Response generateTeamName(TeamParam request) {
        new TeamInterface().generateTeamName(request);
        return Response.status(Response.Status.OK).entity("Team name successfully generated").build(); 
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("leave")
    @RolesAllowed("student")
    public Response leaveTeam(TeamParam request) {
        new TeamInterface().leaveTeam(request);
        return Response.status(Response.Status.OK).entity("Student successfully leave team.").build();
    }


}




