package edu.oswego.cs.controllers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;

@Path("/admin")
@RolesAllowed("admin")
@DenyAll
public class AdminController {

    // Delete Admin User by User id and all associated data
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/admin/{user_id}")
    public Response deleteAdminUser(SecurityContext securityContext, @PathParam("user_id") String userId) {
        return Response.status(Response.Status.OK).entity("Admin user deleted.").build();
    }

    // Delete Student User by User id and all associated data.
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/student/{user_id}")
    public Response deleteStudentUser(SecurityContext securityContext, @PathParam("user_id") String userId) {
        return Response.status(Response.Status.OK).entity("Student user deleted.").build();
    }

    // Delete Professor User by User id and all associated data.
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/professor/{user_id}")
    public Response deleteProfessorUser(SecurityContext securityContext, @PathParam("user_id") String userId) {
        return Response.status(Response.Status.OK).entity("Professor user deleted.").build();
    }

    // Add Admin User by User Id, First and Last Name
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/add/admin/{user_id}/{first_name}/{last_name}")
    public Response addAdminUser(SecurityContext securityContext, @PathParam("user_id") String userId, @PathParam("first_name") String firstName, @PathParam("last_name") String lastName) {
        return Response.status(Response.Status.OK).entity("Admin user added.").build();
    }

    // Add Student User by User Id, First and Last Name
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/add/student/{user_id}/{first_name}/{last_name}")
    public Response addStudentUser(SecurityContext securityContext, @PathParam("user_id") String userId, @PathParam("first_name") String firstName, @PathParam("last_name") String lastName) {
        return Response.status(Response.Status.OK).entity("Student user added.").build();
    }

    // Add Admin User by User Id, First and Last Name
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/add/professor/{user_id}/{first_name}/{last_name}")
    public Response addProfessorUser(SecurityContext securityContext, @PathParam("user_id") String userId, @PathParam("first_name") String firstName, @PathParam("last_name") String lastName) {
        return Response.status(Response.Status.OK).entity("Professor user added.").build();
    }

    // add Admin Role to Professor User by User Id
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/add_role/admin/professor/{user_id}")
    public Response addAdminRoleToProfessorUser(SecurityContext securityContext, @PathParam("user_id") String userId) {
        return Response.status(Response.Status.OK).entity("Admin role added to professor user.").build();
    }

    // Remove Admin Role from Professor User by User Id
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/remove_role/admin/professor/{user_id}")
    public Response removeAdminRoleProfessor(SecurityContext securityContext, @PathParam("user_id") String userId) {
        return Response.status(Response.Status.OK).entity("Admin role removed from professor user.").build();
    }


}
