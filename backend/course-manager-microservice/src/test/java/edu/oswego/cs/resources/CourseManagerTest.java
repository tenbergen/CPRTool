package edu.oswego.cs.resources;

import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.daos.StudentDAO;
import edu.oswego.cs.database.CourseInterface;
import org.junit.jupiter.api.*;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.print.attribute.standard.Media;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class CourseManagerTest {
    private static String port;
    private static String baseUrl;
    private static String targetUrl;

    static CourseDAO course;
    private boolean courseDeletedTest = false;

    private Client client;
    private static final Jsonb jsonb = JsonbBuilder.create();

    private Response addCourseResponse;

    @BeforeAll
    public static void oneTimeSetup() {
        port = "13127";
        baseUrl = "http://localhost:" + port + "/manage/professor/";
        String courseName = "Software-Design-Create-Test";
        int courseSection = 9000;
        String semester = "Spring";
        String abbreviation = "CSC480T";
        course = new CourseDAO(courseName, courseSection, semester, abbreviation);
    }

    @BeforeEach
    public void setup() {
        client = ClientBuilder.newClient();

        targetUrl = "courses/course/create/";
        WebTarget target = client.target(baseUrl + targetUrl);
        addCourseResponse = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(course), MediaType.APPLICATION_JSON));
    }

    @AfterEach
    public void teardown() {
        if (! courseDeletedTest) {
            targetUrl = "courses/course/delete/";
            WebTarget target = client.target(baseUrl + targetUrl);
            addCourseResponse = target.request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(jsonb.toJson(course), MediaType.APPLICATION_JSON));
        }
        courseDeletedTest = false;
        client.close();
    }

    @Test
    public void testCreateCourse() {
        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(addCourseResponse.getStatus()), "Course was not added properly.");
    }

    @Test
    public void testDeleteCourse() {

        //Test delete course to see if endpoint works
        targetUrl = "courses/course/delete/";
        WebTarget target = client.target(baseUrl + targetUrl);
        Response response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(course), MediaType.APPLICATION_JSON));

        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()), "Course was not deleted properly.");
        courseDeletedTest = true;
    }

    @Test
    public void testAddStudent() {
        String email = "timmyTest@oswego.edu";
        StudentDAO studentDAO = new StudentDAO(email, course.courseName, course.abbreviation, course.courseSection, course.semester);

        targetUrl = "courses/course/student/add/";
        WebTarget target = client.target(baseUrl + targetUrl);
        Response response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(studentDAO), MediaType.APPLICATION_JSON));

        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()), "Student was not added properly.");
    }

    @Test
    public void testDeleteStudent() {
        String email = "timmyTest@oswego.edu";
        StudentDAO studentDAO = new StudentDAO(email, course.courseName, course.abbreviation, course.courseSection, course.semester);

        targetUrl = "courses/course/student/add/";
        WebTarget target = client.target(baseUrl + targetUrl);
        target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(studentDAO), MediaType.APPLICATION_JSON));

        targetUrl = "courses/course/student/delete/";
        target = client.target(baseUrl + targetUrl);
        Response response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(studentDAO), MediaType.APPLICATION_JSON));

        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()), "Student was not deleted properly.");
    }

}