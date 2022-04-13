package edu.oswego.cs.database;

import com.mongodb.client.FindIterable;
import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import edu.oswego.cs.daos.FileDAO;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.push;
import static com.mongodb.client.model.Updates.set;

public class PeerReviewAssignmentInterface {
    private final MongoCollection<Document> studentCollection;
    private final MongoCollection<Document> courseCollection;
    private final MongoCollection<Document> teamCollection;
    private final MongoCollection<Document> assignmentCollection;
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
        } catch (WebApplicationException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to retrieve collections.").build());
        }
    }

    public List<String> getCourseTeams(String courseID) {
        ArrayList<String> teamNames = new ArrayList<>();
        for (Document teamDocument : teamCollection.find(eq("course_id", courseID))) {
            String teamName = (String) teamDocument.get("team_id");
            teamNames.add(teamName);
        }
        return teamNames;
    }

    public List<String> getCourseStudentIDs(String courseID) {
        Document courseDocument = courseCollection.find(eq("course_id", courseID)).first();
        return (List<String>) courseDocument.get("students");
    }

    public Document addAssignedTeams(Map<String, List<String>> peerReviewAssignments, String courseID, int assignmentID) {

        for (Document assignmentDocument : assignmentCollection.find(eq("course_id", courseID))) {
            if ((int) assignmentDocument.get("assignment_id") == assignmentID) {

                Document doc = new Document();
                for (String team : peerReviewAssignments.keySet()) {
                    doc.put(team, peerReviewAssignments.get(team));
                }
                makeFileStructure(peerReviewAssignments.keySet(),courseID,assignmentID);
                assignmentCollection.updateOne(assignmentDocument, set("assigned_teams", doc));
                return doc;
            }
        }
        throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to add assigned teams.").build());
    }
    public boolean makeFileStructure(Set<String> teams,String courseID,int assignmentID){
        String path = System.getProperty("user.dir")+ reg + courseID + reg + assignmentID;
        ArrayList<String>validNames = new ArrayList<>();
        validNames.add("peer-reviews");
        validNames.add("assignments");
        validNames.add("team-submissions");
        validNames.add("peer-review-submissions");
        File[] structure = new File(path).listFiles();
        for(File f : structure){
            if(validNames.contains(f.getName())){
                validNames.remove(f.getName());
            }else return false;
        }
        if(validNames.size()!=0)return false;
        if(Objects.requireNonNull(new File(path + reg + "peer-review-submissions").listFiles()).length!=0)return false;
        path += reg + "peer-review-submissions";
        for(String team: teams){
          new File(path+reg+team).mkdir();
        }
        return true;
    }

    public void uploadPeerReview(String courseID, int assignmentID, String srcTeamName, String destTeamName, IAttachment attachment) throws IOException {
        String basePath = FileDAO.peer_review_submission_path+courseID+"/"+assignmentID+"/";
        if (! new File(basePath).exists()) {
            new File(basePath).mkdirs();
        }

        FileDAO fileDAO = FileDAO.fileFactory(courseID, srcTeamName, destTeamName, assignmentID, attachment);

        OutputStream outputStream = new FileOutputStream(basePath+fileDAO.fileName+".pdf");
        outputStream.write(fileDAO.inputStream.readAllBytes());
        outputStream.close();

    }
}