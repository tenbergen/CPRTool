package edu.oswego.cs.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.daos.CourseDAO;
import edu.oswego.cs.daos.FileDAO;
import edu.oswego.cs.daos.StudentDAO;
import edu.oswego.cs.util.CSVUtil;
import org.bson.Document;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static edu.oswego.cs.util.CSVUtil.parseStudentCSV;

public class CourseInterface {
    private String professorCollection = "Professor";
    private String courseCollection = "Courses";
    private String studentCollection = "Student";

    private String Courses = "Courses";
    private String SID = "StudentID";
    private String CID = "courseID";
    private String assignments = "Assignments";
    private String team = "Team";
    private String teams = "Teams";
    private String synced = "Synced";
    private String students = "Students";
    private String tas = "Tas";

    private boolean isSynced = true;
    private MongoDatabase courseDatabase;
    private MongoDatabase userDatabase;

    public CourseInterface() throws IOException {
        DatabaseManager databaseManager = new DatabaseManager();
        try{
            courseDatabase = databaseManager.getProfessorDB();
        }catch(Exception e){
            e.printStackTrace(System.out);
            throw new IOException();
        }
        try{
            userDatabase = databaseManager.getStudentDB();

        }catch(Exception e){
            e.printStackTrace(System.out);
            isSynced = false;
        }
    }
    public void addCourse(CourseDAO dao) throws IOException {

        dao.courseID = dao.courseID + "-" +  DateTimeFormatter.ofPattern("yyyy").format(LocalDateTime.now());
        String courseName = dao.courseID;

        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> courseDAOEntity = Entity.entity(jsonb.toJson(dao), MediaType.APPLICATION_JSON_TYPE);
        Document tempCourse = Document.parse(courseDAOEntity.getEntity());
        tempCourse.append(synced,isSynced);

        MongoCollection<Document> courseCol = courseDatabase.getCollection(courseCollection);
        if(courseCol.find(new Document(CID,courseName)).iterator().hasNext()){
            throw new ArrayIndexOutOfBoundsException();
        }else{
            courseCol.insertOne(tempCourse);
        }
        if(isSynced){
            Document tempProfessor = userDatabase.getCollection(professorCollection).findOneAndDelete(new Document());
            if(tempProfessor == null)throw new IOException();
            ArrayList<Document> tempList = (ArrayList<Document>) tempProfessor.get(Courses);
            tempList.add(tempCourse);
            tempProfessor.put(Courses,tempList);
            userDatabase.getCollection(professorCollection).insertOne(tempProfessor);
        }

    }
    public void addStudent(String StudentName,CourseDAO temp) throws Exception {
        CourseDAO dao = new CourseDAO(temp.courseName,temp.courseSection,temp.semester,temp.abbreviation);
        dao.courseID = dao.courseID + "-" +  DateTimeFormatter.ofPattern("yyyy").format(LocalDateTime.now());

        String courseName = dao.courseID;
        String StudentID = StudentName.split("@")[0];

        //Add Student to the student List for the course
        Document tempCourse = courseDatabase.getCollection(courseCollection).find(new Document(CID,courseName)).first();
        ArrayList StudentList = (ArrayList)tempCourse.get(students);
        if(!StudentList.contains(StudentID)){
            StudentList.add(StudentID);
            tempCourse.put(students,StudentList);
            courseDatabase.getCollection(courseCollection).findOneAndDelete(new Document(CID,courseName));
            courseDatabase.getCollection(courseCollection).insertOne(tempCourse);
        }else{
            throw new Exception("Student Already Exists In Course");
        }
        //If the student Database is connected then add the course refrence to the student if not then return
        if(!isSynced){
            setSyncFalse(courseName);
            return;
        }

        //Generate the course document
        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> courseDAOEntity = Entity.entity(jsonb.toJson(dao), MediaType.APPLICATION_JSON_TYPE);
        Document studentCourse = Document.parse(courseDAOEntity.getEntity());

        //Add student to the professor
        Document tempProfessor = userDatabase.getCollection(professorCollection).findOneAndDelete(new Document());
        ArrayList<Document> professorList = (ArrayList<Document>) tempProfessor.get(Courses);

        for(Document d: professorList){
            if(d.get(CID).equals(courseName)){
                professorList.remove(d);
                d.put(students,StudentList);
                professorList.add(d);
            }
        }

        tempProfessor.put(Courses,professorList);
        userDatabase.getCollection(professorCollection).insertOne(tempProfessor);


        //If the student Exists then update the existing student
        MongoCursor<Document> query = userDatabase.getCollection(studentCollection).find(new Document(SID, StudentID)).iterator();
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
            userDatabase.getCollection(studentCollection).deleteOne(new Document(SID,StudentID));
            userDatabase.getCollection(studentCollection).insertOne(tempStudent);
        }else{
            ArrayList courseList = new ArrayList<>();
            courseList.add(studentCourse);
            Document tempStudent = new Document()
                    .append(SID,StudentID)
                    .append(Courses,courseList);
            userDatabase.getCollection(studentCollection).insertOne(tempStudent);
        }
        for(Object o:StudentList){
            Document TempStudent = userDatabase.getCollection(studentCollection).findOneAndDelete(new Document(SID,o.toString()));
            ArrayList<Document> studentCourseStudentList = (ArrayList<Document>) TempStudent.get(Courses);
            for(Document d : studentCourseStudentList ){
                if(d.get(CID).equals(courseName)){
                    studentCourseStudentList.remove(d);
                    d.put(students,StudentList);
                    studentCourseStudentList.add(d);
                }
            }
            userDatabase.getCollection(studentCollection).insertOne(TempStudent);
        }

    }
    public void removeCourse(CourseDAO dao){

        dao.courseID = dao.courseID + "-" +  DateTimeFormatter.ofPattern("yyyy").format(LocalDateTime.now());
        String courseName = dao.courseID;
        //If student database is up then remove the refrence to this class from each student that has one
        if(isSynced){
            ArrayList studentList = (ArrayList) courseDatabase.getCollection(courseCollection).find(new Document(CID,courseName)).first().get(students);
            for(Object o :studentList){
                Document tempStudent = userDatabase.getCollection(studentCollection).findOneAndDelete(new Document(SID,o.toString()));
                ArrayList<Document> currentCourses = (ArrayList<Document>) tempStudent.get(Courses);
                currentCourses.removeIf(d -> d.get(CID).equals(courseName));
                tempStudent.put(Courses,currentCourses);
                userDatabase.getCollection(studentCollection).insertOne(tempStudent);
            }
            //remove the refrence from the professor
            Document tempProfessor = userDatabase.getCollection(professorCollection).findOneAndDelete(new Document());
            ArrayList<Document> currentCourses = (ArrayList<Document>) tempProfessor.get(Courses);
            currentCourses.removeIf(d -> d.get(CID).equals(courseName));
            tempProfessor.put(Courses,currentCourses);
            userDatabase.getCollection(professorCollection).insertOne(tempProfessor);

        }else setSyncFalse(courseName);

        //delete the course from courses
        courseDatabase.getCollection(courseCollection).findOneAndDelete(new Document(CID,courseName));
    }
    public void removeStudent(String StudentName,CourseDAO temp) throws Exception {
        CourseDAO dao = new CourseDAO(temp.courseName,temp.courseSection,temp.semester,temp.abbreviation);
        dao.courseID = dao.courseID + "-" +  DateTimeFormatter.ofPattern("yyyy").format(LocalDateTime.now());
        String courseName = dao.courseID;
        String StudentID = StudentName.split("@")[0];

        //Add Student to the student List for the course
        Document tempCourse = courseDatabase.getCollection(courseCollection).find(new Document(CID,courseName)).first();
        ArrayList StudentList = (ArrayList)tempCourse.get(students);
        if(StudentList.contains(StudentID)){
            StudentList.remove(StudentID);
            tempCourse.put(students,StudentList);
            courseDatabase.getCollection(courseCollection).findOneAndDelete(new Document(CID,courseName));
            courseDatabase.getCollection(courseCollection).insertOne(tempCourse);
        }else{
            throw new Exception("Student Does Not Exists In Course");
        }
        //If the student Database is connected then add the course refrence to the student if not then return
        if(!isSynced){
            setSyncFalse(courseName);
            return;
        }

        //Generate the course document
        Jsonb jsonb = JsonbBuilder.create();
        Entity<String> courseDAOEntity = Entity.entity(jsonb.toJson(dao), MediaType.APPLICATION_JSON_TYPE);
        Document studentCourse = Document.parse(courseDAOEntity.getEntity());

        //Add student to the professor
        Document tempProfessor = userDatabase.getCollection(professorCollection).findOneAndDelete(new Document());
        ArrayList<Document> professorList = (ArrayList<Document>) tempProfessor.get(Courses);

        for(Document d: professorList){
            if(d.get(CID).equals(courseName)){
                professorList.remove(d);
                d.put(students,StudentList);
                professorList.add(d);
            }
        }

        tempProfessor.put(Courses,professorList);
        userDatabase.getCollection(professorCollection).insertOne(tempProfessor);

        Document tempStudent = userDatabase.getCollection(studentCollection).findOneAndDelete(new Document(SID, StudentID));
        ArrayList<Document> courseList = (ArrayList<Document>)tempStudent.get(Courses);
        //Check to see if the current course is in the student
        courseList.removeIf(d -> d.get(CID).toString().equals(courseName));
        tempStudent.put(Courses,courseList);
        userDatabase.getCollection(studentCollection).insertOne(tempStudent);

        for(Object o:StudentList){
            Document TempStudent = userDatabase.getCollection(studentCollection).findOneAndDelete(new Document(SID,o.toString()));
            ArrayList<Document> studentCourseStudentList = (ArrayList<Document>) TempStudent.get(Courses);
            for(Document d : studentCourseStudentList ){
                if(d.get(CID).equals(courseName)){
                    studentCourseStudentList.remove(d);
                    d.put(students,StudentList);
                    studentCourseStudentList.add(d);
                }
            }
            tempStudent.put(Courses,studentCourseStudentList);
            userDatabase.getCollection(studentCollection).insertOne(TempStudent);
        }

    }
    public void setSyncFalse(String courseName){
        Document tempCourse = courseDatabase.getCollection(courseCollection).findOneAndDelete(new Document(CID,courseName));
        tempCourse.put(synced,false);
        courseDatabase.getCollection(courseCollection).insertOne(tempCourse);

    }
    //Feature to sync the changes that occured in courses while the students db was offline
    public void syncWithStudents(){

    }
    public void addStudentsFromCSV(FileDAO f) throws Exception {
        List<StudentDAO> allStudents = parseStudentCSV(f.getCsvLines());

        String cid = f.getFilename();
        cid = cid.substring(0,cid.length()-4);
        System.out.println(cid);
        Document course = courseDatabase.getCollection(courseCollection).find(new Document(CID,cid)).first();
        CourseDAO courseDAO = new CourseDAO(course.get("CourseName").toString(),Integer.parseInt(
                course.get("CourseSection").toString()),course.get("Semester").toString(),
                course.get("Abbreviation").toString());
        ArrayList oldStudentList = (ArrayList) course.get("Students");
        ArrayList<String> newStudentList = new ArrayList<>();
        ArrayList<String> studentsToRemove = new ArrayList<>();
        ArrayList<String> studentsToAdd = new ArrayList<>();
        System.out.println(oldStudentList);
        for(StudentDAO s:allStudents){
            newStudentList.add(s.email.split("@")[0]);
        }
        for(Object d : oldStudentList){
            if(!newStudentList.contains(d.toString())){
               studentsToRemove.add(d.toString());
            }
        }
        for(String s: newStudentList){
            if(!oldStudentList.contains(s)){
                studentsToAdd.add(s);
            }
        }
        for(String s: studentsToRemove){
            removeStudent(s,courseDAO);
        }
        for(String s:studentsToAdd){
            addStudent(s,courseDAO);
        }






    }
}
