
package edu.oswego.cs.resources;


import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.database.CourseViewerInterfaceV2;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

@Path("professor")
public class CoursesViewerResources {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("courses")
    public ArrayList<CourseDAO> ViewCourses() throws IOException {
        CourseViewerInterfaceV2 courses = new CourseViewerInterfaceV2("bastian.tenbergen");
        return courses.getCourses();
    }
}
