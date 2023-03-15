package edu.oswego.cs.util;

import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;

import edu.oswego.cs.daos.AssignmentDAO;
import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.database.DatabaseManager;
import edu.oswego.cs.services.EmailService;
import org.bson.Document;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DeadlineTimer extends Thread{
    private boolean isPeerReview;
    private String id;

    /**
     * Constructor. Sets id to be checked periodically and isPR to know which deadline to check.
     *
     * @param id_ id of the assignment
     * @param isPR true if the deadline to be checked is the Peer Review Deadline instead of the Assignment Deadline
     */
    public DeadlineTimer(String id_, boolean isPR){
        isPeerReview = isPR;
        id = id_;
    }

    /**
     * override of Thread.run(). Checks every hour that the assignment still exists, and that the deadline hasn't passed.
     * Calls EmailService.assignmentDeadlinePassed() or EmailService.peerReviewDeadlinePassed() when the respective case
     * comes up.
     */
    public void run(){
        while(true){
            DatabaseManager databaseManager = new DatabaseManager();
            MongoCollection<Document> assignmentCollection = databaseManager.getAssignmentDB().getCollection("assignments");
            Document assignment = assignmentCollection.find(eq("assignment_id", id)).first();
            if(assignment == null){
                //assignment was deleted; no need for timer
                return;
            }
            try {
                if(isPeerReview) {
                    if (new SimpleDateFormat("yyyy-mm-dd").parse(assignment.getString("peer_review_due_date")).before(new Date())) {
                        AssignmentDAO aDao = new AssignmentDAO(assignment.getInteger("assignment_id"), assignment.getString("assignment_name"), assignment.getString("due_date"), assignment.getString("peer_review_due_date"));
                        Document course = databaseManager.getCourseDB().getCollection("courses").find(eq("course_id", assignment.getString("course_id"))).first();
                        CourseDAO cDao = new CourseDAO(course.getString("abbreviation"), course.getString("course_name"), course.getString("course_section"), course.getString("crn"), course.getString("semester"), course.getString("year"), true);
                        EmailService.assignmentDeadlinePassed(cDao, aDao);
                        return;
                    }
                }else{
                    if (new SimpleDateFormat("yyyy-mm-dd").parse(assignment.getString("due_date")).before(new Date())) {
                        AssignmentDAO aDao = new AssignmentDAO(assignment.getInteger("assignment_id"), assignment.getString("assignment_name"), assignment.getString("due_date"), assignment.getString("peer_review_due_date"));
                        Document course = databaseManager.getCourseDB().getCollection("courses").find(eq("course_id", assignment.getString("course_id"))).first();
                        CourseDAO cDao = new CourseDAO(course.getString("abbreviation"), course.getString("course_name"), course.getString("course_section"), course.getString("crn"), course.getString("semester"), course.getString("year"), true);
                        EmailService.assignmentDeadlinePassed(cDao, aDao);
                        return;
                    }
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                sleep(3600000); //sleeps for an hour
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
