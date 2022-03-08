package edu.oswego.cs.rest.controllers.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final String protocol = System.getenv("PROTOCOL");
    private static final String domain = System.getenv("DOMAIN");
    private static final String port = System.getenv("PORT");
    private static final String fullURL = protocol + "://" + domain + ":" + port;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getSession().invalidate(); // using this would invalidate the user's session

        response.sendRedirect(fullURL);

        //this right here would completely log the users out with all google devices
        // response.sendRedirect("https://www.google.com/accounts/Logout?continue=https://appengine.google.com/_ah/logout?continue=http://localhost:3000");
    }
}