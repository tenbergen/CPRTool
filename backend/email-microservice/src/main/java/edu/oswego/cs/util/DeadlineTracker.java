package edu.oswego.cs.util;

import edu.oswego.cs.database.AssignmentInterface;
import edu.oswego.cs.services.EmailService;
import org.bson.Document;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * This class is used to keep track of when assignments' deadlines pass, to call the appropriate email methods.
 * It checks approximately every five minutes to see if a deadline has passed.
 * The lists assignments and reviews are updated every time a new assignment is posted.
 * <p>
 * To test methods called by DeadlineTracker on a local build, you can change your computer time to simulate
 * time changes.
 */
@Stateless
public class DeadlineTracker{
    private static List<Document> assignments; //list of assignment IDs whose deadline is yet to pass
    private static List<Document> reviews; //list of assignment IDs whose peer review deadline is yet to pass

    /**
     * Goes through every assignment in the database and adds the ones with future deadlines to the lists
     */
    public void init(){
        List<Document> allAssignments = new AssignmentInterface().getAllAssignments();
        for(Document a : allAssignments){
            if(new SimpleDateFormat("yyyy-MM-dd").parse(a.getString("due_date"), new ParsePosition(0)).getTime()
                    > new Date().getTime()){
                assignments.add(a);
            }
            if(new SimpleDateFormat("yyyy-MM-dd").parse(a.getString("peer_review_due_date"), new ParsePosition(0)).getTime()
                    > new Date().getTime()){
                reviews.add(a);
            }
        }
    }

    /**
     * Checks every five minutes that the assignment still exists, and that the deadline hasn't passed.
     * Calls EmailService.assignmentDeadlinePassed() or EmailService.peerReviewDeadlinePassed() when the respective case
     * comes up.
     */
    @Schedule(hour = "*", minute = "*/5", persistent = false)
    public void check(){
        System.out.println("check"); //proves that this method is actually being run.
        if(assignments == null || reviews == null){
            init();
        }
        AssignmentInterface ai = new AssignmentInterface();
        for(Document a : assignments){
            if(ai.doesAssignmentExist(a.getString("course_id"), a.getInteger("assignment_id"))){
                //assignment exists
                if(new SimpleDateFormat("yyyy-MM-dd").parse(a.getString("due_date"), new ParsePosition(0)).getTime()
                    < new Date().getTime()){
                    //due date has passed
                    try {
                        new EmailService().assignmentDeadlinePassed(a.getString("course_id"), a.getInteger("assignment_id"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    assignments.remove(a);
                }
            }else{
                //assignment doesn't exist
                assignments.remove(a);
            }
        }
        for(Document r : reviews){
            if(ai.doesAssignmentExist(r.getString("course_id"), r.getInteger("assignment_id"))){
                //assignment exists
                if(new SimpleDateFormat("yyyy-MM-dd").parse(r.getString("peer_review_due_date"), new ParsePosition(0)).getTime()
                        < new Date().getTime()){
                    //due date has passed
                    try {
                        new EmailService().peerReviewDeadlinePassed(r.getString("course_id"), r.getInteger("assignment_id"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    reviews.remove(r);
                }
            }else{
                //assignment doesn't exist
                reviews.remove(r);
            }
        }
    }

    public static void addAssignment(Document assignment){
        assignments.add(assignment);
        reviews.add(assignment);
    }
}
