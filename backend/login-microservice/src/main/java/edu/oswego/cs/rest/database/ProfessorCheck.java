package edu.oswego.cs.rest.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        BufferedReader br = new BufferedReader(new FileReader(path + "professor-list.txt"));
        String line;
        ArrayList<String> CurrentList = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            if (line.contains("@")) CurrentList.add(line.split("@")[0]);
        }
        MongoCursor<Document> results = professors.find().iterator();
        ArrayList<String> OldList = new ArrayList<>();
        while (results.hasNext()) {
            OldList.add(results.next().get("professor_id").toString());
        }

        // Promote any new professors.
        for (String s : CurrentList) {
            // Check if there are any student objects by that id if so update the courses of that professor.
            if (students.find(Filters.eq("student_id", s)).iterator().hasNext()) {
                Document newProfessor = new Document("professor_id", s);
                MongoCursor<Document> studentResults = students.find(Filters.eq("student_id", s)).iterator();
                ArrayList<Object> courses = new ArrayList<>();
                while (studentResults.hasNext()) {
                    Document oldStudent = studentResults.next();
                    if (oldStudent.get("courses") != null) {
                        courses.addAll(oldStudent.getList("courses", ArrayList.class));
                    }
                }
                // If they currently have a professor object add to course if there are any.
                if (professors.find(Filters.eq("professor_id", s)).iterator().hasNext()) {
                    for (Document oldProfessor : professors.find(Filters.eq("professor_id", s))) {
                        if (oldProfessor.get("courses") != null) {
                            courses.addAll(oldProfessor.getList("courses", ArrayList.class));
                        }
                    }
                }
                if (!courses.isEmpty()) {
                    newProfessor.append("courses", courses);
                }
                professors.deleteMany(Filters.eq("professor_id", s));
                professors.insertOne(newProfessor);
                students.deleteMany(Filters.eq("student_id", s));
            } else {
                // If they have no student object, and they are not already a professor make an object.
                if (!professors.find(Filters.eq("professor_id", s)).iterator().hasNext()) {
                    Document professorDocument = new Document("professor_id", s);
                    professorDocument.append("courses", new ArrayList<String>());
                    professors.insertOne(professorDocument);
                }
            }
        }
        // Demote any present in Old but not in new.
        for (String s : OldList) {
            if (!CurrentList.contains(s)) {
                Document oldProf = professors.find(Filters.eq("professor_id", s)).first();
                Document newStudent = new Document("student_id", s);
                if (oldProf.get("courses") != null) {
                    List<String> courses = oldProf.getList("courses", String.class);
                    newStudent.append("courses", courses);
                }
                students.insertOne(newStudent);
                professors.deleteMany(Filters.eq("professor_id", s));
            }
        }
        br.close();
    }

    public String getPath() {
        String path = (System.getProperty("user.dir").contains("\\")) ? System.getProperty("user.dir").replace("\\", "/") : System.getProperty("user.dir");
        String[] slicedPath = path.split("/");
        String targetDir = "defaultServer";
        StringBuilder relativePathPrefix = new StringBuilder();

        for (int i = slicedPath.length - 1; !slicedPath[i].equals(targetDir); i--) {
            relativePathPrefix.append("../");
        }
        if (System.getProperty("user.dir").contains("\\")) {
            reg = "//";
            relativePathPrefix = new StringBuilder(relativePathPrefix.toString().replace("/", "\\"));
        }
        return relativePathPrefix.toString();
    }
}
