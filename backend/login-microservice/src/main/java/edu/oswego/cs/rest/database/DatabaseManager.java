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

    int profPort = 27028;//Integer.parseInt(System.getenv("MONGO_PORT"));
    int studentPort = 27027;//Integer.parseInt(System.getenv("MONGO_PORT"));
    
    String mongoDB = "cpr";//System.getenv("MONGO_DATABASE");
    String mongoUser = "root";//System.getenv("MONGO_USERNAME");
    String mongoPassword = "toor";//System.getenv("MONGO_PASSWORD");

    public MongoDatabase getDB() {
        MongoCredential credentials = MongoCredential.createCredential(mongoUser, mongoDB, mongoPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, profPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(mongoDB);
    }

    public MongoDatabase getCourseDB() {
        MongoCredential credentials = MongoCredential.createCredential(mongoUser, mongoDB, mongoPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, profPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(mongoDB);
    }

    public MongoDatabase getStudentDB() {
        MongoCredential credentials = MongoCredential.createCredential(mongoUser, mongoDB, mongoPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, studentPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(mongoDB);
    }

}
