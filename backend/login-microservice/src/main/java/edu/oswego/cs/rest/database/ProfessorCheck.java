package edu.oswego.cs.rest.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.checkerframework.checker.units.qual.C;

import javax.enterprise.context.ApplicationScoped;
import java.io.*;
import java.util.ArrayList;

@ApplicationScoped
public class ProfessorCheck {
    String reg;
    DatabaseManager db;
    MongoCollection<Document> professors;
    MongoCollection<Document> students;

    public ProfessorCheck() throws IOException {
        db = new DatabaseManager();
        professors = db.getProfessorDB().getCollection("professors");
        students = db.getStudentDB().getCollection("students");
        String path = getPath();
        BufferedReader br = new BufferedReader(new FileReader(new File(path + "ProfessorList.txt")));
        String line = "";
        ArrayList<String>CurrentList = new ArrayList<>();
        while((line = br.readLine())!=null){
            System.out.println(line);
            if(line.contains("@"))CurrentList.add(line.split("@")[0]);
        }
        MongoCursor<Document>results = professors.find().iterator();
        ArrayList<String>OldList=new ArrayList<>();
        while(results.hasNext()){
            OldList.add(results.next().get("professor_id").toString());
        }
        System.out.println("Old List : " + OldList);
        System.out.println("Current List: " + CurrentList);

        //Promone any new professors
        for(String s: CurrentList){
            //Check If there are any student objects by that id if so update the courses of that professor
            if(students.find(Filters.eq("student_id",s)).iterator().hasNext()){
                Document newProfessor = new Document("professor_id",s);
                MongoCursor<Document>studentResults = students.find(Filters.eq("student_id",s)).iterator();
                ArrayList courses = new ArrayList<>();
                while(studentResults.hasNext()){
                    Document oldStudent = studentResults.next();
                    if(oldStudent.get("courses")!=null){
                        courses.addAll(oldStudent.get("courses",ArrayList.class));
                    }
                }
                //If they currently have a professor object add to course if there are any
                if(professors.find(Filters.eq("professor_id",s)).iterator().hasNext()) {
                    MongoCursor<Document>professorResults = professors.find(Filters.eq("professor_id",s)).iterator();
                    while(professorResults.hasNext()){
                        Document oldProfessor = professorResults.next();
                        if(oldProfessor.get("courses")!=null){
                            courses.addAll(oldProfessor.get("courses",ArrayList.class));
                        }
                    }
                }
                if(!courses.isEmpty()){
                    newProfessor.append("courses",courses);
                }
                professors.deleteMany(Filters.eq("professor_id",s));
                professors.insertOne(newProfessor);
                students.deleteMany(Filters.eq("student_id",s));
            }else{
                //If they have no student obj and they are not already a professor make an object
                if(!professors.find(Filters.eq("professor_id",s)).iterator().hasNext()){
                    professors.insertOne(new Document("professor_id",s));
                }
            }
        }
        //Demote any present in Old but not in new
        for(String s: OldList){
            if(!CurrentList.contains(s)){
                Document oldProf = professors.find(Filters.eq("professor_id",s)).first();
                Document newStudent = new Document("student_id",s);
                if(oldProf.get("courses")!=null){
                    ArrayList courses = oldProf.get("courses",ArrayList.class);
                    newStudent.append("courses",courses);
                }
                students.insertOne(newStudent);
                professors.deleteMany(Filters.eq("professor_id",s));
            }
        }
    }

    public String getPath(){
        String path = (System.getProperty("user.dir").contains("\\")) ? System.getProperty("user.dir").replace("\\", "/") : System.getProperty("user.dir");
        String[] slicedPath = path.split("/");
        System.out.println(slicedPath);
        String targetDir = "defaultServer";
        StringBuilder relativePathPrefix = new StringBuilder();

        for (int i = slicedPath.length - 1; !slicedPath[i].equals(targetDir); i--) {
            relativePathPrefix.append("../");
        }
        if(System.getProperty("user.dir").contains("\\")){
            System.out.println("Windows System");
            reg = "//";
            relativePathPrefix = new StringBuilder(relativePathPrefix.toString().replace("/","\\"));
        }else{
            System.out.println("Linux System");
        }
        return relativePathPrefix.toString();
    }
}
