package edu.oswego.cs.rest.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DatabaseManager {
    String hostname = "moxie.cs.oswego.edu";

    int coursePort = 27028;//Integer.parseInt(System.getenv("MONGO_PORT"));
    String courseDB = "cpr";//System.getenv("MONGO_DATABASE");
    String courseUser = "root";//System.getenv("MONGO_USERNAME");
    String coursePassword = "toor";//System.getenv("MONGO_PASSWORD");

    int studentPort = 27027;//Integer.parseInt(System.getenv("MONGO_PORT"));
    String studentDB = "cpr";//System.getenv("MONGO_DATABASE");
    String studentUser = "root";//System.getenv("MONGO_USERNAME");
    String studentPassword = "toor";//System.getenv("MONGO_PASSWORD");

    public MongoDatabase getDB() {
        MongoCredential credentials = MongoCredential.createCredential(courseUser, courseDB, coursePassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, coursePort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(courseDB);
    }

    public MongoDatabase getCourseDB() {
        MongoCredential credentials = MongoCredential.createCredential(courseUser, courseDB, coursePassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, coursePort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(courseDB);
    }

    public MongoDatabase getStudentDB() {
        MongoCredential credentials = MongoCredential.createCredential(studentUser, studentDB, studentPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, studentPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(studentDB);
    }

    public DatabaseManager() {
    }
}
