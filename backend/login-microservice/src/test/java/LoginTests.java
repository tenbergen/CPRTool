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
        jwt = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjU4YjQyOTY2MmRiMDc4NmYyZWZlZmUxM2MxZWI" +
                "xMmEyOGRjNDQyZDAiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUu" +
                "Y29tIiwiYXpwIjoiNTMxNzc3MjQ3OTMzLXRjcDEzamxpM3BnOHIzYjY3ZG92MmQ3a2I0" +
                "amlydG1zLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiYXVkIjoiNTMxNzc3MjQ3O" +
                "TMzLXRjcDEzamxpM3BnOHIzYjY3ZG92MmQ3a2I0amlydG1zLmFwcHMuZ29vZ2xldXNlcmN" +
                "vbnRlbnQuY29tIiwic3ViIjoiMTA4NTQwNjQ4NDY5NDM0NDg5MDk2IiwiaGQiOiJvc3dlZ2" +
                "8uZWR1IiwiZW1haWwiOiJsbWNtYWhhbkBvc3dlZ28uZWR1IiwiZW1haWxfdmVyaWZpZWQiO" +
                "nRydWUsImF0X2hhc2giOiJ6X3BZZmhpU2FYcW5WVzBCbU1QZ3RnIiwibmFtZSI6IkxpYW0g" +
                "TWNNYWhhbiIsInBpY3R1cmUiOiJodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20v" +
                "YS0vQU9oMTRHakF3N1FCY2RKVDZYaklJd0Fial9FeVFNRjllTWV4TnZPbVE5eDdsUT1zOTYt" +
                "YyIsImdpdmVuX25hbWUiOiJMaWFtIiwiZmFtaWx5X25hbWUiOiJNY01haGFuIiwibG9jYWxlIj" +
                "oiZW4iLCJpYXQiOjE2NDg3ODU2MzgsImV4cCI6MTY0ODc4OTIzOCwianRpIjoiNzdmYWM1MzA5ZT" +
                "RjNzkxYjIwYTQxZmM0NjRiOGQ5NjEzMzZhMTQxYyJ9.jcN2YBXkHZFDNA3-OG6If-zHu2RyhpqBI9" +
                "x6vDnz9Hi6TPrTIwptvpVlRrDRkxHcdzrj_yPfSfz9FIom0YFYx6YU_EvcA2PHJXRGjjPigQYHgDFq" +
                "cIBj4SPED_vYjSOcCTGX6OkyKQioUeW02KeprO0obCpXqXmPYVzs-OVzNTVU6NbPPHTP9nkrYhTM5M3" +
                "xm1n6W_W_veI3SRwODof6C98tzMa5JPe3NM5QkvJn2PBCAISDbqkrs6hPLbobRTglhYYNRyyT4Wo11qV" +
                "5_paKLZp-e4LFz0oYrtB62GGd-pdf8NhdCREDhfgt0iJR0h_Wj_Le5sOoCdg397-tg36BXw";
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
