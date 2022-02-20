package edu.oswego.cs.resources;

import java.io.IOException;

import javax.decorator.Decorator;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.oswego.cs.resources.logout.ILogout;
import edu.oswego.cs.resources.logout.LogoutHandler;

@ApplicationScoped
@WebServlet(name = "LogoutServlet", urlPatterns = "/logout")
@ServletSecurity(value = @HttpConstraint(rolesAllowed = {"Professor", "Student"},
        transportGuarantee = ServletSecurity.TransportGuarantee.CONFIDENTIAL))

public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Inject
    private LogoutHandler logoutHandler;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

                ILogout logout = logoutHandler.getLogout();
                Response logoutResponse = logout.logout();
        
                Response.Status.Family responseCodeFamily = logoutResponse.getStatusInfo().getFamily();

                if (!responseCodeFamily.equals(Response.Status.Family.SUCCESSFUL)) {
                    // Logger.getLogger("LogoutServlet").log(Level.SEVERE,
                    //         logoutResponse.readEntity(Map.class).toString());
                    // throw new ServletException("Could not delete OAuth2 application grant");
                   
                }
                
                
                request.logout();
                response.sendRedirect("https://namnguyen31.com");
        
            }
}
