package edu.oswego.cs.rest.controllers.servlets;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import com.google.api.client.http.GenericUrl;
import com.ibm.websphere.security.jwt.InvalidBuilderException;
import com.ibm.websphere.security.jwt.InvalidClaimException;
import com.ibm.websphere.security.jwt.JwtException;

import edu.oswego.cs.rest.controllers.utils.OAuthUtils;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/login-callback")
public class LoginCallbackServlet extends AbstractAuthorizationCodeCallbackServlet{


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
    protected void onSuccess(HttpServletRequest request, HttpServletResponse response, Credential credential)
        throws IOException {
            String sessionId = request.getSession().getId();


            try {
                String jwtToken = OAuthUtils.buildJWT(sessionId);
                
               
                Cookie jwtCookie = new Cookie("JWT_token", jwtToken);

                response.addCookie(jwtCookie);


            } catch (JwtException | InvalidBuilderException | InvalidClaimException e) {
                e.printStackTrace();
            }

        response.sendRedirect("/api/authenticated");
    }
    @Override
    protected void onError(
        HttpServletRequest request, HttpServletResponse response, AuthorizationCodeResponseUrl errorResponse)
        throws IOException {
        response.getWriter().print("Error");
    }
    
}
