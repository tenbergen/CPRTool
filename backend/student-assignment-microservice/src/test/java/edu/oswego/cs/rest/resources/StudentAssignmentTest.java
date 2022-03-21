package edu.oswego.cs.rest.resources;

import edu.oswego.cs.daos.CourseDAO;
import org.junit.jupiter.api.*;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

public class StudentAssignmentTest {
    private static String port;
    private static String baseUrl;
    private static String targetUrl;

    static CourseDAO course;
    static DownloadResources download;
    private boolean assignmentUploadedTest = false;
    private boolean assignmentDownloadedTest = false;

    private Client client;
    private static final Jsonb jsonb = JsonbBuilder.create();

    private Response addAssignmentResponse;
    private Response downloadAssignmentResponse;

    @BeforeAll
    public static void oneTimeSetup() {
        port = "13127";
        baseUrl = "http://moxie.cs.oswego.edu:" + port + "/manage/student/";

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

        download = new DownloadResources();

        targetUrl = "courses/course/assignments/upload";
        WebTarget target = client.target(baseUrl + targetUrl);
        addAssignmentResponse = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(course), MediaType.APPLICATION_JSON));

        downloadAssignmentResponse = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .get();
    }

    @AfterEach
    public void tearDown() {
        if (!assignmentUploadedTest) {
            targetUrl = "courses/course/assignments/upload";
            WebTarget target = client.target(baseUrl + targetUrl);
            addAssignmentResponse = target.request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(jsonb.toJson(course), MediaType.APPLICATION_JSON));
        }
        assignmentUploadedTest = false;

        if (!assignmentDownloadedTest) {
            targetUrl = "courses/course/assignments/download";
            WebTarget target = client.target(baseUrl + targetUrl);
            addAssignmentResponse = target.request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .get();
        }

        assignmentDownloadedTest = false;
        client.close();
    }

    @Test
    public void testStudentUpload() {
        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(addAssignmentResponse.getStatus()), "Assignment was not uploaded properly.");
    }

    @Test
    public void testStudentDownload() {
        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(downloadAssignmentResponse.getStatus()), "Assignment was not downloaded properly.");
    }

}