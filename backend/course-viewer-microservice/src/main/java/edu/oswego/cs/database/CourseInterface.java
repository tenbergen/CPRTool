package edu.oswego.cs.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;

public class CourseInterface {
    private final String professor;
    private final ArrayList<Document> courseDocuments = new ArrayList<>();

    public CourseInterface(String professor) {
        this.professor = professor;
    }

    public ArrayList<Document> getCourses() {
        DatabaseManager databaseManager = new DatabaseManager();
        MongoDatabase database = databaseManager.getDB();

        String professors = "professors";
        boolean professorCollectionExists = database.listCollectionNames().into(new ArrayList<>()).contains(professors);
        if (!professorCollectionExists) {
            database.createCollection(professors);
        }

        String students = "students";
        boolean studentsCollectionExists = database.listCollectionNames().into(new ArrayList<>()).contains(students);
        if (!studentsCollectionExists) {
            database.createCollection(students);
        }

        String courses = "courses";
        boolean coursesCollectionExists = database.listCollectionNames().into(new ArrayList<>()).contains(courses);
        if (!coursesCollectionExists) {
            database.createCollection(courses);
        }

        Document professorDocument = database.getCollection(professors).find(new Document("professor_id", professor)).first();
        MongoCollection<Document> courseCollection = database.getCollection(courses);
        for (Object o : (ArrayList) professorDocument.get("courses")) {
            Document courseDocument = courseCollection.find(new Document("course_id", o.toString())).first();
            courseDocuments.add(courseDocument);
        }

        return courseDocuments;
    }
}
