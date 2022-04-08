import org.junit.jupiter.api.*;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

// DISCLAIMER: Don't run all the tests at the same time. You'll likely screw up the database and fail the tests in some way.
// Read through the tests to see what they create, update and delete before you run them please.

public class StudentAssignmentTests {
    private static final Jsonb jsonb = JsonbBuilder.create();
    private static String port;
    private static String baseUrl;
    private static String targetUrl;
    private Client client;

    @BeforeAll
    public static void oneTimeSetup() {
        String courseId = "CSC378-800-54266-Spring-2023";
        int assignmentId = 55; // how can you keep track of this value in this MS???

        port = "13125";
        baseUrl = "http://moxie.cs.oswego.edu:" + port + "/assignments/student/courses/"+courseId+
                "/assignments/"+assignmentId;
    }

    @BeforeEach
    public void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void teardown() {
        client.close();
    }

    // For whatever reason, AttachmentBuilder.newBuilder is recognized at compiler time but not at run time...?
    // gives a NoClassDefFoundError if you try to run this test. I explored different dependency versions to fix this,
    // ... but no dice. Figured this was becoming more trouble than it was worth, so I switched tactics a bit.
    // For this reason, any tests that involve a file upload/download are only ever done in Postman.


    @Test
    public void studentUploadFileTest() {

        targetUrl = "/upload/";

        // Not included here

    }

    @Test
    public void studentDownloadFileTest() {

        String fileName = "";
        targetUrl = "/download/"+fileName+"/";

        // Not included here

    }
}
