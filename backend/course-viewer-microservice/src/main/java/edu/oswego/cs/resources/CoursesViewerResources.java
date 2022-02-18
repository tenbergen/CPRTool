package edu.oswego.cs.resources;

import edu.oswego.cs.database.CourseInterface;

import javax.ws.rs.*;

@Path("/viewcourses")
public class CoursesViewerResources {
    @GET
    public String ViewCourses() {
        return "View Courses Page";
    }
}
