
package edu.oswego.cs.resources;


import edu.oswego.cs.database.CourseViewerInterfaceV2;

import javax.ws.rs.*;
import java.io.IOException;

@Path("/viewcourses")
public class CoursesViewerResources {

    @GET
    public String ViewCourses() throws IOException {
        CourseViewerInterfaceV2 test = new CourseViewerInterfaceV2("ItsPartOfTheProcess");

        return test.getCourses().toString();
    }
}
