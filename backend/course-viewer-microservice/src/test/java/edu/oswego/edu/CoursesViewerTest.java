package edu.oswego.edu;

import edu.oswego.cs.daos.CourseDAO;
import org.junit.jupiter.api.*;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CoursesViewerTest {

    private static String port;
    private static String baseUrl;
    private static String targetUrl;
    private Client client;
    private static ArrayList<CourseDAO> expectedCourses;

    private static Jsonb jsonb = JsonbBuilder.create();

    @BeforeAll
    public static void oneTimeSetup() {
        port = "13125";
        baseUrl = "http://moxie.cs.oswego.edu:" + port + "/view/professor/";
        String course1Name = "Intro to Programming";
        int course1Section = 12345;
        String semester1 = "Fall";
        String abbreviation1 = "CSC212";

        String course2Name = "Software Design II";
        int course2Section = 9000;
        String semester2 = "Spring";
        String abbreviation2 = "CSC481T";

        String course3Name = "Software Design II";
        int course3Section = 9000;
        String semester3 = "Spring";
        String abbreviation3 = "CSC482T";

        expectedCourses = new ArrayList<>();
        expectedCourses.add(new CourseDAO(course1Name, course1Section, semester1, abbreviation1));
        expectedCourses.add(new CourseDAO(course2Name, course2Section, semester2, abbreviation2));
        expectedCourses.add(new CourseDAO(course3Name, course3Section, semester3, abbreviation3));
    }

    @BeforeEach
    public void setup() {
        client = ClientBuilder.newClient();

        expectedCourses.forEach( course -> {
            String createURL = baseUrl+"courses/course/create/";
            WebTarget target = client.target(createURL);
            target.request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(jsonb.toJson(course), MediaType.APPLICATION_JSON));
        });
        client.close();
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void teardown() {

        expectedCourses.forEach( course -> {
            String deleteURL = baseUrl+"courses/course/delete/";
            WebTarget target = client.target(deleteURL);
            target.request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(jsonb.toJson(course), MediaType.APPLICATION_JSON));
        });
        client.close();
    }

    @Test
    public void viewCoursesTest() {
        targetUrl = "courses/";

        WebTarget target = client.target(baseUrl + targetUrl);
        Response response = target.request().get();
        System.out.println("response: " + response.readEntity(String.class));
        List courseObjects = jsonb.fromJson(response.readEntity(String.class), ArrayList.class);
        ArrayList<CourseDAO> courses = new ArrayList<>();

        for (Object o : courseObjects) {
            HashMap mapO = (HashMap) o;
            courses.add(new CourseDAO(
                    (String) mapO.get("course_name"),
                    (Integer.parseInt((String) mapO.get("course_section"))),
                    (String) mapO.get("semester"),
                    (String) mapO.get("abbreviation")
            ));
        }

        ArrayList<CourseDAO> actualCourses = new ArrayList<>();

        courses.forEach(course -> {
            for (CourseDAO c : expectedCourses) {
                if (c.courseID.equals(course.courseID))
                    actualCourses.add(course);
            }
        });

        Assertions.assertEquals(expectedCourses.size(), actualCourses.size(), "Not all courses were retrieved.");
    }

    @Test
    public void viewSpecificCourseTest() {

        String courseIDToObserve = "CSC345-800-Spring-2022";
        targetUrl = "courses/"+courseIDToObserve;

        WebTarget target = client.target(baseUrl + targetUrl);
        Response response = target.request().get();
        System.out.println("response: " + response.readEntity(String.class));
        CourseDAO courseObject = jsonb.fromJson(response.readEntity(String.class), CourseDAO.class);

        CourseDAO comparison = new CourseDAO();
        comparison.courseID = courseIDToObserve;

        Assertions.assertEquals(courseObject.courseID, comparison.courseName, "Course not found.");
    }
}
