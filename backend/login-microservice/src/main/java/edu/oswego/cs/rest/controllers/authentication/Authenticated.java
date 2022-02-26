package edu.oswego.cs.rest.controllers.authentication;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.oauth2.model.Userinfo;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.ibm.websphere.security.social.UserProfileManager;


import edu.oswego.cs.rest.controllers.utils.OAuthUtils;

@WebServlet("/authenticated")
public class Authenticated extends HttpServlet{
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        PrintWriter pwriter = response.getWriter();
        

        String sessionId = request.getSession().getId();
        boolean isUserLoggedIn = OAuthUtils.isUserLoggedIn(sessionId);
        
        // request.getSession().
        
        
        if (isUserLoggedIn) {
            Credential cred = OAuthUtils.newFlow().loadCredential(sessionId);
            Userinfo userInfo = OAuthUtils.getUserInfo(sessionId);

            // response.getWriter().println("ID: " + userInfo.getId());
            // response.getWriter().println("Email: " + userInfo.getEmail());
            // response.getWriter().println("First name: " + userInfo.getGivenName());
            // response.getWriter().println("Last name: " + userInfo.getFamilyName());
            // response.getWriter().println("Full name: " + userInfo.getName());
            pwriter.println("User Info:"+userInfo.toPrettyString());


            String accessToken = cred.getAccessToken();
            pwriter.println();
            pwriter.println("Access Token: " +accessToken);
            pwriter.println();


            Tokeninfo tokenInfo = OAuthUtils.getTokenInfo(sessionId, accessToken);
            tokenInfo.setExpiresIn(10);
            pwriter.println("Token Info: " + tokenInfo.toPrettyString());


            pwriter.println();
            pwriter.println("Session ID: " +request.getSession().getId());
            pwriter.println();
            pwriter.println("User ID: " +userInfo.getId());

        } else{
            pwriter.println("Not authenticated");
        }
    }
}
