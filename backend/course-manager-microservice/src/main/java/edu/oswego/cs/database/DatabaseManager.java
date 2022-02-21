package edu.oswego.cs.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;

@ApplicationScoped
public class DatabaseManager {

    String hostname = "moxie.cs.oswego.edu";
    int port = 27027;
    String database = "cpr";
    String user = "root";
    String password = "toor";

    public MongoDatabase getDB() {
        MongoCredential credentials = MongoCredential.createCredential(user, database, password.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, port),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(database);
    }

    //public void close(@Disposes MongoClient toClose) {
    //    toClose.close();
    //}

    public DatabaseManager() {}
}