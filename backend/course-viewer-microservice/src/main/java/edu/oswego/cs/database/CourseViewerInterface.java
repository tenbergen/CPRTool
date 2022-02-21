package edu.oswego.cs.database;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import java.util.ArrayList;

public class CourseViewerInterface {
    //Inject for hte database
    MongoDatabase database;

    //Collection names
    private String prof = "professors";
    private String stud = "students";
    private String course = "courses";

    //Courses stored inside to interface
    private ArrayList<String> courses;

    CourseInterface selectedCourse;

    public CourseViewerInterface(String Id){
        DatabaseManager databaseManager = new DatabaseManager();
        database = databaseManager.getDB();
        courses = new ArrayList<>();
        try{
            ArrayList temp_courses;
            MongoCursor<Document> info = database.getCollection(prof).find(new Document("ProfessorID", Id)).iterator();
            //the ID belongs to the professor
            if(info.hasNext()){
                temp_courses = (ArrayList) info.next().get("Courses");
            }else{
                temp_courses = (ArrayList)database.getCollection(stud).find(new Document("StudentID", Id)).first().get("Courses");
            }
            for(Object o : temp_courses){
                courses.add(o.toString());
            }
        }catch(Exception e){
            e.printStackTrace(System.out);
        }
    }
    public ArrayList<String> getCourses(){
        return courses;
    }

    public CourseInterface getCourse(String CID){
            selectedCourse = new CourseInterface(CID);
            return selectedCourse;
    }




}
