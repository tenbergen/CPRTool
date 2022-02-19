package edu.oswego.cs.interfaces;

import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.database.database;
import jakarta.inject.Inject;
import org.bson.Document;

import java.util.ArrayList;

public class CourseInterface {
    //Course Fields
    private ArrayList<String> students;
    private ArrayList<String> teams;
    private ArrayList<String>tas;
    private String professor;
    private String Course_Name;
    private String CID;

    //Collections
    private String studentCollection = "students";
    private String teamCollection = "teams";
    private String courseCollection = "courses";

    //Inject the database

    MongoDatabase database;

    //Generate a course from the Course ID(Stored in the sudents courses)
    public CourseInterface(String Course_ID){
        edu.oswego.cs.database.database x = new database();
        MongoDatabase database = x.getDB(x.createMongoClient());
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
            professor = Selected_Course.get("Professor").toString();
            Course_Name = Selected_Course.get("Course").toString();
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
    public String getProfessor(){
        return professor;
    }
    public String getCourse_Name(){
        return getCourse_Name();
    }
}
