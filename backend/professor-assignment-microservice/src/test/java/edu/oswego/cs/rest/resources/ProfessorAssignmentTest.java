package edu.oswego.cs.rest.resources;

import edu.oswego.cs.rest.daos.CourseDAO;
import org.junit.jupiter.api.*;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

public class ProfessorAssignmentTest {
    private static String port;
    private static String baseUrl;
    private static String targetUrl;

    static CourseDAO course;
    private boolean courseDeletedTest = false;

    private Client client;
    private static final Jsonb jsonb = JsonbBuilder.create();

    private Response addAssignmentResponse;

    @BeforeAll
    public static void oneTimeSetup() {
        port = "13127";
        baseUrl = "http://moxie.cs.oswego.edu:" + port + "/manage/professor/";

        String courseName = "Software Engineering";
        int courseSection = 9000;
        int badCourseSection = -1;

        String semester = "Spring";
        String wrongSemester = "Apple Juice";
        String abbreviation = "CSC480";
        course = new CourseDAO(courseName, courseSection, semester, abbreviation);
    }

    @BeforeEach
    public void setup() {
        client = ClientBuilder.newClient();
        int assignment = 5731;

        targetUrl = "courses/course/assignment/create";
        WebTarget target = client.target(baseUrl + targetUrl);
        addCourseResponse = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(course), MediaType.APPLICATION_JSON));
    }

    @AfterEach
    public void tearDown() {
        if (!assignmentDeletedTest) {
            targetUrl = "courses/course/assignment/delete";
            WebTarget target = client.target(baseUrl + targetUrl);
            addCourseResponse = target.request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(jsonb.toJson(course), MediaType.APPLICATION_JSON));
        }
        courseDeletedTest = false;
        client.close();
    }

    @Test
    public void testProfessorUpload() {
        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(addAssignmentResponse.getStatus()), "Assignment was not uploaded properly.");
    }

    @Test
    public void testProfessorDownload() {
        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(addAssignmentResponse.getStatus()), "Assignment was not downloaded properly.");
    }//needs work

    @Test
    public void testProfessorDelete() {

        targetUrl = "courses/course/assignment/delete";
        WebTarget target = client.target(baseUrl + targetUrl);
        Response response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(course), MediaType.APPLICATION_JSON));

        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()), "Assignment was not deleted properly.");
        courseDeletedTest = true;
    }

}