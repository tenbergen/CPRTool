package edu.oswego.cs.rest.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.rest.daos.AssignmentDAO;
import edu.oswego.cs.rest.daos.FileDAO;
import org.bson.Document;

import javax.print.Doc;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;


public class AssignmentInterface {

    static MongoDatabase assignmentDatabase;
    static MongoDatabase teamsDatabase;
    static MongoCollection<Document> assignmentsCollection;
    static MongoCollection<Document> teamsCollection;
    static MongoCollection<Document> submissionCollection;

    private final List<AssignmentDAO> assignments = new ArrayList<>();
    static String reg = "/";
    static int nextPos = 0;

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
        String path = "courses" + reg
                + fileDAO.getCourseID() + reg
                + fileDAO.getAssignmentID() + reg
                + "team-submissions";

        if (!new File(path).exists()) {
            new File(path).mkdirs();
        }
        fileDAO.writeFile(path + reg + fileDAO.getFilename());
        String team = fileDAO.getFilename().substring(0, fileDAO.getFilename().indexOf(".")) ;
        makeSubmission(fileDAO.getCourseID(), fileDAO.getAssignmentID(), fileDAO.getFilename(), team);
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

    public List<Document> getAllUserAssignments(String courseID, int assignmentID, String studentID){
        MongoCursor<Document> query = submissionCollection.find(and(eq("course_id",courseID),
                eq("assignment_id",assignmentID),
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

    public List<AssignmentDAO> getAssignmentsByCourse(String courseID) {
        for (Document document : assignmentsCollection.find()) {
            if (document.get("course_id").equals(courseID)) {
                AssignmentDAO assignmentDAO = new AssignmentDAO(
                        document.getString("assignment_name"),
                        document.getString("instructions"),
                        document.getString("due_date"),
                        document.getString("course_id"),
                        document.getInteger("points")
                );
                assignments.add(assignmentDAO);
            }
        }
        return assignments;
    }

    public List<AssignmentDAO> getAllAssignments() {
        for (Document document : assignmentsCollection.find()) {
            AssignmentDAO assignmentDAO = new AssignmentDAO(
                    document.getString("assignment_name"),
                    document.getString("course_id"),
                    document.getString("due_date"),
                    document.getString("instructions"),
                    document.getInteger("points")
            );
            assignments.add(assignmentDAO);
        }
        return assignments;
    }

    public Document getTeam(String courseID,String studentID){
        DatabaseManager manager = new DatabaseManager();
        MongoCollection<Document> teams = manager.getTeamDB().getCollection("teams");
        return teams.find(and(eq("course_id",courseID),eq("team_members",studentID))).first();
    }

    public void makeSubmission(String course_id,int assignment_id,String file_name,String teamName){
        Document team = teamsCollection.find(eq("team_id", teamName)).first();
        if(team == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("no team for this student").build());
        }
        if (team.getList("team_members", String.class) == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Members not defined in team").build());
        }
        if (team.get("team_id", String.class) == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("team_id not defined").build());
        }
        String path = "courses"+ reg+course_id+reg+assignment_id+reg+"team_submissions";
        Document new_submission = new Document()
                .append("course_id",course_id)
                .append("assignment_id",assignment_id)
                .append("submision_name",file_name)
                .append("team_name",team.getString("team_id"))
                .append("members",team.getList("team_members",String.class))
                .append("type","team_submission")
                .append("path",path+reg+file_name);
        System.out.println(new_submission);
        if(submissionCollection.find(new_submission).iterator().hasNext()){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("submission already exists").build());
        }else submissionCollection.insertOne(new_submission);
    }

    public static String findFile(String courseID, int assignmentID, String fileName) {
        return getRelPath() + "courses" + reg + courseID + reg + assignmentID + reg + "assignments" + reg + fileName;
    }

    public static String findAssignment(String courseID, int assID) {
        return getRelPath() + "courses" + reg + courseID + reg + assID + reg + "assignments";
    }
}