package edu.oswego.cs.util;

import java.util.Collections;
import java.util.List;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bson.Document;
import org.bson.conversions.Bson;

public class CourseUtil {
    public void updateCoursesArrayInDbs(MongoCollection<Document> studentCollection , String originalCourseID, String newCourseID ) {
        MongoCursor<Document> cursor = studentCollection.find().iterator();

        while (cursor.hasNext()) {
            Document studentDocument = cursor.next();
            List<String> courses = studentDocument.getList("courses", String.class);
            Collections.replaceAll(courses, originalCourseID, newCourseID);
            Bson studentFilter = Filters.eq("student_id", studentDocument.getString("student_id"));
            studentCollection.updateOne(studentFilter, Updates.set("courses", courses));
        }
        cursor.close();
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
