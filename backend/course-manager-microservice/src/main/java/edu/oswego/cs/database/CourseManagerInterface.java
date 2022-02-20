package edu.oswego.cs.database;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;


import javax.inject.Inject;
import java.util.ArrayList;

public class CourseManagerInterface {

    //Collection names
    private String prof = "professors";
    private String stud = "students";
    private String course = "courses";

    private ArrayList<String> courses;
    private String professorID;
    private boolean result;


    public CourseManagerInterface(String profID) {
        DatabaseManager databaseManager = new DatabaseManager();
        MongoDatabase database = databaseManager.getDB();

        try {
            MongoCursor<Document> collection = database.getCollection(prof).find(new Document("ProfessorID", profID)).iterator();
            ArrayList tempCourses = (ArrayList) collection.next().get("courses");

            for (Object o : tempCourses) {
                courses.add(o.toString());
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public ArrayList<String> getCourses(){return courses;}
    public CourseInterface getCourse(String CID) {
        return new CourseInterface(CID);
    }

    public boolean createCourse(String CID, ArrayList<String> students) {
        DatabaseManager databaseManager = new DatabaseManager();
        MongoDatabase database = databaseManager.getDB();
        Document newCourse = new Document("CourseID", CID).append("Professor", this.professorID)
                .append("Course", CID.substring(0, 5)).append("Students", students)
                .append("Teams", new ArrayList<String>()).append("TAs", new ArrayList<String>());

        try {
            if (database.getCollection(course).find(new Document("CourseID", CID)).iterator().hasNext()) {
                result = false;

            } else {
                database.getCollection(course).insertOne(newCourse);
                result = true;
            }

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        return result;
    }

    public boolean removeCourse(String CID) {
        DatabaseManager databaseManager = new DatabaseManager();
        MongoDatabase database = databaseManager.getDB();
        try {
            Document professor = database.getCollection(prof).find(new Document("ProfessorID", this.professorID)).first();

            ArrayList<String> courses = new ArrayList<>();
            for (Object o : (ArrayList) professor.get("Courses")) {
                courses.add(o.toString());
            }
            courses.remove(CID);

            Document updatedProfessor = professor;
            updatedProfessor.put("Courses", courses);
            database.getCollection(prof).updateOne(professor, updatedProfessor);

            Document deletedCourse = database.getCollection(course).find(new Document("CourseID", CID)).first();
            database.getCollection(course).deleteOne(deletedCourse);

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        return true;
    }

    public StudentInterface getStudent(String StudID) {
        return new StudentInterface(StudID);
    }

    public boolean addStudent(String CID, String StudID) {
        DatabaseManager databaseManager = new DatabaseManager();
        MongoDatabase database = databaseManager.getDB();
        try {
            Document originalCourse = database.getCollection(course).find(new Document("CourseID", CID)).first();
            Document updatedCourse = originalCourse;

            ArrayList<String> students = new ArrayList<>();
            for (Object o : (ArrayList) updatedCourse.get("Students")) {
                students.add(o.toString());
            }
            students.add(StudID);
            updatedCourse.put("Students", students);
            database.getCollection(course).updateOne(originalCourse, updatedCourse);

            boolean checkStudent = database.getCollection(stud).find(new Document("StudentID", StudID)).iterator().hasNext();
            if (checkStudent) {
                Document originalStudent = database.getCollection(stud).find(new Document("StudentID", StudID)).first();
                Document updatedStudent = originalStudent;

                ArrayList<String> courses = new ArrayList<>();
                for (Object o : (ArrayList) updatedCourse.get("Courses")) {
                    courses.add(o.toString());
                }
                courses.add(CID);
                updatedStudent.put("Courses", courses);
                database.getCollection(stud).updateOne(originalStudent, updatedStudent);
            } else {
                ArrayList<String> newCourseList = new ArrayList<>();
                newCourseList.add(CID);
                Document newStudent = new Document("StudentID", StudID).append("Courses", newCourseList);
                database.getCollection(stud).insertOne(newStudent);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            result = false;
        }
        return result;
    }

    public boolean removeStudent(String CID, String StudID) {
        DatabaseManager databaseManager = new DatabaseManager();
        MongoDatabase database = databaseManager.getDB();
        try {
            Document originalCourse = database.getCollection(course).find(new Document("CourseID", CID)).first();
            Document updatedCourse = originalCourse;

            ArrayList<String> students = new ArrayList<>();
            for (Object o : (ArrayList) updatedCourse.get("Students")) {
                students.add(o.toString());
            }
            students.remove(StudID);
            updatedCourse.put("Students", students);
            database.getCollection(course).updateOne(originalCourse, updatedCourse);

            Document originalStudent = database.getCollection(stud).find(new Document("StudentID", StudID)).first();
            Document updatedStudent = originalStudent;

            ArrayList<String> courses = new ArrayList<>();
            for (Object o : (ArrayList) updatedCourse.get("Courses")) {
                courses.add(o.toString());
            }
            courses.remove(CID);
            updatedStudent.put("Courses", courses);
            database.getCollection(stud).updateOne(originalStudent, updatedStudent);
            result = true;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            result = false;
        }
        return result;
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
}
