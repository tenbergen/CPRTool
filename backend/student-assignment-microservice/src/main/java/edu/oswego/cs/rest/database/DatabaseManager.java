package edu.oswego.cs.rest.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DatabaseManager {
    int studentPort = 27037;//Integer.parseInt(System.getenv("MONGO_PORT"));
    int professorPort = 27038;//Integer.parseInt(System.getenv("MONGO2_PORT"));
    int coursePort = 27039;//Integer.parseInt(System.getenv("MONGO3_PORT"));
    int assignmentPort = 27040;//Integer.parseInt(System.getenv("MONGO4_PORT"));
    int teamPort = 27041;//Integer.parseInt(System.getenv("MONGO5_PORT"));
    String hostname = System.getenv("MONGO_HOSTNAME");
    String mongoDatabase = System.getenv("MONGO_DATABASE");
    String mongoUser = "csc480";//System.getenv("MONGO_USERNAME");
    String mongoPassword = "BaI2dktnzj1di6DRWcgobX37yPYZ7rmg";//System.getenv("MONGO_PASSWORD");

    public MongoDatabase getStudentDB() {
        MongoCredential credentials = MongoCredential.createCredential(mongoUser, mongoDatabase, mongoPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, studentPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(mongoDatabase);
    }

    public MongoDatabase getProfessorDB() {
        MongoCredential credentials = MongoCredential.createCredential(mongoUser, mongoDatabase, mongoPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, professorPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(mongoDatabase);
    }

    public MongoDatabase getCourseDB() {
        MongoCredential credentials = MongoCredential.createCredential(mongoUser, mongoDatabase, mongoPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, coursePort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(mongoDatabase);
    }

    public MongoDatabase getAssignmentDB() {
        MongoCredential credentials = MongoCredential.createCredential(mongoUser, mongoDatabase, mongoPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, assignmentPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(mongoDatabase);
    }

    public MongoDatabase getTeamDB() {
        MongoCredential credentials = MongoCredential.createCredential(mongoUser, mongoDatabase, mongoPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, teamPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(mongoDatabase);
    }

    public DatabaseManager() {
    }
}
