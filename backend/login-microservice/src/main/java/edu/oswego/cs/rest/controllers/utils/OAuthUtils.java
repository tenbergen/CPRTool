package edu.oswego.cs.rest.controllers.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
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

import javax.crypto.spec.SecretKeySpec;
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


    private static String oauthClientId = "952282231282-ned8emonjrqbhj8v5b8efcr94d3nh13j.apps.googleusercontent.com";
    // private static String oauthClientId = System.getenv("CLIENT_ID");

    private static String oauthClientSecret = "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";
    // private static String oauthClientSecret = System.getenv("CLIENT_SECRET");

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
                    .claim("aud", "http://moxie.cs.oswego.edu:13126") // audience
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

    public static Map<String, Object> getRSAKeys() throws Exception {
        // Key pair Generator 
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        
        KeyPair keyPair = keyPairGenerator.generateKeyPair();// generate key pair
        
        PrivateKey privateKey = keyPair.getPrivate(); // generate private
        String privateKeyString = (String) keyPair.getPrivate().toString(); // generate private
        
        PublicKey publicKey = keyPair.getPublic(); // generate public
        String publicKeyString = keyPair.getPublic().toString(); // generate public

        Path privatePath = Paths.get("/Users/logan/coding/CPR-480-22S/CSC480-22S-BE/backend/login-microservice/src/main/java/edu/oswego/cs/rest/controllers/utils/privateKey.txt");
        Path publicPath = Paths.get("/Users/logan/coding/CPR-480-22S/CSC480-22S-BE/backend/login-microservice/src/main/java/edu/oswego/cs/rest/controllers/utils/publicKey.txt");
        Files.write(privatePath, privateKeyString.getBytes());
        Files.write(publicPath, publicKeyString.getBytes());

        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put("private", privateKey);
        keys.put("public", publicKey);
        return keys;
    }


}
