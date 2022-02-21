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

    String hostname = "moxie.cs.oswego.edu";//System.getenv("MONGO_HOSTNAME");
    int port = 27027;//Integer.parseInt(System.getenv("MONGO_PORT"));
    String database = "cpr";//System.getenv("MONGO_DATABASE");
    String user = "root";//System.getenv("MONGO_USERNAME");
    String password = "toor";//System.getenv("MONGO_PASSWORD");

    public MongoDatabase getDB() {
        MongoCredential credentials = MongoCredential.createCredential(user, database, password.toCharArray());
        MongoClient client = new MongoClient(
                new ServerAddress(hostname, port),
                credentials,
                new MongoClientOptions.Builder().build()
        );
        return client.getDatabase(database);
    }

   // public void close(@Disposes MongoClient toClose) {
   //     toClose.close();
   // }

    public DatabaseManager() {}
}
