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
        getEnv();
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

    public void getEnv() {
        System.out.println("URL = " + System.getenv("URL"));
        System.out.println("JWK_ACCESS_URL = " + System.getenv("JWK_ACCESS_URL"));
        System.out.println("JWK_REFRESH_URL = " + System.getenv("JWK_REFRESH_URL"));
        System.out.println("CLIENT_ID = " + System.getenv("CLIENT_ID"));
        System.out.println("CLIENT_SECRET = " + System.getenv("CLIENT_SECRET"));
        System.out.println("MONGO_INITDB_DATABASE = " + System.getenv("MONGO_INITDB_DATABASE"));
        System.out.println("MONGO_INITDB_ROOT_USERNAME = " + System.getenv("MONGO_INITDB_ROOT_USERNAME"));
        System.out.println("MONGO_INITDB_ROOT_PASSWORD = " + System.getenv("MONGO_INITDB_ROOT_PASSWORD"));
        System.out.println("LOCALHOST = " + System.getenv("LOCALHOST"));
        System.out.println("MONGO_PORT = " + System.getenv("MONGO_PORT"));
        System.out.println("MONGO2_PORT = " + System.getenv("MONGO2_PORT"));
        System.out.println("MONGO3_PORT = " + System.getenv("MONGO3_PORT"));
        System.out.println("MONGO4_PORT = " + System.getenv("MONGO4_PORT"));
        System.out.println("MONGO5_PORT = " + System.getenv("MONGO5_PORT"));
        System.out.println("MONGO_USERNAME = " + System.getenv("MONGO_USERNAME"));
        System.out.println("MONGO_PASSWORD = " + System.getenv("MONGO_PASSWORD"));
        System.out.println("MONGO_DATABASE = " + System.getenv("MONGO_DATABASE"));

    }
}
