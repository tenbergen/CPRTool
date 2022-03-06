package edu.oswego.cs.rest.controllers.servlets;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        
        // invalidate the user's session
        request.getSession().invalidate();
        
        response.sendRedirect("http://localhost:3000");
        // response.sendRedirect("http://localhost:13126/authenticated");

        // completely log the users out with all google devices
        // response.sendRedirect("https://www.google.com/accounts/Logout?continue=https://appengine.google.com/_ah/logout?continue=http://localhost:3000");
    }
}
