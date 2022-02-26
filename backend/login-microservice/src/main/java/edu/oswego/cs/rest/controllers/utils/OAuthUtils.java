package edu.oswego.cs.rest.controllers.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.api.services.oauth2.model.Userinfo;

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
        String appName = System.getenv("APP_NAME");
        Credential credential = newFlow().loadCredential(sessionId);
        Oauth2 oauth2Client =
            new Oauth2.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(appName)
                .build();
      
        Userinfo userInfo = oauth2Client.userinfo().get().execute();
        return userInfo;
      }

      public static Tokeninfo getTokenInfo(String sessionId, String accessToken) throws IOException {
        String appName = System.getenv("APP_NAME");

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


}
