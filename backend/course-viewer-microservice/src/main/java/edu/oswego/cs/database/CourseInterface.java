package edu.oswego.cs.database;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;

public class CourseInterface {
    //Course Fields
    private ArrayList<String> students;
    private ArrayList<String> teams;
    private ArrayList<String>tas;
    private String CourseName;
    private String CID;
    private String CourseSection;

    //Collections
    private String studentCollection = "students";
    private String teamCollection = "teams";
    private String courseCollection = "courses";

    //Inject the database

    MongoDatabase database;

    //Generate a course from the Course ID(Stored in the sudents courses)
    public CourseInterface(String Course_ID){
        DatabaseManager databaseManager = new DatabaseManager();
        database = databaseManager.getDB();

        CID = Course_ID;
        students = new ArrayList<String>();
        teams = new ArrayList<String>();
        tas = new ArrayList<String>();
        try{
            Document Selected_Course = database.getCollection(courseCollection).find(new Document("CourseID", CID)).first();
            for(Object o:(ArrayList) Selected_Course.get("Students")){
                students.add(o.toString());
            }
            for(Object o:(ArrayList)Selected_Course.get("Teams")){
                teams.add(o.toString());
            }
            for(Object o:(ArrayList)Selected_Course.get("TAs")){
                tas.add(o.toString());
            }
            CourseSection = Selected_Course.get("CourseSection").toString();
            CourseName = Selected_Course.get("CourseName").toString();
        }catch(Exception e){
            e.printStackTrace(System.out);
        }
    }
    //Getters for the various fields within the course
    public ArrayList<String> getTeams(){
        return teams;
    }
    public ArrayList<String> getStudnts(){
        return students;
    }
    public ArrayList<String> getTAs(){
        return tas;
    }
    public String getCourseSection(){return CourseSection;}
    public String getCourseName(){
        return getCourseName();
    }
}
