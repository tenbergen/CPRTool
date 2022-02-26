package edu.oswego.cs.rest.controllers;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.ibm.websphere.security.social.UserProfileManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends AbstractAuthorizationCodeServlet {
    

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException {
        return OAuthUtils.newFlow();
    }

    @Override
    protected String getRedirectUri(HttpServletRequest request) throws ServletException, IOException {
        GenericUrl url = new GenericUrl(request.getRequestURL().toString());
        url.setRawPath("/api/login-callback");
        return url.build();

    }

    @Override
    protected String getUserId(HttpServletRequest request) throws ServletException, IOException {
        return request.getSession().getId();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        PrintWriter pwriter = response.getWriter();
        String socialMediaName = UserProfileManager.getUserProfile().getSocialMediaName();
        pwriter.print(socialMediaName);
    }

   
    
}
