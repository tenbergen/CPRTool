package edu.oswego.cs.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.push;
import static com.mongodb.client.model.Updates.set;

public class PeerReviewAssignmentInterface {
    private final MongoCollection<Document> studentCollection;
    private final MongoCollection<Document> courseCollection;
    private final MongoCollection<Document> teamCollection;
    private final MongoCollection<Document> assignmentCollection;

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