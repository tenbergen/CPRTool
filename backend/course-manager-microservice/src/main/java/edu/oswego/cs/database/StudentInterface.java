package edu.oswego.cs.database;

import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.ArrayList;

public class StudentInterface {
    //Course Fields
    private ArrayList<String> courses;
    private String StudentID;

    //Collections
    private String studentCollection = "students";
    private String teamCollection = "teams";
    private String courseCollection = "courses";

    //Inject the database

    MongoDatabase database;


    public StudentInterface(String Student_ID){
        DatabaseManager databaseManager = new DatabaseManager();
        MongoDatabase database = databaseManager.getDB();
        StudentID = Student_ID;

        try{
            Document Selected_Student = database.getCollection(studentCollection).find(new Document("StudentID", Student_ID)).first();
            for(Object o:(ArrayList) Selected_Student.get("Courses")){
                this.courses.add(o.toString());
            }

        }catch(Exception e){
            e.printStackTrace(System.out);
        }
    }
    //Getters for the various fields within the course
    public String getStudentID(){
        return StudentID;
    }
    public ArrayList<String> getCourses(){
        return courses;
    }

}

