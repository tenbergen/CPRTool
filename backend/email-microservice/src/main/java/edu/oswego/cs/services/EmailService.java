package edu.oswego.cs.services;

import edu.oswego.cs.daos.AssignmentDAO;
import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.daos.StudentDAO;
import edu.oswego.cs.daos.TeamDAO;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

/**
 * This class contains all the methods that deal with sending emails. These methods are called in edu.oswego.cs.services.EmailService.
 *
 */
public class EmailService {

    String to = "pschmitt@oswego.edu";
    String from = "schmittsLaptop@patrick.com";
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

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
    public void allAssignmentsSubmittedEmail(CourseDAO course, AssignmentDAO assignment){

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

    }

    /**
     * Sends an email to all the students in the course when an assignment is created by the professor.
     * Called when the professor finished posting the assignment.
     *
     * @param course the course in which the peer review is assigned
     * @param assignment the assignment that has been created
     */
    public void assignmentCreatedEmail(CourseDAO course, AssignmentDAO assignment) {
        String msgSubject = "A New Assignment Has Been Created";
        String msgBody = "";
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from, "NoReply"));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to, "Patrick Schmitt"));
            msg.setSubject("Test Email");
            msg.setText(msgBody);
            Transport.send(msg);
            System.out.println("Email sent successfully...");
        } catch (AddressException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
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
    public void assignmentDeadlinePassed(CourseDAO course, AssignmentDAO assignment){

    }

    /**
     * Sends an email to all the members of a team after they submit an assignment to act as a digital receipt.
     *
     * @param course course for which the assignment was submitted
     * @param team team that submitted the assignment
     * @param assignment assignment that was submitted
     * @param timestamp time at which the assignment was submitted
     */
    public void assignmentSubmittedEmail(CourseDAO course, TeamDAO team, AssignmentDAO assignment, Date timestamp){

    }

    /**
     * Method to create a new thread that acts as a timer for a deadline. This timer checks avery hour to see if
     * the deadline has passed, at which point it calls the appropriate method. The timer checks repeatedly instead of
     * just waiting the full time to account for when the assignment deadline changes or if the assignment is deleted.
     *
     * @param course course in which the assignment is assigned.
     * @param assignment The assignment whose deadline is being checked for.
     * @param isPeerReview a true value will call peerReviewDeadlinePassed instead of assignmentDeadlinePassed.
     */
    public void createDeadlineTimer(CourseDAO course, AssignmentDAO assignment, boolean isPeerReview){

    }

    /**
     * Sends the student an email when the professor submits a grade for their assignment.
     *
     * @param course course for which the grade is given
     * @param assignment assignment for which the grade is given
     * @param student student receiving the email
     */
    public void gradeReceivedEmail(CourseDAO course, AssignmentDAO assignment, StudentDAO student){

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

    /**
     * Sends the professor an email when someone submits a peer review containing profanity.
     *
     * @param course course for which the peer review is submitted
     * @param team team that submitted the peer review
     * @param assignment assignment for which the peer review contained profanity
     */
}
