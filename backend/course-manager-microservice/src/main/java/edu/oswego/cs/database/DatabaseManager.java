package edu.oswego.cs.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DatabaseManager {
    String hostname = System.getenv("MONGO_HOSTNAME");

    int studentPort = Integer.parseInt(System.getenv("MONGO_PORT"));
    String studentDatabase = System.getenv("MONGO_DATABASE");
    String studentUser = System.getenv("MONGO_USERNAME");
    String studentPassword = System.getenv("MONGO_PASSWORD");

    int professorPort = Integer.parseInt(System.getenv("MONGO2_PORT"));
    String professorDatabase = System.getenv("MONGO_DATABASE");
    String professorUser = System.getenv("MONGO_USERNAME");
    String professorPassword = System.getenv("MONGO_PASSWORD");

    int coursePort = Integer.parseInt(System.getenv("MONGO3_PORT"));
    String courseDatabase = System.getenv("MONGO_DATABASE");
    String courseUser = System.getenv("MONGO_USERNAME");
    String coursePassword = System.getenv("MONGO_PASSWORD");

    int teamPort = Integer.parseInt(System.getenv("MONGO4_PORT"));
    String teamDatabase = System.getenv("MONGO_DATABASE");
    String teamUser = System.getenv("MONGO_USERNAME");
    String teamPassword = System.getenv("MONGO_PASSWORD");

    public MongoDatabase getStudentDB() {
        MongoCredential credentials = MongoCredential.createCredential(studentUser, studentDatabase, studentPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, studentPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(studentDatabase);
    }

    public MongoDatabase getProfessorDB() {
        MongoCredential credentials = MongoCredential.createCredential(professorUser, professorDatabase, professorPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, professorPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(professorDatabase);
    }

    public MongoDatabase getCourseDB() {
        MongoCredential credentials = MongoCredential.createCredential(courseUser, courseDatabase, coursePassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, coursePort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(courseDatabase);
    }

    public MongoDatabase getTeamDB() {
        MongoCredential credentials = MongoCredential.createCredential(teamUser, teamDatabase, teamPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, teamPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(teamDatabase);
    }

    public DatabaseManager() {
    }
}
