package edu.oswego.cs.rest.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/handler")
public class Controller {
    @GET
    public String handler() {
        return "handler";
    }
}
