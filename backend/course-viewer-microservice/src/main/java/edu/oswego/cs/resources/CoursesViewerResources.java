
package edu.oswego.cs.resources;


import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.database.CourseViewerInterfaceV2;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Optional;

@Path("professor")
public class CoursesViewerResources{

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses")
    public ArrayList<CourseDAO> ViewCourses() throws IOException {
        CourseViewerInterfaceV2 courses = new CourseViewerInterfaceV2("bastian.tenbergen");
        return courses.getCourses();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses/{courseID}")
    public Response viewCourse(@PathParam("courseID") String courseID) throws IOException {
        CourseViewerInterfaceV2 courses = new CourseViewerInterfaceV2("bastian.tenbergen");
        Optional<CourseDAO> courseDAO = courses.getCourses().stream()
                .filter(course -> course.getCourseID().equals(courseID))
                .findFirst();
        return (courseDAO.isPresent()) ?
                Response.status(Response.Status.OK).entity(courseDAO).build() :
                Response.status(Response.Status.BAD_REQUEST).entity("CourseID Does Not Exist.").build();

    }
}
