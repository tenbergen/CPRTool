package edu.oswego.cs.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import edu.oswego.cs.daos.PeerReviewFileDAO;
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
import java.util.*;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.push;
import static com.mongodb.client.model.Updates.set;

public class PeerReviewAssignmentInterface {
    private final MongoCollection<Document> studentCollection;
    private final MongoCollection<Document> courseCollection;
    private final MongoCollection<Document> teamCollection;
    private final MongoCollection<Document> assignmentCollection;

    private MongoDatabase assignmentDB;
    private final String reg = "\\";
    private final String peer_review = "peer-reviews";
    private final String assignments = "assignments";
    private final String team_submissions = "team-submissions";
    private final String team_peer_reviews = "peer-review-submissions";
    private final String root_name = "courses";

    public PeerReviewAssignmentInterface() {
        DatabaseManager databaseManager = new DatabaseManager();
        try {
            MongoDatabase studentDB = databaseManager.getStudentDB();
            MongoDatabase courseDB = databaseManager.getCourseDB();
            MongoDatabase teamDB = databaseManager.getTeamDB();
            assignmentDB = databaseManager.getAssignmentDB();
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
        String path = System.getProperty("user.dir")+ reg +root_name+reg+ courseID + reg + assignmentID;
        ArrayList<String>validNames = new ArrayList<>();
        validNames.add(peer_review);
        validNames.add(assignments);
        validNames.add(team_submissions);
        validNames.add(team_peer_reviews);
        File[] structure = new File(path).listFiles();
        for(File f : structure){
            if(validNames.contains(f.getName())){
                validNames.remove(f.getName());
            }else return false;
        }
        if(validNames.size()!=0)return false;
        if(Objects.requireNonNull(new File(path + reg + team_peer_reviews).listFiles()).length!=0)return false;
        path += reg +team_peer_reviews;
        for(String team: teams){
          new File(path+reg+team).mkdir();
        }
        return true;
    }
    public void addSubmission(PeerReviewFileDAO fileDAO,String course_id,int assignment_id,String team_id,int grade){
        String path = System.getProperty("user.dir")+reg+root_name+reg+course_id+reg+assignment_id+team_peer_reviews+team_id;
        //write method in the filedao to the path
        assignmentDB.getCollection("submissions").insertOne(
                new Document()
                        .append("course_id",course_id)
                        .append("assignment_id",assignment_id)
                        .append("grade",grade)
                        .append("file_name","*****NAME_FROM_DAO******")
        );
    }
    public void makeGrades(String course_id,int assignment_id){
        String path = System.getProperty("user.dir")+reg+root_name+reg+course_id+reg+assignment_id+team_peer_reviews;
        File[] allSubmissions = new File(path).listFiles();
        if(allSubmissions==null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("No Assignment found").build());
        MongoCollection<Document>submissions = assignmentDB.getCollection("submissions");
        ArrayList<Document>grades_to_be_added = new ArrayList<>();
        for(File f:allSubmissions){
            String current_team_name = f.getName();
            Document team = teamCollection.find(and(eq("team_id",current_team_name),eq("course_id",course_id))).first();
            if(team==null)throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Team no longer exits").build());
            File[] review_files = new File(path+reg+current_team_name).listFiles();
            List<Document>reviews = new ArrayList<>();
            for(File review:review_files){
                Document submission = submissions.find(
                        and(
                                eq("course_id",course_id),
                                eq("assignment_id",assignment_id),
                                eq("file_name",review.getName())))
                        .first();
                if(submission == null)throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment in file Structure but not in DB").build());
                if(submission.get("grade",Integer.class)==null)throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("no grade present in db").build());
                reviews.add(new Document("submission_name",review.getName()).append("grade",submission.getInteger("grade")));
            }
            List<String> team_members = team.get("team_members",List.class );
            for(String member:team_members){
                grades_to_be_added.add(new Document()
                        .append("course_id",course_id)
                        .append("student_id",member)
                        .append("assignment_id",assignment_id)
                        .append("answer_path",path)
                        .append("reviews",reviews));
            }
        }
        assignmentDB.getCollection("grades").insertMany(grades_to_be_added);
    }

    /**
     * Update the course DAO's courseID, then add the course if it is not already existed in the database. At the same
     * time, update the students' course list in the student database if a student list in the request is specified.
     */
//    public void addCourse(CourseDAO dao) {
//        Jsonb jsonb = JsonbBuilder.create();
//        Entity<String> courseDAOEntity = Entity.entity(jsonb.toJson(dao), MediaType.APPLICATION_JSON_TYPE);
//        Document course = Document.parse(courseDAOEntity.getEntity());
//
//        MongoCursor<Document> courseQuery = courseCollection.find(eq("course_id", dao.courseID)).iterator();
//        if (courseQuery.hasNext()) throw new WebApplicationException(Response.status(Response.Status.OK).entity("Course already existed.").build());
//        courseCollection.insertOne(course);
//
//        List<String> students = course.getList("students", String.class);
//        for (String student : students) {
//            MongoCursor<Document> studentQuery = studentCollection.find(eq("student_id", student)).iterator();
//            if (!studentQuery.hasNext()) studentCollection.updateOne(eq("student_id", student), push("courses", dao.courseID));
//        }
//    }
//
//
//    /**
//     * Remove the course from the student's list of courses, and then remove the course itself from the course database.
//     */
//    public void removeCourse(CourseDAO dao) {
//        MongoCursor<Document> courseQuery = courseCollection.find(eq("course_id", dao.courseID)).iterator();
//        if (!courseQuery.hasNext()) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This course does not exist.").build());
//
//        Document courseDocument = courseQuery.next();
//        List<String> students = courseDocument.getList("students", String.class);
//        for (String student : students) {
//            MongoCursor<Document> studentQuery = studentCollection.find(eq("student_id", student)).iterator();
//            if (studentQuery.hasNext()) {
//                Document studentDocument = studentQuery.next();
//                List<String> courses = studentDocument.getList("courses", String.class);
//                courses.remove(dao.courseID);
//                studentCollection.updateOne(eq("student_id", student), set("courses", courses));
//            }
//        }
//        courseCollection.findOneAndDelete(eq("course_id", dao.courseID));
//    }
}