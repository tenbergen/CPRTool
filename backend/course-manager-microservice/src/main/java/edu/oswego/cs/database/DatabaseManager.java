package edu.oswego.cs.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DatabaseManager {
    int studentPort = Integer.parseInt(System.getenv("MONGO_PORT"));
    int professorPort = Integer.parseInt(System.getenv("MONGO2_PORT"));
    int coursePort = Integer.parseInt(System.getenv("MONGO3_PORT"));
    int assignmentPort = Integer.parseInt(System.getenv("MONGO4_PORT"));
    int teamPort = Integer.parseInt(System.getenv("MONGO5_PORT"));
    boolean localhost = Boolean.parseBoolean(System.getenv("LOCALHOST"));
    String host = "localhost";
    String mongoDatabase = System.getenv("MONGO_DATABASE");
    String mongoUser = System.getenv("MONGO_USERNAME");
    String mongoPassword = System.getenv("MONGO_PASSWORD");

    public DatabaseManager() {
    }

    public MongoDatabase getStudentDB() {
        if (!localhost) host = "mongo";
        MongoCredential credentials = MongoCredential.createCredential(mongoUser, mongoDatabase, mongoPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(host, studentPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(mongoDatabase);
    }

    public MongoDatabase getProfessorDB() {
        if (!localhost) host = "mongo2";
        MongoCredential credentials = MongoCredential.createCredential(mongoUser, mongoDatabase, mongoPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(host, professorPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(mongoDatabase);
    }

    public MongoDatabase getCourseDB() {
        if (!localhost) host = "mongo3";
        MongoCredential credentials = MongoCredential.createCredential(mongoUser, mongoDatabase, mongoPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(host, coursePort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(mongoDatabase);
    }

    public MongoDatabase getAssignmentDB() {
        if (!localhost) host = "mongo4";
        MongoCredential credentials = MongoCredential.createCredential(mongoUser, mongoDatabase, mongoPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(host, assignmentPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(mongoDatabase);
    }

    public MongoDatabase getTeamDB() {
        if (!localhost) host = "mongo5";
        MongoCredential credentials = MongoCredential.createCredential(mongoUser, mongoDatabase, mongoPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(host, teamPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(mongoDatabase);
    }
}