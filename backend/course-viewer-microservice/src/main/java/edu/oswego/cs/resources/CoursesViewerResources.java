package edu.oswego.cs.resources;

import javax.ws.rs.*;

@Path("/viewcourses")
public class CoursesViewerResources {
    @GET
    public String ViewCourses() {
        return "View Courses Page";
    }
}
