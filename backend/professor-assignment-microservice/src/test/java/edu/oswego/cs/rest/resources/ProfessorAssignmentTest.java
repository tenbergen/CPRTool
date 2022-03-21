package edu.oswego.cs.rest.resources;

import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.rest.daos.FileDAO;
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

public class ProfessorAssignmentTest {
    private static String port;
    private static String baseUrl;
    private static String targetUrl;

    static CourseDAO course;
    static FileDAO assignment;
    private boolean assignmentUploadedTest = false;
    private boolean assignmentDownloadedTest = false;
    private boolean assignmentDeletedTest = false;

    private Client client;
    private static final Jsonb jsonb = JsonbBuilder.create();

    private Response addAssignmentResponse;
    private Response downloadAssignmentResponse;
    private Response deleteAssignmentResponse;

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

        String fileName = "ImportantAss";
        String attachment = ".pdf";

        course = new CourseDAO(courseName, courseSection, semester, abbreviation);
        assignment = new FileDAO(fileName, attachment);
    }

    @BeforeEach
    public void setup() {
        client = ClientBuilder.newClient();

        targetUrl = "courses/course/assignments/upload";
        WebTarget target = client.target(baseUrl + targetUrl);
        addAssignmentResponse = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(assignment), MediaType.APPLICATION_JSON));
    }

    @AfterEach
    public void tearDown() {
        if (!assignmentUploadedTest) {
            targetUrl = "courses/course/assignments/upload";
            WebTarget target = client.target(baseUrl + targetUrl);
            addAssignmentResponse = target.request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(jsonb.toJson(assignment), MediaType.APPLICATION_JSON));
        }
        assignmentUploadedTest = false;

        if (!assignmentDownloadedTest) {
            targetUrl = "courses/course/assignments/download";
            WebTarget target = client.target(baseUrl + targetUrl);
            downloadAssignmentResponse = target.request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .get();
        }
        assignmentDownloadedTest = false;

        if (!assignmentDeletedTest) {
            targetUrl = "courses/course/assignments/delete";
            WebTarget target = client.target(baseUrl + targetUrl);
            deleteAssignmentResponse = target.request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(jsonb.toJson(assignment), MediaType.APPLICATION_JSON));
        }
        assignmentDeletedTest = false;
        client.close();
    }

    @Test
    public void testProfessorUpload() {
        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(addAssignmentResponse.getStatus()), "Assignment was not uploaded properly.");
    }

    @Test
    public void testProfessorDownload() {
        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(downloadAssignmentResponse.getStatus()), "Assignment was not downloaded properly.");
    }

    @Test
    public void testProfessorDeleteAss() {

        targetUrl = "courses/course/assignments/delete";
        WebTarget target = client.target(baseUrl + targetUrl);
        deleteAssignmentResponse = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(assignment), MediaType.APPLICATION_JSON));

        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(deleteAssignmentResponse.getStatus()), "Assignment was not deleted properly.");
        assignmentDeletedTest = true;
    }

}