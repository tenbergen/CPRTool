package edu.oswego.cs.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import edu.oswego.cs.daos.CourseDAO;
import org.bson.Document;


import javax.print.Doc;
import java.io.*;
import java.sql.Array;
import java.util.ArrayList;

public class CourseManagerInterface {

    //Collection names
    private String prof = "professors";
    private String stud = "students";
    private String course = "courses";

    private ArrayList<String> courses;
    private String professorID;
    private Document professorDoc;
    private boolean result;
    MongoDatabase database;

    public CourseManagerInterface() {
        DatabaseManager databaseManager = new DatabaseManager();
        database = databaseManager.getDB();
        courses = new ArrayList<String>();
        professorDoc = database.getCollection(prof).find().first();
        try{
           for(Object o:(ArrayList)professorDoc.get("Courses")){
             courses.add(o.toString());
           }
           professorID = professorDoc.get("ProfessorID").toString();
        }catch(Exception e){
            e.printStackTrace(System.out);
        }
    }
    public ArrayList<String> getCourses(){return courses;}
    public CourseInterface getCourse(String CID) {
        return new CourseInterface(CID);
    }

    public void addCourse(CourseDAO courseDAO){
        Document courseToAdd = new Document()
                .append("CourseName",courseDAO.courseName)
                .append("CourseSection",courseDAO.courseSection)
                .append("CourseID",MakeCID(courseDAO))
                .append("Students",new ArrayList<>())
                .append("Teams",new ArrayList<>())
                .append("Tas",new ArrayList<>());
        try{
            database.getCollection(course).insertOne(courseToAdd);
            courses.add(MakeCID(courseDAO));
            professorDoc.put("Courses",courses);
            database.getCollection(prof).findOneAndDelete(new Document());
            database.getCollection(prof).insertOne(professorDoc);
        }catch(Exception e){
            e.printStackTrace(System.out);
        }
    }
    public boolean addStudent(String StudentName,CourseDAO courseDAO) throws IOException {
        String StudentID = StudentName.split("@")[0];
        try{
            MongoCursor<Document> tempStudent = database.getCollection(stud).find(new Document("StudentId", StudentID)).iterator();
            if(tempStudent.hasNext()){
                Document cursor = tempStudent.next();
                ArrayList StudentCourses = (ArrayList)cursor.get("courses");
                StudentCourses.add(MakeCID(courseDAO));
                database.getCollection(stud).updateOne(cursor,(Document)cursor.put("courses",StudentCourses));
            }else{
                ArrayList<String> newCourseList = new ArrayList<>();
                newCourseList.add(MakeCID(courseDAO));
                Document StudentDoc = new Document()
                        .append("StudentId",StudentID)
                        .append("courses",newCourseList);
                database.getCollection(stud).insertOne(StudentDoc);
            }
            Document tempCourse = database.getCollection(course).find(new Document("CourseID",MakeCID(courseDAO))).first();
            ArrayList tempList =(ArrayList) tempCourse.get("Students");
            tempList.add(StudentID);
            tempCourse.put("Students",tempList);
            database.getCollection(course).deleteOne(new Document("CourseID",MakeCID(courseDAO)));
            tempCourse.put("Students",tempList);
            database.getCollection(course).insertOne(tempCourse);

        }catch(Exception e){
            e.printStackTrace(System.out);
            throw new IOException();
        }
        return true;
    }
    private String MakeCID(CourseDAO courseDAO){
        return courseDAO.courseName.replace(" ","-")+"-"+courseDAO.courseSection;
    }

    public boolean removeCourse(CourseDAO courseDAO) throws IOException {
        try {
            String CID = MakeCID(courseDAO);
            courses.remove(CID);
            database.getCollection(prof).findOneAndDelete(professorDoc);
            professorDoc.put("Courses",courses);
            database.getCollection(prof).insertOne(professorDoc);

            database.getCollection(course).findOneAndDelete(new Document("CourseID",CID));

            FindIterable<Document> allStudents = database.getCollection(stud).find();
            for(Document d : allStudents){
                ArrayList tempCourses = (ArrayList) d.get("courses");
                if(tempCourses.contains(CID)){
                    tempCourses.remove(CID);
                    Document tempRef = d;
                    d.put("courses",tempCourses);
                    database.getCollection(stud).findOneAndUpdate(tempRef,d);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw new IOException();
        }
        return true;
    }

    public StudentInterface getStudent(String StudID) {
        return new StudentInterface(StudID);
    }


    public boolean removeStudent(CourseDAO courseDAO,String StudentName) throws IOException {
        String StudentID = StudentName.split("@")[0];
        try{
            Document tempStudent = database.getCollection(stud).find(new Document("StudentId", StudentID)).first();
            ArrayList tempList = (ArrayList)tempStudent.get("courses");
            if(tempList.contains(MakeCID(courseDAO))){
                tempList.remove(MakeCID(courseDAO));
            }else return false;
            database.getCollection(stud).deleteOne((new Document("StudentId", StudentID)));
            tempStudent.put("courses",tempList);
            database.getCollection(stud).insertOne(tempStudent);
        }catch(Exception e){
            e.printStackTrace(System.out);
            throw new IOException();
        }
        return true;
    }

    public boolean updateTaRole(String CID, String StudID) {
        DatabaseManager databaseManager = new DatabaseManager();
        MongoDatabase database = databaseManager.getDB();
        try {
            Document originalCourse = database.getCollection(course).find(new Document("CourseID", CID)).first();
            Document updatedCourse = originalCourse;

            ArrayList<String> currentTAs = new ArrayList<>();

            for (Object o : (ArrayList<Object>) updatedCourse.get("TAs")) {
                currentTAs.add(o.toString());
            }
            if (currentTAs.contains(StudID)) {
                currentTAs.remove(StudID);
                updatedCourse.put("TAs", currentTAs);
                database.getCollection(course).updateOne(originalCourse, updatedCourse);
                result = true;
            } else {
                currentTAs.add(StudID);
                updatedCourse.put("TAs", currentTAs);
                database.getCollection(course).updateOne(originalCourse, updatedCourse);
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            result = false;
        }
        return result;
    }
    public boolean csv(FileReader csv) throws IOException {
        ArrayList<String>students= new ArrayList<>();
        String line = "";
        String CourseID = "";
        BufferedReader br = new BufferedReader(csv);
        while((line = br.readLine())!=null){
            String[] student = line.split(",");
            students.add(student[7].split("@")[0]);
            CourseID = student[6];
        }
        Document NewCourse = new Document()
                .append("CourseID",CourseID)
                .append("Course",CourseID.substring(0,5))
                .append("Professor",professorID)
                .append("Students",students)
                .append("Teams",new ArrayList<String>())
                .append("TAs",new ArrayList<String>());
        try{
            if(database.getCollection(course).find(new Document("CourseID",CourseID)).iterator().hasNext()){
                return false;
            }else{
                //Insert Course
                database.getCollection(course).insertOne(NewCourse);
                //Update Professor
                Document tempprof = database.getCollection(prof).findOneAndDelete(new Document());
                ArrayList tempcourses = (ArrayList) tempprof.get("Courses");
                if(!tempcourses.contains((Object) CourseID)){
                    tempcourses.add((Object)CourseID);
                    tempprof.remove("Courses");
                    tempprof.append("Courses",tempcourses);
                }
                database.getCollection(prof).insertOne(tempprof);
                //Insert or update all students
                MongoCollection<Document> studentCol = database.getCollection(stud);
                for(String s:students){
                    MongoCursor<Document> cursor = studentCol.find(new Document("StudentId", s)).iterator();
                    if(cursor.hasNext()){
                        Document CurrentStudent = cursor.next();
                        ArrayList studentCourses = ((ArrayList)CurrentStudent.get("courses"));
                        if(!studentCourses.contains(CourseID)){
                            studentCourses.add(CourseID);
                            CurrentStudent.remove("courses");
                            CurrentStudent.append("courses",studentCourses);
                            studentCol.findOneAndUpdate(new Document("StudentID",s),CurrentStudent);
                        }

                    }else{
                        ArrayList<String> studentCourses = new ArrayList<String>();
                        studentCourses.add(CourseID);
                        Document CurrentStudent = new Document()
                                .append("StudentId",s)
                                .append("courses",studentCourses);
                        studentCol.insertOne(CurrentStudent);
                    }
                }
                return true;
            }
        }catch(Exception e){
            e.printStackTrace(System.out);
            throw new IOException();
        }
    }
}
