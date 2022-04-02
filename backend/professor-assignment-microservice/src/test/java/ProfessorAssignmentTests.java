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
import java.util.HashMap;
import java.util.List;

// DISCLAIMER: Don't run all the tests at the same time. You'll screw up the database and fail the tests in some way
// most likely. Read through the tests to see what they create, update and delete. Now that you're in the loop, don't
// be a dingus.

public class ProfessorAssignmentTests {

    private static final Jsonb jsonb = JsonbBuilder.create();
    private static final ArrayList<AssignmentDAO> expectedAssignments = new ArrayList<>();
    private static String port;
    private static String baseUrl;
    private static String targetUrl;
    private static AssignmentDAO assignment1, assignment1Edited, assignment2, assignment3;
    private Client client;
    private static int assignmentIdCount;

    @BeforeAll
    public static void oneTimeSetup() {
        port = "13125";
        baseUrl = "http://moxie.cs.oswego.edu:" + port + "/assignments/professor";


        // variables for inserted assignments
        String aName1 = "Response Types Essay 1";
        String instructions1 = "Wait, there's an instructions field in the AssignmentDAO?";
        String instructions1E = "Okay, I lied, I'm still to lazy to put actual instructions in here, but c'mon!" +
                "It's still a String at the end of the day!";
        String dueDate1 = "03/10/2022";
        String courseID1 = "CSC378-800-54266-Spring-2023"; // JUnit Theory Course
        int points1 = 25;

        String aName2 = "Response Types Essay 2";
        String instructions2 = "Awesome! I'm not entirely sure what to put here, so I'll just leave this brief stream of consciousness here for now.";
        String dueDate2 = "04/10/2022";
        String courseID2 = "CSC378-800-54266-Spring-2023"; // JUnit Theory Course
        int points2 = 35;

        String aName3 = "Response Types Essay 3";
        String instructions3 = " If you're seeing this, know that I plan to update it soon to something more appropriate. :D";
        String dueDate3 = "05/10/2022";
        String courseID3 = "CSC378-800-54266-Spring-2023"; // JUnit Theory Course
        int points3 = 45;

        // what happens if we make an assignment without the allocated course being there...
        assignment1 = new AssignmentDAO(aName1, instructions1, dueDate1, courseID1, points1);
        assignment1Edited = new AssignmentDAO(aName1, instructions1E, dueDate1, courseID1, points1);
//        assignment2 = new AssignmentDAO(aName2, instructions2, dueDate2, courseID2, points2);
//        assignment3 = new AssignmentDAO(aName3, instructions3, dueDate3, courseID3, points3);

        // we will add these assignments to the database
        expectedAssignments.add(assignment1);
//        expectedAssignments.add(assignment2);
//        expectedAssignments.add(assignment3);
    }

    @BeforeEach
    public void setup() {
        client = ClientBuilder.newClient();

        // add some temp assignments to the DB
        expectedAssignments.forEach(assignment -> {
            String createURL = baseUrl + "/courses/create-assignment/";
            WebTarget target = client.target(createURL);
            target.request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(jsonb.toJson(assignment), MediaType.APPLICATION_JSON));
        });
    }

    @AfterEach
    public void teardown() {

        // remove added assignments from the DB
        expectedAssignments.forEach(assignment -> {
            String deleteURL = baseUrl + "/courses/" + assignment.getCourseID() + "/assignments/" + assignment.getAssignment_id() + "/remove/";
            WebTarget target = client.target(deleteURL);
            target.request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .delete();
        });
        client.close();
    }

    @Test
    public void testCreateAssignment() {

        targetUrl = "/courses/create-assignment/";
        WebTarget target = client.target(baseUrl + targetUrl);
        Response addAssignmentResponse = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(assignment1), MediaType.APPLICATION_JSON));

        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(addAssignmentResponse.getStatus()), "Assignment was not added properly.");
    }

    @Test
    public void testUpdateAssignment() {

        targetUrl = "/courses/" + assignment1.getCourseID() + "/assignments/" + assignmentIdCount + "/edit/";
        WebTarget target = client.target(baseUrl + targetUrl);
        Response updateAssignmentResponse = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity(jsonb.toJson(assignment1Edited), MediaType.APPLICATION_JSON));

        // test will observe to see if it returned a positive response
        // think of how you can enter faulty assignments here, like in M1 and log the results

        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(updateAssignmentResponse.getStatus()), "Assignment was not revised properly.");
    }

    @Test
    public void testDeleteAssignment() {

        targetUrl = "/courses/" + assignment1.getCourseID() + "/assignments/" + assignmentIdCount + "/remove/";
        WebTarget target = client.target(baseUrl + targetUrl);
        Response removeAssignmentResponse = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        // test will observe to see if it returned a positive response
        // think of how you can enter faulty assignments here, like in M1 and log the results

        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(removeAssignmentResponse.getStatus()), "Assignment was not deleted properly.");
    }

    @Test
    public void testCreateUpdateAndDeleteAssignment() {

        targetUrl = "/courses/create-assignment/";
        WebTarget target = client.target(baseUrl + targetUrl);
        Response addAssignmentResponse = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(assignment1), MediaType.APPLICATION_JSON));

        // test will observe to see if it returned a positive response
        // think of how you can enter faulty assignments here, like in M1 and log the results

        // derive the assignmentID number, since this is tied to the course object, but out of the assignment objects territory
        targetUrl = "/courses/"+assignment1.getCourseID()+"/assignments/";
        target = client.target(baseUrl + targetUrl);
        Response response = target.request().get();
        List assignmentObjects = jsonb.fromJson(response.readEntity(String.class), ArrayList.class);
        for (Object o : assignmentObjects) {
            HashMap mapO = (HashMap) o;
            if(assignment1.getAssignmentName().equals(mapO.get("assignment_name"))){
                assignmentIdCount = Integer.parseInt(String.valueOf(mapO.get("assignment_id")));
                System.out.println("AssignmentID = "+ assignmentIdCount);
            }
        }

        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(addAssignmentResponse.getStatus()), "Assignment was not added properly.");

        targetUrl = "/courses/" + assignment1.getCourseID() + "/assignments/" + assignmentIdCount + "/edit/";
        target = client.target(baseUrl + targetUrl);
        Response updateAssignmentResponse = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity(jsonb.toJson(assignment1Edited), MediaType.APPLICATION_JSON));

        // test will observe to see if it returned a positive response
        // think of how you can enter faulty assignments here, like in M1 and log the results

        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(updateAssignmentResponse.getStatus()), "Assignment was not revised properly.");

        targetUrl = "/courses/" + assignment1.getCourseID() + "/assignments/" + assignmentIdCount + "/remove/";
        target = client.target(baseUrl + targetUrl);
        Response removeAssignmentResponse = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        // test will observe to see if it returned a positive response
        // think of how you can enter faulty assignments here, like in M1 and log the results

        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(removeAssignmentResponse.getStatus()), "Assignment was not deleted properly.");
    }

    // courseID's cannot have spaces in the abbreviations
    // assID will still go up even if you delete them from the assignment's database! The course will always remember how many assignments there have been!
    @Test
    public void testCreateAndDeleteAssignment() {

        targetUrl = "/courses/create-assignment/";
        WebTarget target = client.target(baseUrl + targetUrl);
        Response addAssignmentResponse = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(assignment1), MediaType.APPLICATION_JSON));

        // test will observe to see if it returned a positive response
        // think of how you can enter faulty assignments here, like in M1 and log the results

        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(addAssignmentResponse.getStatus()), "Assignment was not added properly.");

        targetUrl = "/courses/" + assignment1.getCourseID() + "/assignments/" + assignment1.getAssignment_id() + "/remove/";
        target = client.target(baseUrl + targetUrl);
        Response removeAssignmentResponse = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        // test will observe to see if it returned a positive response
        // think of how you can enter faulty assignments here, like in M1 and log the results

        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(removeAssignmentResponse.getStatus()), "Assignment was not deleted properly.");
    }

    @Test
    public void viewAssignmentsTest() {

//            @NonNull @JsonbProperty("assignment_name") String assignmentName,
//            @NonNull @JsonbProperty("instructions") String instructions,
//            @NonNull @JsonbProperty("due_date") String dueDate,
//            @NonNull @JsonbProperty("course_id") String courseID,
//            @NonNull @JsonbProperty("points") int points)

        targetUrl = "/assignments/";

        WebTarget target = client.target(baseUrl + targetUrl);
        Response response = target.request().get();
        System.out.println("response: " + response.readEntity(String.class));

        // read each of the objects from the db as jsons!
        List assignmentObjects = jsonb.fromJson(response.readEntity(String.class), ArrayList.class);
        ArrayList<AssignmentDAO> assignments = new ArrayList<>();

        for (Object o : assignmentObjects) {
            HashMap mapO = (HashMap) o;
            assignments.add(new AssignmentDAO(
                    (String) mapO.get("assignment_name"),
                    (String) mapO.get("instructions"),
                    (String) mapO.get("due_date"),
                    (String) mapO.get("course_id"),
                    Integer.parseInt(String.valueOf(mapO.get("points")))
            ));
        }

        ArrayList<AssignmentDAO> actualAssignments = new ArrayList<>();

        // compare the assignments we made before to the assignments in the database.
        // This should NOT work if we entered anything funky that could mess things up DB side...
        assignments.forEach(assignment -> {
            for (AssignmentDAO a : expectedAssignments) {
                if (a.getAssignmentName().equals(assignment.getAssignmentName()) &&
                        a.getCourseID().equals(assignment.getCourseID()) &&
                        a.getDueDate().equals(assignment.getDueDate()) &&
                        a.getInstructions().equals(assignment.getInstructions()) &&
                        a.getPoints() == assignment.getPoints())
                    actualAssignments.add(assignment);
            }
        });

        // test passes if the courses were successfully entered into the db
        Assertions.assertEquals(expectedAssignments.size(), actualAssignments.size(), "Not all assignments were retrieved.");
    }

    // won't work if you accidentally put assignments with the same data in them beforehand
    @Test
    public void viewAssignmentsWithinCourseTest() {

//            @NonNull @JsonbProperty("assignment_name") String assignmentName,
//            @NonNull @JsonbProperty("instructions") String instructions,
//            @NonNull @JsonbProperty("due_date") String dueDate,
//            @NonNull @JsonbProperty("course_id") String courseID,
//            @NonNull @JsonbProperty("points") int points)

        targetUrl = "/courses/"+assignment1.getCourseID()+"/assignments/";

        WebTarget target = client.target(baseUrl + targetUrl);
        Response response = target.request().get();
        System.out.println("response: " + response.readEntity(String.class));

        // read each of the objects from the db as jsons!
        List assignmentObjects = jsonb.fromJson(response.readEntity(String.class), ArrayList.class);
        ArrayList<AssignmentDAO> assignments = new ArrayList<>();

        for (Object o : assignmentObjects) {
            HashMap mapO = (HashMap) o;
            assignments.add(new AssignmentDAO(
                    (String) mapO.get("assignment_name"),
                    (String) mapO.get("instructions"),
                    (String) mapO.get("due_date"),
                    (String) mapO.get("course_id"),
                    Integer.parseInt(String.valueOf(mapO.get("points")))
            ));
        }

        ArrayList<AssignmentDAO> actualAssignments = new ArrayList<>();

        // compare the assignments we made before to the assignments in the database.
        // This should NOT work if we entered anything funky that could mess things up DB side...
        assignments.forEach(assignment -> {
            for (AssignmentDAO a : expectedAssignments) {
                if (a.getAssignmentName().equals(assignment.getAssignmentName()) &&
                        a.getCourseID().equals(assignment.getCourseID()) &&
                        a.getDueDate().equals(assignment.getDueDate()) &&
                        a.getInstructions().equals(assignment.getInstructions()) &&
                        a.getPoints() == assignment.getPoints())
                    actualAssignments.add(assignment);
            }
        });

        // test passes if the courses were successfully entered into the db
        Assertions.assertEquals(expectedAssignments.size(), actualAssignments.size(), "Not all assignments were retrieved.");
    }

    // BELOW REQUIRES PROCESSING OF IATTATCHMENT LISTS!!! DO THE COURSE CSV BEFORE YOU DO THESE!

//    @Test
//    public void uploadFileTest() {
//        targetUrl = "/courses/"+assignment1.getCourseID()+"/assignments/"+assignmentIdCount+"/upload";
//    }
//
//    @Test
//    public void createAssignmentAndUploadFileTest() {
//
//        targetUrl = "/courses/create-assignment/";
//        WebTarget target = client.target(baseUrl + targetUrl);
//        Response addAssignmentResponse = target.request(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .post(Entity.entity(jsonb.toJson(assignment1), MediaType.APPLICATION_JSON));
//
//        // derive the assignmentID number, since this is tied to the course object, but out of the assignment objects territory
//        targetUrl = "/courses/"+assignment1.getCourseID()+"/assignments/";
//        target = client.target(baseUrl + targetUrl);
//        Response response = target.request().get();
//        List assignmentObjects = jsonb.fromJson(response.readEntity(String.class), ArrayList.class);
//        for (Object o : assignmentObjects) {
//            HashMap mapO = (HashMap) o;
//            if(assignment1.getAssignmentName().equals(mapO.get("assignment_name"))){
//                assignmentIdCount = Integer.parseInt(String.valueOf(mapO.get("assignment_id")));
//                System.out.println("AssignmentID = "+ assignmentIdCount);
//            }
//        }
//
//        Assertions.assertEquals(Response.Status.OK, Response.Status.fromStatusCode(addAssignmentResponse.getStatus()), "Assignment was not added properly.");
//
//        targetUrl = "/courses/"+assignment1.getCourseID()+"/assignments/"+assignmentIdCount+"/upload";
//    }
//
//    @Test
//    public void downloadFileTest() {
//      targetUrl = "/courses/"+assignment1.getCourseID()+"/assignments/"+assignmentIdCount+"/download/"+fileName;
//    }
//
//    @Test
//    public void viewFilesTest() {
//      targetUrl = "/courses/"+assignment1.getCourseID()+"/assignments/"+assignmentIdCount+"/view-files";
//    }
}

