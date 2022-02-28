package edu.oswego.cs.rest.controllers.utils;

import java.io.IOException;
import java.security.Key;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.crypto.spec.SecretKeySpec;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.api.services.oauth2.model.Userinfo;
import com.ibm.websphere.security.jwt.Claims;
import com.ibm.websphere.security.jwt.InvalidBuilderException;
import com.ibm.websphere.security.jwt.InvalidClaimException;
import com.ibm.websphere.security.jwt.JwtBuilder;
import com.ibm.websphere.security.jwt.JwtException;
import com.ibm.websphere.security.jwt.KeyException;


public class OAuthUtils {
    static List<String> scopes = Arrays.asList(
  "https://www.googleapis.com/auth/userinfo.profile",
  "https://www.googleapis.com/auth/userinfo.email");

    private static String oauthClientId = "952282231282-ned8emonjrqbhj8v5b8efcr94d3nh13j.apps.googleusercontent.com";

    private static String oauthClientSecret = "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";

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
        String appName = "CPR480S22";
        Credential credential = newFlow().loadCredential(sessionId);
        Oauth2 oauth2Client =
            new Oauth2.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(appName)
                .build();
      
        Userinfo userInfo = oauth2Client.userinfo().get().execute();
        return userInfo;
      }

      public static Tokeninfo getTokenInfo(String sessionId, String accessToken) throws IOException {
        String appName = "CPR480S22";

        Credential credential = newFlow().loadCredential(sessionId);
        Oauth2 oauth2Client =
            new Oauth2.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(appName)
                .build();
         
        Tokeninfo tokenInfo = oauth2Client.tokeninfo().setAccessToken(accessToken).execute();
        return tokenInfo;
      }

    // make sure the logged in user is an @oswego.edu account
    public static boolean isOswego(String sessionId) {
        try {
            Userinfo userinfo = getUserInfo(sessionId);
            return userinfo.getHd().equals("oswego.edu");
        } catch (Exception e) {
            return false;

        }
    }

    // JWTbuilder
    public static String buildJWT(String sessionId) throws IOException, JwtException, InvalidBuilderException, InvalidClaimException {

        String accessToken = newFlow().loadCredential(sessionId).getAccessToken();
        
        Userinfo userinfo = getUserInfo(sessionId);
        Tokeninfo tokeninfo = getTokenInfo(sessionId, accessToken);
        
        Set<String> roles = new HashSet<String>();
        roles.add("students");

        Key jwtSecret = new SecretKeySpec("What should I put here?".getBytes(), "RS256");

        String jwtToken;
        try {
            jwtToken = JwtBuilder.create()
                                        .claim(Claims.SUBJECT, userinfo.getEmail()) // subject (the user)
                                        .claim("upn", userinfo.getEmail()) // user principle name
                                        .claim("groups", roles.toArray(new String[roles.size()])) // group
                                        .claim("aud", "http://localhost:13126") // audience
                                        .claim("access_token", accessToken) // access token from google
                                        .claim("hd", userinfo.getHd())
                                        .signWith("HS256", "jwtSecret")
                                        .buildJwt().compact();
                                        return jwtToken;
        } catch (KeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "qweqwe";
        }

    } 


}
