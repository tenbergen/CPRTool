package edu.oswego.cs.rest.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.ibm.websphere.security.jwt.InvalidBuilderException;
import com.ibm.websphere.security.jwt.InvalidClaimException;
import com.ibm.websphere.security.jwt.JwtBuilder;
import com.ibm.websphere.security.jwt.JwtException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.rest.database.DatabaseManager;
import org.bson.Document;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Set;

import static com.mongodb.client.model.Filters.eq;

public class AuthServices {
    private MongoCollection<Document> professorCollection;
    GoogleService googleService = new GoogleService();

    public AuthServices() {
        DatabaseManager databaseManager = new DatabaseManager();
        try {
            MongoDatabase professorDB = databaseManager.getProfessorDB();
            professorCollection = professorDB.getCollection("professors");
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public String generateNewToken(String token) {
        Payload payload = googleService.validateToken(token);
        Set<String> roles = new HashSet<>();
        if (payload == null) 
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).entity("Invalid token.").build());
        
        String lakerID = payload.getEmail().split("@")[0];
        
        if (professorCollection.find(eq("professor_id", lakerID)).first() != null) {
            roles.add("professor");
        } else {
            roles.add("student");
        }
        
        try {
            return JwtBuilder.create("cpr22s")
                    .claim("sub", payload.getSubject())
                    .claim("upn", payload.getEmail())
                    .claim("name", payload.get("name"))
                    .claim("lakerID", lakerID)
                    .claim("groups", roles)
                    .claim("aud", "CPR.22S.480")
                    .claim("iss", "edu.oswego.cs-CPR.22S.480")
                    .buildJwt().compact();
        } catch (JwtException | InvalidBuilderException | InvalidClaimException e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Unable to find token.").build());
        }
    }

}