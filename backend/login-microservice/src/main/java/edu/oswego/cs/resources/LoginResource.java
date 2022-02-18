package edu.oswego.cs.resources;

import jakarta.enterprise.context.RequestScoped;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("/login")
public class LoginResource {
    
    @GET
    public String Login() {
        return "Login Page";
    }
}
