package edu.oswego.cs;

import edu.oswego.cs.application.DeadlineTracker;
import edu.oswego.cs.database.CourseInterface;
import edu.oswego.cs.services.EmailService;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;


/*
    NOTE: before testing, you need to have two professors, 7 students, 2 courses, 4 teams, and 3 assignments set up in
    the database as follows:

    Main Course: Main Professor, Students 1-6
    Alt Course: Alt Professor, Students 6 and Alt

    Teams: (In Main Course) Students 1 + 2, Students 3 + 4, Students 5 + 6
           (In Alt Course) Students 5 and Alt

    Assignments: (All in Main Course)
        Assignment 1: Do not have anybody submit anything
        Assignment 2: Have all the teams submit something, but don't send out Peer Reviews
        Assignment 3: Have all the teams submit something and do all the peer reviews (2 per team)

    It's not part of the tests, but if you set up these objects through the product's frontend then you'll
    probably get a lot of emails while doing it. Gmail tends to double-up similar emails into
    threads so I like to delete old emails from the CPR tool so the new ones don't get lost.

    Put all the ids for the database objects in the variables below.

    If you don't see any emails, try checking your spam folder. It should go without saying that all the emails you
    put for the students and professors should be ones you have access to.

    If running the tests gives an "array out of bounds" error, make sure you've updated the getPath() method in
    EmailService.java

    Finally, the service is liable to non-critical errors if you create an assignment between when the microservice goes
    up and when the clock next hits a 5-minute mark.
 */
public class EmailTests {
    private static final Jsonb jsonb = JsonbBuilder.create();
    private static Client client;
    private Response okResponse = Response.status(Response.Status.OK).build();

    //You must set these emails to emails you can access in order to properly test the microservice.
    private final String mainProfessorEmail = "pschmitt@oswego.edu";
    private final String altProfessorEmail = "squeeshyandpike@gmail.com"; //should never receive any emails
    private static final String student1Email = "quantummechanistjp@gmail.com";
    private static final String student2Email = "yoinkysploinky994@gmail.com";
    private static final String student3Email = "starrringo889@gmail.com";
    private static final String student4Email = "tunneldiggertsegrot@gmail.com";
    private static final String student5Email = "gronkoxtreme@gmail.com";
    private static final String student6Email = "peteymuskrat@gmail.com";
    private static final String altStudentEmail = "balonjalbatross@gmail.com"; //should never receive any emails
    private static final String team1Name = "12"; //students 1 and 2
    private static final String team2Name = "34"; //students 3 and 4
    private static final String team3Name = "56"; //students 5 and 6
    private static final String team4Name = "78"; //students 5 and 6
    private static String mainCourseID = "MAI101-1-101-Spring-2023";
    private static String altCourseID = "ALT101-1-101-Spring-2023";
    private int ass1ID = 1;
    private int ass2ID = 2;
    private int ass3ID = 3;

    @Test
    public void testCreateAssignmentEmail() throws IOException {
        new EmailService().assignmentCreatedEmail(mainCourseID, ass1ID);
        //should send every student in the main course an email about Assignment 1.
    }

    @Test
    public void testAssignmentSubmittedEmail() throws IOException {
        new EmailService().assignmentSubmittedEmail(mainCourseID, team1Name, ass2ID);

        //should send an email about Assignment 2 to all students in Team 1.
    }

    @Test
    public void testAllAssignmentsSubmittedEmail() throws IOException {
        new EmailService().allAssignmentsSubmittedEmail(mainCourseID, ass2ID);
        //should send an email about Assignment 2 to the professor of the main course.
    }

    @Test
    public void testAllAssignmentsSubmittedEmailButNotAllTheAssignmentsAreSubmitted() throws IOException {
        new EmailService().allAssignmentsSubmittedEmail(mainCourseID, ass1ID);
        //should not send any emails, since not all assignments have been submitted for assignment 1.
    }

    @Test
    public void testPeerReviewAssignedEmail() throws IOException {
        new EmailService().peerReviewAssignedEmail(mainCourseID, ass3ID);
        //should send an email about Assignment 3 to all the students in the main course.
    }

    @Test
    public void testPeerReviewSubmittedEmail() throws IOException {
        new EmailService().peerReviewSubmittedEmail(new CourseInterface().getStudent(student3Email), mainCourseID, team1Name, ass3ID);
        //should send an email about Assignment 3 to all the students in students 3's team.
    }

    @Test
    public void testAllPeerReviewsSubmittedEmailButNotAllThePeerReviewsAreSubmitted() throws IOException {
        new EmailService().allPeerReviewsSubmittedEmail(mainCourseID, ass2ID);
        //should not send an email to the professor because not all peer reviews for assignment 2 have been submitted.
    }

    @Test
    public void testAllPeerReviewsSubmittedEmail() throws IOException {
        new EmailService().allAssignmentsSubmittedEmail(mainCourseID, ass3ID);
        //should send an email about Assignment 3 to the main course professor
    }

    @Test
    public void testAssignmentDeadlinePassedEmail() throws IOException {
        new EmailService().assignmentDeadlinePassed(mainCourseID, ass1ID);

        //(the actual deadline check is done by DeadlineTracker.check(), so this method won't actually check if the
        // assignment is late)
        //should send an email about Assignment 1 to the main course professor
    }

    @Test
    public void testAssignmentDeadlinePassedEmailButTheyWereAllSubmitted() throws IOException {
        new EmailService().assignmentDeadlinePassed(mainCourseID, ass2ID);
        //should not send any emails because all the teams submitted for Assignment 2.
    }

    @Test
    public void testPeerReviewDeadlinePassedEmail() throws IOException {
        new EmailService().peerReviewDeadlinePassed(mainCourseID, ass2ID);

        //(the actual deadline check is done by DeadlineTracker.check(), so this method won't actually check if the
        // assignment is late)
        //should send an email about Assignment 2 to the main course professor
    }

    @Test
    public void testGradeReceivedEmail() throws IOException {
        new EmailService().gradeReceivedEmail(mainCourseID, ass3ID, team1Name);
        //(the method itself doesn't actually check if the grade is finalized, since that's done on the frontend)
        //should send an email about Assignment 3 to all the students in team 1.
    }

    @Test
    public void testOutlierEmail() throws IOException {
        new EmailService().outlierDetectedEmail(mainCourseID, team3Name, ass3ID);
        //(the method itself doesn't actually check for an outlier, since that's done on the frontend)
        //should send the professor an email about Assignment 3.
    }

    /*
        Unfortunately, to check the DeadlineTracker's functionality and thus the full functionality of the
        deadline passed emails, you'll need to mess with the computer clock. Make sure that all three assignments
        have been in the database with deadlines and PR deadlines prior to your current computer time.
        Next, set your computer time so it's past all the deadlines and PR deadlines, and wait another 5 minutes.
        The main professor should receive one assignment deadline passed email for Assignment 1, and 2 peer
        review deadline passed emails for Assignments 1 and 2.

        You are also unknowingly testing the DeadlineTracker just by having the microservice run. The check()
        method is called every 5 minutes according to your computer's time. To make sure the check() method is
        being called, you can run the command docker logs cpr-email. The DeadlineTracker prints "--------check"
        whenever the check() method is called. With that in mind, the outcome of the check() method depends
        entirely on the contents of the database.
     */

    /*
        To test the API calls of the email microservice, you should actually complete the tasks on the
        running product. The tasks to complete are:
        1) Create an assignment in a class with students
        2) Turn in an assignment as a student in a team with at least one other student
        3) Have every team in a class turn in an assignment
        4) Distribute peer reviews in a class with students
        5) Submit a peer review as a student in a team with at least one other student
        6) Have every team in a class submit all of their peer reviews
        7) Finalize an assignment's grades as a professor (currently the frontend of that is not complete
            so this one won't work until the page is done and I add the API call.)
        8) Let an assignment's deadline and peer review deadline pass without all teams submitting
        9) Have a team submit a statistical outlier grade (currently the frontend for that is not complete
            so this one won't work until the page is done and I add the API call.)
     */
}