package edu.oswego.cs;

import edu.oswego.cs.application.DeadlineTracker;
import edu.oswego.cs.daos.AssignmentDAO;
import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.daos.StudentDAO;
import edu.oswego.cs.daos.TeamDAO;
import edu.oswego.cs.database.AssignmentInterface;
import edu.oswego.cs.database.CourseInterface;
import edu.oswego.cs.services.EmailService;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


/*
    NOTE: before testing, please set up emails.txt correctly, otherwise you won't know if the emails are properly set up.
    You should also double-check the database to make sure you don't have duplicate students or professors, which would
    probably cause the microservice to send double emails.

    Additionally, I don't know how to add professors to the database through API calls so you need to manually add your
    professor emails to the database before running the tests.

    If running the tests gives an "array out of bounds" error, make sure you've updated the getPath() method in
    EmailService.java
 */
public class EmailTests {
    private static final Jsonb jsonb = JsonbBuilder.create();
    private static String port = "3000";
    private static String baseUrl = "http://localhost:" + port;
    private static Client client;
    private Response okResponse = Response.status(Response.Status.OK).build();

    private static final String createCourseURL = baseUrl + "/manage/professor/courses/course/create/";
    private static final String createStudentURL = baseUrl + "/manage/professor/courses/course/student/add/";
    private final String createAssignmentURL = baseUrl + "/assignments/professor/courses/create-assignment";
    private static final String createTeamURL = baseUrl + "/teams/team/create";

    //You must set these emails to emails you can access in order to properly test the microservice.
    private final String mainProfessorEmail = "pschmitt@oswego.edu";
    private final String altProfessorEmail = "squeeshyandpike@gmail.com";
    private static final String student1Email = "quantummechanistjp@gmail.com";
    private static final String student2Email = "yoinkysploinky994@gmail.com";
    private static final String student3Email = "starrringo889@gmail.com";
    private static final String student4Email = "peteymuskrat@gmail.com";
    private static final String student5Email = "gronkoxtreme@gmail.com";
    private static final String student6Email = "tunneldiggertsegrot@gmail.com";
    private static final String altStudentEmail = "balonjalbatross@gmail.com";


    //The following variables are all data that will be added to the database and will end up in the emails. Feel free
    //to change these up. The numbers correspond to the order of students' emails above.
    private static final String courseName = "test course";
    private static final String courseAbb = "TST101";
    private static final String crn = "101";
    private static final String altCourseName = "alt course";
    private static final String altCourseAbb = "ALT480";
    private static final String altcrn = "480";
    private final String fn1 = "George";
    private final String ln1 = "Washington";
    private final String fn2 = "John";
    private final String ln2 = "Adams";
    private final String fn3 = "Thomas";
    private final String ln3 = "Jefferson";
    private final String fn4 = "James";
    private final String ln4 = "Madison";
    private final String fn5 = "James";
    private final String ln5 = "Monroe";
    private final String fn6 = "John";
    private final String ln6 = "Quincy Adams";
    private final String fnAlt = "Andrew";
    private final String lnAlt = "Jackson";
    private static final String ass1Name = "Assignment 1";
    private static final String ass1Due = "2023-5-20";
    private static final String ass1PRDue = "2023-5-30";
    private static final String ass2Name = "Assignment 2";
    private static final String ass2Due = "2023-6-10";
    private static final String ass2PRDue = "2023-6-20";
    private static final String team1Name = "12"; //students 1 and 2
    private static final String team2Name = "34"; //students 3 and 4
    private static final String team3Name = "56"; //students 5 and 6


    private static CourseDAO course;
    private static String courseID;
    private static CourseDAO alt;
    private static StudentDAO s1;
    private static StudentDAO s2;
    private static StudentDAO s3;
    private static StudentDAO s4;
    private static StudentDAO s5;
    private static StudentDAO s6;
    private static StudentDAO sAlt;
    private static AssignmentDAO ass1;
    private int ass1ID;
    private static AssignmentDAO ass2;
    private int ass2ID;
    private static TeamDAO team1;
    private static TeamDAO team2;
    private static TeamDAO team3;


    @BeforeAll
    public static void setUpDB(){
        //turn all the data into epic JSON format and add it to the database.
        course = new CourseDAO(courseAbb, courseName, "1", crn, "Spring", "2023");
        alt = new CourseDAO(altCourseAbb, altCourseName, "1", altcrn, "Spring", "2023");
        s1 = new StudentDAO(student1Email, courseAbb, courseName, "1", crn, "Spring", "2023");
        s2 = new StudentDAO(student2Email, courseAbb, courseName, "1", crn, "Spring", "2023");
        s3 = new StudentDAO(student3Email, courseAbb, courseName, "1", crn, "Spring", "2023");
        s4 = new StudentDAO(student4Email, courseAbb, courseName, "1", crn, "Spring", "2023");
        s5 = new StudentDAO(student5Email, courseAbb, courseName, "1", crn, "Spring", "2023");
        s6 = new StudentDAO(student6Email, courseAbb, courseName, "1", crn, "Spring", "2023");
        sAlt = new StudentDAO(altStudentEmail, altCourseAbb, altCourseName, "1", altcrn, "Spring", "2023");
        courseID = courseAbb + "-1-" + crn + "-Spring-2023";
        ass1 = new AssignmentDAO(courseID, ass1Name, "instructions", ass1Due, 100, "pr instructions", ass1PRDue, 100);
        ass2 = new AssignmentDAO(courseID, ass2Name, "instructions", ass2Due, 100, "pr instructions", ass2PRDue, 100);
        team1 = new TeamDAO(team1Name, courseID, 2, student1Email);
        team2 = new TeamDAO(team1Name, courseID, 2, student3Email);
        team3 = new TeamDAO(team1Name, courseID, 2, student5Email);

        client = ClientBuilder.newClient();
        WebTarget target = client.target(createCourseURL);
        target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(course), MediaType.APPLICATION_JSON));
        target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(alt), MediaType.APPLICATION_JSON));
        target = client.target(createStudentURL);
        target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(s1), MediaType.APPLICATION_JSON));
        target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(s2), MediaType.APPLICATION_JSON));
        target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(s3), MediaType.APPLICATION_JSON));
        target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(s4), MediaType.APPLICATION_JSON));
        target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(s5), MediaType.APPLICATION_JSON));
        target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(s6), MediaType.APPLICATION_JSON));
        target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(sAlt), MediaType.APPLICATION_JSON));
        target = client.target(createTeamURL);
        target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(team1), MediaType.APPLICATION_JSON));
        target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(team2), MediaType.APPLICATION_JSON));
        target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(team3), MediaType.APPLICATION_JSON));
        target = client.target(baseUrl + "/teams/professor/team/add-student");
        String teamParam1 = "{\n" +
                "  \"course_id\": \"" + courseID + "\",\n" +
                "  \"nominated_team_lead\": \"" + student1Email + "\",\n" +
                "  \"team_id\": \"" + team1Name + "\",\n" +
                "  \"team_name\": \"" + team1Name + "\",\n" +
                "  \"team_size\": 2,\n" +
                "  \"student_id\": \"" + student2Email + "\",\n" +
                "}";
        String teamParam2 = "{\n" +
                "  \"course_id\": \"" + courseID + "\",\n" +
                "  \"nominated_team_lead\": \"" + student3Email + "\",\n" +
                "  \"team_id\": \"" + team2Name + "\",\n" +
                "  \"team_name\": \"" + team2Name + "\",\n" +
                "  \"team_size\": 2,\n" +
                "  \"student_id\": \"" + student4Email + "\",\n" +
                "}";
        String teamParam3 = "{\n" +
                "  \"course_id\": \"" + courseID + "\",\n" +
                "  \"nominated_team_lead\": \"" + student5Email + "\",\n" +
                "  \"team_id\": \"" + team3Name + "\",\n" +
                "  \"team_name\": \"" + team3Name + "\",\n" +
                "  \"team_size\": 2,\n" +
                "  \"student_id\": \"" + student6Email + "\",\n" +
                "}";
        target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity(jsonb.toJson(teamParam1), MediaType.APPLICATION_JSON));
        target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity(jsonb.toJson(teamParam2), MediaType.APPLICATION_JSON));
        target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .put(Entity.entity(jsonb.toJson(teamParam3), MediaType.APPLICATION_JSON));
    }

    @Test
    public void testCreateAssignmentEmail() throws IOException {
        WebTarget target = client.target(createAssignmentURL);
        target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(ass1), MediaType.APPLICATION_JSON));
        List<Document> assignments = new AssignmentInterface().getAssignmentsByCourse(courseID);
        for(Document assignment : assignments){
            if(assignment.getString("assignment_name").equals(ass1Name)){
                ass1ID = assignment.getInteger("assignment_id");
            }
        }
        new EmailService().assignmentCreatedEmail(courseID, ass1ID);
        //The appropriate email should be sent to all students in the main course, and nobody else.
    }

    @Test
    public void testAssignmentSubmittedEmail() throws IOException {
        new EmailService().assignmentSubmittedEmail(courseID, team1Name, ass1ID);

        //should send an email to students 1 & 2. This method should always send an email if called, unless the database
        //is screwed up, in which case it will throw an exception.
    }

    @Test
    public void testAllAssignmentsSubmittedEmail() throws IOException {
        WebTarget target = client.target(createAssignmentURL);
        target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(jsonb.toJson(ass1), MediaType.APPLICATION_JSON));
        List<Document> assignments = new AssignmentInterface().getAssignmentsByCourse(courseID);
        for(Document assignment : assignments){
            if(assignment.getString("assignment_name").equals(ass1Name)){
                ass1ID = assignment.getInteger("assignment_id");
            }
        }
        target = client.target(baseUrl + "assignments/student/courses/" + courseID + "/assignments/" + ass1ID + "/" + team1Name + "/upload");
        target.request(MediaType.MULTIPART_FORM_DATA).accept(MediaType.MULTIPART_FORM_DATA)
                .post(Entity.entity(jsonb.toJson(samplepdf), MediaType.APPLICATION_JSON));
        target = client.target(baseUrl + "assignments/student/courses/" + courseID + "/assignments/" + ass1ID + "/" + team2Name + "/upload");
        target.request(MediaType.MULTIPART_FORM_DATA).accept(MediaType.MULTIPART_FORM_DATA)
                .post(Entity.entity(jsonb.toJson(samplepdf), MediaType.APPLICATION_JSON));
        target = client.target(baseUrl + "assignments/student/courses/" + courseID + "/assignments/" + ass1ID + "/" + team3Name + "/upload");
        target.request(MediaType.MULTIPART_FORM_DATA).accept(MediaType.MULTIPART_FORM_DATA)
                .post(Entity.entity(jsonb.toJson(samplepdf), MediaType.APPLICATION_JSON));

        new EmailService().allAssignmentsSubmittedEmail(courseID, ass1ID);
        //should send an email to the professor of the main course.
    }

    @Test
    public void testAllAssignmentsSubmittedEmailButNotAllTheAssignmentsAreSubmitted() throws IOException {
        new EmailService().allAssignmentsSubmittedEmail(courseID, ass2ID);
        //should not send any emails, since not all assignments have been submitted for assignment 2.
    }

    @Test
    public void testPeerReviewAssignedEmail() throws IOException {
        new EmailService().peerReviewAssignedEmail(courseID, ass1ID);
        //should send an email to all the students in the main course.
    }

    @Test
    public void testPeerReviewSubmittedEmail() throws IOException {
        new EmailService().peerReviewSubmittedEmail(new CourseInterface().getStudent(student3Email), courseID, team1Name, ass1ID);
        //should send an email to students 3 and 4.
    }

    @Test
    public void testAllPeerReviewsSubmittedEmailButNotAllThePeerReviewsAreSubmitted() throws IOException {
        new EmailService().allPeerReviewsSubmittedEmail(courseID, ass2ID);
    }

    @Test
    public void testAllPeerReviewsSubmittedEmail() throws IOException {
        WebTarget target = client.target(baseUrl + "peer-review/assignments/" + courseID + "/" + ass1ID + "/" + team1Name + "/" + team2Name + "/100/upload");
        target.request(MediaType.MULTIPART_FORM_DATA).accept(MediaType.MULTIPART_FORM_DATA)
                .post(Entity.entity(jsonb.toJson(samplepdf), MediaType.APPLICATION_JSON));
        target = client.target(baseUrl + "peer-review/assignments/" + courseID + "/" + ass1ID + "/" + team1Name + "/" + team3Name + "/100/upload");
        target.request(MediaType.MULTIPART_FORM_DATA).accept(MediaType.MULTIPART_FORM_DATA)
                .post(Entity.entity(jsonb.toJson(samplepdf), MediaType.APPLICATION_JSON));
        target = client.target(baseUrl + "peer-review/assignments/" + courseID + "/" + ass1ID + "/" + team2Name + "/" + team1Name + "/100/upload");
        target.request(MediaType.MULTIPART_FORM_DATA).accept(MediaType.MULTIPART_FORM_DATA)
                .post(Entity.entity(jsonb.toJson(samplepdf), MediaType.APPLICATION_JSON));
        target = client.target(baseUrl + "peer-review/assignments/" + courseID + "/" + ass1ID + "/" + team2Name + "/" + team3Name + "/100/upload");
        target.request(MediaType.MULTIPART_FORM_DATA).accept(MediaType.MULTIPART_FORM_DATA)
                .post(Entity.entity(jsonb.toJson(samplepdf), MediaType.APPLICATION_JSON));
        target = client.target(baseUrl + "peer-review/assignments/" + courseID + "/" + ass1ID + "/" + team3Name + "/" + team1Name + "/100/upload");
        target.request(MediaType.MULTIPART_FORM_DATA).accept(MediaType.MULTIPART_FORM_DATA)
                .post(Entity.entity(jsonb.toJson(samplepdf), MediaType.APPLICATION_JSON));
        target = client.target(baseUrl + "peer-review/assignments/" + courseID + "/" + ass1ID + "/" + team3Name + "/" + team2Name + "/100/upload");
        target.request(MediaType.MULTIPART_FORM_DATA).accept(MediaType.MULTIPART_FORM_DATA)
                .post(Entity.entity(jsonb.toJson(samplepdf), MediaType.APPLICATION_JSON));

        new EmailService().allAssignmentsSubmittedEmail(courseID, ass1ID);
        //should send an email to the professor of the main course.
    }

    @Test
    public void testAssignmentDeadlinePassedEmail() throws IOException {
        new EmailService().assignmentDeadlinePassed(courseID, ass2ID);

        //the method itself doesn't check if the deadline passed, so this should send the professor an email
    }

    @Test
    public void testAssignmentDeadlinePassedEmailButTheyWereAllSubmitted() throws IOException {
        new EmailService().assignmentDeadlinePassed(courseID, ass1ID);

        //No email should be sent.
    }

    @Test
    public void testPeerReviewDeadlinePassedEmail() throws IOException {
        new EmailService().peerReviewDeadlinePassed(courseID, ass2ID);

        //the method itself doesn't check if the deadline passed, so this should send the professor an email
    }

    @Test
    public void testPeerReviewDeadlinePassedEmailButTheyWereAllSubmitted() throws IOException {
        new EmailService().peerReviewDeadlinePassed(courseID, ass1ID);

        //the method itself doesn't check if the deadline passed, so this should send the professor an email
    }

    @Test
    public void testGradeReceivedEmail() throws IOException {
        new EmailService().gradeReceivedEmail(courseID, ass1ID, team1Name);
        //should send an email to Students 1 and 2.
    }

    @Test
    public void testOutlierEmail() throws IOException {
        new EmailService().outlierDetectedEmail(courseID, team3Name, ass1ID);
        //should send the professor an email
    }

    @Test
    public void testDeadlineTimer(){
        new DeadlineTracker().init();
    }

    private final String samplepdf = "{\n" +
            "  \"file\": {\n" +
            "    \"mime\": \"application/pdf\",\n" +
            "    \"data\": \"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MjAwMCAxMDM5LjU1MDc4XQovRm9udEZpbGUyIDkgMCBSPj4KZW5kb2JqCjExIDAgb2JqCjw8L1R5cGUgL0ZvbnQKL0ZvbnREZXNjcmlwdG9yIDEwIDAgUgovQmFzZUZvbnQgL0FBQUFBQStBcmlhbE1UCi9TdWJ0eXBlIC9DSURGb250VHlwZTIKL0NJRFRvR0lETWFwIC9JZGVudGl0eQovQ0lEU3lzdGVtSW5mbyA8PC9SZWdpc3RyeSAoQWRvYmUpCi9PcmRlcmluZyAoSWRlbnRpdHkpCi9TdXBwbGVtZW50IDA+PgovVyBbMCBbNzUwIDAgMCAyNzcuODMyMDNdIDM5IFs3MjIuMTY3OTddIDc2IFsyMjIuMTY3OTddIDgyIFs1NTYuMTUyMzRdIDg3IFsyNzcuODMyMDNdXQovRFcgMD4+CmVuZG9iagoxMiAwIG9iago8PC9GaWx0ZXIgL0ZsYXRlRGVjb2RlCi9MZW5ndGggMjUwPj4gc3RyZWFtCnicXZDLasMwEEX3+opZJosgx3UaCkZQXAJe9EHdfoAsjV1BLAlZXvjvK41DCh2Q4DD3zos37UtrTQT+EZzqMMJgrA44uyUohB5HY9mxBG1UvBH9apKe8WTu1jni1NrBsboG4J8pO8ewwu5Zux73jL8HjcHYEXbfTZe4W7y/4oQ2QsGEAI1DqvQq/ZucEDjZDq1OeRPXQ/L8Kb5Wj1ASH7dplNM4e6kwSDsiq4sUAupLCsHQ6n/50+bqB/UjA6kfkrooykJkKs9EVUVUNUSPT0SncqPLRpvyXFGXW73cL9/lvoxaQkh70PFogTy6sXi/r3c+u/L7BTYCeiYKZW5kc3RyZWFtCmVuZG9iago0IDAgb2JqCjw8L1R5cGUgL0ZvbnQKL1N1YnR5cGUgL1R5cGUwCi9CYXNlRm9udCAvQUFBQUFBK0FyaWFsTVQKL0VuY29kaW5nIC9JZGVudGl0eS1ICi9EZXNjZW5kYW50Rm9udHMgWzExIDAgUl0KL1RvVW5pY29kZSAxMiAwIFI+PgplbmRvYmoKeHJlZgowIDEzCjAwMDAwMDAwMDAgNjU1MzUgZiAKMDAwMDAwMDAxNSAwMDAwMCBuIAowMDAwMDAwNTc0IDAwMDAwIG4gCjAwMDAwMDAyNjIgMDAwMDAgbiAKMDAwMDAxMDU1NiAwMDAwMCBuIAowMDAwMDAwMjk5IDAwMDAwIG4gCjAwMDAwMDA3ODIgMDAwMDAgbiAKMDAwMDAwMDgzNyAwMDAwMCBuIAowMDAwMDAwODc5IDAwMDAwIG4gCjAwMDAwMDA5MzkgMDAwMDAgbiAKMDAwMDAwOTcwOSAwMDAwMCBuIAowMDAwMDA5OTQzIDAwMDAwIG4gCjAwMDAwMTAyMzUgMDAwMDAgbiAKdHJhaWxlcgo8PC9TaXplIDEzCi9Sb290IDggMCBSCi9JbmZvIDEgMCBSPj4Kc3RhcnR4cmVmCjEwNjk1CiUlRU9G\"\n" +
            "  }\n" +
            "}";
}