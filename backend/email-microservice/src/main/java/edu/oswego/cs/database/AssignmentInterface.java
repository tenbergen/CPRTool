package edu.oswego.cs.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.util.CPRException;
import org.bson.Document;

import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;


/**
 * AssignmentInterface was originally copied from professor-assignment-microservice, but has had methods added and
 * removed and is no longer equivalent.
 */
public class AssignmentInterface {
    private final MongoCollection<Document> assignmentsCollection;
    private final MongoCollection<Document> courseCollection;
    private final MongoCollection<Document> submissionCollection;
    private static String reg;

    /**
     * Constructor
     */
    public AssignmentInterface() {
        try {
            DatabaseManager manager = new DatabaseManager();
            MongoDatabase assignmentDatabase = manager.getAssignmentDB();
            assignmentsCollection = assignmentDatabase.getCollection("assignments");
            submissionCollection = assignmentDatabase.getCollection("submissions");
            MongoDatabase courseDatabase = manager.getCourseDB();
            courseCollection = courseDatabase.getCollection("courses");
        } catch (CPRException e) {
            throw new CPRException(Response.Status.BAD_REQUEST,"Failed to retrieve collections.");
        }
    }

    /**
     * gets every assignment in the database
     *
     * @return List of every assignment in the database
     */
    public List<Document> getAllAssignments() {
        MongoCursor<Document> query = assignmentsCollection.find().iterator();
        List<Document> assignments = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            assignments.add(document);
        }
        return assignments;
    }

    /**
     * gets the assignments in the specified course
     *
     * @param courseID course with desired assignments
     * @return list of assignments
     */
    public List<Document> getAssignmentsByCourse(String courseID) {
        MongoCursor<Document> query = assignmentsCollection.find(eq("course_id", courseID)).iterator();
        if (!query.hasNext()) return Collections.emptyList();

        List<Document> assignments = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            assignments.add(document);
        }
        return assignments;
    }


    /**
     * gets a particular assignment
     *
     * @param courseID course with desired assignment
     * @param AssignmentID desired assignment's ID
     * @return the desired assignment
     */
    public Document getSpecifiedAssignment(String courseID, int AssignmentID) {
        Document assignment = assignmentsCollection.find(and(
                eq("course_id", courseID),
                eq("assignment_id", AssignmentID))).first();
        if (assignment == null) throw new CPRException(Response.Status.BAD_REQUEST,"No assignment by this name found");
        return assignment;
    }

    /**
     * Determines if an assignment is in the database and returns true if it is
     *
     * @param courseID course in which assignment would belong
     * @param AssignmentID assignment being searched for
     * @return true if the assignment exists
     */
    public boolean doesAssignmentExist(String courseID, int AssignmentID){
        Document assignment = assignmentsCollection.find(and(
                eq("course_id", courseID),
                eq("assignment_id", AssignmentID))).first();
        if (assignment == null) return false;
        return true;
    }

    /**
     * gets a particular submission
     *
     * @param assignmentID id of assignment submission is for
     * @param teamID id of team that made the submission
     * @return the desired submission
     */
    public Document getSubmission(int assignmentID, String teamID){
        return submissionCollection.find(and(eq("assignment_id", assignmentID),eq("team_name", teamID))).first();
    }

    /**
     * Returns true if there is a submission with the matching assignment id and team id. false otherwise.
     *
     * @param assignmentID id of assignment in question
     * @param teamID id of team in question
     * @return true if there is a submission with the matching assignment id and team id. false otherwise.
     */
    public boolean hasTeamSubmitted(int assignmentID, String teamID){
        Document submission = submissionCollection.find(and(eq("team_name", teamID), eq("assignment_id", assignmentID))).first();
        if(submission == null){
            return false;
        }
        return true;
    }
}