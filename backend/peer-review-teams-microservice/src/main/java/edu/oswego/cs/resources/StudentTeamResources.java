package edu.oswego.cs.resources;

import edu.oswego.cs.database.TeamInterface;
import edu.oswego.cs.requests.TeamParam;
import edu.oswego.cs.services.IdentifyingService;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/teams/team")
@DenyAll
public class StudentTeamResources {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("create")
    @RolesAllowed({"professor","student"})
    public Response createTeam(@Context SecurityContext securityContext, TeamParam request) {
        // new IdentifyingService().identifyingStudentService(securityContext, request.getStudentID());
        new TeamInterface().createTeam(request);
        return Response.status(Response.Status.CREATED).entity("Team successfully created.").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("get/all/{course_id}")
    @RolesAllowed({"professor", "student"})
    public Response getAllUnlockedTeam(@PathParam("course_id") String courseID) {
        return Response.status(Response.Status.OK).entity(new TeamInterface().getAllTeams(courseID)).build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("get/{course_id}/{student_id}")
    @RolesAllowed({"professor", "student"})
    public Response getTeamByStudentID(@Context SecurityContext securityContext, @PathParam("course_id") String courseID, @PathParam("student_id") String studentID) {
        // new IdentifyingService().identifyingStudentService(securityContext, studentID);
        return Response.status(Response.Status.OK).entity(new TeamInterface().getTeamByStudentID(courseID, studentID)).build();
    }

    @GET
    @Path("{course_id}/get/{team_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"professor","student"})
    public Response getTeamByTeamID(@Context SecurityContext securityContext, @PathParam("course_id") String courseID, @PathParam("team_id") String teamID) {
        return Response.status(Response.Status.OK).entity(new TeamInterface().getTeamByTeamID(courseID, teamID)).build();
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("join")
    @RolesAllowed({"professor", "student"})
    public Response joinTeam(@Context SecurityContext securityContext, TeamParam request) {
        // new IdentifyingService().identifyingStudentService(securityContext, request.getStudentID());
        new TeamInterface().joinTeam(request);
        return Response.status(Response.Status.OK).entity("Student successfully join team.").build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("team-lead/step-down")
    @RolesAllowed("admin")
    /* deprecated */
    public Response giveUpTeamLead(@Context SecurityContext securityContext, TeamParam request) {
        // new IdentifyingService().identifyingStudentService(securityContext, request.getStudentID());
        new TeamInterface().giveUpTeamLead(request);
        return Response.status(Response.Status.OK).entity("Team Lead successfully updated.").build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("team-lead/cede")
    @RolesAllowed("admin")
    /* deprecated */
    public Response nominateTeamLead(@Context SecurityContext securityContext, TeamParam request) {
        // new IdentifyingService().identifyingStudentService(securityContext, request.getStudentID());
        new TeamInterface().nominateTeamLead(request);
        return Response.status(Response.Status.OK).entity("Team Lead successfully updated.").build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("member/confirm/toggle")
    @RolesAllowed("admin")
    /* deprecated */
    public Response memberConfirmToggle(@Context SecurityContext securityContext, TeamParam request) {
        // new IdentifyingService().identifyingStudentService(securityContext, request.getStudentID());
        new TeamInterface().memberConfirmToggle(request);
        return Response.status(Response.Status.OK).entity("Confirmed members successfully updated.").build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("generate-team-name")
    @RolesAllowed("admin")
    /* deprecated */
    public Response generateTeamName(@Context SecurityContext securityContext, TeamParam request) {
        // new IdentifyingService().identifyingStudentService(securityContext, request.getStudentID());
        new TeamInterface().generateTeamName(request);
        return Response.status(Response.Status.OK).entity("Team name successfully generated").build(); 
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("leave")
    @RolesAllowed("admin")
    /* deprecated */
    public Response leaveTeam(@Context SecurityContext securityContext, TeamParam request) {
        // new IdentifyingService().identifyingStudentService(securityContext, request.getStudentID());
        new TeamInterface().removeTeamMember(request);
        return Response.status(Response.Status.OK).entity("Student successfully leave team.").build();
    }
}




