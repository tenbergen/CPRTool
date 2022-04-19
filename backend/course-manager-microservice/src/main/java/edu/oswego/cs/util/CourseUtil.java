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
    public void updateCoursesArrayInStudenDb(MongoCollection<Document> collection , String originalCourseID, String newCourseID ) {
        
        MongoCursor<Document> cursor = collection.find().iterator();
        while (cursor.hasNext()) {
            Document studentDocument = cursor.next();
            List<String> courses = studentDocument.getList("courses", String.class);
            Collections.replaceAll(courses, originalCourseID, newCourseID);
            Bson studentFilter = Filters.eq("student_id", studentDocument.getString("student_id"));
            collection.updateOne(studentFilter, Updates.set("courses", courses));
        }
        cursor.close();
    }

    public void updateCoursesArrayInProfessorDb(SecurityContext securityContext, MongoCollection<Document> collection , String originalCourseID, String newCourseID ) {
        String professorID = securityContext.getUserPrincipal().getName().split("@")[0];
        Bson professorDocumentFilter = Filters.eq("professor_id", professorID);
        Document professorDocument = collection.find(professorDocumentFilter).first();
        List<String> professorDocumentCourses = professorDocument.getList("courses", String.class);
        if (professorDocumentCourses == null)
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).entity("Professor profile is not set up properly.").build());
        Collections.replaceAll(professorDocumentCourses, originalCourseID, newCourseID);
        collection.updateOne(professorDocumentFilter, Updates.set("courses", professorDocumentCourses));
    }


    public void updateCoursesKeyInDBs(MongoCollection<Document> collection, String originalCourseID, String newCourseID) {
        Bson documentFilter = Filters.eq("course_id", originalCourseID);
        collection.updateMany(documentFilter, Updates.set("course_id", newCourseID));
    }

    public void collectionWipeOff(MongoCollection<Document> collection) {
        MongoCursor<Document> cursor = collection.find().iterator();
        while (cursor.hasNext()) {
            collection.deleteOne(cursor.next());
        }
        cursor.close();
    }
}
