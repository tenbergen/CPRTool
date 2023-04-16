import org.junit.jupiter.api.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

// DISCLAIMER: Don't run all the tests at the same time. You'll likely screw up the database and fail the tests in some way.
// Read through the tests to see what they create, update and delete before you run them please.

public class LoginTests {

    private static String baseUrl;
    private Client client;
    private static String jwt;

    @BeforeAll
    public static void onTimeSetup(){
        baseUrl = "https://moxie.cs.oswego.edu:13125/auth/token/generate/";

        // base jwt token found by intercepting the request in Burp ;D
        // we can manipulate this to see what else we can do with this request. XD

        // NOTE! Get your own token, as one would quickly expire if I just put one in here
        // Team QA can help you if you would like to acquire a new one.
        jwt = "";
    }

    @BeforeEach
    public void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void teardown() {
        client.close();
    }

    @Test
    public void testTokenGenerationOnLogin() {

        // we want to test to see if we're generating a new token correctly
        WebTarget target = client.target(baseUrl);

        // How should this response be structured if we're passing in a bearer token instead of a json?
        Response tokenGenResponse = target.request(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer "+jwt)
                .post(Entity.entity("", MediaType.APPLICATION_JSON));

        // we also get the new token returned to us! This has been stripped of some information, but now includes roles, based on the list in the professor DB!
        System.out.println("response: " + tokenGenResponse.readEntity(String.class));

        Assertions.assertEquals(Response.Status.OK,
                Response.Status.fromStatusCode(tokenGenResponse.getStatus()),
                "New Token was not generated properly from old token.");
    }

}
