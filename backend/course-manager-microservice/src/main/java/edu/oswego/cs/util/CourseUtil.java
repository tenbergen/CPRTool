package edu.oswego.cs.util;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bson.Document;
import org.bson.conversions.Bson;

public class CourseUtil {
    public void updateCoursesArrayInProfessorDb(SecurityContext securityContext, MongoCollection<Document> collection , String originalCourseID, String newCourseID, String mode) {
        String professorID = securityContext.getUserPrincipal().getName().split("@")[0];
        Bson professorDocumentFilter = Filters.eq("professor_id", professorID);
        Document professorDocument = collection.find(professorDocumentFilter).first();
        
        List<String> professorDocumentCourses = professorDocument.getList("courses", String.class);
        if (professorDocumentCourses == null)
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Professor profile is not set up properly.").build());
        
        if (mode.equals("UPDATE")) 
            Collections.replaceAll(professorDocumentCourses, originalCourseID, newCourseID);
        else if (mode.equals("DELETE")) 
            professorDocumentCourses.remove(originalCourseID);
        
        collection.updateOne(professorDocumentFilter, Updates.set("courses", professorDocumentCourses));
    }

    public void updateCoursesArrayInStudenDb(MongoCollection<Document> collection , String originalCourseID, String newCourseID, String mode ) {
        MongoCursor<Document> cursor = collection.find().iterator();
        while (cursor.hasNext()) {
            Document studentDocument = cursor.next();
            List<String> studentDocumentCourses = studentDocument.getList("courses", String.class);
            Bson studentFilter = Filters.eq("student_id", studentDocument.getString("student_id"));
            if (mode.equals("UPDATE")) { 
                Collections.replaceAll(studentDocumentCourses, originalCourseID, newCourseID);
            } else if (mode.equals("DELETE")) {
                studentDocumentCourses.remove(originalCourseID);
            }
            collection.updateOne(studentFilter, Updates.set("courses", studentDocumentCourses));
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
}
