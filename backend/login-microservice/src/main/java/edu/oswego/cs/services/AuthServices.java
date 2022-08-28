package edu.oswego.cs.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.ibm.websphere.security.jwt.InvalidBuilderException;
import com.ibm.websphere.security.jwt.InvalidClaimException;
import com.ibm.websphere.security.jwt.JwtBuilder;
import com.ibm.websphere.security.jwt.JwtException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.database.DatabaseManager;
import edu.oswego.cs.database.ProfessorCheck;
import org.bson.Document;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;

public class AuthServices {
    GoogleService googleService = new GoogleService();
    private MongoCollection<Document> professorCollection;

    public AuthServices() {
        DatabaseManager databaseManager = new DatabaseManager();
        try {
            new ProfessorCheck();
            MongoDatabase professorDB = databaseManager.getProfessorDB();
            professorCollection = professorDB.getCollection("professors");
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Map<String, String> generateNewToken(String token) {
        Payload payload = googleService.validateToken(token);
        if (payload == null)
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).entity("Invalid token.").build());

        Map<String, String> tokens = new HashMap<>();

        String lakerID = payload.getEmail().split("@")[0];
        Set<String> roles = getRoles(lakerID);

        try {
            String access_token = JwtBuilder.create("cpr_access")
                    .claim("sub", payload.getSubject())
                    .claim("upn", payload.getEmail())
                    .claim("full_name", payload.get("name"))
                    .claim("laker_id", lakerID)
                    .claim("groups", roles)
                    .claim("aud", "cpr")
                    .claim("iss", "cpr")
                    .buildJwt().compact();

            String refresh_token = JwtBuilder.create("cpr_refresh")
                    .claim("sub", payload.getSubject())
                    .claim("upn", payload.getEmail())
                    .claim("full_name", payload.get("name"))
                    .claim("groups", Collections.singleton("lakers"))
                    .claim("aud", "cpr")
                    .claim("iss", "cpr")
                    .buildJwt().compact();

            tokens.put("access_token", access_token);
            tokens.put("refresh_token", refresh_token);

            return tokens;

        } catch (JwtException | InvalidBuilderException | InvalidClaimException e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Unable to find token.").build());
        }
    }

    public Map<String, String> refreshToken(SecurityContext securityContext) {
        Principal user = securityContext.getUserPrincipal();
        JsonWebToken payload = (JsonWebToken) user;
        if (payload == null)
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).entity("JWT is not available.").build());

        Map<String, String> tokens = new HashMap<>();

        String lakerID = payload.getName().split("@")[0];
        Set<String> roles = getRoles(lakerID);

        try {
            String access_token = JwtBuilder.create("cpr_access")
                    .claim("sub", payload.getSubject())
                    .claim("upn", payload.getName())
                    .claim("full_name", payload.getClaim("full_name"))
                    .claim("laker_id", lakerID)
                    .claim("groups", roles)
                    .claim("aud", "cpr")
                    .claim("iss", "cpr")
                    .buildJwt().compact();
            tokens.put("access_token", access_token);
        } catch (JwtException | InvalidBuilderException | InvalidClaimException e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Unable to find token.").build());
        }

        return tokens;
    }

    public Set<String> getRoles(String lakerID) {
        Set<String> roles = new HashSet<>();

        if (professorCollection.find(eq("professor_id", lakerID)).first() != null) {
            roles.add("professor");
        } else {
            roles.add("student");
        }
        if (roles.size() == 0)
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Can't connect to database.").build());

        return roles;
    }

}
