package edu.oswego.cs.rest.services;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.ibm.websphere.security.jwt.InvalidBuilderException;
import com.ibm.websphere.security.jwt.InvalidClaimException;
import com.ibm.websphere.security.jwt.JwtBuilder;
import com.ibm.websphere.security.jwt.JwtException;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import edu.oswego.cs.rest.database.DatabaseManager;

public class AuthServices {
    private MongoDatabase profDB;
    private final String profID = "professor_id";
    GoogleService googleService = new GoogleService();
  
    public AuthServices() {
        DatabaseManager databaseManager = new DatabaseManager();
        try {
            profDB = databaseManager.getProfDB();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public String generateNewToken(String token) {
        Payload payload = googleService.validateToken(token);
        Set<String> roles = new HashSet<>();
        if (payload == null) {
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).entity("401 Invalid Token").build());
        }

        if (isProfessor(token)) {
            roles.add("professor");
        } else {
            roles.add("student");
        }

        try {
            String newToken = JwtBuilder.create("cpr22s")
                .claim("sub", payload.getSubject())
                .claim("email", payload.getEmail())
                .claim("hd", payload.getHostedDomain())
                .claim("name", payload.get("name"))
                .claim("roles", roles)
                .claim("aud", "CPR22S480")
                .buildJwt().compact();
            return newToken;
        } catch (JwtException e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("404 Token Not Available").build());
        } catch (InvalidBuilderException e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("404 Token Not Available").build());
        } catch (InvalidClaimException e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("404 Token Not Available").build());
        }
    }

    protected boolean isProfessor(String token) {
        Payload payload = googleService.validateToken(token);
        if (payload == null) {
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).entity("401 Invalid Token").build());
        }
        String userID = payload.getEmail().split("@")[0];
        if (profDB.getCollection("professors").find(new Document(profID, userID)).first() != null) {
            return true;
        }
        return false;
    }

}
