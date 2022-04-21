package edu.oswego.cs.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class GradeInterface {


    static MongoCollection<Document>submissionsCollection;

    public GradeInterface(){
        DatabaseManager manage = new DatabaseManager();
        try{
            submissionsCollection = manage.getAssignmentDB().getCollection("submissions");
        }catch (WebApplicationException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to connect to the submissions collection").build());
        }
    }
    public Document getGrade(String course_id, int assignment_id,String student_id){
        Document result = submissionsCollection.find(
                and(
                        eq("course_id",course_id),
                        eq("assignmnt_id",assignment_id),
                        eq("members",student_id),
                        eq("type","team_submission")
                )
        ).first();
        if(result == null ) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("team submission does not exist").build());
        if(result.getInteger("grade") == null){
            return new Document("grade",-1);
        }else return new Document("grade", result.getInteger("grade"));
    }

}


/*
    public GradeInterface() {
        try {
            DatabaseManager manager = new DatabaseManager();
            gradesDatabase = manager.getAssignmentDB();
            gradesCollection = gradesDatabase.getCollection("grades");
        } catch (WebApplicationException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Failed to retrieve collections.").build());
        }
    }

    public List<Document> getAllGrades(String courseID, String studentID) {
        MongoCursor<Document> query = gradesCollection.find(and(
                eq("course_id", courseID),
                eq("student_id", studentID))).iterator();
        List<Document> grades = new ArrayList<>();
        while (query.hasNext()) {
            Document document = query.next();
            grades.add(document);
        }
        if (grades.isEmpty()) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Assignment does not exist.").build());
        return grades;
    }

    public Document getGrade(String courseID, int assignmentID, String studentID) {
        Document document = gradesCollection.find(and(
                eq("course_id", courseID),
                eq("assignment_id", assignmentID),
                eq("student_id", studentID))).first();
        if (document == null) throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("This assignment does not exist.").build());
        return document;
    }
}

 */