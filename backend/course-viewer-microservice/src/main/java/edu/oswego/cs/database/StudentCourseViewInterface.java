package edu.oswego.cs.database;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.management.openmbean.InvalidKeyException;
import java.io.IOException;
import java.util.ArrayList;

class Course{
    public String CourseID;
    public String Team;
    public ArrayList<String>Assignments = new ArrayList<>();
    public Course(Document d){
        CourseID = d.get("CourseID").toString();
        Team = d.get("Team").toString();
        if(!(d.get("Assignments") == null)){
            for(Object o:(ArrayList)d.get("Assignments")){
                Assignments.add(o.toString());
            }
        }

    }
}

public class StudentCourseViewInterface {
    public final String SID = "StudentID";
    public final String courses = "Courses";
    public final String team = "Team";
    public final String CID = "CourseID";
    public final String assignments = "Assignments";


    private String studentCollection = "NewStudent";

    MongoDatabase studentDatabase;
    private ArrayList<Course>courseList = new ArrayList<>();
    String StudentID;

    public StudentCourseViewInterface(String studentName) throws IOException {
        StudentID = studentName.split("@")[0];
        try{
            DatabaseManager manager = new DatabaseManager();
            studentDatabase = manager.getStudentDB();
            Document Student = studentDatabase.getCollection(studentCollection).find(new Document(SID,StudentID)).first();
            for(Object o: (ArrayList)Student.get(courses)){
                courseList.add(new Course((Document)o));
            }

        }catch(Exception e){
            e.printStackTrace(System.out);
            throw new IOException();
        }
    }
    public ArrayList<String> getCourses(){
        ArrayList<String>Out= new ArrayList<>();
        for(Course c:courseList){
            Out.add(c.CourseID);
        }
        return Out;
    }
    public Course viewCourse(String courseName){
        for(Course c:courseList){
            if(c.CourseID.equals(courseName))return c;
        }
        throw new InvalidKeyException();
    }
}
