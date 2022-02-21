package edu.oswego.cs.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.daos.CourseDAO;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class CourseInterfaceV2 {
    private String professorCollection = "NewProfessor";
    private String courseCollection = "NewCourses";
    private String studentCollection = "NewStudent";

    private String Courses = "Courses";
    private String SID = "StudentID";
    private String CID = "CourseID";
    private String assignments = "Assignments";
    private String team = "Team";
    private String teams = "Teams";
    private String synced = "Synced";
    private String students = "Students";
    private String tas = "Tas";

    private boolean isSynced = true;
    private MongoDatabase professorDatabase;
    private MongoDatabase studentDatabase;

    public CourseInterfaceV2() throws IOException {
        DatabaseManager databaseManager = new DatabaseManager();
        try{
            professorDatabase = databaseManager.getProfessorDB();
        }catch(Exception e){
            e.printStackTrace(System.out);
            throw new IOException();
        }
        try{
            studentDatabase = databaseManager.getStudentDB();

        }catch(Exception e){
            e.printStackTrace(System.out);
            isSynced = false;
        }
    }
    public void addCourse(CourseDAO dao) throws IOException {
        String coureName = dao.courseName;
        MongoCollection<Document> courseCol = professorDatabase.getCollection(courseCollection);
        if(courseCol.find(new Document(CID,coureName)).iterator().hasNext()){
            throw new ArrayIndexOutOfBoundsException();
        }else{
            Document tempCourse = new Document()
                    .append(CID,coureName)
                    .append(students,new ArrayList<>())
                    .append(tas,new ArrayList<>())
                    .append(assignments,new ArrayList<>())
                    .append(synced,isSynced);
            courseCol.insertOne(tempCourse);
        }
        Document tempProfessor = professorDatabase.getCollection(professorCollection).find().first();
        if(tempProfessor == null)throw new IOException();
        ArrayList tempList = (ArrayList) tempProfessor.get(Courses);
        tempList.add(coureName);
        tempProfessor.put(Courses,tempList);
        professorDatabase.getCollection(professorCollection).findOneAndDelete(new Document());
        professorDatabase.getCollection(professorCollection).insertOne(tempProfessor);
    }
    public void addStudent(String StudentName,CourseDAO dao){
        String courseName = dao.courseName;
        String StudentID = StudentName.split("@")[0];

        //Add Student to the student List for the course
        Document tempCourse = professorDatabase.getCollection(courseCollection).find().first();
        ArrayList StudentList = (ArrayList)tempCourse.get(students);
        if(!StudentList.contains(StudentID)){
            StudentList.add(StudentID);
            tempCourse.put(students,StudentList);
            professorDatabase.getCollection(courseCollection).findOneAndDelete(new Document());
            professorDatabase.getCollection(courseCollection).insertOne(tempCourse);
        }
        //If the student Database is connected then add the course refrence to the student if not then return
        if(!isSynced){
            setSyncFalse(courseName);
            return;
        }
        MongoCursor<Document> query = studentDatabase.getCollection(studentCollection).find(new Document(SID, StudentID)).iterator();
        //Generate the course document for the student
        Document studentCourse = new Document()
                .append(CID,courseName)
                .append(team,"")
                .append(assignments,new ArrayList<>());
        //If the student Exists then update the existing student
        if(query.hasNext()){
           Document tempStudent = query.next();
           ArrayList courseList = (ArrayList)tempStudent.get(Courses);
           //Check to see if the current course is in the student
            for(Object d:courseList){
                if(((Document) d).get(CID).toString().equals(courseName)){
                    //Contains the course already so we can exit
                    return;
                }
            }
            courseList.add(studentCourse);
            tempStudent.put(Courses,courseList);
            studentDatabase.getCollection(studentCollection).deleteOne(new Document(SID,StudentID));
            studentDatabase.getCollection(studentCollection).insertOne(tempStudent);
        }else{
            ArrayList courseList = new ArrayList<>();
            courseList.add(studentCourse);
            Document tempStudent = new Document()
                    .append(SID,StudentID)
                    .append(Courses,courseList);
            studentDatabase.getCollection(studentCollection).insertOne(tempStudent);
        }
    }
    public void removeCourse(CourseDAO dao){
        String courseName = dao.courseName;
        //If student database is up then remove the refrence to this class from each student that has one
        if(isSynced){
            for(Object o:(ArrayList)professorDatabase.getCollection(courseCollection).find(new Document(CID,courseName)).first().get(students)){
                Document tempStudent = studentDatabase.getCollection(studentCollection).find(new Document(SID,o.toString())).first();
                ArrayList tempCourseList = new ArrayList();
                for(Object d:(ArrayList)tempStudent.get(Courses)){
                    if(!((Document)d).get(CID).equals(courseName)) tempCourseList.add(d);
                }
                tempStudent.put(Courses,tempCourseList);
                studentDatabase.getCollection(studentCollection).findOneAndDelete(new Document(CID,courseName));
                studentDatabase.getCollection(studentCollection).insertOne(tempStudent);
            }
        }else setSyncFalse(courseName);
        //remove the refrence from the professor
        Document tempProfessor = professorDatabase.getCollection(professorCollection).find().first();
        ArrayList tempList = (ArrayList) tempProfessor.get(Courses);
        if(tempList.contains(courseName)){
            tempList.remove(courseName);
            tempProfessor.put(Courses,tempList);
            professorDatabase.getCollection(professorCollection).findOneAndDelete(new Document());
            professorDatabase.getCollection(professorCollection).insertOne(tempProfessor);
        }
        //delete the course from courses
        professorDatabase.getCollection(courseCollection).findOneAndDelete(new Document(CID,courseName));
    }
    public void removeStudent(String StudentName,CourseDAO dao){
        String StudentID = StudentName.split("@")[0];
        String courseName = dao.courseName;

        Document tempCourse = professorDatabase.getCollection(courseCollection).findOneAndDelete(new Document(CID,courseName));
        System.out.println(tempCourse);
        ArrayList tempStudentList = (ArrayList)tempCourse.get(students);
        if(tempStudentList.contains(StudentID)){
            tempStudentList.remove(StudentID);
            tempCourse.put(students,tempStudentList);
            professorDatabase.getCollection(courseCollection).insertOne(tempCourse);
        }else {
            professorDatabase.getCollection(courseCollection).insertOne(tempCourse);
            throw new IndexOutOfBoundsException();
        }
        if(!isSynced){
            setSyncFalse(courseName);
            return;
        }
        Document tempStudent = studentDatabase.getCollection(studentCollection).findOneAndDelete(new Document(SID,StudentID));
        ArrayList tempCourses = (ArrayList)tempStudent.get(Courses);
        for(Object o:tempCourses){
            if(((Document)o).get(CID).equals(courseName)){
                tempCourses.remove(o);
                tempStudent.put(Courses,tempCourses);
                studentDatabase.getCollection(studentCollection).insertOne(tempStudent);
                return;
            }
        }
        throw new IndexOutOfBoundsException();
    }
    public void setSyncFalse(String courseName){
        Document tempCourse = professorDatabase.getCollection(courseCollection).findOneAndDelete(new Document(CID,courseName));
        tempCourse.put(synced,false);
        professorDatabase.getCollection(courseCollection).insertOne(tempCourse);

    }
    //Feature to sync the changes that occured in courses while the students db was offline
    public void syncWithStudents(){

    }
}
