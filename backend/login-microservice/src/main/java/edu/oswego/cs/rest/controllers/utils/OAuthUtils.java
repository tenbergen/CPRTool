package edu.oswego.cs.rest.controllers.utils;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

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

@ApplicationScoped
public class OAuthUtils {
    static List<String> scopes = Arrays.asList(
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email");

    private static final String fullURL = "http://localhost:13126"; //System.getenv("REACT_APP_URL");
    private static final String oauthClientId = "952282231282-ned8emonjrqbhj8v5b8efcr94d3nh13j.apps.googleusercontent.com";//System.getenv("CLIENT_ID");
    private static final String oauthClientSecret = "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";//System.getenv("CLIENT_SECRET");
    private static final String oauthAppName = "cpr"; //System.getenv("APP_NAME");
    private static final String emailDomain = "oswego.edu"; //System.getenv("EMAIL_DOMAIN");

    public static GoogleAuthorizationCodeFlow flow;

    public static GoogleAuthorizationCodeFlow newFlow() throws IOException {
        flow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), // Sends requests to the OAuth server
                JacksonFactory.getDefaultInstance(), // Converts between JSON and Java
                oauthClientId,
                oauthClientSecret,
                scopes) // Tells the user what permissions they're giving you
                .setDataStoreFactory(MemoryDataStoreFactory.getDefaultInstance()) // Stores the user's credential in memor
                .setAccessType("offline")                                         // @TODO Need to change this to DataStoreFactory with StoredCredential
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

    // userInfo
    public static Userinfo getUserInfo(String sessionId) throws IOException {
        Credential credential = newFlow().loadCredential(sessionId);
        Oauth2 oauth2Client =
                new Oauth2.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                        .setApplicationName(oauthAppName)
                        .build();

        return oauth2Client.userinfo().get().execute();
    }

    //tokenInfo
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

        // init
        String accessToken = newFlow().loadCredential(sessionId).getAccessToken();
        Userinfo userinfo = getUserInfo(sessionId);
        Set<String> roles = new HashSet<String>();
        roles.add("students");
        

        // Generate RSA Keys
        Map<String, Object> rsaKeys = null;
        
        try {
            rsaKeys = getRSAKeys();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // get public/private key
        PublicKey publicKey = (PublicKey) rsaKeys.get("public");
        PrivateKey privateKey = (PrivateKey) rsaKeys.get("private");

        String jwtToken;
        try {
            jwtToken = JwtBuilder.create()
                    .claim(Claims.SUBJECT, userinfo.getEmail()) // subject (the user)
                    .claim("upn", userinfo.getEmail()) // user principle name
                    .claim("roles", roles.toArray(new String[roles.size()])) // group
                    .claim("aud", fullURL) // audience
                    .claim("access_token", accessToken) // access token from google
                    .claim("hd", userinfo.getHd())
                    .claim("first_name", userinfo.getGivenName())
                    .claim("last_name", userinfo.getFamilyName())
                    .claim("userID", userinfo.getId())
                    .signWith("RS512", privateKey) // signWith won't work with key yet
                    .buildJwt().compact();
            return jwtToken;
        } catch (KeyException e) {
            e.printStackTrace();
            return "JWT Token is not available!";
        }
    }
    // getRSAKeys()
    public static Map<String, Object> getRSAKeys() throws Exception {
        // Key pair Generator 
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        
        KeyPair keyPair = keyPairGenerator.generateKeyPair();// generate key pair
        
        // gen privateKey
        PrivateKey privateKey = keyPair.getPrivate(); // generate private

        // gen publicKey        
        PublicKey publicKey = keyPair.getPublic(); // generate public

        // store into a keys:map
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put("private", privateKey);
        keys.put("public", publicKey);
        return keys;
    }
}
