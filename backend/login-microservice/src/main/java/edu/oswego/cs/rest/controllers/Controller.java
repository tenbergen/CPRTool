package edu.oswego.cs.rest.controllers;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/auth")
public class Controller {
    @POST
    @Path("generate-token")
    public String gene() {
        return "handler";
    }
}
