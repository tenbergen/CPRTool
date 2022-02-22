
package edu.oswego.cs.resources;


import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.database.CourseViewerInterface;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("professor")
public class CoursesViewerResources {

    @GET
    @Path("courses")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CourseDAO> getCourses() {
        ArrayList<String> courseStrs = new CourseViewerInterface("bastian.tenbergen").getCourses();
        List<CourseDAO> courses = courseStrs.stream()
                    .map( courseStr -> Arrays.asList(courseStr.split("-")))
                    .map( courseArr -> new CourseDAO(
                            String.join(" ", courseArr.subList(0, courseArr.size() - 1)),
                            Integer.parseInt(courseArr.get(courseArr.size() - 1)))
                    )
                    .collect(Collectors.toList());
        return courses;
    }
}
