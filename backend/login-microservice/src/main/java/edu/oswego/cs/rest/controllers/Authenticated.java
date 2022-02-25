package edu.oswego.cs.rest.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.GoogleBrowserClientRequestUrl;
import com.google.api.services.oauth2.model.Userinfo;
import com.ibm.websphere.security.social.UserProfileManager;

@WebServlet("/authenticated")
@ServletSecurity()
public class Authenticated extends HttpServlet{
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        PrintWriter pwriter = response.getWriter();
        

        String sessionId = request.getSession().getId();
        boolean isUserLoggedIn = OAuthUtils.isUserLoggedIn(sessionId);

        if (isUserLoggedIn) {
            Userinfo userInfo = OAuthUtils.getUserInfo(sessionId);

            response.getWriter().println("ID: " + userInfo.getId());
            response.getWriter().println("Email: " + userInfo.getEmail());
            response.getWriter().println("First name: " + userInfo.getGivenName());
            response.getWriter().println("Last name: " + userInfo.getFamilyName());
            response.getWriter().println("Full name: " + userInfo.getName());


        } else{
            pwriter.println("Not authenticated");
        }
    }
}
