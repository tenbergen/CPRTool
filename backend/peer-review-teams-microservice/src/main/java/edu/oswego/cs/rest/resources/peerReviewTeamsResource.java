package edu.oswego.cs.rest.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.bind.annotation.JsonbProperty;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.Document;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import edu.oswego.cs.rest.daos.TeamDAO;
import edu.oswego.cs.rest.database.TeamInterface;
import edu.oswego.cs.rest.requests.InitTeamRequest;

@Path("/teams")
public class peerReviewTeamsResource {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("team/professor/initialize")
    public String initTeam(@RequestBody InitTeamRequest initTeam) {
        /*
            scope: professor
            desc: Initialize teams for each course
            FE: 
                + This API returns an array of integer outlines the team size for each team for the whole course.
                + FE will use this array to decide the total teams each course has and how many team members each team has
                    i.e. create placeholders for each team
        */
        
        try {
            ArrayList<Integer> res = new TeamInterface().initTeamHandler(initTeam);
            JsonArrayBuilder builder = Json.createArrayBuilder();
            for (Integer value : res) {
                builder.add(value);
            }
            JsonArray teams = builder.build();
            return teams.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Team outline is not available";
        }

    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("team/create")
    public Response createTeam(String studentID, String newTeamID) { 
        /* DESCRIPTION:
                - requirements 4.2.3-1, 4.2.3-2   
                - Happen right after a team lead hit generate team button
                - Team name is not available at this point so teamID is the pointer
                - The FE just need to send the newTeamID:string and studentID:string along with the HTTP request
        */
        try {

            /* TODO
                TeamInterface::createTeamHandler(String studentID, newTeamID) {
                    Compare DB.max_teams with DB.team_counts
                    - if team_counts < max_teams
                        + make a new TeamDAO instance
                            * TeamDAO newTeamDAO = new TeamDAO(newTeamID);
                        + Find the student with the param studentID 
                            * make this student team lead
                        + make a HashMap<String, boolean> members = {teamLead=false} 
                        + push the new hashmap members to the newTeamDAO
                            * newTeamDAO.teamMembers = members;
                        + add the team to DB
                        + DB.team_counts += 1
                        + HTTP 200 OK 

                    - if team_counts >= max_teams 
                        + HTTP 400 BAD REQUEST
                }
            */
             
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Too Many Teams").build();
        }
        return Response.status(Response.Status.OK).entity("Team Successfully generated").build();

    }


    @GET
    @Path("team/getallteams")
    public ArrayList<TeamDAO> getAllTeams() { 
        /* DESCRIPTION
                - requirements 4.2.3-3 : display non-full teams for non-team student
                - To non-team students, return only non-full-teams
                - To alread-in-a-team students, return its team
                - FE can apply the logic above ^
        */

        /* TODO

                - TeamInterface::getNonFullTeams() {
                    - find the targetedTeam in DB using targetedTeamID
                
                    if targetedTeam.teamMembers.size() + targetedTeam.memRequest.length < DB.max_team_member_count => TeamDAO.isFull = false
                        ***(max_team_member_count is provided by the Profeessor once he/she created the course) ***

                    return a list of non-full teams
                 }

                Then, 
                if (studentDAO.teamID = " " || NULL) {
                    call TeamInterface::getNonFull to get a list of non-full teams
                }
                if (studentDAO.teamID = "a team ID" ie. not NULL) {
                    get a list which contains only the team that has the same teamID
                }
        */

        return new ArrayList<TeamDAO>(List.of(new TeamDAO("teamID"))); // change this
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("team/join/request")
    public Response joinRequest(String studentID, String requestedTeamID) { 
        /* DESCRIPTION
                - requirements 4.2.3-4 : request to join a team
                - scope: only for non-team students
                - task: 
                    + add the student with studentID into the requestedTeam.memRequest
                    + update requestedTeam.isFull if needed
                - FE: after a student hit the "request to join" button
                    + if requestedTeam.isFull = true => 400 BAD_REQUEST
                    + else => 200 OK successfully add the student waitlist
        */

        /* TODO
            if (requestedTeam.isFull) {
                + HTTP 400 BAD_REQUEST
            } else {
                - TeamInterface::joinRequestHandler(String studentID, String requestedTeamID) {
                    - find the targetedTeam in DB using targetedTeamID
                    - find the requestingStudent in DB using studentID
                    - finally, add the requestingStudent to the targetedTeam.memRequests array (this means the request is pending)
                    - notes, update requestedTeam.isFull if .memRequests.size() + .teamMembers.size() = DB.max_team_member_count
                    - HTTP 200 success
                }
            }
        */
             
        return Response.status(Response.Status.OK).entity("Successfully added student to requested team's waitlist").build(); // change the message
    }


    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("team/join/request/getjoinrequests") 
    public ArrayList<String> getJoinRequests(String studenID, String requestedTeamID) { 
        /* DESCRIPTION
                - requirements 4.2.3-5 : notify team lead if there is/are new join request(s)
                - scope: only for team lead 
                - task: returns the list of joinRequests
                - FE: should always call this rest API for team lead 
                    + if this api return a list.size() != 0 => notify teamLead
                    + if this api return an empty list i.e. list.size() == 0 => do nothing
                    + limit this opetion to teamLead only 
        */

        /* TODO
            - TeamInterface::getJoinRequests(String studentID, String requestedTeamID) {
                - Make sure the student is team lead (just for safety purpose)
                - find the requestedTeam in DB using requestedTeamID
                - find the teamLead in the requestedTeam using studentID

                if (teamLead != null) {
                    requestedTeam.memRequests.size() != 0 ? return ArrayList<String> memRequests : empty ArrayList<String> memRequests
                    i.e.: return ArrayList<String> memRequest. FE can decided what to do if the list is empty or not
                }
            }

        */

        return new ArrayList<String>(List.of("studentID")); // change this 
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("team/join/request/accept")
    public Response accpetJoinRequest(String studentID, String requestedTeamID, String requestingStudentID) { 
        /* DESCRIPTION
                - requirements 4.2.3-4.2 : accept a join request
                - scope: only for teamlead
                - task: 
                    + allow team lead to accept the join request
                    + add the student to the requestedTeam.teamMember hashmap
                    + update requestedTeam.isFull if needed
                    + update requestingStudent.teamID = requestedTeamID
                - FE: 
                    + if this api returns 200 => sucessfully accept the student => inform the requestingStudent
                    + if this api returns 400 => fail to accept the student => do nothing
        */

        /* TODO
            - TeamInterface::acceptJoinRequestHandler(String studentID, String requestedTeamID, String requestingStudentID) {
                - find the requestedTeam in DB using requestedTeamID
                - find the teamLead in the requestedTeam using studentID
                - find the requestingStudentID by looping through the requestedTeam.memRequest
                    + add the requestingStudentID to the requestedTeam.teamMembers hashmap
                        * requestedTeam.teamMembers.put(requestingStudentID, "false")
                    + drop the requestingStudentID from the requestedTeam.memRequests array 
                    + update requestingStudent.teamID = requestedTeamID
                - notes, update requestedTeam.isFull if .teamMembers.size() + .memRequests.size() = DB.max_team_member_count
                    + just for safety purpose since .isFull should be updated in joinRequest API
            }
            - Exception here?
        */

        return Response.status(Response.Status.OK).entity("Sucessfully accepted the requesting student").build(); // change the message

    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("team/join/request/reject")
    public Response rejectJoinRequest(String studentID, String requestedTeamID, String requestingStudentID) { 
        /* DESCRIPTION
                - requirements 4.2.3-4.3 : reject a join request
                - scope: only for teamlead
                - task: 
                    + allow team lead to reject the join request
                    + drop the requestingStudentID from requestedTeam.memRequests array
                    + update requestedTeam.isFull if needed (since .memRequests.size() is now -= 1 but .teamMembers.size() stays the same)
                - FE: 
                    + if this api returns 200 => sucessfully reject the student => inform the requestingStudent
                    + if this api returns 400 => fail to reject the student => show Error then do nothing
        */

        /* TODO
            - TeamInterface::rejectJoinRequestHandler(String studentID, String requestedTeamID, String requestingStudentID) {
                - find the requestedTeam in DB using requestedTeamID
                - find the teamLead in the requestedTeam using studentID
                - find the requestingStudentID by looping through the requestedTeam.memRequest
                    + drop the requestingStudentID from the requestedTeam.memRequests array 
                - notes, update requestedTeam.isFull since .memRequests.size() is now -= 1 but .teamMembers.size() stays the same
            }
            - Exception here?
        */

        return Response.status(Response.Status.OK).entity("Sucessfully accepted the requesting student").build(); // change the message

    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("team/generateteamname") 
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
    @Path("team/finalizeteam") 
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
    @Path("team/isteamfinalized")
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
