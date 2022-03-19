package edu.oswego.cs.rest.controllers.resources;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.api.services.oauth2.model.Userinfo;
import com.ibm.websphere.security.jwt.InvalidBuilderException;
import com.ibm.websphere.security.jwt.InvalidClaimException;
import com.ibm.websphere.security.jwt.JwtException;
import edu.oswego.cs.rest.controllers.utils.OAuthUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/authenticated")
public class AuthtedInformation extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        PrintWriter pwriter = response.getWriter();

        String sessionId = request.getSession().getId();
        boolean isUserLoggedIn = OAuthUtils.isUserLoggedIn(sessionId);

        if (isUserLoggedIn) {
            // Google credentials
            Credential cred = OAuthUtils.newFlow().loadCredential(sessionId);

            // information models
            Userinfo userInfo = OAuthUtils.getUserInfo(sessionId);
            String accessToken = cred.getAccessToken();
            Tokeninfo tokenInfo = OAuthUtils.getTokenInfo(sessionId, accessToken);

            boolean isOswego = OAuthUtils.isOswego(sessionId);

            if (isOswego) {
                pwriter.println("User Info:" + userInfo.toPrettyString());
                pwriter.println();
                pwriter.println("Access Token: " + accessToken);
                pwriter.println();
                pwriter.println("Token Info: " + tokenInfo.toPrettyString());
                pwriter.println();
                pwriter.println("Session ID: " + request.getSession().getId());
                pwriter.println();
                pwriter.println("User ID: " + userInfo.getId());

                try {
                    String jwtToken = OAuthUtils.buildJWT(sessionId);
                    pwriter.println();
                    pwriter.println("JWT: " + jwtToken);
                } catch (JwtException | InvalidBuilderException | InvalidClaimException e) {
                    e.printStackTrace();
                }
            } else {
                pwriter.println("401 Not Authenticated!");
                pwriter.println("Please log in using your @oswego.edu account!");
                // do a logout in the react page and redirect to login page
            }
        } else {
            pwriter.println("Not authenticated");
        }
    }
}
