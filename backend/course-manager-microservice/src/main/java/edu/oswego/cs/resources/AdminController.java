package edu.oswego.cs.resources;

import edu.oswego.cs.database.AdminInterface;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("admin")
@DenyAll
public class AdminController {

    // Delete Admin User by User id and all associated data
    @RolesAllowed("admin")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/admin/{user_id}")
    public Response deleteAdminUser(@Context SecurityContext securityContext, @PathParam("user_id") String userId)
            throws Exception {
        new AdminInterface(userId).deleteAdminUser(userId);
        return Response.status(Response.Status.OK).entity("Admin user deleted.").build();
    }

    // Delete Student User by User id and all associated data.
    @RolesAllowed("admin")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/student/{user_id}")
    public Response deleteStudentUser(@Context SecurityContext securityContext, @PathParam("user_id") String userId) {
        new AdminInterface(userId).deleteStudentUser(userId);
        return Response.status(Response.Status.OK).entity("Student user deleted.").build();
    }

    // Delete Professor User by User id and all associated data.
    @RolesAllowed("admin")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/professor/{user_id}")
    public Response deleteProfessorUser(@Context SecurityContext securityContext, @PathParam("user_id") String userId) {
        new AdminInterface(userId).deleteProfessorUser(userId);
        return Response.status(Response.Status.OK).entity("Professor user deleted.").build();
    }

    @RolesAllowed("admin")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/course/{course_id}")
    public Response deleteCourse(@Context SecurityContext securityContext, @PathParam("course_id") String courseId) {
        System.out.printf("Deleting course %s", courseId);
        new AdminInterface(securityContext.getUserPrincipal().getName()).removeCourseAsAdmin(securityContext, courseId);
        return Response.status(Response.Status.OK).entity("Course deleted.").build();
    }

    // Add Admin User by User Id, First and Last Name
    @RolesAllowed("admin")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/add/admin/{user_id}/{first_name}/{last_name}")
    public Response addAdminUser(@Context SecurityContext securityContext, @PathParam("user_id") String userId,
            @PathParam("first_name") String firstName, @PathParam("last_name") String lastName) {
        new AdminInterface(userId).addAdminUser(firstName, lastName, userId);
        return Response.status(Response.Status.OK).entity("Admin user added.").build();
    }

    // Add Student User by User Id, First and Last Name
    @RolesAllowed("admin")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/add/student/{user_id}/{first_name}/{last_name}")
    public Response addStudentUser(@Context SecurityContext securityContext, @PathParam("user_id") String userId,
            @PathParam("first_name") String firstName, @PathParam("last_name") String lastName) {
        System.out.println("Adding student user");
        new AdminInterface(userId).addStudentUser(firstName, lastName, userId);
        return Response.status(Response.Status.OK).entity("Student user added.").build();
    }

    // Add Admin User by User Id, First and Last Name
    @RolesAllowed("admin")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/add/professor/{user_id}/{first_name}/{last_name}")
    public Response addProfessorUser(@Context SecurityContext securityContext, @PathParam("user_id") String userId,
            @PathParam("first_name") String firstName, @PathParam("last_name") String lastName) {
        new AdminInterface(userId).addProfessorUser(firstName, lastName, userId);
        return Response.status(Response.Status.OK).entity("Professor user added.").build();
    }

    // Promote Professor User to Admin User by User Id
    @RolesAllowed("admin")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/roles/promote/professorToAdmin/{user_id}")
    public Response promoteProfessorToAdmin(@Context SecurityContext securityContext,
            @PathParam("user_id") String userId) {
        new AdminInterface(userId).promoteProfessorToAdmin(userId);
        return Response.status(Response.Status.OK).entity("Admin role added to professor user.").build();
    }

    // Promote Student User to Professor User by User Id
    @RolesAllowed("admin")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/roles/promote/studentToProfessor/{user_id}")
    public Response promoteStudentToProfessor(@Context SecurityContext securityContext,
            @PathParam("user_id") String userId) {
        new AdminInterface(userId).promoteStudentToProfessor(userId);
        return Response.status(Response.Status.OK).entity("Student promoted to professor role.").build();
    }

    // Promote Student User to Admin User by User Id
    @RolesAllowed("admin")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/roles/promote/studentToAdmin/{user_id}")
    public Response promoteStudentToAdmin(@Context SecurityContext securityContext,
            @PathParam("user_id") String userId) {
        new AdminInterface(userId).promoteStudentToAdmin(userId);
        return Response.status(Response.Status.OK).entity("Student promoted to admin role.").build();
    }

    // Demote Professor User to Student User by User Id
    @RolesAllowed("admin")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/roles/demote/professorToStudent/{user_id}")
    public Response demoteProfessorToStudent(@Context SecurityContext securityContext,
            @PathParam("user_id") String userId) {
        new AdminInterface(userId).demoteProfessorToStudent(userId);
        return Response.status(Response.Status.OK).entity("Admin role removed from professor user.").build();
    }

    // Demote Admin User to Professor User by User Id
    @RolesAllowed("admin")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/roles/demote/adminToProfessor/{user_id}")
    public Response demoteAdminToProfessor(@Context SecurityContext securityContext,
            @PathParam("user_id") String userId) {
        new AdminInterface(userId).demoteAdminToProfessor(userId);
        return Response.status(Response.Status.OK).entity("Admin role removed from professor user.").build();
    }

    // Demote Admin User to Student User by User Id
    @RolesAllowed("admin")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/roles/demote/adminToStudent/{user_id}")
    public Response demoteAdminToStudent(@Context SecurityContext securityContext,
            @PathParam("user_id") String userId) {
        new AdminInterface(userId).demoteAdminToStudent(userId);
        return Response.status(Response.Status.OK).entity("Admin role removed from student user.").build();
    }

    // Add Blocked Word
    @RolesAllowed("admin")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/profanity/update")
    public Response updateBlockedWords(@Context SecurityContext securityContext, @RequestBody String payload)  {
        new AdminInterface().updateBlockedWords(payload);
        return Response.status(Response.Status.OK).entity("Profanity settings updated.").build();
    }

    // Get Profanity Settings View
    @RolesAllowed({"admin","professor"})
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/views/profanity")
    public Response getProfanitySettingsView(@Context SecurityContext securityContext) {
        System.out.printf("Getting profanity settings view");
        return Response.status(Response.Status.OK).entity(new AdminInterface().getBlockedWords()).build();
    }

    // Get Users View
    @RolesAllowed("admin")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/views/users")
    public Response getUsersView(@Context SecurityContext securityContext) {
        return Response.status(Response.Status.OK).entity(new AdminInterface().getUsersView()).build();
    }

    // Get Courses view
    @RolesAllowed("admin")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/views/courses")
    public Response getCoursesView(@Context SecurityContext securityContext) {
        return Response.status(Response.Status.OK).entity(new AdminInterface().getCourseView()).build();
    }
}
