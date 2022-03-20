package edu.oswego.cs.rest.controllers.servlets;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import com.google.api.client.http.GenericUrl;
import edu.oswego.cs.rest.controllers.utils.OAuthUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends AbstractAuthorizationCodeServlet {

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException {
        return OAuthUtils.newFlow();
    }

    @Override
    protected String getRedirectUri(HttpServletRequest request) throws ServletException, IOException {
        GenericUrl url = new GenericUrl(request.getRequestURL().toString());
        url.setRawPath("/login-callback");
        return url.build();

    }

    @Override
    protected String getUserId(HttpServletRequest request) throws ServletException, IOException {
        return request.getSession().getId();
    }
}
