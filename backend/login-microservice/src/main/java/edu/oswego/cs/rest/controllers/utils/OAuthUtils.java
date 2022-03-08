package edu.oswego.cs.rest.controllers.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.api.services.oauth2.model.Userinfo;
import com.ibm.websphere.security.jwt.*;

import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.security.Key;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class OAuthUtils {
    static List<String> scopes = Arrays.asList(
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email");

    private static final String protocol = System.getenv("PROTOCOL");
    private static final String domain = System.getenv("DOMAIN");
    private static final String port = System.getenv("PORT");
    private static final String fullURL = protocol + "://" + domain + ":" + port;

    private static final String oauthClientId = System.getenv("CLIENT_ID");
    private static final String oauthClientSecret = System.getenv("CLIENT_SECRET");
    private static final String oauthAppName = System.getenv("APP_NAME");
    private static final String emailDomain = System.getenv("EMAIL_DOMAIN");

    public static GoogleAuthorizationCodeFlow flow;

    public static GoogleAuthorizationCodeFlow newFlow() throws IOException {
        flow = new GoogleAuthorizationCodeFlow.Builder(
                // Sends requests to the OAuth server
                new NetHttpTransport(),
                // Converts between JSON and Java
                JacksonFactory.getDefaultInstance(),
                // Your OAuth client ID
                oauthClientId,
                // Your OAuth client secret
                oauthClientSecret,
                // Tells the user what permissions they're giving you
                scopes)
                // Stores the user's credential in memory
                // @TODO Need to change this to DataStoreFactory with StoredCredential
                .setDataStoreFactory(MemoryDataStoreFactory.getDefaultInstance())
                .setAccessType("offline")
                .build();
        return flow;
    }

    // make sure if the user is logged in
    public static boolean isUserLoggedIn(String sessionID) {
        try {
            return newFlow().loadCredential(sessionID) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static Userinfo getUserInfo(String sessionId) throws IOException {
        Credential credential = newFlow().loadCredential(sessionId);
        Oauth2 oauth2Client =
                new Oauth2.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                        .setApplicationName(oauthAppName)
                        .build();

        return oauth2Client.userinfo().get().execute();
    }

    public static Tokeninfo getTokenInfo(String sessionId, String accessToken) throws IOException {
        Credential credential = newFlow().loadCredential(sessionId);
        Oauth2 oauth2Client =
                new Oauth2.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                        .setApplicationName(oauthAppName)
                        .build();

        return oauth2Client.tokeninfo().setAccessToken(accessToken).execute();
    }

    // make sure the logged in user is an @oswego.edu account
    public static boolean isOswego(String sessionId) {
        try {
            Userinfo userinfo = getUserInfo(sessionId);
            return userinfo.getHd().equals(emailDomain);
        } catch (Exception e) {
            return false;
        }
    }

    // JWTbuilder
    public static String buildJWT(String sessionId) throws IOException, JwtException, InvalidBuilderException, InvalidClaimException {

        String accessToken = newFlow().loadCredential(sessionId).getAccessToken();

        Userinfo userinfo = getUserInfo(sessionId);

        Set<String> roles = new HashSet<String>();
        roles.add("students");

        Key jwtSecret = new SecretKeySpec("SecretKey".getBytes(), "RS256");

        String jwtToken;
        try {
            jwtToken = JwtBuilder.create()
                    .claim(Claims.SUBJECT, userinfo.getEmail()) // subject (the user)
                    .claim("upn", userinfo.getEmail()) // user principle name
                    .claim("roles", roles.toArray(new String[roles.size()])) // group
                    // .claim("aud", "http://localhost:13126") // audience
                    .claim("aud", fullURL) // audience
                    .claim("access_token", accessToken) // access token from google
                    .claim("hd", userinfo.getHd())
                    .claim("first_name", userinfo.getGivenName())
                    .claim("last_name", userinfo.getFamilyName())
                    .claim("userID", userinfo.getId())
                    .signWith("HS256", "jwtSecret") // signWith won't work with key yet
                    .buildJwt().compact();
            return jwtToken;
        } catch (KeyException e) {
            e.printStackTrace();
            return "JWT Token is not available!";
        }
    }
}
