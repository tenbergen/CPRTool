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
        //get teams
        MongoCursor<Document> query = teamCollection.find().iterator();
        List<Document> teams = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            teams.add(document);
        }
        query.close();

        //check if all assignments have been submitted
        for(Document team : teams){
            //get list of submissions
            query = submissionCollection.find().iterator();
            List<Document> submissions = new ArrayList<>();
            while(query.hasNext()){
                Document document = query.next();
                submissions.add(document);
            }
            //check each team to match course id, assignment id, and team name
            boolean teamHasSubmitted = false;
            for(Document submission : submissions){
                if(submission.getString("course_id").equals(course.courseID) &&
                   submission.getString("assignment_id").equals(assignment.assignmentID) &&
                   submission.getString("team_name").equals(team.getString("team_id"))){
                    teamHasSubmitted = true;
                    break;
                }
            }
            if(!teamHasSubmitted){
                //not all teams have submitted, do not send email.
                return;
            }
        }
        //if control gets to this point, that means all teams have submitted.

        //load email body from template
        BufferedReader reader = new BufferedReader(new FileReader(new File("./templates/allAssignmentsSubmitted.html")));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        while((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        String msgBody = stringBuilder.toString();

        Document professor = null;
        String to = "pschmitt@oswego.edu"; //don't know how to get professor from course.

        //fill in specifics
        msgBody.replace("[Today's Date]", LocalDate.now().toString());
        msgBody.replace("[Course Name]", course.courseName);
        msgBody.replace("[Assignment Name]", assignment.assignmentName);
        msgBody.replace("[Name of Instructor]", professor.getString("professor_id"));
        String teamsList = "";
        for(Document team : teams){
            teamsList += team.getString("team_name") + " ";
        }
        msgBody.replace("[All Team Names]", teamsList);

        //send email
        String msgSubject = "All teams have submitted for an assignment";
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from, "NoReply"));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to, "Patrick Schmitt")); //to fix once I get professor name
            msg.setSubject(msgSubject);
            msg.setText(msgBody);
            Transport.send(msg);
            System.out.println("Email sent successfully...");
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
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
        //load email body from template
        File[] files = new File(System.getProperty("user.dir") + "/resources").listFiles();
        for(int i = 0; i < files.length; i++){
            System.out.println(files[i].getName());
        }
        BufferedReader reader = new BufferedReader(new FileReader(new File("resources" + File.separator +
                "assignmentCreatedEmail.html")));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        while((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        String msgBody = stringBuilder.toString();

        //get list of recipients
        MongoCursor<Document> query = studentCollection.find().iterator();
        List<Document> students = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            students.add(document);
        }
        query.close();
        List courseStudents = new ArrayList<>();
        //don't know how to get students in a particular course

        //get professor
        Document professor = null; //I don't know how to get that

        for(Document student : students) {
            String to = student.getString("student_id" + "@oswego.edu"); //to be changed when db starts storing email domains.
            //fill in specifics
            msgBody.replace("[Today's Date]", LocalDate.now().toString());
            msgBody.replace("[Name of Student]", student.getString("first_name") + " " + student.getString("last_name"));
            msgBody.replace("[Name of Course]",course.courseName);
            msgBody.replace("[Course Name]", course.courseName);
            msgBody.replace("[Assignment Name]", assignment.assignmentName);
            msgBody.replace("[Due Date]", assignment.dueDate);
            msgBody.replace("[Instructor Name]", professor.getString("professor_id"));

            //send email
            String msgSubject = "A New Assignment Has Been Created";
            try {
                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(from, "NoReply"));
                msg.addRecipient(Message.RecipientType.TO,
                        new InternetAddress(to, "Patrick Schmitt")); //to fix once I get professor name
                msg.setSubject(msgSubject);
                msg.setText(msgBody);
                Transport.send(msg);
                System.out.println("Email sent successfully...");
            } catch (MessagingException | UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
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
        //load email body from template
        BufferedReader reader = new BufferedReader(new FileReader(new File("templates/assignmentDeadlinePassed.html")));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        while((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        String msgBody = stringBuilder.toString();

        //get professor
        Document professor = null; //I don't know how to get that
        String to = "pschmitt@oswego.edu";

        //get teams that haven't submitted
        MongoCursor<Document> query = teamCollection.find().iterator();
        List<Document> teams = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            teams.add(document);
        }
        query.close();
        List<Document> lateTeams = new ArrayList<>();
        for(Document team : teams){
            //get list of submissions
            query = submissionCollection.find().iterator();
            List<Document> submissions = new ArrayList<>();
            while(query.hasNext()){
                Document document = query.next();
                submissions.add(document);
            }
            //check each team to match course id, assignment id, and team name
            boolean teamHasSubmitted = false;
            for(Document submission : submissions){
                if(submission.getString("course_id").equals(course.courseID) &&
                        submission.getString("assignment_id").equals(assignment.assignmentID) &&
                        submission.getString("team_name").equals(team.getString("team_id"))){
                    teamHasSubmitted = true;
                    break;
                }
            }
            if(!teamHasSubmitted){
                //not all teams have submitted, do not send email.
                lateTeams.add(team);
            }
        }
        String teamsList = "";
        for(Document team : lateTeams){
            teamsList += team.getString("team_name") + " ";
        }

        //fill in specifics
        msgBody.replace("[Assignment Name]", assignment.assignmentName);
        msgBody.replace("[Course Name]", course.courseName);
        msgBody.replace("[Today's Date]", LocalDate.now().toString());
        msgBody.replace("[Name of Instructor]", professor.getString("professor_id"));
        msgBody.replace("[Assignment Due Date]", assignment.dueDate);
        msgBody.replace("[All Team Names That Have Not Submitted the Assignment]", teamsList);

        //send email
        String msgSubject = "An Assignment's Deadline has Passed";
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from, "NoReply"));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to, "Patrick Schmitt")); //to fix once I get professor name
            msg.setSubject(msgSubject);
            msg.setText(msgBody);
            Transport.send(msg);
            System.out.println("Email sent successfully...");
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
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
        //load email body from template
        BufferedReader reader = new BufferedReader(new FileReader(new File("templates/assignmentSubmittedEmail.html")));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        while((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        String msgBody = stringBuilder.toString();

        //get list of recipients
        MongoCursor<Document> query = studentCollection.find().iterator();
        List<Document> students = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            students.add(document);
        }
        List<Document> studentsInTeam = new ArrayList<>();
        for(String id : team.getTeamMembers()) {
            for (Document student : students) {
                if(student.getString("student_id").equals(id)){
                    studentsInTeam.add(student);
                    break;
                }
            }
        }

        //get professor
        Document professor = null; //I don't know how to get that

        for(Document student : studentsInTeam){
            String to = student.getString("student_id") + "oswego.edu"; //to fix once email domain is stored in db
            //fill in specifics
            msgBody.replace("[Team Name]", team.getTeamID());
            msgBody.replace("[Name of Course]", course.courseName);
            msgBody.replace("[Date of Submission]", LocalDate.now().toString());
            msgBody.replace("[Instructor Name]", professor.getString("professor_id"));

            //send email
            String msgSubject = "Assignment Submission Confirmation";
            try {
                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(from, "NoReply"));
                msg.addRecipient(Message.RecipientType.TO,
                        new InternetAddress(to, "Patrick Schmitt")); //to fix once I get professor name
                msg.setSubject(msgSubject);
                msg.setText(msgBody);
                Transport.send(msg);
                System.out.println("Email sent successfully...");
            } catch (MessagingException | UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

        }
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
        //load email body from template
        BufferedReader reader = new BufferedReader(new FileReader(new File("templates/gradeReceivedEmail.html")));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        while((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        String msgBody = stringBuilder.toString();

        //get list of recipients
        MongoCursor<Document> query = studentCollection.find().iterator();
        List<Document> students = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            students.add(document);
        }
        List<Document> studentsInTeam = new ArrayList<>();

        for(Document student : studentsInTeam){
            String to = student.getString("student_id") + "@oswego.edu"; //To be fixed once db has email domains

            //fill in specifics
            msgBody.replace("[Today's Date]", LocalDate.now().toString());
            msgBody.replace("[Name of Student]", student.getString("first_name") + " " + student.getString("last_name"));
            msgBody.replace("[Team Name]", team.getTeamID());
            msgBody.replace("[Assignment Name]", assignment.assignmentName);
            msgBody.replace("[Course Name]", course.courseName);

        }

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
}
