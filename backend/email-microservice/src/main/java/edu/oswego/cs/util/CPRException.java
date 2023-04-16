package edu.oswego.cs.util;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class CPRException extends WebApplicationException {
    public CPRException(Response.Status responseStatus, String errorMessage) {
        super(Response.status(responseStatus).entity(errorMessage).build());
    }
}
