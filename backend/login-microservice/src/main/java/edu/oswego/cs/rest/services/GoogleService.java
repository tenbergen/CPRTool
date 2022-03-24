package edu.oswego.cs.rest.services;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GoogleService {
    private final String CLIENT_ID = "644041850309-32m3qpk5jlq07pmqem0tasjph8ge77pp.apps.googleusercontent.com"; // System.getenv("CLIENT_ID");


    protected Payload validateToken(String token) {
        GoogleIdToken idToken = verifyToken(token);
        if (idToken == null) {
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).entity("401 Invalid Token").build());
        }
        Payload payload = idToken.getPayload();
        return payload;
    }

    protected GoogleIdToken verifyToken(String token) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
                    new GsonFactory())
                            .setAudience(Collections.singletonList(CLIENT_ID))
                            .build();

            return verifier.verify(token);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
    }
}
