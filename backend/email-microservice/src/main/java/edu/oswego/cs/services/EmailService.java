package edu.oswego.cs.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import edu.oswego.cs.daos.AssignmentDAO;
import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.daos.TeamDAO;
import edu.oswego.cs.database.DatabaseManager;
import edu.oswego.cs.util.DeadlineTimer;
import org.bson.Document;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * This class contains all the methods that deal with sending emails. These methods are called in edu.oswego.cs.services.EmailService.
 *
 */
public class EmailService {
    static String from = "schmittsLaptop@patrick.com";
    static Properties props = new Properties();
    static Session session = Session.getDefaultInstance(props, null);

    static DatabaseManager databaseManager = new DatabaseManager();
    MongoCollection<Document> studentCollection = databaseManager.getStudentDB().getCollection("students");
    static MongoCollection<Document> teamCollection = databaseManager.getTeamDB().getCollection("teams");
    MongoCollection<Document> courseCollection = databaseManager.getCourseDB().getCollection("courses");
    static MongoCollection<Document> submissionCollection = databaseManager.getAssignmentDB().getCollection("submissions");

    /**
     * Constructor for the EmailResource Class
     */
    public EmailService(){

    }

    /**
     * Sends an email to the professor informing them that all teams in the course have submitted an assignment.
     * This method will not be called if the assignment deadline has passed.
     * This method is otherwise called every time a team submits an assignment, at which point
     * it checks every team in the course to see if all of them have submitted it. Sends the email if and only if
     * all teams have submitted.
     *
     * @param course the course in which the assignment is assigned
     * @param assignment the assignment which has been submitted
     */

    public void allAssignmentsSubmittedEmail(CourseDAO course, AssignmentDAO assignment) throws IOException {

    }

    /**
     * Sends an email to the professor informing them that all teams in the course have submitted a peer review.
     * This method will not be called if the peer review deadline has passed.
     * This method is otherwise called every time a team submits a peer review, at which point
     * it checks every team in the course to see if all of them have submitted it. Sends the email if and only if
     * all teams have submitted.
     *
     * @param course the course in which the peer review is assigned
     * @param assignment the peer review which has been submitted
     */
    public void allPeerReviewsSubmittedEmail(CourseDAO course, AssignmentDAO assignment){
        //How to tell if peer review is submitted? Where is that stored?
    }

    /**
     * Sends an email to all the students in the course when an assignment is created by the professor.
     * Called when the professor finished posting the assignment.
     *
     * @param course the course in which the peer review is assigned
     * @param assignment the assignment that has been created
     */
    public void assignmentCreatedEmail(CourseDAO course, AssignmentDAO assignment) throws IOException {
        //read contents of template

        System.out.println(System.getProperty("user.dir"));


        String path = getPath();
        System.out.println("Path: " + path);
        BufferedReader in = new BufferedReader(new FileReader(path + "templates"
                                                                + File.separator +
                                                                "assignmentCreatedEmail.html"));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        String ls = System.getProperty("line.separator");
        while((line = in.readLine()) != null){
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        in.close();
        System.out.println(stringBuilder.toString());

        //send email

        String to="pschmitt@oswego.edu";
        String from="cprtoolemail@gmail.com";
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String msgBody = "This is how you send mail using your laptop as an SMTP server";

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from, "NoReply"));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to, "Patrick Schmitt"));
            msg.setSubject("Here's an example");
            msg.setText(msgBody);
            Transport.send(msg);
            System.out.println("Email sent successfully...");
        } catch (AddressException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

        /**
     * Sends an email to the professor when the deadline for an assignment has passed and not all teams have made a
     * submission for it. A timer is created when the assignment is created (See createDeadlineTimer). This method is
     * called when the timer reaches its end.
     *
     * @param course the course in which the assignment is assigned
     * @param assignment the assignment whose deadline has passed
     */
    public static void assignmentDeadlinePassed(CourseDAO course, AssignmentDAO assignment) throws IOException {

    }

    /**
     * Sends an email to all the members of a team after they submit an assignment to act as a digital receipt.
     *
     * @param course course for which the assignment was submitted
     * @param team team that submitted the assignment
     * @param assignment assignment that was submitted
     * @param timestamp time at which the assignment was submitted
     */
    public void assignmentSubmittedEmail(CourseDAO course, TeamDAO team, AssignmentDAO assignment, Date timestamp) throws IOException {

    }

    /**
     * Method to create a new thread that acts as a timer for a deadline. This timer checks avery hour to see if
     * the deadline has passed, at which point it calls the appropriate method. The timer checks repeatedly instead of
     * just waiting the full time to account for when the assignment deadline changes or if the assignment is deleted.
     *
     * @param assignment The assignment whose deadline is being checked for.
     * @param isPeerReview a true value will call peerReviewDeadlinePassed instead of assignmentDeadlinePassed.
     */
    public void createDeadlineTimer(AssignmentDAO assignment, boolean isPeerReview){
        new DeadlineTimer(assignment.courseID, isPeerReview).start();
    }

    /**
     * Sends the student an email when the professor submits a grade for their assignment.
     *
     * @param course course for which the grade is given
     * @param assignment assignment for which the grade is given
     * @param team team receiving the email
     */
    public void gradeReceivedEmail(CourseDAO course, AssignmentDAO assignment, TeamDAO team) throws IOException {


    }

    /**
     * Sends the professor an email when an outlier is detected.
     */
    public void outlierDetectedEmail(){

    }

    /**
     * Sends all the students in a team an email when that team is assigned a peer review.
     *
     * @param course course in which the peer review is assigned
     * @param team team reviewing the submission
     * @param assignment submission being peer reviewed
     */
    public void peerReviewAssignedEmail(CourseDAO course, TeamDAO team, AssignmentDAO assignment){

    }

    /**
     * Sends an email to the professor when the deadline for a peer review has passed and not all teams have made a
     * submission for it. A timer is created when the peer review assignment is created (See createDeadlineTimer). This
     * method is called when the timer reaches its end.
     *
     * @param course the course in which the assignment is assigned
     * @param assignment the assignment whose deadline has passed
     */
    public void peerReviewDeadlinePassed(CourseDAO course, AssignmentDAO assignment){

    }

    /**
     * Sends an email to all the members of a team after they submit a peer review to act as a digital receipt.
     *
     * @param course course for which the peer review was submitted
     * @param team team that submitted the peer review
     * @param assignment peer review that was submitted
     * @param timestamp time at which the peer review was submitted
     */
    public void peerReviewSubmittedEmail(CourseDAO course, TeamDAO team, AssignmentDAO assignment, Date timestamp){

    }

    /**
     * Sends the professor an email when someone submits an assignment containing profanity.
     *
     * @param course course for which the assignment is submitted
     * @param team team that submitted the assignment
     * @param assignment assignment for which the submission contained profanity
     */
    public void profanityEmail(CourseDAO course, TeamDAO team, AssignmentDAO assignment){

    }

    public String getPath() {
        String path = (System.getProperty("user.dir").contains("\\")) ? System.getProperty("user.dir").replace("\\", "/") : System.getProperty("user.dir");
        String[] slicedPath = path.split("/");
        String targetDir = "defaultServer";
        StringBuilder relativePathPrefix = new StringBuilder();

        for (int i = slicedPath.length - 1; !slicedPath[i].equals(targetDir); i--) {
            relativePathPrefix.append("../");
        }

        if (System.getProperty("user.dir").contains("\\")) {
            String reg = "//";
            relativePathPrefix = new StringBuilder(relativePathPrefix.toString().replace("/", "\\"));
        }

        return relativePathPrefix.toString();
    }
}
