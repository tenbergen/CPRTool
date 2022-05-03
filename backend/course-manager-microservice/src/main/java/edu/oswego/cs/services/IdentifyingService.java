package edu.oswego.cs.services;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public class IdentifyingService {
    public void identifyingProfessorService(SecurityContext securityContext, String professorID) {
        String userID = securityContext.getUserPrincipal().getName().split("@")[0];
        if (!professorID.equals(userID))
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity("User principal name doesn't match.").build());
    }
}
