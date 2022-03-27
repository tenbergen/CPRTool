package edu.oswego.cs.rest.database;

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

    int assPort = Integer.parseInt(System.getenv("MONGO2_PORT"));
    String assDatabase = System.getenv("MONGO_DATABASE");
    String assUser = System.getenv("MONGO_USERNAME");
    String assPassword = System.getenv("MONGO_PASSWORD");

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
