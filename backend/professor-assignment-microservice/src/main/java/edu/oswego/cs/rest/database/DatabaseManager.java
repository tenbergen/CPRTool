package edu.oswego.cs.rest.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DatabaseManager {
    String hostname = "moxie.cs.oswego.edu";//System.getenv("MONGO_HOSTNAME");

    int studentPort = 27027;//Integer.parseInt(System.getenv("MONGO_PORT"));
    String studentDatabase = "cpr";//System.getenv("MONGO_DATABASE");
    String studentUser = "root";//System.getenv("MONGO_USERNAME");
    String studentPassword = "toor";//System.getenv("MONGO_PASSWORD");

    int assPort = 27030;//Integer.parseInt(System.getenv("MONGO2_PORT"));
    String assDatabase = "cpr";//System.getenv("MONGO_DATABASE");
    String assUser = "root";//System.getenv("MONGO_USERNAME");
    String assPassword = "toor";//System.getenv("MONGO_PASSWORD");

    public MongoDatabase getAssDB() {
        MongoCredential credentials = MongoCredential.createCredential(assUser, assDatabase, assPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, assPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(assDatabase);
    }
    public MongoDatabase getUserDB(){
        MongoCredential credentials = MongoCredential.createCredential(studentUser, studentDatabase, studentPassword.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, studentPort),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(studentDatabase);
    }

    public DatabaseManager() {}
}
