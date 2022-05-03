package edu.oswego.cs.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;

import org.bson.Document;
import org.bson.conversions.Bson;

public class CourseUtil {
    public void updateCoursesArrayInProfessorDb(SecurityContext securityContext, MongoCollection<Document> collection , String originalCourseID, String newCourseID, String mode) {
        String professorID = securityContext.getUserPrincipal().getName().split("@")[0];
        Bson professorDocumentFilter = Filters.eq("professor_id", professorID);
        Document professorDocument = collection.find(professorDocumentFilter).first();
        
        List<String> professorDocumentCourses = professorDocument.getList("courses", String.class);
        if (professorDocumentCourses == null) throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Professor profile is not set up properly.").build());
        
        if (mode.equals("UPDATE")) Collections.replaceAll(professorDocumentCourses, originalCourseID, newCourseID);
        else if (mode.equals("DELETE")) professorDocumentCourses.remove(originalCourseID);

        collection.updateOne(professorDocumentFilter, Updates.set("courses", professorDocumentCourses));
        
        /* production conditions */
        // if (professorDocument.size() > 0) collection.updateOne(professorDocumentFilter, Updates.set("courses", professorDocumentCourses));
        // else collection.deleteOne(professorDocumentFilter);
    }

    public void updateCoursesArrayInStudenDb(MongoCollection<Document> collection , String originalCourseID, String newCourseID, String mode ) {
        MongoCursor<Document> cursor = collection.find().iterator();
        while (cursor.hasNext()) {
            Document studentDocument = cursor.next();
            List<String> studentDocumentCourses = studentDocument.getList("courses", String.class);
            Bson studentFilter = Filters.eq("student_id", studentDocument.getString("student_id"));

            if (mode.equals("UPDATE")) Collections.replaceAll(studentDocumentCourses, originalCourseID, newCourseID);
            else if (mode.equals("DELETE")) studentDocumentCourses.remove(originalCourseID);
            
            if (studentDocument.size() > 0) collection.updateOne(studentFilter, Updates.set("courses", studentDocumentCourses));
            else collection.deleteOne(studentFilter);
        }
        cursor.close();
    }

    public void updateCoursesKeyInDBs(MongoCollection<Document> collection, String originalCourseID, String newCourseID, String mode) {
        Bson documentFilter = Filters.eq("course_id", originalCourseID);
        if (mode.equals("UPDATE")) {
            collection.updateMany(documentFilter, Updates.set("course_id", newCourseID));
        } else if (mode.equals("DELETE")) {
            collection.deleteMany(documentFilter);
        }
    }

    public void collectionWipeOff(MongoCollection<Document> collection) {
        MongoCursor<Document> cursor = collection.find().iterator();
        while (cursor.hasNext()) {
            collection.deleteOne(cursor.next());
        }
        cursor.close();
    }

    public void updateTeamSize(MongoCollection<Document> collection, String courseID, int originalTeamSize, int newTeamSize) {
        Bson teamFilter = Filters.eq("course_id", courseID);
        MongoCursor<Document>  cursor = collection.find(teamFilter).iterator();
        
        List<Integer> teamMembersCount = collection.aggregate(
            Arrays.asList(
                Aggregates.match(Filters.eq("course_id", courseID)),
                Aggregates.project(Projections.computed(
                    "membersCount",
                    Projections.computed("$size", "$team_members"))
                    )
            ))
            .map(follower -> follower.getInteger("membersCount"))
            .into(new ArrayList<>());
        
        if (newTeamSize < Collections.max(teamMembersCount)) 
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Team size conflict with the current number of team members in team").build());
            
        while(cursor.hasNext()) {
            Document teamDocument = cursor.next();
            List<String> teamMembers = teamDocument.getList("team_members", String.class);
            if (newTeamSize == teamMembers.size()) collection.updateOne(teamDocument, Updates.set("team_full", true));
            else collection.updateOne(teamDocument, Updates.set("team_full", false));
        }
        collection.updateMany(teamFilter, Updates.set("team_size", newTeamSize));
        cursor.close();
    }
}
