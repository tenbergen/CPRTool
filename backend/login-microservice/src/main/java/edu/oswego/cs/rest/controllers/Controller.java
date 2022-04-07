package edu.oswego.cs.rest.controllers;

import javax.json.JsonException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edu.oswego.cs.rest.database.ProfessorCheck;
import edu.oswego.cs.rest.services.AuthServices;

import java.io.IOException;


@Path("/auth")
public class Controller {
    @POST
    @Path("token/generate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateToken(@Context HttpHeaders request) throws JsonException{
        if (request.getRequestHeader(HttpHeaders.AUTHORIZATION) == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("No token found.").build();
        }
        String authToken = request.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0).split(" ")[1];
        String newToken = new AuthServices().generateNewToken(authToken);
        return Response.status(Response.Status.OK).entity(newToken).build();
    }
    @GET
    @Path("check")
    public String checkProfessor() throws IOException {
        new ProfessorCheck();
        return "Done";
    }
    
}
