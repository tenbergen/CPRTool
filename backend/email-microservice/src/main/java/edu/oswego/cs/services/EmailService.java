package edu.oswego.cs.services;

import edu.oswego.cs.database.AssignmentInterface;
import edu.oswego.cs.database.CourseInterface;
import org.bson.Document;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.*;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * This class contains all the methods that deal with sending emails. These methods are called in edu.oswego.cs.services.EmailService.
 *
 */
public class EmailService {
    /**
     * Sends an email to the professor informing them that all teams in the course have submitted an assignment.
     * The email will not be sent if the assignment deadline has passed.
     * This method is otherwise called every time a team submits an assignment, at which point
     * it checks every team in the course to see if all of them have submitted it. Sends the email if and only if
     * all teams have submitted.
     *
     * @param courseID ID of the course in which the assignment is assigned
     * @param assignmentID ID the assignment which has been submitted
     */

    public void allAssignmentsSubmittedEmail(String courseID, int assignmentID) throws IOException {
        Document assignment = new AssignmentInterface().getSpecifiedAssignment(courseID, assignmentID);
        //turns string date into unix timestamp and compares it to current time to tell if due date has passed.
        if(new SimpleDateFormat("yyyy-MM-dd").parse((assignment.getString("due_date")), new ParsePosition(0)).getTime() < new Date().getTime()){
            //overdue, don't send email.
            return;
        }
        if(!allSubmitted(assignment)){
            //not everyone has submitted
            return;
        }
        //prerequisites are met

        //load template
        String body = getTemplate("allAssignmentsSubmitted.html");

        String subject = "An assignment has been submitted by all students and is ready for grading.";
        Document course = new CourseInterface().getCourse(courseID);

        //fill in specifics
        body = body.replace("[Course Name]", course.getString("course_name"));
        body = body.replace("[Today's Date]", new Date().toString());
        body = body.replace("[Name of Instructor]", course.getString("professor_id"));
        body = body.replace("[Assignment Name]", assignment.getString("assignment_name"));

        sendEmail(new CourseInterface().getProfessorEmail(course.getString("professor_id")), subject, body);
    }

    /**
     * Sends an email to the professor informing them that all teams in the course have submitted a peer review.
     * This method will not be called if the peer review deadline has passed.
     * This method is otherwise called every time a team submits a peer review, at which point
     * it checks every team in the course to see if all of them have submitted it. Sends the email if and only if
     * all teams have submitted.
     *
     * @param courseID the course in which the peer review is assigned
     * @param assignmentID the peer review which has been submitted
     */
    public void allPeerReviewsSubmittedEmail(String courseID, int assignmentID) throws IOException {
        Document assignment = new AssignmentInterface().getSpecifiedAssignment(courseID, assignmentID);
        //turns string date into unix timestamp and compares it to current time to tell if due date has passed.
        if(new SimpleDateFormat("yyyy-MM-dd").parse((assignment.getString("peer_review_due_date")), new ParsePosition(0)).getTime() < new Date().getTime()){
            //overdue, don't send email.
            return;
        }
        if(!allPRSubmitted(assignment)){
            //not everyone has submitted
            return;
        }
        //prerequisites are met

        //load template
        String body = getTemplate("allPeerReviewsSubmittedEmail.html");

        String subject = "An assignment has been submitted by all students and is ready for grading.";
        Document course = new CourseInterface().getCourse(courseID);

        //fill in specifics
        body = body.replace("[Course Name]", course.getString("course_name"));
        body = body.replace("[Today's Date]", new Date().toString());
        body = body.replace("[Name of Instructor]", course.getString("professor_id"));
        body = body.replace("[Assignment Name]", assignment.getString("assignment_name"));

        sendEmail(new CourseInterface().getProfessorEmail(course.getString("professor_id")), subject, body);
    }

    /**
     * Sends an email to all the students in the course when an assignment is created by the professor.
     * Called when the professor finished posting the assignment.
     *
     * @param courseID the course in which the peer review is assigned
     * @param assignmentID the assignment that has been created
     */
    public void assignmentCreatedEmail(String courseID, int assignmentID) throws IOException {
        //read contents of template
        String template = getTemplate("assignmentCreatedEmail.html");

        //get a list of all the students in the course
        List<Document> students = new CourseInterface().getStudentsInCourse(courseID);

        String subject = "A new assignment has been created in one of your classes.";

        for(Document student : students){
            String body = "" + template; //copy template
            //Fill in specifics
            body = body.replace("[Name of Student]", student.getString("first_name") + " " + student.getString("last_name"));
            body = body.replace("[Today's Date]", new Date().toString());
            body = body.replace("[Course Name]", new CourseInterface().getCourse(courseID).getString("course_name"));
            body = body.replace("[Assignment Name]", new AssignmentInterface().getSpecifiedAssignment(courseID, assignmentID).getString("assignment_name"));
            body = body.replace("[Due Date]", new AssignmentInterface().getSpecifiedAssignment(courseID, assignmentID).getString("due_date"));
            body = body.replace("[Instructor Name]", new CourseInterface().getCourse(courseID).getString("professor_id"));

            //send email
            sendEmail(new CourseInterface().getProfessorEmail(student.getString("student_id")), subject, body);
        }
    }

        /**
     * Sends an email to the professor when the deadline for an assignment has passed and not all teams have made a
     * submission for it. A timer is created when the assignment is created (See createDeadlineTimer). This method is
     * called when the timer reaches its end.
     *
     * @param courseID the course in which the assignment is assigned
     * @param assignmentID the assignment whose deadline has passed
     */
    public  void assignmentDeadlinePassed(String courseID, int assignmentID) throws IOException {
        Document assignment = new AssignmentInterface().getSpecifiedAssignment(courseID, assignmentID);

        if(allSubmitted(assignment)){
            //everyone has submitted
            return;
        }

        //load template
        String body = getTemplate("assignmentDeadlinePassed.html");

        String subject = "An assignment's deadline has passed and not all teams have submitted.";
        Document course = new CourseInterface().getCourse(courseID);

        //fill in specifics
        body = body.replace("[Course Name]", course.getString("course_name"));
        body = body.replace("[Today's Date]", new Date().toString());
        body = body.replace("[Name of Instructor]", course.getString("professor_id"));
        body = body.replace("[Assignment Name]", assignment.getString("assignment_name"));
        body = body.replace("[Assignment Due Date]", assignment.getString("due_date"));

        sendEmail(new CourseInterface().getProfessorEmail(course.getString("professor_id")), subject, body);
    }

    /**
     * Sends an email to all the members of a team after they submit an assignment to act as a digital receipt.
     *
     * @param courseID course for which the assignment was submitted
     * @param teamID team that submitted the assignment
     * @param assignmentID assignment that was submitted
     */
    public void assignmentSubmittedEmail(String courseID, String teamID, int assignmentID) throws IOException {
        //read contents of template
        String template = getTemplate("assignmentSubmittedEmail.html");

        String subject = "Assignment Submission Receipt";
        Document course = new CourseInterface().getCourse(courseID);
        Document assignment = new AssignmentInterface().getSpecifiedAssignment(courseID, assignmentID);

        //get all students in team
        List<Document> teams = new CourseInterface().getTeamsInCourse(courseID);
        Document team = null;
        for(Document t : teams){
            if(t.getString("team_id").equals(teamID)){
                team = t;
            }
        }
        List<String> students = team.getList("team_members", String.class);
        for(String student : students){
            String to = student;
            Document studentDoc = new CourseInterface().getStudent(student);

            String body = "" + template; //copy template
            body = body.replace("[Team Name]", team.getString("team_id"));
            body = body.replace("[Today's Date]", new Date().toString());
            body = body.replace("[Name of Student]", studentDoc.getString("first_name") + " " + studentDoc.getString("last_name"));
            body = body.replace("[Name of Course]", course.getString("course_name"));
            body = body.replace("[Time of Submission]", new Date().toString());
            body = body.replace("[Assignment Name]", assignment.getString("assignment_name"));
            body = body.replace("[Instructor Name]", course.getString("professor_id"));

            sendEmail(new CourseInterface().getStudentEmail(to), subject, body);
        }
    }

    /**
     * Sends the student an email when the professor submits a grade for their assignment.
     * Does not work right now due to DB troubles.
     *
     * @param courseID course for which the grade is given
     * @param assignmentID assignment for which the grade is given
     * @param teamID team receiving the email
     */
    public void gradeReceivedEmail(String courseID, int assignmentID, String teamID) throws IOException {
        //read contents of template
        String template = getTemplate("gradeReceivedEmail.html");

        String subject = "An assignment has been graded";
        Document course = new CourseInterface().getCourse(courseID);
        Document assignment = new AssignmentInterface().getSpecifiedAssignment(courseID, assignmentID);
        Document submission = new AssignmentInterface().getSubmission(assignmentID, teamID);

        //get all students in team
        List<Document> teams = new CourseInterface().getTeamsInCourse(courseID);
        Document team = null;
        for(Document t : teams){
            if(t.getString("team_id").equals(teamID)){
                team = t;
            }
        }
        List<String> students = team.getList("team_members", String.class);

        for(String student : students){
            String to = student;
            Document studentDoc = new CourseInterface().getStudent(student);

            String body = "" + template; //copy template
            body = body.replace("[Team Name]", team.getString("team_id"));
            body = body.replace("[Today's Date]", new Date().toString());
            body = body.replace("[Name of Student]", studentDoc.getString("first_name") + " " + studentDoc.getString("last_name"));
            body = body.replace("[Course Name]", course.getString("course_name"));
            body = body.replace("[Assignment Name]", assignment.getString("assignment_name"));
            body = body.replace("[Grade]", submission.getDouble("grade").toString());
            body = body.replace("[Instructor Name]", course.getString("professor_id"));

            sendEmail(new CourseInterface().getStudentEmail(to), subject, body);
        }
    }

    /**
     * Sends the professor an email when an outlier is detected.
     *
     * @param courseID course in which the outlier review was submitted
     * @param teamID team that submitted the outlier review
     * @param assignmentID assignment for which the outlier view was submitted
     */
    public void outlierDetectedEmail(String courseID, String teamID, int assignmentID) throws IOException {
        //outlier detection isn't implemented yet.
        //read contents of template
        String body = getTemplate("outlierDetectedEmail.html");

        String subject = "An outlier peer review grade has been detected";
        Document course = new CourseInterface().getCourse(courseID);
        Document assignment = new AssignmentInterface().getSpecifiedAssignment(courseID, assignmentID);

        //fill in specifics
        body = body.replace("[Course Name]", course.getString("course_name"));
        body = body.replace("[Today's Date]", new Date().toString());
        body = body.replace("[Name of Instructor]", course.getString("professor_id"));
        body = body.replace("[Assignment Name]", assignment.getString("assignment_name"));
        body = body.replace("[Name of Team That Submitted the Outlier Peer Review]", teamID);

        sendEmail(new CourseInterface().getProfessorEmail(course.getString("professor_id")), subject, body);

    }

    /**
     * Sends all the students in a course an email when peer reviews get assigned.
     *
     * @param courseID course in which the peer review is assigned
     * @param assignmentID submission being peer reviewed
     */
    public void peerReviewAssignedEmail(String courseID, int assignmentID) throws IOException {
        //read contents of template
        String template = getTemplate("peerReviewAssignedEmail.html");

        String subject = "Peer reviews have been assigned for your team";
        Document course = new CourseInterface().getCourse(courseID);
        Document assignment = new AssignmentInterface().getSpecifiedAssignment(courseID, assignmentID);

        List<Document> students = new CourseInterface().getStudentsInCourse(courseID);

        for(Document student : students){
            String to = student.getString("student_id");

            String body = "" + template; //copy template
            body = body.replace("[Number of Peer Reviews]", assignment.getInteger("reviews_per_team").toString());
            body = body.replace("[Course Name]", course.getString("course_name"));
            body = body.replace("[Today's Date]", new Date().toString());
            body = body.replace("[Name of Student]", student.getString("first_name") + " " + student.getString("last_name"));
            body = body.replace("[Due Date]", assignment.getString("peer_review_due_date"));
            body = body.replace("[Assignment Name]", assignment.getString("assignment_name"));
            body = body.replace("[Instructor Name]", course.getString("professor_id"));

            sendEmail(new CourseInterface().getStudentEmail(to), subject, body);
        }
    }

    /**
     * Sends an email to the professor when the deadline for a peer review has passed and not all teams have made a
     * submission for it. A timer is created when the peer review assignment is created (See createDeadlineTimer). This
     * method is called when the timer reaches its end.
     *
     * @param courseID the course in which the assignment is assigned
     * @param assignmentID the assignment whose deadline has passed
     */
    public void peerReviewDeadlinePassed(String courseID, int assignmentID) throws IOException {
        Document assignment = new AssignmentInterface().getSpecifiedAssignment(courseID, assignmentID);

        if(allPRSubmitted(assignment)){
            //everyone has submitted
            return;
        }

        //prerequisites met

        //load template
        String body = getTemplate("peerReviewDeadlinePassed.html");

        String subject = "An peer review's deadline has passed and not all teams have submitted.";
        Document course = new CourseInterface().getCourse(courseID);

        //fill in specifics
        body = body.replace("[Course Name]", course.getString("course_name"));
        body = body.replace("[Today's Date]", new Date().toString());
        body = body.replace("[Name of Instructor]", course.getString("professor_id"));
        body = body.replace("[Assignment Name]", assignment.getString("assignment_name"));
        body = body.replace("[Peer Review Due Date]", assignment.getString("peer_review_due_date"));

        sendEmail(new CourseInterface().getProfessorEmail(course.getString("professor_id")), subject, body);
    }

    /**
     * Sends an email to all the members of a team after they submit a peer review to act as a digital receipt.
     *
     * @param s student who submitted the assignment
     * @param courseID course for which the peer review was submitted
     * @param revTeamID ID of team that was reviewed
     * @param assignmentID peer review that was submitted
     */
    public void peerReviewSubmittedEmail(Document s, String courseID, String revTeamID, int assignmentID) throws IOException {
        //read contents of template
        String template = getTemplate("peerReviewSubmittedEmail.html");

        String subject = "Peer Review Submission Receipt";
        Document course = new CourseInterface().getCourse(courseID);
        Document assignment = new AssignmentInterface().getSpecifiedAssignment(courseID, assignmentID);

        //get all students in team
        List<Document> teams = new CourseInterface().getTeamsInCourse(courseID);
        Document team = null;
        for(Document t : teams){
            for(String m : t.getList("team_members", String.class)){
                if(m.equals(s.getString("student_id"))){
                    team = t;
                    break;
                }
            }
            if(team != null){
                break;
            }
        }
        List<String> students = team.getList("team_members", String.class);
        for(String student : students){
            String to = student;
            Document studentDoc = new CourseInterface().getStudent(student);

            String body = "" + template; //copy template
            body = body.replace("[Reviewed Team Name]", revTeamID);
            body = body.replace("[Team Name]", team.getString("team_id"));
            body = body.replace("[Course Name]", course.getString("course_name"));
            body = body.replace("[Today's Date]", new Date().toString());
            body = body.replace("[Name of Student]", studentDoc.getString("first_name") + " " + studentDoc.getString("last_name"));
            body = body.replace("[Name of Course]", course.getString("course_name"));
            body = body.replace("[Time of Submission]", new Date().toString());
            body = body.replace("[Assignment Name]", assignment.getString("assignment_name"));
            body = body.replace("[Instructor Name]", course.getString("professor_id"));

            sendEmail(new CourseInterface().getStudentEmail(to), subject, body);
        }
    }

    /**
     * Reads a file in the templates folder and returns its contents as a string. Used for getting html templates of
     * email bodies.
     *
     * @param fileName the name of the file to be read. Do not pass the entire path, only the file's name.
     * @return the contents of the file as a string
     */
    public String getTemplate(String fileName) throws IOException {
        String path = getPath();
        BufferedReader in = new BufferedReader(new FileReader(path + "templates"
                + File.separator + fileName));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        String ls = System.getProperty("line.separator");
        while((line = in.readLine()) != null){
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        in.close();
        return stringBuilder.toString();
    }

    /**
     * Sends an email with the specified recipients, subject, and body. Called by all the email methods in this class.
     *
     * @param to recipient's email address
     * @param subject subject of the email to be sent
     * @param body body of the email to be sent
     */
    public void sendEmail(String to, String subject, String body){
        String from="cprtoolemail@gmail.com";
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        Session session = Session.getInstance(prop, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("cprtoolemail@gmail.com", "nxscmpuonighvnfp");
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from, "NoReply"));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to, "Recipient"));
            msg.setSubject(subject);
            msg.setContent(body, "text/html");
            Transport.send(msg);
            System.out.println("Email sent successfully...");
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * gets the path from the working directory to "defaultserver". On the production build, you'll need to change this
     * to the equivalent base target directiory directly above the "templates" folder.
     *
     * @return the path from the working directory to "defaultserver"
     */
    public String getPath() {
        String path = (System.getProperty("user.dir").contains("\\")) ? System.getProperty("user.dir").replace("\\", "/") : System.getProperty("user.dir");
        String[] slicedPath = path.split("/");
        String targetDir = "defaultServer";
        StringBuilder relativePathPrefix = new StringBuilder();

        if(slicedPath.length == 0){
            return "";
        }
        for (int i = slicedPath.length - 1; !slicedPath[i].equals(targetDir); i--) {
            relativePathPrefix.append("../");
        }

        if (System.getProperty("user.dir").contains("\\")) {
            String reg = "//";
            relativePathPrefix = new StringBuilder(relativePathPrefix.toString().replace("/", "\\"));
        }

        return relativePathPrefix.toString();
    }

    /**
     * checks to see if every team has submitted a given assignment.
     *
     * @param assignment assignment being checked
     * @return true if every team in the course has submitted the assignment, false otherwise
     */
    public boolean allSubmitted(Document assignment) {
        String courseID = assignment.getString("course_id");
        List<Document> teams = new CourseInterface().getTeamsInCourse(courseID);
        for(Document team : teams){
            if(!new AssignmentInterface().hasTeamSubmitted(assignment.getInteger("assignment_id"), team.getString("team_id"))){
                //has not submitted
                return false;
            }
        }
        //everyone has submitted
        return true;
    }


    /**
     * Returns true if all teams have submitted all their peer reviews for the given assignment
     *
     * @param assignment assignment in question
     * @return true if all peer reviews have been submitted, false otherwise
     */
    public boolean allPRSubmitted(Document assignment) {
        //get completed teams to see if all peer reviews have been submitted
        Map<String, List<String>> completedTeams = (Map<String, List<String>>) assignment.get("completed_teams");
        if(!assignment.containsKey("reviews_per_team")){
            //never got to PR stage
            return false;
        }
        int reviewsPerTeam = assignment.getInteger("reviews_per_team");
        for(Map.Entry<String, List<String>> entry : completedTeams.entrySet()){
            if(entry.getValue().size() != reviewsPerTeam){
                return false;
            }
        }
        return true;
    }
}
