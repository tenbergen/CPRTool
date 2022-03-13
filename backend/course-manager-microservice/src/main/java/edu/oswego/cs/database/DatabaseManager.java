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

    public MongoDatabase getDB() {
        MongoCredential credentials = MongoCredential.createCredential(professorUser, professorDatabase, professorPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, professorPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(professorDatabase);
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

    public MongoDatabase getStudentDB() {
        MongoCredential credentials = MongoCredential.createCredential(studentUser, studentDatabase, studentPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, studentPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(studentDatabase);
    }

    public DatabaseManager() {
    }
}
