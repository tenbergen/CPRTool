package edu.oswego.cs.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/viewcourses")
public class CoursesViewerResources {
    @GET
    public String ViewCourses() {
        return "View Courses Page";
    }
}
