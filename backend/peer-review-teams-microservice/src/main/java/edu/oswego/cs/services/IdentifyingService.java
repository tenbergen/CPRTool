package edu.oswego.cs.services;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public class IdentifyingService {
    public void identifyingStudentService(SecurityContext securityContext, String studentID) {
        if (!securityContext.isUserInRole("professor")) {
            String userID = securityContext.getUserPrincipal().getName().split("@")[0];
            if (!studentID.equals(userID))
                throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity("User principal name doesn't match.").build());
        }
    }
}
