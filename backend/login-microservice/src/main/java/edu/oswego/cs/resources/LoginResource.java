package edu.oswego.cs.resources;

import jakarta.enterprise.context.RequestScoped;

import javax.ws.rs.*;

@RequestScoped
@Path("/login")
public class LoginResource {
    
    @GET
    public String Login() {
        return "Login Page";
    }
}
