package edu.oswego.cs.rest.util;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class CPRException extends WebApplicationException {
    public CPRException(Response.Status responseStatus, String errorMessage) {
        super(Response.status(responseStatus).entity(errorMessage).build());
    }
}
