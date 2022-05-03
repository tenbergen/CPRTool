package edu.oswego.cs.rest.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.rest.daos.AssignmentDAO;
import edu.oswego.cs.rest.daos.FileDAO;
import org.bson.Document;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.set;


public class AssignmentInterface {

    static MongoDatabase assignmentDatabase;
    static MongoDatabase teamsDatabase;
    static MongoCollection<Document> assignmentsCollection;
    static MongoCollection<Document> teamsCollection;
    static MongoCollection<Document> submissionCollection;

    static String reg = "/";

    public AssignmentInterface() {
        try {
            DatabaseManager manager = new DatabaseManager();
            assignmentDatabase = manager.getAssignmentDB();
            teamsDatabase = manager.getTeamDB();
            assignmentsCollection = assignmentDatabase.getCollection("assignments");
            submissionCollection = assignmentDatabase.getCollection("submissions");
            teamsCollection = teamsDatabase.getCollection("teams");

        } catch (WebApplicationException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to retrieve collections.").build());
        }
    }

    public void writeToAssignment(FileDAO fileDAO) throws IOException {
        String path = "assignments" + reg
                + fileDAO.getCourseID() + reg
                + fileDAO.getAssignmentID() + reg
                + "team-submissions";

        if (!new File(path).exists()) {
            new File(path).mkdirs();
        }
        fileDAO.writeFile(path + reg + fileDAO.getFilename());
        makeSubmission(fileDAO.getCourseID(), fileDAO.getAssignmentID(), fileDAO.getFilename(), fileDAO.getTeamName());
    }

    /**
     * Retrieves the relative location of the root Directory
     *
     * @return String directory location the hw files should be saved to
     */
    public static String getRelPath() {
        String path = (System.getProperty("user.dir").contains("\\")) ? System.getProperty("user.dir").replace("\\", "/") : System.getProperty("user.dir");
        String[] slicedPath = path.split("/");
        String targetDir = "student-assignment-microservice";
        int i;
        StringBuilder relativePathPrefix = new StringBuilder();
        System.out.println(Arrays.toString(slicedPath));
        for (i = slicedPath.length - 1; !slicedPath[i].equals(targetDir); i--) {
            relativePathPrefix.append("../");
        }
        reg = "/";
        if (System.getProperty("user.dir").contains("\\")) {
            reg = "\\";
            relativePathPrefix = new StringBuilder(relativePathPrefix.toString().replace("/", "\\"));
        }
        return relativePathPrefix.toString();
    }

    public List<Document> getAllUserAssignments(String courseID, String studentID){
        MongoCursor<Document> query = submissionCollection.find(and(eq("course_id",courseID),
                eq("members",studentID),
                eq("type","team_submission"))).iterator();
        List<Document> assignments = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            assignments.add(document);
        }
        if (assignments.isEmpty())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment does not exist").build());
        query.close();
        return assignments;
    }

    public List<Document> getSpecifiedUserAssignment(String courseID, int assignmentID,String studentID){
        MongoCursor<Document> query = submissionCollection.find(and(eq("course_id",courseID),
                eq("members",studentID),
                eq("assignment_id",assignmentID),
                eq("type","team_submission"))).iterator();
        List<Document> assignments = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            assignments.add(document);
        }
        if (assignments.isEmpty())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment does not exist").build());
        query.close();
        return assignments;
    }

    public List<Document> getAssignmentSubmissions(String courseID, int assignmentID){
        MongoCursor<Document> query = submissionCollection.find(and(eq("course_id",courseID),
                eq("assignment_id",assignmentID),
                eq("type","team_submission"))).iterator();
        List<Document> assignments = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            assignments.add(document);
        }
        if (assignments.isEmpty())
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment does not exist").build());
        query.close();
        return assignments;
    }

    public void makeSubmission(String course_id,int assignment_id,String file_name, String teamName){
        Document team = teamsCollection.find(and(eq("team_id", teamName), eq("course_id", course_id))).first();
        Document assignment = assignmentsCollection.find(and(
                                                            eq("course_id", course_id),
                                                            eq("assignment_id", assignment_id)
                                                            )).first();
        if(assignment == null)
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("this assignment was not found in this course").build());
        String assignmentName = assignment.getString("assignment_name");
        System.out.println(team);

        if(team == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("this team was not found in this course").build());
        }
        if (team.getList("team_members", String.class) == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Members not defined in team").build());
        }
        if (team.get("team_id", String.class) == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("team_id not defined").build());
        }
        String path = "assignments"+ reg+course_id+reg+assignment_id+reg+"team_submissions";
        Document new_submission = new Document()
                .append("course_id",course_id)
                .append("assignment_id",assignment_id)
                .append("assigment_name", assignmentName)
                .append("submission_name",file_name)
                .append("team_name",team.getString("team_id"))
                .append("members",team.getList("team_members",String.class))
                .append("type","team_submission")
                .append("grade", -1)
                .append("path",path+reg+file_name)
                .append("peer_review_due_date",assignment.get("peer_review_due_date"));
        System.out.println(new_submission);
        boolean submissionCheck = submissionCollection.find(and(eq("course_id",course_id),eq("assignment_id",assignment_id),eq("team_name",team.getString("team_id")))).iterator().hasNext();
        if(submissionCheck){
            Document extensionCheck = submissionCollection.find(and(eq("course_id",course_id),eq("assignment_id",assignment_id),eq("team_name",team.getString("team_id")))).first();
            if (extensionCheck.getString("submission_name").equals(file_name)) {
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("submission already exists").build());
            }else {
                submissionCollection.deleteOne(extensionCheck);
                submissionCollection.insertOne(new_submission);
            }
        }else submissionCollection.insertOne(new_submission);
    }

    public Document allAssignments(String couse_id,String student_id){

        //Set up for checking due date
        Calendar calendar = Calendar.getInstance();
        String[] splitPeerReviewDueDate;
        Date peerReviewDueDate;
        Date currentDate = new Date();

        MongoCursor<Document> submissions = submissionCollection.find(
                and(
                        eq("course_id",couse_id),
                        or(eq("members",student_id),eq("reviewed_by_members",student_id)),
                        or(eq("type","team_submission"),eq("type","peer_review_submission"))
                )
        ).iterator();
        if(!submissions.hasNext()) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No submissions").build());
        List<Document>AllSubmissions = new ArrayList<>();
        while(submissions.hasNext()){
            Document submission = submissions.next();

            // check if peer review is pass due and not graded, finalize grades if so.     sideNote: date format -> yyyy-MM-dd
            if ((int) submission.get("grade") != -1) {
                splitPeerReviewDueDate = submission.get("peer_review_due_date").toString().split("-");
                calendar.set(Calendar.YEAR, Integer.parseInt(splitPeerReviewDueDate[0]));
                calendar.set(Calendar.MONTH, Integer.parseInt(splitPeerReviewDueDate[1]));
                calendar.set(Calendar.DATE, Integer.parseInt(splitPeerReviewDueDate[2]));
                peerReviewDueDate = calendar.getTime();

                if (currentDate.after(peerReviewDueDate)) {
                    makeFinalGrades(couse_id, (int) submission.get("assignment_id"));
                }
            }
            if(submission.getInteger("assignment_id")==null){
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No ID in this submission").build());
            }
            Document Assignment = assignmentsCollection.find(
                    and(
                            eq("course_id",couse_id),
                            eq("assignment_id",submission.getInteger("assignment_id"))
                    )
            ).first();
            if(Assignment == null){
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No assignment matching the ID for this course").build());
            }
            int grade = -1;
            if(Assignment.getInteger("grade")!=null){
                grade = Assignment.getInteger("grade");
            }
            AllSubmissions.add(new Document()
                    .append("assignment_name",Assignment.getString("assignment_name"))
                    .append("grade", grade));
        }
        return new Document("submissions",AllSubmissions);
    }

    public void makeFinalGrades(String courseID, int assignmentID) {
        Document assignment = assignmentsCollection.find(and(eq("course_id", courseID), eq("assignment_id", assignmentID))).first();
        if (assignment == null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment not found.").build());
        List<String> allTeams = assignment.getList("all_teams", String.class);
        int points = assignment.getInteger("points");
        for (String team : allTeams) {
            Document team_submission = submissionCollection.find(and(
                    eq("course_id", courseID),
                    eq("assignment_id", assignmentID),
                    eq("team_name", team),
                    eq("type", "team_submission"))).first();

            if (team_submission == null) {
                Document blankSubmission = new Document()
                        .append("course_id", courseID)
                        .append("assignment_id", assignmentID)
                        .append("team_name", team)
                        .append("type", "team_submission")
                        .append("grade", 0);
                submissionCollection.insertOne(blankSubmission);
            } else {
                List<String> teams_that_graded = team_submission.getList("reviews", String.class);
                if (teams_that_graded == null)
                    throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assigned teams not found for: " + team + "for assignment: " + assignmentID).build());
                int total_points = 0;
                int count_of_reviews_submitted = teams_that_graded.size();
                for (String review : teams_that_graded) {
                    Document team_review = submissionCollection.find(and(
                            eq("course_id", courseID),
                            eq("assignment_id", assignmentID),
                            eq("reviewed_by", review),
                            eq("type", "peer_review_submission"))).first();
                    if (team_review == null) {
                        count_of_reviews_submitted--;
                    } else {
                        if (team_review.get("grade", Integer.class) == null) {
                            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("team: " + review + "'s review has no points.").build());
                        } else {
                            total_points += team_review.get("grade", Integer.class);
                        }
                    }
                }
                double final_grade = (((double)total_points / count_of_reviews_submitted) / points) * 100;
                final_grade = ((int)(final_grade * 100)/100.0); //round to the nearest 10th
                submissionCollection.findOneAndUpdate(team_submission, set("grade", final_grade));
                assignmentsCollection.findOneAndUpdate(and(eq("course_id", courseID), eq("assignment_id", assignmentID)), set("grade_finalized", true));
            }
        }
    }

    public List<Document> getToDosByCourse(String courseID, String studentID) {
        MongoCursor<Document> query = assignmentsCollection.find(eq("course_id", courseID)).iterator();

        if (!query.hasNext()) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This course does not exist").build());

        List<Document> assignments = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            Document ifNotSubmitted =submissionCollection.find(and(
                                                            eq("course_id", courseID),
                                                            eq("assignment_id", document.get("assignment_id")),
                                                            eq("type", "team_submission"),
                                                            eq("members", studentID))).first();
            if (ifNotSubmitted == null)
                assignments.add(document);
        }
        return assignments;
    }
}