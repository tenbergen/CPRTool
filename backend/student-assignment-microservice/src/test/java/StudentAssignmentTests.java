import edu.oswego.cs.rest.daos.AssignmentDAO;
import org.junit.jupiter.api.*;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

public class StudentAssignmentTests {
    private static final Jsonb jsonb = JsonbBuilder.create();
    private static final ArrayList<AssignmentDAO> expectedAssignments = new ArrayList<>();
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

    // BELOW REQUIRES PROCESSING OF IATTATCHMENT LISTS!!! DO THE COURSE CSV BEFORE YOU DO THESE!

    @Test
    public void studentUploadFileTest() {

        targetUrl = "/upload/";

        // ...

    }

    @Test
    public void studentDownloadFileTest() {

        String fileName = "daFile"; // what the heck should this be?
        targetUrl = "/download/"+fileName+"/";

        // ...

    }
}
