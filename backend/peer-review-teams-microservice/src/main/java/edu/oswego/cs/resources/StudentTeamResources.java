package edu.oswego.cs.resources;

import edu.oswego.cs.database.TeamInterface;
import edu.oswego.cs.requests.SwitchTeamParam;
import edu.oswego.cs.requests.TeamParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/teams/team")
public class StudentTeamResources {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("create")
    public Response createTeam(TeamParam request) {
        new TeamInterface().createTeam(request);
        return Response.status(Response.Status.CREATED).entity("Team successfully created.").build();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("get/student_id")
    public Response getTeamByStudentID(TeamParam request) {
        return Response.status(Response.Status.OK).entity(new TeamInterface().getTeamByStudentID(request)).build();
    }

    @GET
    @Path("get/team_id")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeamByTeamID(TeamParam request) {
        return Response.status(Response.Status.OK).entity(new TeamInterface().getTeamByTeamID(request)).build();
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("join")
    public Response joinTeam(TeamParam request) {
        new TeamInterface().studentJoinTeam(request);
        return Response.status(Response.Status.OK).entity("Student successfully join team.").build();
    }
    
    @PUT
    @Path("switch")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("leave")
    public Response leaveTeam(TeamParam request) {
        new TeamInterface().leaveTeam(request);
        return Response.status(Response.Status.OK).entity("Student successfully leave team.").build();
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("generate-team-name")
    public Response generateTeamName(TeamParam request) {
        new TeamInterface().generateTeamName(request);
        return Response.status(Response.Status.OK).entity("Team name successfully generated").build(); 
    }

    // student unimously agree to team name (agree-to-team-name) => create a securityCheck allAgreedTeamName to check generateTeamName

    @PUT
    @Path("finalize-team")
    public Response finalizeTeam(String studenID, String targetedTeamID) {

        /* DESCRIPTION
            - requirements 4.2.3-11: Make sure the whole team click the "finalize button" to move on with the course (see other assignments)
            - Every one in the team can see the finalize button
            - If one clicks the finalize button, it triggers this API
            - For FE,
                + If this API returns 200 => the student successfully click the finalize button
                + If this API returns 400 => do nothing
        */

        /* TODO
            - Build TeamInterface::finalizeHandler() {
                + find the targetedTeam in DB using targetedTeamID
                + loop through the TargetdTeam.teamMembers.keySet()
                    * to get the element with the same studentID
                    * then update the value from false to true
                + then another loop through the TartedTeam.teamMembers hashmap to see if
                    * all the members' value is set to true => update targetedTeam.isFinalized to true
                    * at least one member's value is still false => do nothing (leave the .isFanalized as default i.e. false)
            }

            - Any exceptions here?
        */

        return Response.status(Response.Status.OK).entity("Finalize Successfully ").build(); // change the message
    }

    @GET
    @Path("is-team-finalized")
    public Boolean isTeamFinallized(String targetedTeamID) {
        /* DESCRIPTION
            - requirements 4.2.3-11: Make sure the whole team click the "finalize button" to move on with the course (see other assignments)
            - This API is particularly for FE
            - For FE,
                + If this API returns true  => the team is finalized
                + If this API returns false => do nothing
        */

        /* TODO
            - TeamInterface::isTeamFinalized() {
                + find the targetedTeam in DB using targetedTeamID
                + check targetedTeam.isFinalized
                    * if true   => the team is finalized
                    * if false  => do nothing
            }

        */

        return false; // change the message
    }

    /* TODO: 
        + Move/remove a user from a team (professor)
        + Delete Team (members.size() == 1) (TL + professor)
    */

}




