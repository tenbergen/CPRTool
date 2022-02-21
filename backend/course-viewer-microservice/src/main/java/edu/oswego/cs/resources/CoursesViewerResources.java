
package edu.oswego.cs.resources;


import edu.oswego.cs.database.CourseViewerInterface;
import javax.ws.rs.*;

@Path("/viewcourses")
public class CoursesViewerResources {

    @GET
    public String ViewCourses() {
        CourseViewerInterface test = new CourseViewerInterface("bastian.tenbergen");
        System.out.println(test.getCourses());
        return "View Courses Page";
    }
}
