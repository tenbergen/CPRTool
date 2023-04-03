package edu.oswego.cs.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class CourseInterface {
    private final MongoCollection<Document> studentCollection;
    private final MongoCollection<Document> courseCollection;
    private final MongoCollection<Document> professorCollection;

    private final MongoCollection<Document> teamCollection;

    public CourseInterface() {
        DatabaseManager databaseManager = new DatabaseManager();
        try {
            MongoDatabase studentDB = databaseManager.getStudentDB();
            MongoDatabase courseDB = databaseManager.getCourseDB();
            MongoDatabase professorDB = databaseManager.getProfessorDB();
            MongoDatabase teamDB = databaseManager.getTeamDB();
            studentCollection = studentDB.getCollection("students");
            courseCollection = courseDB.getCollection("courses");
            professorCollection = professorDB.getCollection("professors");
            teamCollection = teamDB.getCollection("teams");
        } catch (WebApplicationException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to retrieve collections.").build());
        }
    }

    public Document getCourse(SecurityContext securityContext, String courseID) {
        String professorID = securityContext.getUserPrincipal().getName();
        Document document = courseCollection.find(and(eq("course_id", courseID), eq("professor_id", professorID))).first();
        if (document == null)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("This course does not exist.").build());
        return document;
    }

    public Document getCourse(String courseID){
        Document document = courseCollection.find(eq("course_id", courseID)).first();
        if(document == null)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("This course does not exist.").build());
        return document;
    }


    public Document getStudent(String studentID) {
        Document document = studentCollection.find(eq("student_id", studentID)).first();
        if (document == null)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("This student does not exist.").build());
        return document;
    }

    public Document getStudent(SecurityContext securityContext){
        String studentID = securityContext.getUserPrincipal().getName();
        return studentCollection.find(eq("student_id", studentID)).first();
    }

    public Document getProfessor(SecurityContext securityContext) {
        String professorID = securityContext.getUserPrincipal().getName();
        return professorCollection.find(eq("professor_id", professorID)).first();
    }

    public List<Document> getStudentsInCourse(SecurityContext securityContext, String courseID) {
        String professorID = securityContext.getUserPrincipal().getName();
        Document courseDocument = courseCollection.find(and(eq("course_id", courseID), eq("professor_id", professorID))).first();
        if (courseDocument == null) throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build());
        List<String> studentIDs = courseDocument.getList("students", String.class);
        return studentIDs.stream()
                .map(id -> studentCollection.find(eq("student_id", id)).first())
                .collect(Collectors.toList());
    }

    public List<Document> getTeamsInCourse(String courseID) {
        List<Document> teamsInCourse = new ArrayList<>();
        teamCollection.find(eq("course_id", courseID)).into(teamsInCourse);
        return teamsInCourse;
    }
}