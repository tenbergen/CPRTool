package edu.oswego.cs.resources;

import edu.oswego.cs.database.TeamInterface;
import edu.oswego.cs.requests.SwitchTeamParam;
import edu.oswego.cs.requests.TeamParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/teams")
public class PeerReviewTeamsResource {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("team/create")
    public Response createTeam(TeamParam request) {
        new TeamInterface().createTeam(request);
        return Response.status(Response.Status.CREATED).entity("Team successfully created.").build();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("team/get/all")
    public Response getAllTeams(TeamParam request) {
        return Response.status(Response.Status.OK).entity(new TeamInterface().getAllTeams(request)).build();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("team/get/student_id")
    public Response getTeamByStudentID(TeamParam request) {
        return Response.status(Response.Status.OK).entity(new TeamInterface().getTeamByStudentID(request)).build();
    }

    @GET
    @Path("team/get/team_id")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeamByTeamID(TeamParam request) {
        return Response.status(Response.Status.OK).entity(new TeamInterface().getTeamByTeamID(request)).build();
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("team/join")
    public Response joinTeam(TeamParam request) {
        new TeamInterface().joinTeam(request);
        return Response.status(Response.Status.OK).entity("Student successfully added to team.").build();
    }
    
    @PUT
    @Path("team/switch")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response switchTeam(SwitchTeamParam request) {
        new TeamInterface().switchTeam(request);
        return Response.status(Response.Status.OK).entity("Student successfully switch to a new team.").build();
    }


    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("team/generate-team-name")
    public Response generateTeamName(String studenID, String targetedTeamID, String teamName) {
        /* DESCRIPTION
            - requirements 4.2.3-7 : teamLead creates a name for the team
            - requirements 4.2.3-8 & 4.2.3-9 : validate teamName (teamName is unique && !teamNames.contains(students' name in the team))
            - requirements 4.2.3-10 : a finalize button will appear after teamName is valid and team is full
            - only for team lead to create the name for the team
            - For FE,
                + if this API returns 200, that means
                    * 3-8 && 3-9 is checked true
                    * targetedTeam.isFull = true
                    * new TeamName is sucessfuly generated
                    => show the team name
                    => the team is ready to show the "finalize" button
                + if this API returns 400, that means
                    * 3-8 && 3-9 is not true (409 or 400)
                    * targetedTeam.isFull = false (400)
                    => the team is not ready to show the "finalize" button
        */

        /* TODO
            - find the targetedTeam in DB using targetedTeamID
            - make sure the param studentID is team lead's id
            - make sure the param targetedTeam.isFull = true
            - make sure the param teamName is unique
                + TeamInterface::isTeamNameUnique(String teamName) {
                    * loop through DB.teams (array), parse the team names and store the names into a String[] all team names array
                    * then compare the param teamName to the array all team names to make sure the param teamName is unique
                }
            - make sure the param teamName does not contains students' name
                + TeamInterface::containsName(String teamName){
                    * String[] teamNameArr = teamName.split(" ") // regex can be applied here for - _ / \
                    * ArrayList<String> memNameArr = memNameArr.add(memNames.split(" ")) // break into one word
                    * loop through memNameArr to make sure it doesn't contain any element in teamNameArr
                }

            - NOTES: Technically, TeamInterface::isTeamNameUnique && TeamInterface::containName can be wrapped in one method TeamInterface::validateTeamName()

            - If all the tests above passed,
                + targetedTeam.teamName = teamName
                + DB.teams.add(teamName) // Should we make an array for all the names of all teams (persisted)?
                                            Or parse the team names from DB everytime we do the isTeamNameUnique check?
                + HTTP 200 OK
            - If one of the test failed,
                + HTTP 400 BAD_REQUEST (409 CONFLICT if teamName is already existed)
        */

        return Response.status(Response.Status.OK).entity("Team Name Generated Successfully ").build(); // change the message

    }

    @PUT
    @Path("team/finalize-team")
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
    @Path("team/is-team-finalized")
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

}




