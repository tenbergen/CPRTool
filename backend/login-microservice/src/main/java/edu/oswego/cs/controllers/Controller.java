package edu.oswego.cs.controllers;

import edu.oswego.cs.services.AuthServices;

import javax.annotation.security.RolesAllowed;
import javax.json.JsonException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.io.IOException;


@Path("/auth")
public class Controller {
    @POST
    @Path("token/generate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateToken(@Context HttpHeaders request) throws JsonException, IOException {
        System.out.println("Generating token!");
        if (request.getRequestHeader(HttpHeaders.AUTHORIZATION) == null)
            return Response.status(Response.Status.FORBIDDEN).entity("No token found.").build();

        String authToken = request.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0).split(" ")[1];
        return Response.status(Response.Status.OK).entity(new AuthServices().generateNewToken(authToken)).build();
    }

    @POST
    @RolesAllowed("lakers")
    @Path("token/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    public Response refreshToken(@Context SecurityContext securityContext) throws IOException {
        return Response.status(Response.Status.OK).entity(new AuthServices().refreshToken(securityContext)).build();

    }

}
