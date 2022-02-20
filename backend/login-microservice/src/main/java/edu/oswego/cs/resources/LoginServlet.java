package edu.oswego.cs.resources;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
@ServletSecurity(value = @HttpConstraint(rolesAllowed = {"Student", "Professor"},
        transportGuarantee = ServletSecurity.TransportGuarantee.CONFIDENTIAL))
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                String username = request.getUserPrincipal().getName();
                request.setAttribute("username", username); 
                request.getRequestDispatcher("securedLogin.jsp").forward(request,response);
                // response.sendRedirect("REDIRECT_URL");
            }
            
}
