package edu.oswego.cs.rest.controllers;

import javax.json.JsonException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edu.oswego.cs.rest.services.AuthServices;


@Path("/auth")
public class Controller {
    
    @POST
    @Path("generate-token")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateToken(@Context HttpHeaders request) throws JsonException{
        if (request.getRequestHeader(HttpHeaders.AUTHORIZATION) == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("403 No Token found").build();
        }
        String authToken = request.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0).split(" ")[1];
        String newToken = new AuthServices().generateNewToken(authToken);

        return Response.status(Response.Status.OK).entity(newToken).build();
    }
}
