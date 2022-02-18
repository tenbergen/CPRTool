package edu.oswego.cs.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@RequestScoped
@Path("/login")
public class LoginResource {
    
    @GET
    public String Login() {
        return "Login Page";
    }
}
