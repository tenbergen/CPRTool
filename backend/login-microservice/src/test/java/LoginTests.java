import org.junit.jupiter.api.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class LoginTests {

    private static String baseUrl;
    private Client client;
    private static String jwt;

    @BeforeAll
    public static void onTimeSetup(){
        baseUrl = "http://moxie.cs.oswego.edu:13125/auth/token/generate/";

        // base jwt token found by intercepting the request in Burp ;D
        // we can manipulate this to see what else we can do with this request. XD
        jwt = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImNlYzEzZGViZjRiOTY0Nzk2ODM3MzYyMDUwODI0NjZjMTQ3OTdiZDAiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXpwIjoiNTMxNzc3MjQ3OTMzLXRjcDEzamxpM3BnOHIzYjY3ZG92MmQ3a2I0amlydG1zLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiYXVkIjoiNTMxNzc3MjQ3OTMzLXRjcDEzamxpM3BnOHIzYjY3ZG92MmQ3a2I0amlydG1zLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTA4NTQwNjQ4NDY5NDM0NDg5MDk2IiwiaGQiOiJvc3dlZ28uZWR1IiwiZW1haWwiOiJsbWNtYWhhbkBvc3dlZ28uZWR1IiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF0X2hhc2giOiJRSFNDRUx3WkV5SS1ZU2FyWk5VdmZRIiwibmFtZSI6IkxpYW0gTWNNYWhhbiIsInBpY3R1cmUiOiJodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vYS0vQU9oMTRHakF3N1FCY2RKVDZYaklJd0Fial9FeVFNRjllTWV4TnZPbVE5eDdsUT1zOTYtYyIsImdpdmVuX25hbWUiOiJMaWFtIiwiZmFtaWx5X25hbWUiOiJNY01haGFuIiwibG9jYWxlIjoiZW4iLCJpYXQiOjE2NDg5NTgyNjMsImV4cCI6MTY0ODk2MTg2MywianRpIjoiNWU0NTM0OTZiMGUxZGVjNGVkM2I1ZWQ3Y2Y4OTY4MGI5ZDZiMjM4NSJ9.oQ47HH_lB5CHkBaqjyvxqChE34oYq2kfdZgeWn8XrZpmVQh6C84P-BKiynbrKjdNupq8XCSjMJylHI4D8PlTBgLWKTEuOQWQG_SQQgiS46xUR_3BgVzdYH-WwiQirZBjmxqEM9S8YjvH4uulu3jlpoVWhYGtb1JMyOLIShYHHg5jmJOhFxwz8mRYHSe_if-r2QsPo6HZeZOICvrS8gdLuQ1YCjQ3KzxQg2_QaBi8zBGRcd1ncYkt8lfkUB4SP-QMTESBxl2du2jvIdFMlmeX7vkWK1ncie8d21ggIaQhuhGNnupJaxKG7ydjHbmXgmhk4T3HS0eHXU-vwQqjFCCU5w";
    }

    @BeforeEach
    public void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void teardown() {
        client.close();
    }

    // for reference, postman gives a 200 when a jwt token is passed in initially for revision
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
