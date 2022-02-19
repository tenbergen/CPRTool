package edu.oswego.cs.resources;

import jakarta.enterprise.context.RequestScoped;

import javax.ws.rs.*;

@RequestScoped
@Path("/loginpage")
public class LoginResource {
    
    @GET
    public String Login() {
        return "Login Page";
    }
}
