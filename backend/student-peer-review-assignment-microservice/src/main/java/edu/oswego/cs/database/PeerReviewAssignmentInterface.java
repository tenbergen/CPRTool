package edu.oswego.cs.database;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.daos.FileDAO;
import org.bson.Document;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class PeerReviewAssignmentInterface {
    private final MongoCollection<Document> studentCollection;
    private final MongoCollection<Document> courseCollection;
    private final MongoCollection<Document> teamCollection;
    private final MongoCollection<Document> assignmentCollection;
    private final MongoCollection<Document> submissionsCollection;
    private final String reg = "/";
    MongoDatabase assignmentDB;
    private final String peer_review = "peer-reviews";
    private final String assignments = "assignments";
    private final String team_submissions = "team-submissions";
    private final String team_peer_reviews = "peer-review-submissions";
    private final String root_name = "courses";
    private  MongoDatabase teamDB;

    public PeerReviewAssignmentInterface() {
        DatabaseManager databaseManager = new DatabaseManager();
        try {
            MongoDatabase studentDB = databaseManager.getStudentDB();
            MongoDatabase courseDB = databaseManager.getCourseDB();
            teamDB = databaseManager.getTeamDB();
            assignmentDB = databaseManager.getAssignmentDB();
            studentCollection = studentDB.getCollection("students");
            courseCollection = courseDB.getCollection("courses");
            teamCollection = teamDB.getCollection("teams");
            assignmentCollection = assignmentDB.getCollection("assignments");
            submissionsCollection = assignmentDB.getCollection("submissions");
        } catch (WebApplicationException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to retrieve collections.").build());
        }
    }

    public void addPeerReviewSubmission(String course_id,int assignment_id,String srcTeamName, String fileName, int grade){
        Document team = teamCollection.find(eq("team_id", srcTeamName)).first();
        if(team == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("no team for this student").build());
        }
        if (team.getList("team_members", String.class) == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Members not defined in team").build());
        }
        if (team.get("team_id", String.class) == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("team_id not defined").build());
        }
        String path = "courses"+ reg+course_id+reg+assignment_id+reg+"peer-review-submissions";
        Document new_submission = new Document()
                .append("course_id",course_id)
                .append("assignment_id",assignment_id)
                .append("submision_name", fileName)
                .append("team_name",team.getString("team_id"))
                .append("members",team.getList("team_members",String.class))
                .append("type","peer_review_submission")
                .append("grade", grade)
                .append("path",path+reg+fileName);
        System.out.println(new_submission);
        if(submissionsCollection.find(new_submission).iterator().hasNext()){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("submission already exists").build());
        }else submissionsCollection.insertOne(new_submission);
    }

    public List<String> getCourseStudentIDs(String courseID) {
        Document courseDocument = courseCollection.find(eq("course_id", courseID)).first();
        return (List<String>) courseDocument.get("students");
    }


    public void uploadPeerReview(String courseID, int assignmentID, String srcTeamName, String destTeamName, IAttachment attachment) throws IOException {
        FileDAO fileDAO = FileDAO.fileFactory(courseID, srcTeamName, destTeamName, assignmentID, attachment);
        String path = "courses/"+courseID+"/"+assignmentID+"/peer-review-submissions/";
        if (! new File(path).exists()) {
            new File(path).mkdirs();
        }
        OutputStream outputStream = new FileOutputStream(path+fileDAO.fileName+".pdf");

        outputStream.write(fileDAO.inputStream.readAllBytes());
        outputStream.close();

    }

    public File downloadFinishedPeerReview(String courseID, int assignmentID, String srcTeamName, String destTeamName) {
        String path = "courses/"+courseID+"/"+assignmentID+"/peer-review-submissions/";
        if (!new File(path).exists())
            throw new WebApplicationException("Peer reviews do not exist for this course yet.");

        Optional<File> file = Arrays.stream(new File(path).listFiles())
                .filter( f -> f.getName().contains(srcTeamName) && f.getName().contains(destTeamName) )
                .findFirst();

        if (file.isEmpty()) throw new WebApplicationException("No peer review from team " + srcTeamName + " for " + destTeamName);
        return file.get();

    }

    public List<Document> getUsersGradedAssignments(String courseID, int assignmentID, String studentID){
        MongoCursor<Document> query = submissionsCollection.find(and(eq("course_id",courseID),
                                                                    eq("assignment_id",assignmentID),
                                                                    eq("members",studentID),
                                                                    eq("type","peer_review"))).iterator();
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

    public List<Document> getAssignmentsReviewedByUser(String courseID, int assignmentID, String studentID){
        MongoCursor<Document> query = submissionsCollection.find(and(eq("course_id",courseID),
                eq("assignment_id",assignmentID),
                eq("members",studentID),
                eq("type","peer_review_by_me"))).iterator();
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

    public List<String> getAssignedTeams(String courseID, int assignmentID, String teamName) {
        Document assignmentDocument = assignmentCollection.find(and(
                                                                eq("course_id", courseID),
                                                                eq("assignment_id", assignmentID)
        )).first();
        if (assignmentDocument == null) throw new WebApplicationException("Course/Assignment ID does not exist.");
        Document teamAssignmentDocument = (Document) assignmentDocument.get("assigned_teams");
        return (List<String>) teamAssignmentDocument.get(teamName);
    }


    public List<String> getCourseTeams(String courseID) {
        ArrayList<String> teamNames = new ArrayList<>();
        for (Document teamDocument : teamCollection.find(eq("course_id", courseID))) {
            String teamName = (String) teamDocument.get("team_id");
            teamNames.add(teamName);
        }
        return teamNames;
    }

    public Document addAssignedTeams(Map<String, List<String>> peerReviewAssignments, String courseID, int assignmentID) {

        for (Document assignmentDocument : assignmentCollection.find(eq("course_id", courseID))) {
            if ((int) assignmentDocument.get("assignment_id") == assignmentID) {

                Document doc = new Document();
                for (String team : peerReviewAssignments.keySet()) {
                    doc.put(team, peerReviewAssignments.get(team));
                }
                assignmentCollection.updateOne(assignmentDocument, set("assigned_teams", doc));
                return doc;
            }
        }
        throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to add assigned teams.").build());
    }

    public Document getAssignmentDocument(String courseID, int assignmentID) {
        for (Document assignmentDocument : assignmentCollection.find(eq("course_id", courseID))) {
            if ((int) assignmentDocument.get("assignment_id") == assignmentID) {
                return assignmentDocument;
            }
        }
        throw new WebApplicationException("No course/assignmentID found.");
    }
    public void addSubmission(String course_id, int assignment_id, String team_id, int grade, String file_name) {
        String path = root_name + reg + course_id + reg + assignment_id + team_peer_reviews + reg + file_name;
        //write method in the filedao to the path
        Document team = teamDB.getCollection("teams").find(and(eq("course_id", course_id), eq("team_id", team_id))).first();
        if (team == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Team not found in DB").build());
        }
        if (team.getList("team_members", String.class) == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Members not defined in team").build());
        }
        Document new_submission = new Document()
                .append("course_id", course_id)
                .append("assignment_id", assignment_id)
                .append("grade", grade)
                .append("submission_name", file_name)
                .append("members", team.getList("team_members", String.class))
                .append("path", path)
                .append("type","peer_review")
                .append("team_name",team.get("team_id"));
        if(assignmentDB.getCollection("submissions").find(new_submission).iterator().hasNext()){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("submission already exists").build());
        }else assignmentDB.getCollection("submissions").insertOne(new_submission);
    }

    public void makeGrades(String course_id, int assignment_id) {
        String path = root_name + reg + course_id + reg + assignment_id +reg+ team_peer_reviews + reg;
        Document assignment = assignmentCollection.find(and(eq("course_id", course_id), eq("assignment_id", assignment_id))).first();
        if (assignment == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No assignment found in DB").build());
        }
        if (assignment.get("points", Integer.class) == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No points defined in assignment").build());
        }
        int points = assignment.get("points", Integer.class);
        MongoCursor<Document> teams = teamDB.getCollection("teams").find(eq("course_id", course_id)).iterator();
        ArrayList<Document> grades_to_add = new ArrayList<>();
        while (teams.hasNext()) {
            Document team = teams.next();
            //generate all of the reviews for this team
            String temp = "to-"+team.getString("team_id");
            MongoCursor<Document> team_submissions = assignmentDB.getCollection("submissions")
                    .find(and(
                            eq("course_id", course_id),
                            eq("assignment_id", assignment_id),
                            eq("type", "peer_review"),
                            eq("submission_name", Pattern.compile(temp))
                    )).iterator();
            List<Document> reviews = new ArrayList<>();
            while(team_submissions.hasNext()) {
                Document submission = team_submissions.next();
                if (submission.get("submission_name") == null || submission.getInteger("grade") == null) {
                    throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Improperly formed submission").build());
                }
                Document review = new Document()
                        .append("submission_name", submission.get("submission_name"))
                        .append("grade", submission.getInteger("grade"));
                reviews.add(review);
            }
            List<String> members = team.getList("team_members", String.class);
            for (String member : members) {
                Document new_grade = new Document()
                        .append("course_id", course_id)
                        .append("assignment_id", assignment_id)
                        .append("student_id", member)
                        .append("answer_path", path)
                        .append("points", points)
                        .append("reviews", reviews);
                grades_to_add.add(new_grade);
            }
        }
        assignmentDB.getCollection("grades").insertMany(grades_to_add);
    }
}