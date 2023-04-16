import com.ibm.websphere.jaxrs20.multipart.AttachmentBuilder;
import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.daos.StudentDAO;
import org.junit.jupiter.api.*;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

// DISCLAIMER: Don't run all the tests at the same time. You'll likely screw up the database and fail the tests in some way.
// Read through the tests to see what they create, update and delete before you run them please.

public class CourseManagerTests {

    private static final Jsonb jsonb = JsonbBuilder.create();
    static CourseDAO course, courseDNE; // create a course that does not exist in the db
    private static String port;
    private static String baseUrl;
    private static String targetUrl;
    private boolean courseDeletedTest = false;
    private Client client;
    private Response addCourseResponse;

    @BeforeAll
    public static void oneTimeSetup() {

        port = "13125";
        baseUrl = "http://moxie.cs.oswego.edu:" + port + "/manage/professor/";
        String courseName = "JUnit Theory";
        String courseSection = "800";
        String crn = "54266";
        String semester = "Spring";
        String abbreviation = "CSC378";
        String year = "2023";
        course = new CourseDAO(abbreviation, courseName, courseSection, crn, semester, year);
        courseDNE = new CourseDAO("CSC999", courseName, courseSection, crn, semester, year);
    }

    @BeforeEach
    public void setup() {

        // make a course in the setup, since we'll need one no matter what we do here

        client = ClientBuilder.newClient();
        targetUrl = "courses/course/create/";
        WebTarget target = client.target(baseUrl + targetUrl);
        addCourseResponse = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(course), MediaType.APPLICATION_JSON));
    }

    @AfterEach
    public void teardown() {

        // if we didn't delete this course with a course delete test, let's delete it in the teardown
        // to keep the db clean

        if (!courseDeletedTest) {
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
        // simply returns the response of the default course creation process
        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(addCourseResponse.getStatus()), "Course was not added properly.");
    }

    @Test
    public void testDeleteCourse() {
        targetUrl = "courses/course/delete/";
        WebTarget target = client.target(baseUrl + targetUrl);
        Response response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(course), MediaType.APPLICATION_JSON));

        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()), "Course was not deleted properly.");
        courseDeletedTest = true;
    }

    @Test
    public void testCreateAndDeleteCourse() {
        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(addCourseResponse.getStatus()), "Course was not added properly.");
        targetUrl = "courses/course/delete/";
        WebTarget target = client.target(baseUrl + targetUrl);
        Response deleteCourseResponse = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(course), MediaType.APPLICATION_JSON));
        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(deleteCourseResponse.getStatus()), "Course was not deleted properly.");
        courseDeletedTest = true;
    }

    @Test
    public void updateCourse(){
        // update and delete will happen at the same time since it's harder to delete a course
        // when the courseID changes in an update test
        targetUrl = "courses/course/update/";

        // course DAO can be updated with new information, but it needs to START with the original courseID,
        // which is then updated afterwards
        String courseName = "JUnit Theory";
        String courseSection = "700";
        String crn = "54269";
        String semester = "Summer";
        String abbreviation = "CSC343";
        String year = "2022";
        CourseDAO updatedCourse = new CourseDAO(abbreviation, courseName, courseSection, crn, semester, year);
        updatedCourse.courseID = course.courseID; // these will be forced to be the same thing, so we know we're modifying the original course instance
        WebTarget target = client.target(baseUrl + targetUrl);
        Response response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity(jsonb.toJson(updatedCourse), MediaType.APPLICATION_JSON));
        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()), "Course was not updated properly.");
    }

    @Test
    public void testUpdateAndDeleteCourse() {
        // update and delete will happen at the same time since it's harder to delete a course
        // when the courseID changes in an update test
        targetUrl = "courses/course/update/";

        // course DAO can be updated with new information, but it needs to START with the original courseID,
        // which is then updated afterwards
        String courseName = "JUnit Theory";
        String courseSection = "700";
        String crn = "54269";
        String semester = "Summer";
        String abbreviation = "CSC343";
        String year = "2022";
        CourseDAO updatedCourse = new CourseDAO(abbreviation, courseName, courseSection, crn, semester, year);
        updatedCourse.courseID = course.courseID; // these will be forced to be the same thing, so we know we're modifying the original course instance
        WebTarget target = client.target(baseUrl + targetUrl);
        Response response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity(jsonb.toJson(updatedCourse), MediaType.APPLICATION_JSON));
        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()), "Course was not updated properly.");
        targetUrl = "courses/course/delete/";
        target = client.target(baseUrl + targetUrl);
        response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(course), MediaType.APPLICATION_JSON));
        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()), "Course was not deleted properly.");
        courseDeletedTest = true;
    }

    @Test
    public void testAddStudent() {
        String email = "ThisIsNotAnEmail";
        StudentDAO studentDAO = new StudentDAO(email, course.abbreviation, course.courseName, course.courseSection,
                course.crn, course.semester, course.year);
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
        StudentDAO studentDAO = new StudentDAO(email, course.abbreviation, course.courseName, course.courseSection,
                course.crn, course.semester, course.year);
        targetUrl = "courses/course/student/delete/";
        WebTarget target = client.target(baseUrl + targetUrl);
        target = client.target(baseUrl + targetUrl);
        Response response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(studentDAO), MediaType.APPLICATION_JSON));
        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()), "Student was not deleted properly.");
    }

    @Test
    public void testAddAndDeleteStudent() {
        String email = "timmyTest@oswego.edu";
        StudentDAO studentDAO = new StudentDAO(email, course.abbreviation, course.courseName, course.courseSection,
                course.crn, course.semester, course.year);
        StudentDAO studentDNEDAO = new StudentDAO("tommyTrial@oswego.edu", course.abbreviation, course.courseName, course.courseSection,
                course.crn, course.semester, course.year);
        targetUrl = "courses/course/student/add/";
        WebTarget target = client.target(baseUrl + targetUrl);
        Response response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(studentDAO), MediaType.APPLICATION_JSON));
        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()), "Student was not added properly.");
        targetUrl = "courses/course/student/delete/";
        target = client.target(baseUrl + targetUrl);
        response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(studentDNEDAO), MediaType.APPLICATION_JSON));
        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()), "Student was not deleted properly.");
    }


    // For whatever reason, AttachmentBuilder.newBuilder is recognized at compiler time but not at run time...?
    // gives a NoClassDefFoundError if you try to run this test. I explored different dependency versions to fix this,
    // ... but no dice. Figured this was becoming more trouble than it was worth, so I switched tactics a bit.
    // For this reason, any tests that involve a file upload/download are only ever done in Postman.

    @Test
    public void testMassAddStudents() throws FileNotFoundException {
        targetUrl = "/courses/course/student/mass-add/";
        List<IAttachment> attachments = new ArrayList<>();
        File testFile = new File("src/test/testRosters/test.csv");
        attachments.add(AttachmentBuilder.newBuilder("studentRoster")
                .inputStream(new FileInputStream(testFile))
                .build());
        Response r = client.target(baseUrl + targetUrl)
                .request()
                .post(Entity.entity(attachments, MediaType.MULTIPART_FORM_DATA));
        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(r.getStatus()), "Student was not added properly.");
    }
}
