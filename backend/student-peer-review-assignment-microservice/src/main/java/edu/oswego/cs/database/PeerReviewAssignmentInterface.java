package edu.oswego.cs.database;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.daos.FileDAO;
import org.bson.Document;

import javax.print.Doc;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

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

    public PeerReviewAssignmentInterface() {
        DatabaseManager databaseManager = new DatabaseManager();
        try {
            MongoDatabase studentDB = databaseManager.getStudentDB();
            MongoDatabase courseDB = databaseManager.getCourseDB();
            MongoDatabase teamDB = databaseManager.getTeamDB();
            MongoDatabase assignmentDB = databaseManager.getAssignmentDB();
            studentCollection = studentDB.getCollection("students");
            courseCollection = courseDB.getCollection("courses");
            teamCollection = teamDB.getCollection("teams");
            assignmentCollection = assignmentDB.getCollection("assignments");
            submissionsCollection = assignmentDB.getCollection("submissions");
        } catch (WebApplicationException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to retrieve collections.").build());
        }
    }

    public List<String> getCourseStudentIDs(String courseID) {
        Document courseDocument = courseCollection.find(eq("course_id", courseID)).first();
        return (List<String>) courseDocument.get("students");
    }

    public boolean makeFileStructure(Set<String> teams, String courseID, int assignmentID) {
        String path = System.getProperty("user.dir") + reg + courseID + reg + assignmentID;
        ArrayList<String> validNames = new ArrayList<>();
        validNames.add("peer-reviews");
        validNames.add("assignments");
        validNames.add("team-submissions");
        validNames.add("peer-review-submissions");
        File[] structure = new File(path).listFiles();
        for (File f : structure) {
            if (validNames.contains(f.getName())) {
                validNames.remove(f.getName());
            } else return false;
        }
        if (validNames.size() != 0) return false;
        if (Objects.requireNonNull(new File(path + reg + "peer-review-submissions").listFiles()).length != 0)
            return false;
        path += reg + "peer-review-submissions";
        for (String team : teams) {
            new File(path + reg + team).mkdir();
        }
        return true;
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
}