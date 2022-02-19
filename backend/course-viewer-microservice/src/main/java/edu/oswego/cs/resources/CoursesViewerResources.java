package edu.oswego.cs.resources;

import com.mongodb.Mongo;
import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.database.database;
import edu.oswego.cs.interfaces.CourseViewerInterface;
import jakarta.inject.Inject;

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
