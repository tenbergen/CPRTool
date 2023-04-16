package edu.oswego.cs.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.oswego.cs.util.CPRException;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class ProfessorCheck {
    private final MongoCollection<Document> professorCollection;
    String reg;

    public ProfessorCheck() {
        DatabaseManager databaseManager = new DatabaseManager();
        try {
            MongoDatabase professorDB = databaseManager.getProfessorDB();
            professorCollection = professorDB.getCollection("professors");
        } catch (WebApplicationException e) {
            throw new CPRException(Response.Status.BAD_REQUEST, "Failed to retrieve collections.");
        }
    }

    public void addProfessors() throws IOException {
        String path = getPath();
        var reader = new BufferedReader(new FileReader(path + "professor-list.txt"));
        String line = reader.readLine();
        ArrayList<String> professorList = new ArrayList<>();
        while (line != null) {
            if (line.contains("@")) {
                String[] token = line.split("@");
                professorList.add(token[0]);
            }
            line = reader.readLine();
        }

        for (String professorID : professorList) {
            Document professorDocument = professorCollection.find(eq("professor_id", professorID)).first();
            if (professorDocument == null) {
                List<String> courseList = new ArrayList<>();
                Document newProfessor = new Document()
                        .append("professor_id", professorID)
                        .append("courses", courseList);
                professorCollection.insertOne(newProfessor);
            }
        }

        for (Document professor : professorCollection.find()) {
            String professorID = professor.getString("professor_id");
            if (!professorList.contains(professorID)) professorCollection.deleteOne(eq("professor_id", professorID));
        }

        reader.close();
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
