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
    int port = Integer.parseInt(System.getenv("MONGO_PORT"));
    String database = System.getenv("MONGO_DATABASE");
    String user = System.getenv("MONGO_USERNAME");
    String password = System.getenv("MONGO_PASSWORD");

    public MongoDatabase getDB() {
        MongoCredential credentials = MongoCredential.createCredential(user, database, password.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, port),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(database);
    }

    public DatabaseManager() {
    }
}