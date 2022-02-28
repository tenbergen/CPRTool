package edu.oswego.cs.database;

import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.daos.CourseDAO;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;

public class CourseViewerInterfaceV2 {
    private String Courses = "Courses";
    private String SID = "StudentID";
    private String CID = "courseID";
    private String assignments = "Assignments";
    private String team = "Team";
    private String teams = "Teams";
    private String synced = "Synced";
    private String students = "Students";
    private String tas = "Tas";

    private String studentCollection = "Student";
    private String professorCollection = "Professor";

    MongoDatabase userDatabase;
    private ArrayList<CourseDAO>courseList = new ArrayList<>();
    String ID;

    public CourseViewerInterfaceV2(String Email) throws IOException {
        ID =Email.split("@")[0];
        try{
            DatabaseManager manager = new DatabaseManager();
            userDatabase = manager.getStudentDB();
            Document user;
            if(ID.contains(".")){
               user = userDatabase.getCollection(professorCollection).find().first();
            }else  user = userDatabase.getCollection(studentCollection).find(new Document(SID,ID)).first();
            for(Document d: (ArrayList<Document>)user.get(Courses)){
                CourseDAO dao = new CourseDAO(
                        d.get("CourseName").toString(),
                        Integer.valueOf(d.get("CourseSection").toString()),
                        d.get("Semester").toString(),
                        d.get("Abbreviation").toString(),
                        (ArrayList<String>)d.get("Students"),
                        (ArrayList)d.get("Tas"),
                        (ArrayList)d.get("Teams"),
                        d.get("courseID").toString());
                courseList.add(dao);
            }

        }catch(Exception e){
            e.printStackTrace(System.out);
            throw new IOException();
        }
    }
    public ArrayList<CourseDAO> getCourses(){
        return courseList;
    }
}
