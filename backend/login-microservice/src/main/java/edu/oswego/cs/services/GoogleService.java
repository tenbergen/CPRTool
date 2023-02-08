package edu.oswego.cs.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.NoArgsConstructor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@NoArgsConstructor
public class GoogleService {

    protected Payload validateToken(String token) {
        System.out.println("Here's the token");
        System.out.println(token);
        GoogleIdToken idToken = verifyToken(token);
        System.out.println(idToken==null);
        if (idToken == null)
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).entity("Invalid token.").build());

        return idToken.getPayload();
    }

    protected GoogleIdToken verifyToken(String token) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                    .Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(System.getenv("CLIENT_ID")))
                    .setIssuer("https://accounts.google.com")
                    .build();
            System.out.println(verifier.verify(token));
            return verifier.verify(token);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
    }
}